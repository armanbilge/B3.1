/*
 * XMLObject.java
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * This class represents an XML element.
 *
 * @author Alexei Drummond
 * @author Arman Bilge
 */
public final class XMLObject implements XMLObjectChild, Identifiable {

    private static final long serialVersionUID = 1;

    private final String name;
    private final Map<String,String> attributes = new HashMap<>();
    private final List<Object> children = new ArrayList<>();
    private Identifiable nativeObject = null;

    public XMLObject(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean hasNativeObject() {
        return nativeObject != null;
    }

    public Optional<Identifiable> getNativeObject() {
        return Optional.ofNullable(nativeObject);
    }

    public void setNativeObject(final Identifiable nativeObject) {
        this.nativeObject = nativeObject;
    }

    public Set<String> getAttributeNames() {
        return Collections.unmodifiableSet(attributes.keySet());
    }

    public boolean hasAttribute(final String name) {
        return attributes.containsKey(name);
    }

    private String getAttribute(final String name) throws XMLParseException {
        if (hasAttribute(name))
            return attributes.get(name);
        else
            throw new XMLParseException("\"" + name + "\" attribute was not found in " + getName() + " element.");
    }

    public void setAttribute(final String name, final String value) {
        attributes.put(name, value);
    }

    public boolean getBooleanAttribute(final String name) throws XMLParseException {
        return getBoolean(getAttribute(name));
    }

    public boolean getAttribute(final String name, final boolean defaultValue) throws XMLParseException {
        if (hasAttribute(name))
            return getBooleanAttribute(name);
        else
            return defaultValue;
    }

    public void setAttribute(final String name, final boolean value) {
        setAttribute(name, getString(value));
    }

    public int getIntegerAttribute(final String name) throws XMLParseException {
        return getInteger(getAttribute(name));
    }

    public int getAttribute(final String name, final int defaultValue) throws XMLParseException {
        if (hasAttribute(name))
            return getIntegerAttribute(name);
        else
            return defaultValue;
    }

    public void setAttribute(final String name, final int value) throws XMLParseException {
        setAttribute(name, getString(value));
    }

    public int[] getIntegerArrayAttribute(final String name) throws XMLParseException {
        return getIntegerArray(getAttribute(name));
    }

    public int[] getAttribute(final String name, final int[] defaultValue) throws XMLParseException {
        if (hasAttribute(name))
            return getIntegerArrayAttribute(name);
        else
            return defaultValue;
    }

    public void setAttribute(final String name, final int[] value) {
        setAttribute(name, getString(value));
    }

    public long getLongAttribute(final String name) throws XMLParseException {
        return getLong(getAttribute(name));
    }

    public long getAttribute(final String name, final long defaultValue) throws XMLParseException {
        if (hasAttribute(name))
            return getLongAttribute(name);
        else
            return defaultValue;
    }

    public void setAttribute(final String name, final long value) {
        setAttribute(name, getString(value));
    }

    public double getDoubleAttribute(final String name) throws XMLParseException {
        return getDouble(getAttribute(name));
    }

    public double getAttribute(final String name, final double defaultValue) throws XMLParseException {
        if (hasAttribute(name))
            return getDoubleAttribute(name);
        else
            return defaultValue;
    }

    public void setAttribute(final String name, final double value) {
        setAttribute(name, getString(value));
    }

    public double[] getDoubleArrayAttribute(final String name) throws XMLParseException {
        return getDoubleArray(getAttribute(name));
    }

    public double[] getAttribute(final String name, final double[] defaultValue) throws XMLParseException {
        if (hasAttribute(name))
            return getDoubleArrayAttribute(name);
        else
            return defaultValue;
    }

    public void setAttribute(final String name, final double[] value) {
        setAttribute(name, getString(value));
    }

    public String getStringAttribute(final String name) throws XMLParseException {
        return getAttribute(name);
    }

    public String getAttribute(final String name, final String defaultValue) throws XMLParseException {
        if (hasAttribute(name))
            return getStringAttribute(name);
        else
            return defaultValue;
    }

    public String[] getStringArrayAttribute(final String name) throws XMLParseException {
        return getStringArray(getAttribute(name));
    }

    public String[] getAttribute(final String name, final String[] defaultValue) throws XMLParseException {
        if (hasAttribute(name))
            return getStringArrayAttribute(name);
        else
            return defaultValue;
    }

    public void setAttribute(final String name, final String[] value) {
        setAttribute(name, getString(value));
    }

    public Stream<Object> getChildren() {
        return children.stream().map(XMLObject::processChild);
    }

    public Stream<XMLObject> getXMLObjectChildren() {
        return getChildren().filter(c -> c instanceof XMLObject).map(xo -> (XMLObject) xo);
    }

    public int getChildCount() {
        return children.size();
    }

    private static Object processChild(final Object obj) {
        final XMLObject xo;
        if (obj instanceof XMLObject)
            xo = (XMLObject) obj;
        else if (obj instanceof Reference)
            xo = ((Reference) obj).getReferencedObject();
        else
            return obj;

        if (xo.hasNativeObject())
            return xo.getNativeObject().get();
        else
            return xo;
    }

    public Object getChild(final int i) {
        return processChild(children.get(i));
    }

    public <T extends Identifiable> Optional<T> getChild(final Class<T> c) {
        return getChildren().filter(c::isInstance).map(c::cast).findFirst();
    }

    public Optional<XMLObject> getChild(final String name) {
        return getXMLObjectChildren().filter(xo -> xo.getName().equals(name)).findFirst();
    }

    public boolean hasChild(final String name) {
        return getXMLObjectChildren().anyMatch(xo -> xo.getName().equals(name));
    }

    public void addChild(final XMLObjectChild xo) {
        children.add(xo);
    }

    public void addChild(final String str) {
        children.add(str);
    }

    public void removeChild(final XMLObjectChild xo) {
        children.remove(xo);
    }

    public void removeChild(final String str) {
        children.remove(str);
    }

    public boolean getBooleanChild(final int i) throws XMLParseException {
        return getBoolean(getChild(i).toString());
    }

    public void addChild(final boolean child) {
        addChild(getString(child));
    }

    public void removeChild(final boolean child) {
        removeChild(getString(child));
    }

    public int getIntegerChild(final int i) throws XMLParseException {
        return getInteger(getChild(i).toString());
    }

    public void addChild(final int child) {
        addChild(getString(child));
    }

    public void removeChild(final int child) {
        removeChild(getString(child));
    }

    public int[] getIntegerArrayChild(final int i) throws XMLParseException {
        return getIntegerArray(getChild(i).toString());
    }

    public void addChild(final int[] child) {
        addChild(getString(child));
    }

    public void removeChild(final int[] child) {
        removeChild(getString(child));
    }

    public long getLongChild(final int i) throws XMLParseException {
        return getLong(getChild(i).toString());
    }

    public void addChild(final long child) {
        addChild(getString(child));
    }

    public void removeChild(final long child) {
        removeChild(getString(child));
    }

    public double getDoubleChild(final int i) throws XMLParseException {
        return XMLObject.getDouble(getChild(i).toString());
    }

    public void addChild(final double child) {
        addChild(getString(child));
    }

    public void removeChild(final double child) {
        removeChild(getString(child));
    }

    public double[] getDoubleArrayChild(final int i) throws XMLParseException {
        return XMLObject.getDoubleArray(getChild(i).toString());
    }

    public void addChild(final double[] child) {
        addChild(getString(child));
    }

    public void removeChild(final double[] child) {
        removeChild(getString(child));
    }

    public String getStringChild(final int i) throws XMLParseException {
        final Object child = getChild(i);
        if (child instanceof String)
            return (String) child;
        else
            throw new XMLParseException("Expected String, got " + child);
    }

    public String[] getStringArrayChild(final int i) throws XMLParseException {
        return getStringArray(getChild(i).toString());
    }

    public void addChild(final String[] child) {
        addChild(getString(child));
    }

    public void removeChild(final String[] child) {
        removeChild(getString(child));
    }

    public boolean hasId() {
        return hasAttribute(Identifiable.ID);
    }

    public String getId() {
        try {
            return getStringAttribute(Identifiable.ID);
        } catch (final XMLParseException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void setId(final String id) {
        setAttribute(Identifiable.ID, id);
    }

    public String toString() {
        return getName() + (hasId() ? ":" + getId() : "");
    }

    private static boolean getBoolean(final String value) throws XMLParseException {
        if (value.equalsIgnoreCase("true"))
            return true;
        else if (value.equalsIgnoreCase("false"))
            return false;
        throw new XMLParseException("Expected a boolean (true|false), but got " + value);
    }

    private static String getString(final boolean value) {
        return Boolean.toString(value);
    }

    private static int getInteger(final String value) throws XMLParseException {
        try {
            return Integer.parseInt(value);
        } catch (final NumberFormatException ex) {
            throw new XMLParseException("Expected integer, got " + value);
        }
    }

    private static String getString(final int value) {
        return Integer.toString(value);
    }

    private static int[] getIntegerArray(final String value) throws XMLParseException {

        final String[] values = value.split("\\s+");
        final int[] ints = new int[values.length];
        try {
            for (int i = 0; i < values.length; ++i)
                ints[i] = getInteger(values[i]);
        } catch (final XMLParseException ex) {
            throw new XMLParseException("Expected array of integers, but got " + value);
        }
        return ints;

    }

    private static String getString(final int[] value) {
        return String.join(" ", (Iterable<String>) Arrays.stream(value).mapToObj(XMLObject::getString)::iterator);
    }

    private static long getLong(final String value) throws XMLParseException {
        try {
            return Long.parseLong(value);
        } catch (final NumberFormatException ex) {
            throw new XMLParseException("Expected long, got " + value);
        }
    }

    private static String getString(final long value) {
        return Long.toString(value);
    }

    private static double getDouble(final String value) throws XMLParseException {
        try {
            return Double.parseDouble(value);
        } catch (final NumberFormatException ex) {
            throw new XMLParseException("Expected double, got " + value);
        }
    }

    private static String getString(final double value) {
        return Double.toString(value);
    }

    private static double[] getDoubleArray(final String value) throws XMLParseException {

        final String[] values = value.split("\\s+");
        final double[] doubles = new double[values.length];
        try {
            for (int i = 0; i < values.length; ++i)
                doubles[i] = getDouble(values[i]);
        } catch (final XMLParseException ex) {
            throw new XMLParseException("Expected array of doubles, but got " + value);
        }
        return doubles;

    }

    private static String getString(final double[] value) {
        return String.join(" ", (Iterable<String>) Arrays.stream(value).mapToObj(XMLObject::getString)::iterator);
    }

    private static String[] getStringArray(final String value) throws XMLParseException {
        return value.split("\\s+");
    }

    private static final Pattern WHITESPACE = Pattern.compile("\\s");

    private static String getString(final String[] value) {
        if (Arrays.stream(value).map(WHITESPACE::matcher).anyMatch(Matcher::find))
            throw new IllegalArgumentException("Values of String array may not contain any whitespace.");
        return String.join(" ", value);
    }

}
