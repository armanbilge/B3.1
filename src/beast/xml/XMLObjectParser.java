/*
 * XMLObjectParser.java
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

import java.util.Collections;
import java.util.Set;

public interface XMLObjectParser<T extends Identifiable> {

    T parseXMLObject(XMLObject xo, boolean strict) throws XMLParseException;

    /**
     * @return (Java) class of parsed element (i.e. class of object returned in {@link #parseXMLObject(XMLObject, boolean) parseXMLObject})
     */
    Class<T> getReturnType();

    /**
     * @return Parser name, which is identical to name of xml element parsed by it.
     */
    String getName();

    /**
     * @return A set of parser name synonyms (including name returned by {@link #getName() getName})
     */
    default Set<String> getNames() {
        return Collections.singleton(getName());
    }

    default String getNameSpace() {
        return getClass().getPackage().getName();
    }

    /**
     * @return Human readable description of xml element parsed by parser.
     */
    String getDescription();

    /**
     * @return The syntax rule for this element.
     */
    XMLSyntaxRule getSyntaxRule();

}
