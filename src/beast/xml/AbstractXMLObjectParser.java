/*
 * AbstractXMLObjectParser.java
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

import java.util.logging.Logger;

/**
 * @author Arman Bilge
 */
public abstract class AbstractXMLObjectParser<T extends Identifiable> implements XMLObjectParser<T> {

    private static final Logger LOGGER = Logger.getLogger(AbstractXMLObjectParser.class.getPackage().getName());

    @Override
    public final T parseXMLObject(final XMLObject xo, boolean strict)
            throws XMLParseException {

        final XMLSyntaxRule rule = getSyntaxRule();

        if (!rule.isSatisfied(xo))
            throw new XMLParseException("The \"<" + getName() +
                    ">\" element with id, \"" + xo.getId() +
                    "\", is incorrectly constructed.\nThe following was expected:\n" +
                    rule.ruleString());

        // TODO Check for unexpected elements
//        for (final String name : (Iterable<String>) xo.getAttributeNames().stream()
//                .filter(n -> !n.equals(Identifiable.ID))
//                .filter(rule::containsAttribute)
//                ::iterator) {
//            final String msg = "Unhandled attribute (typo?) " + name + " in " + xo;
//            if (strict)
//                throw new XMLParseException(msg);
//            else
//                LOGGER.warning(msg);
//        }
//
//        for (Object child : (Iterable<Object>) xo.getProcessedChildren()::iterator) {
//            if (child instanceof XMLObject) {
//
//        }

        try {
            return parseXMLObject(xo);
        } catch (final XMLParseException ex) {
            throw new XMLParseException("Error parsing \"<" + getName() +
                    ">\" element with id, \"" + xo.getId() + "\":\n" +
                    ex.getMessage());
        }
    }

    protected abstract T parseXMLObject(XMLObject xo) throws XMLParseException;

    /**
     * @return a description of this parser as a string.
     */
    @Override
    public String toString() {
        return "ELEMENT " + getName() + "\n" + getSyntaxRule().ruleString() + "\n";
    }

}
