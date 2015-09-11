/*
 * CompoundRule.java
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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Arman Bilge
 */
abstract class CompoundRule extends XMLSyntaxRule {

    private final Set<XMLSyntaxRule> rules;
    private final String operator;

    public CompoundRule(final String operator, final XMLSyntaxRule... rules) {
        this.rules = new HashSet<>(Arrays.asList(rules));
        this.operator = operator;
    }

    public final Set<XMLSyntaxRule> getRules() {
        return Collections.unmodifiableSet(rules);
    }

    @Override
    public final boolean containsAttribute(final String name) {
        return rules.stream().anyMatch(rule -> rule.containsAttribute(name));
    }

    /**
     * Describes the rule.
     */
    @Override
    public final String ruleString() {
        return "(" + String.join(" " + operator + " ", (Iterable<String>) rules.stream().map(XMLSyntaxRule::ruleString)::iterator) + ")";
    }

    /**
     * @return a set containing the required types of this rule.
     */
    @Override
    public final Set<Class<?>> getRequiredTypes() {
        return rules.stream().map(XMLSyntaxRule::getRequiredTypes).flatMap(Set::stream).collect(Collectors.toSet());
    }

    @Override
    public final boolean isLegalElementName(final String elementName) {
        return rules.stream().anyMatch(rule -> rule.isLegalElementName(elementName));
    }

    @Override
    public final boolean isLegalElementClass(final Class<?> c) {
        return rules.stream().anyMatch(rule -> rule.isLegalElementClass(c));

    }

    @Override
    public final boolean isLegalSubelementName(final String elementName) {
        return rules.stream().anyMatch(rule -> rule.isLegalSubelementName(elementName));
    }

}
