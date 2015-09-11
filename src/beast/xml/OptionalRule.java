/*
 * OptionalRule.java
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

/**
 * @author Arman Bilge
 */
final class OptionalRule extends XMLSyntaxRule {

    private final XMLSyntaxRule rule;

    public static XMLSyntaxRule newOptionalRule(final XMLSyntaxRule rule, final boolean optional) {
        return optional ? new OptionalRule(rule) : rule;
    }

    private OptionalRule(final XMLSyntaxRule rule) {
        this.rule = rule;
    }

    public XMLSyntaxRule getRule() {
        return rule;
    }

    /**
     * Returns true if the rule is satisfied for the given XML object.
     *
     * @param object
     */
    @Override
    public boolean isSatisfied(final XMLObject object) {
        return true;
    }

    /**
     * Check if rule contains attribute of that name
     *
     * @param name attribute name
     * @return true if contains attribute
     */
    @Override
    public boolean containsAttribute(final String name) {
        return rule.containsAttribute(name);
    }

    /**
     * @return the classes potentially required by this rule.
     */
    @Override
    public Set<Class<?>> getRequiredTypes() {
        return rule.getRequiredTypes();
    }

    /**
     * Check for possible elements: catch typos, old syntax and elements with identical names to global
     * xml element parsers.
     *
     * @param tag
     * @return true if rule allows a element with that tag
     */
    @Override
    public boolean isLegalElementName(final String tag) {
        return rule.isLegalElementName(tag);
    }

    /**
     * @param tag
     * @return true if rule allows a sub-element with that tag
     */
    @Override
    public boolean isLegalSubelementName(final String tag) {
        return rule.isLegalSubelementName(tag);
    }

    /**
     * @param c class type
     * @return true if rule accepts an element which, after parsing, is represented as a class of type 'c'
     */
    @Override
    public boolean isLegalElementClass(final Class<?> c) {
        return rule.isLegalElementClass(c);
    }

    /**
     * Describes the rule in general.
     */
    @Override
    public String ruleString() {
        return "Optionally, " + rule.ruleString();
    }
}
