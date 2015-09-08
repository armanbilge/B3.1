/*
 * XMLParser.java
 *
 * BEAST: Bayesian Evolutionary Analysis by Sampling Trees
 * Copyright (C) 2015 BEAST Developers
 *
 * BEAST is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * BEAST is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with BEAST.  If not, see <http://www.gnu.org/licenses/>.
 */

package beast.xml;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.Reader;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 * @author Arman Bilge
 */
public class XMLParser {

    private static final Logger LOGGER = Logger.getLogger(XMLParser.class.getPackage().getName());
    private static final XMLInputFactory FACTORY = XMLInputFactory.newFactory();

    private final boolean verbose;
    private final boolean warnings;
    private final boolean strict;

    private final TreeMap<QName,XMLObjectParser<?>> parsers = new TreeMap<>();
    private final ObjectStore store = new ObjectStore();

    public XMLParser(final boolean warnings, final boolean strict) {
        this(false, warnings, strict);
    }

    public XMLParser(final boolean verbose, final boolean warnings, final boolean strict) {
        this.verbose = verbose;
        this.warnings = warnings;
        this.strict = strict;
    }

    public void addXMLObjectParser(final XMLObjectParser<?> parser) {
        addXMLObjectParser(parser, false);
    }

    public boolean addXMLObjectParser(final XMLObjectParser<?> parser, final boolean canReplace) {

        if (!canReplace && parser.getNames().stream().allMatch(parsers::containsKey))
            throw new IllegalArgumentException("New parser ("
                    + String.join(",", parser.getNames())
                    + ") cannot replace existing parsers.");

        parser.getNames().stream()
                .map(name -> new QName(parser.getNameSpace(), name))
                .filter(name -> canReplace || !parsers.containsKey(name))
                .forEach(name -> parsers.put(name, parser));

        return parser.getNames().stream().anyMatch(parsers::containsKey);

    }

    protected Optional<XMLObjectParser<?>> getParser(final QName name) {
        return Optional.ofNullable(parsers.get(name));
    }

    protected Set<QName> getParserTags() {
        return Collections.unmodifiableSet(parsers.keySet());
    }

    protected Collection<XMLObjectParser<?>> getParsers() {
        return Collections.unmodifiableCollection(parsers.values());
    }

    public XMLObject parse(final Reader reader, boolean run) throws XMLStreamException, XMLParseException {
        final XMLEventReader eventReader = FACTORY.createXMLEventReader(reader);
        eventReader.nextEvent();
        return (XMLObject) parse(eventReader, run, true);
    }

    private XMLObjectChild parse(final XMLEventReader reader, final boolean run, final boolean process) throws XMLStreamException, XMLParseException {

        if (reader.hasNext()) {

            final XMLEvent event = reader.nextEvent();

            if (event.isStartElement()) {

                final StartElement startElement = event.asStartElement();
                final String idref = Optional.ofNullable(startElement.getAttributeByName(new QName(Identifiable.IDREF)))
                        .map(Attribute::getValue).orElse(null);

                if (idref != null) {

                    {
                        final Iterator it = startElement.getAttributes();
                        it.next();
                        if (it.hasNext() || (reader.hasNext() && !reader.nextEvent().isEndElement()))
                            throw new XMLParseException("Object with idref=" + idref + " must not have other content or attributes (or perhaps it was not intended to be a reference?).");
                    }

                    final XMLObject referencedObject;
                    try {
                        referencedObject = store.getObject(idref);
                    } catch (final ObjectNotFoundException ex) {
                        throw new XMLParseException("Object with idref=" + idref + " has not been previously declared.");
                    }

                    if (!referencedObject.hasNativeObject())
                        throw new XMLParseException("Object with idref=" + idref + " has not been parsed.");

                    if (!startElement.getName().toString().equals(referencedObject.getName())) {
                        final String msg = "Element named " + startElement.getName() + " with idref=" + idref +
                                " does not match stored object with same id and tag name " + referencedObject.getName();
                        if (strict)
                            throw new XMLParseException(msg);
                        else if (warnings)
                            LOGGER.warning(msg);
                    }

                    if (verbose) LOGGER.info("Restoring idref=" + idref);

                    return new Reference(referencedObject);

                } else {

                    final QName qname = startElement.getName();
                    final XMLObject xo = new XMLObject(qname.getNamespaceURI(), qname.getLocalPart());

                    final XMLObjectParser<?> parser;
                    if (process)
                        parser = getParser(qname).orElseThrow(
                            () -> new XMLParseException("No parser for name " + xo.getName() + ".")
                        );
                    else
                        parser = null;

                    for (XMLEvent nextEvent = reader.peek(); !nextEvent.isEndElement(); nextEvent = reader.peek()) {
                        if (nextEvent.isStartElement()) {
                            final StartElement nextStartElement = nextEvent.asStartElement();

                            final String name = nextStartElement.getName().getLocalPart();
                            if (verbose) LOGGER.info("Parsing " + name);

                            // Don't parse elements that may be legal here with global parsers
                            XMLObjectChild xoc = parse(reader, run, parser == null || !parser.getSyntaxRule().isLegalElementName(name));
                            xo.addChild(xoc);

                        } else if (nextEvent.isCharacters()) {
                            final Characters characters = reader.nextEvent().asCharacters();
                            if (!characters.isWhiteSpace())
                                xo.addChild(characters.getData());
                        } else {
                            throw new XMLParseException("Encountered unexpected element: " + nextEvent.getClass().getName());
                        }
                    }

                    final EndElement endElement = reader.nextEvent().asEndElement();
                    if (!endElement.getName().equals(startElement.getName()))
                        throw new XMLParseException("Element <" + startElement.getName()
                                + "> ends with non-matching tag </" + endElement.getName() + ">.");

                    final String id = Optional.ofNullable(startElement.getAttributeByName(new QName(Identifiable.ID)))
                            .map(Attribute::getValue).orElse(null);

                    if (id != null) {
                        xo.setId(id);
                        try {
                            store.addObject(xo);
                            if (verbose) LOGGER.info("Storing " + xo.getName() + " with id=" + id);
                        } catch (final IllegalArgumentException ex) {
                            throw new XMLParseException("Object with id=" + id + " already exists");
                        }
                    }

                    if (parser != null) {
                        final Identifiable obj = parser.parseXMLObject(xo, strict);
                        obj.setId(id);
                        xo.setNativeObject(obj);
                        if (run && obj instanceof Spawnable)
                            ((Spawnable) obj).run();
                    }

                    return xo;
                }
            } else {
                throw new XMLParseException("Encountered unexpected element: " + event.getClass().getName());
            }

        } else {
            throw new XMLParseException("Unexpected end of XML tree.");
        }
    }

}


