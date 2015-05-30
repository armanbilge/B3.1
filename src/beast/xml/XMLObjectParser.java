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

import java.util.HashSet;
import java.util.Set;

public interface XMLObjectParser<T extends Identifiable> {

    T parseXMLObject(XMLObject xo, boolean strict) throws XMLParseException;

    /**
     * @return (Java) class of parsed element (i.e. class of object returned in  parseXMLObject)
     */
    Class<T> getReturnType();

    /**
     * @return Parser tag, which is identical to tag of xml element parsed by it.
     */
    String getTag();

    /**
     * @return A set of parser tag synonyms (including tag returned by getParserTag)
     */
    default Set<String> getTags() {
        final Set<String> names = new HashSet<>(2);
        names.add(getTag());
        names.add(getClass().getPackage().getName() + "." + getTag());
        return names;
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
