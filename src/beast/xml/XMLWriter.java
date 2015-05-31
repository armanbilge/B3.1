/*
 * XMLWriter.java
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
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.Writer;
import java.util.stream.Stream;

/**
 * @author Arman Bilge
 */
public class XMLWriter {

    private static final XMLOutputFactory FACTORY = XMLOutputFactory.newFactory();
    private static final XMLEventFactory EVENT_FACTORY = XMLEventFactory.newFactory();

    public void write(final Writer writer, final XMLObject xo) throws XMLStreamException {
        final XMLEventWriter eventWriter = FACTORY.createXMLEventWriter(writer);
        eventWriter.add(EVENT_FACTORY.createStartDocument(null, "1.0", true));
        write(eventWriter, xo);
        eventWriter.add(EVENT_FACTORY.createEndDocument());
        eventWriter.close();
    }

    private void write(final XMLEventWriter writer, final Object obj) throws XMLStreamException {
        if (obj instanceof XMLObject) {

            final XMLObject xo = (XMLObject) obj;
            writer.add(EVENT_FACTORY.createStartElement(new QName(xo.getName()),
                    xo.getAttributes().entrySet().stream()
                            .map(e -> EVENT_FACTORY.createAttribute(new QName(e.getKey()), e.getValue())).iterator(),
                    null));

            for (final Object child : xo.getChildren())
                write(writer, child);

            writer.add(EVENT_FACTORY.createEndElement(new QName(xo.getName()), null));

        } else if (obj instanceof Reference) {

            final XMLObject xo = ((Reference) obj).getReferencedObject();
            writer.add(EVENT_FACTORY.createStartElement(new QName(xo.getName()),
                    Stream.of(EVENT_FACTORY.createAttribute(new QName(Identifiable.IDREF), xo.getId())).iterator(),
                    null));
            writer.add(EVENT_FACTORY.createEndElement(new QName(xo.getName()), null));

        } else if (obj instanceof String) {

            writer.add(EVENT_FACTORY.createCharacters((String) obj));

        }
    }

}
