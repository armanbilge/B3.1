/*
 * XMLSyntaxRule.java
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

import java.util.Set;

public abstract class XMLSyntaxRule {

    XMLSyntaxRule() {}

    /**
     * Returns true if the rule is satisfied for the given XML object.
     */
    public abstract boolean isSatisfied(XMLObject object);

    public boolean isAttributeRule() {
        return false;
    }

    /**
     * Check if rule contains attribute of that name
     *
     * @param name attribute name
     * @return true if contains attribute
     */
    public abstract boolean containsAttribute(String name);

    /**
     * @return the classes potentially required by this rule.
     */
    public abstract Set<Class<?>> getRequiredTypes();

    /**
     * Check for possible elements: catch typos, old syntax and elements with identical names to global
     * xml element parsers.
     *
     * @param name
     * @return true if rule allows a element with that name
     */
    public abstract boolean isLegalElementName(String name);

    /**
     * @param name
     * @return true if rule allows a sub-element with that name
     */
    public abstract boolean isLegalSubelementName(String name);

    /**
     * @param c class type
     * @return true if rule accepts an element which, after parsing, is represented as a class of type 'c'
     */
    public abstract boolean isLegalElementClass(Class<?> c);

    /**
     * Describes the rule in general.
     */
    public abstract String ruleString();

}
