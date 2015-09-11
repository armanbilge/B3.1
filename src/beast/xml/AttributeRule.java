/*
 * AttributeRule.java
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
 * @author Arman Bilge
 */
public final class AttributeRule<T> extends XMLSyntaxRule {

    private final String name;
    private final Class<T> c;
    private final String description;

    /**
     * Creates an attribute rule.
     */
    private AttributeRule(final String name, final Class<T> c, final String description) {
        this.name = name;
        this.c = c;
        this.description = description;
    }

    public static XMLSyntaxRule newBooleanRule(final String name) { return newBooleanRule(name, false); }
    public static XMLSyntaxRule newIntegerRule(final String name) { return newIntegerRule(name, false); }
    public static XMLSyntaxRule newIntegerArrayRule(final String name) { return newIntegerArrayRule(name, false); }
    public static XMLSyntaxRule newLongRule(final String name) { return newLongRule(name, false); }
    public static XMLSyntaxRule newDoubleRule(final String name) { return newDoubleRule(name, false); }
    public static XMLSyntaxRule newDoubleArrayRule(final String name) { return newDoubleArrayRule(name, false); }
    public static XMLSyntaxRule newStringRule(final String name) { return newStringRule(name, false); }
    public static XMLSyntaxRule newStringArrayRule(final String name) { return newStringArrayRule(name, false); }

    public static XMLSyntaxRule newBooleanRule(final String name, final boolean optional) { return newBooleanRule(name, optional, null); }
    public static XMLSyntaxRule newIntegerRule(final String name, final boolean optional) { return newIntegerRule(name, optional, null); }
    public static XMLSyntaxRule newIntegerArrayRule(final String name, final boolean optional) { return newIntegerArrayRule(name, optional, null); }
    public static XMLSyntaxRule newLongRule(final String name, final boolean optional) { return newLongRule(name, optional, null); }
    public static XMLSyntaxRule newDoubleRule(final String name, final boolean optional) { return newDoubleRule(name, optional, null); }
    public static XMLSyntaxRule newDoubleArrayRule(final String name, final boolean optional) { return newDoubleArrayRule(name, optional, null); }
    public static XMLSyntaxRule newStringRule(final String name, final boolean optional) { return newStringRule(name, optional, null); }
    public static XMLSyntaxRule newStringArrayRule(final String name, final boolean optional) { return newStringArrayRule(name, optional, null); }

    public static XMLSyntaxRule newBooleanRule(final String name, final boolean optional, final String description) {
        return OptionalRule.newOptionalRule(new AttributeRule<>(name, boolean.class, description), optional);
    }

    public static XMLSyntaxRule newIntegerRule(final String name, final boolean optional, final String description) {
        return OptionalRule.newOptionalRule(new AttributeRule<>(name, int.class, description), optional);
    }

    public static XMLSyntaxRule newIntegerArrayRule(final String name, final boolean optional, final String description) {
        return OptionalRule.newOptionalRule(new AttributeRule<>(name, int[].class, description), optional);
    }

    public static XMLSyntaxRule newLongRule(final String name, final boolean optional, final String description) {
        return OptionalRule.newOptionalRule(new AttributeRule<>(name, long.class, description), optional);
    }

    public static XMLSyntaxRule newDoubleRule(final String name, final boolean optional, final String description) {
        return OptionalRule.newOptionalRule(new AttributeRule<>(name, double.class, description), optional);
    }

    public static XMLSyntaxRule newDoubleArrayRule(final String name, final boolean optional, final String description) {
        return OptionalRule.newOptionalRule(new AttributeRule<>(name, double[].class, description), optional);
    }

    public static XMLSyntaxRule newStringRule(final String name, final boolean optional, final String description) {
        return OptionalRule.newOptionalRule(new AttributeRule<>(name, String.class, description), optional);
    }

    public static XMLSyntaxRule newStringArrayRule(final String name, final boolean optional, final String description) {
        return OptionalRule.newOptionalRule(new AttributeRule<>(name, String[].class, description), optional);
    }

    public String getName() { return name; }
    public Class<T> getAttributeClass() { return c; }
    public String getDescription() { return description; }

    public T getAttribute(final XMLObject xo) throws XMLParseException {
        final Object value;
        if (c == boolean.class)
            value = xo.getBooleanAttribute(getName());
        else if (c == int.class)
            value = xo.getIntegerAttribute(getName());
        else if (c == int[].class)
            value = xo.getIntegerArrayAttribute(getName());
        else if (c == long.class)
            value = xo.getLongAttribute(getName());
        else if (c == double.class)
            value = xo.getDoubleAttribute(getName());
        else if (c == double[].class)
            value = xo.getDoubleArrayAttribute(getName());
        else if (c == String.class)
            value = xo.getStringAttribute(getName());
        else if (c == String[].class)
            value = xo.getStringArrayAttribute(getName());
        else
            throw new XMLParseException("Attribute rule of unknown class.");
        return c.cast(value);
    }

    public boolean hasDescription() { return description != null; }

    /**
     * @return true if the required attribute of the correct type is present.
     */
    @Override
    public boolean isSatisfied(final XMLObject xo) {
        try {
            getAttribute(xo);
        } catch (final XMLParseException ex) {
            return false;
        }
        return true;
    }

    @Override
    public boolean containsAttribute(String name) {
        return name.equals(getName());
    }

    /**
     * @return a string describing the rule.
     */
    @Override
    public String ruleString() {
        return "ATTRIBUTE " + getTypeName() + " " + name;
    }

    /**
     * @return a set containing the required types of this rule.
     */
    @Override
    public Set<Class<?>> getRequiredTypes() { return Collections.singleton(c); }

    @Override
    public boolean isLegalElementName(final String elementName) {
        return false;
    }

    @Override
    public boolean isLegalElementClass(final Class<?> c) {
        return false;
    }

    @Override
    public boolean isLegalSubelementName(final String elementName) {
        return false;
    }

    @Override
    public boolean isAttributeRule() { return true; }

    /**
     * @return a pretty name for a class.
     */
    private String getTypeName() {
        if (c.isArray())
            return "Array of " + c.getComponentType().getSimpleName() + "s";
        else
            return c.getSimpleName();
    }
}
