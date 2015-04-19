/*
 * XMLSyntaxRule.java
 *
 * BEAST: Bayesian Evolutionary Analysis by Sampling Trees
 * Copyright (C) 2015 BEAST Developers
 *
 * BEAST is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * BEAST is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BEAST.  If not, see <http://www.gnu.org/licenses/>.
 */

package beast.xml;

import java.util.Set;

public interface XMLSyntaxRule {

    /**
     * Returns true if the rule is satisfied for the given XML object.
     */
    boolean isSatisfied(XMLObject object, boolean strict);

    /**
     * Check if rule contains attribute of that name
     *
     * @param name attribute name
     * @return true if contains attribute
     */
    boolean containsAttribute(String name);

    /**
     * @return the classes potentially required by this rule.
     */
    Set<Class> getRequiredTypes();

    /**
     * Check for possible elements: catch typos, old syntax and elements with identical names to global
     * xml element parsers.
     *
     * @param tag
     * @return true if rule allows a element with that tag
     */
    boolean isLegalElementName(String tag);

    /**
     * @param tag
     * @return true if rule allows a sub-element with that tag
     */
    boolean isLegalSubelementName(String tag);

    /**
     * @param c class type
     * @return true if rule accepts an element which, after parsing, is represented as a class of type 'c'
     */
    boolean isLegalElementClass(Class<?> c);

    /**
     * Describes the rule in general.
     */
    String ruleString();

    /**
     * Describes the rule as pertains to the given object.
     * In particular if object does not satisfy the rule then how.
     */
    String ruleString(XMLObject object);

}
