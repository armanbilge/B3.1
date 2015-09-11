/*
 * ElementRule.java
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

/**
 * A syntax rule to ensure that the given element appears as a child.
 *
 * @author Alexei Drummond
 * @author Andrew Rambaut
 * @author Arman Bilge
 */
public abstract class ElementRule extends XMLSyntaxRule {

    private final String description;
    private final int min;
    private final int max;

    public static ElementRule newElementRule(final Class<?> type) {
        return newElementRule(type, false);
    }

    public static ElementRule newElementRule(final Class<?> type, final boolean optional) {
        return newElementRule(type, optional, null);
    }

    public static ElementRule newElementRule(final Class<?> type, final boolean optional, final String description) {
        return newElementRule(type, optional ? 0 : 1, 1, description);
    }

    public static ElementRule newElementRule(final Class<?> type, final int min, final int max) {
        return newElementRule(type, min, max, null);
    }

    public static ElementRule newElementRule(final Class<?> type, final int min, final int max, final String description) {
        return new ClassElementRule<>(type, min, max, description);
    }

    public static ElementRule newElementRule(final String name, final XMLSyntaxRule rule) {
        return newElementRule(name, rule, false);
    }

    public static ElementRule newElementRule(final String name, final XMLSyntaxRule rule, final boolean optional) {
        return newElementRule(name, rule, optional, null);
    }

    public static ElementRule newElementRule(final String name, final XMLSyntaxRule rule, final boolean optional, final String description) {
        return newElementRule(name, rule, optional ? 0 : 1, 1, description);
    }

    public static ElementRule newElementRule(final String name, final XMLSyntaxRule rule, final int min, final int max) {
        return newElementRule(name, rule, min, max, null);
    }

    public static ElementRule newElementRule(final String name, final XMLSyntaxRule rule, final int min, final int max, final String description) {
        return new XMLElementRule(name, rule, min, max, description);
    }

    public static ElementRule newElementRule(final String name, final Class<?> type, final boolean optional) {
        return newElementRule(name, type, optional, null);
    }

    public static ElementRule newElementRule(final String name, final Class<?> type, final boolean optional, final String description) {
        return newElementRule(name, type, optional ? 0 : 1, 1, description);
    }

    public static ElementRule newElementRule(final String name, final Class<?> type, final int min, final int max) {
        return newElementRule(name, type, min, max, null);
    }

    public static ElementRule newElementRule(final String name, final Class<?> type, final int min, final int max, final String description) {
        return ElementRule.newElementRule(name, ElementRule.newElementRule(type, min, max), min == 0, description);
    }

    public static final class ClassElementRule<T> extends ElementRule {
        private final Class<T> c;

        private ClassElementRule(final Class<T> type, final int min, final int max, final String description) {
            super(min, max, description);
            this.c = type;
        }

        public Class getElementClass() { return c; }

        @Override
        public boolean isSatisfied(final XMLObject xo) {
            final long count = xo.getProcessedChildren().filter(c::isInstance).count();
            return getMin() <= count && count <= getMax();
        }

        /**
         * @return a set containing the required types of this rule.
         */
        @Override
        public Set<Class<?>> getRequiredTypes() {
            return Collections.singleton(c);
        }

        /**
         * @return a pretty name for a class.
         */
        private String getTypeName() {
            return c.getSimpleName();
        }

        @Override
        public boolean isLegalElementName(final String elementName) {
            return false;
        }

        @Override
        public boolean isLegalElementClass(Class<?> c) {
            return this.c.isAssignableFrom(c);
        }

        @Override
        public boolean isLegalSubelementName(final String elementName) {
            return false;
        }

        @Override
        protected String uncountedRuleString() {
            return "ELEMENT of type " + getTypeName() + " REQUIRED";
        }

    }

    public static final class XMLElementRule extends ElementRule {
        private final String name;
        private final XMLSyntaxRule rule;

        private XMLElementRule(final String name, final XMLSyntaxRule rule, final int min, final int max, final String description) {
            super(min, max, description);
            this.name = name;
            this.rule = rule;
        }

        public String getName() { return name; }

        public XMLSyntaxRule getRule() { return rule; }

        @Override
        public boolean isSatisfied(final XMLObject xo) {
            final long count = xo.getXMLObjectChildren()
                    .filter(cxo -> cxo.getName().equals(name))
                    .filter(rule::isSatisfied).count();
            return getMin() <= count && count <= getMax();
        }

        /**
         * @return a set containing the required types of this rule.
         */
        @Override
        public Set<Class<?>> getRequiredTypes() {
            return rule.getRequiredTypes();
        }

        /**
         * @return a pretty name for a class.
         */
        private String getTypeName() {
            return "Object";
        }

        @Override
        public boolean isLegalElementName(final String elementName) {
            return name.equals(elementName);
        }

        @Override
        public boolean isLegalElementClass(final Class<?> c) {
            return false;
        }

        @Override
        public boolean isLegalSubelementName(final String elementName) {
            return rule.isLegalElementName(elementName);
        }

        @Override
        protected String uncountedRuleString() {
            return "ELEMENT of name " + name + " REQUIRED containing " + getRule().ruleString();
        }

    }

    private ElementRule(final boolean optional, final String description) {
        this(optional ? 0 : 1, 1, description);
    }

    private ElementRule(final int min, final int max, final String description) {
        this.min = min;
        this.max = max;
        this.description = description;
    }


    public String getDescription() {
        return description;
    }

    public boolean hasDescription() { return description != null; }

    @Override
    public boolean containsAttribute(final String name) {
        return false;
    }

    /**
     * @return a string describing the rule.
     */
    @Override
    public String ruleString() {
        final String howMany;
        if (min == 1 && max == 1)
            howMany = "Exactly one";
        else if (min == max)
            howMany = "Exactly " + min;
        else if (min <= 1 && max == Integer.MAX_VALUE)
            howMany = "Any number of";
        else
            howMany = "Between " + min + " and " + max;
        return howMany + " " + uncountedRuleString();
    }

    /**
     * @return A string describing the rule ignoring count
     */
    protected abstract String uncountedRuleString();

    public int getMin() { return min; }
    public int getMax() { return max; }

    @Override
    public String toString() { return ruleString(); }

}
