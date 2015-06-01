/*
 * SimpleXMLObjectParser.java
 *
 * BEAST: Bayesian Evolutionary Analysis by Sampling Trees
 * Copyright (C) 2014 BEAST Developers
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

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Arman Bilge
 */
public final class SimpleXMLObjectParser<T extends Identifiable> extends AbstractXMLObjectParser<T> {

    public static <T extends Identifiable> Set<SimpleXMLObjectParser<T>> generateParsers(final Class<T> parsedType) throws ParserCreationException {
        final Set<SimpleXMLObjectParser<T>> parsers = new HashSet<>();
        for (final Constructor constructor : parsedType.getConstructors())
            if (constructor.isAnnotationPresent(Parseable.class))
                parsers.add(new SimpleXMLObjectParser<>(constructor));
        return Collections.unmodifiableSet(parsers);
    }

    private final String name;
    private final String description;
    private final String[] synonyms;
    private final Class<T> parsedType;
    private final Constructor<T> constructor;
    private final AnnotationXMLObjectParser[] components;
    private final XMLSyntaxRule rule;

    private SimpleXMLObjectParser(final Constructor<T> constructor) throws ParserCreationException {

        this.constructor = constructor;
        parsedType = constructor.getDeclaringClass();
        final Parseable annotation = constructor.getAnnotation(Parseable.class);

        name = camelCase(parsedType.getSimpleName());
        description = annotation.description();
        synonyms = annotation.synonyms();

        components = new AnnotationXMLObjectParser[constructor.getParameterCount()];
        final XMLSyntaxRule[] constructorRules = new XMLSyntaxRule[components.length];
        for (int i = 0; i < components.length; ++i) {
            for (final Annotation a : constructor.getParameterAnnotations()[i]) {
                if (isParseableComponent(a)) {
                    components[i] = newXMLComponent(constructor.getParameterTypes()[i], a);
                    break;
                }
            }
            if (components[i] == null)
                throw new ParserCreationException(constructor, "Parameter missing parseable annotation.");
            constructorRules[i] = components[i].getSyntaxRule();
        }

        rule = AndRule.newAndRule(constructorRules);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<String> getNames() {
        final Set<String> names = super.getNames();
        Arrays.stream(synonyms).forEach(names::add);
        return names;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Class<T> getReturnType() {
        return parsedType;
    }

    @Override
    public T parseXMLObject(final XMLObject xo) throws XMLParseException {
        final Object[] parameters = new Object[components.length];
        for (int i = 0; i < parameters.length; ++i)
            parameters[i] = components[i].parse(xo);

        try {
            return constructor.newInstance(parameters);
        } catch (final InvocationTargetException ex) {
            throw new XMLParseException(ex.getTargetException().getMessage());
        } catch (final InstantiationException|IllegalAccessException ex) {
            throw new XMLParseException(ex.getMessage());
        }
    }

    @Override
    public XMLSyntaxRule getSyntaxRule() {
        return rule;
    }

    private <X> AnnotationXMLObjectParser<X> newXMLComponent(final Class<X> parameterType, final Annotation annotation) throws ParserCreationException {
        final AnnotationXMLObjectParserFactory factory = PARSEABLE_ANNOTATIONS.get(annotation.annotationType());
        if (!factory.validate(parameterType))
            throw new ParserCreationException(constructor, factory.getAnnotationType().getSimpleName() + " annotation must be associated with " + factory.getParsedType().getSimpleName() + " parameter.");
        return factory.newXMLObjectParser(parameterType, annotation);
    }

    public interface AnnotationXMLObjectParser<T> {
        T parse(XMLObject xo) throws XMLParseException;
        XMLSyntaxRule getSyntaxRule();
    }

    public static abstract class AnnotationXMLObjectParserFactory<A extends Annotation> {
        private final Class<A> annotationType;
        public AnnotationXMLObjectParserFactory(final Class<A> annotationType) {
            this.annotationType = annotationType;
        }
        public final Class<A> getAnnotationType() {
            return annotationType;
        }
        public abstract Class getParsedType();
        public boolean validate(final Class<?> c) {
            return c.isAssignableFrom(getParsedType());
        }
        public abstract AnnotationXMLObjectParser newXMLObjectParser(Class parameterType, A annotation);
    }

    private static final Map<Class<? extends Annotation>, AnnotationXMLObjectParserFactory<? extends Annotation>> PARSEABLE_ANNOTATIONS = new HashMap<>();

    private static final boolean isParseableComponent(final Annotation annotation) {
        return PARSEABLE_ANNOTATIONS.keySet().contains(annotation.annotationType());
    }

    public static final void registerXMLComponentFactory(final AnnotationXMLObjectParserFactory<?> factory) {
        PARSEABLE_ANNOTATIONS.put(factory.getAnnotationType(), factory);
    }

    static {

        registerXMLComponentFactory(new AnnotationXMLObjectParserFactory<BooleanAttribute>(BooleanAttribute.class) {
            @Override
            public Class getParsedType() {
                return boolean.class;
            }
            @Override
            public AnnotationXMLObjectParser<Boolean> newXMLObjectParser(final Class parameterType, final BooleanAttribute ba) {
                return new AnnotationXMLObjectParser<Boolean>() {
                    @Override
                    public Boolean parse(XMLObject xo) throws XMLParseException {
                        return xo.getAttribute(ba.name(), ba.defaultValue());
                    }
                    @Override
                    public XMLSyntaxRule getSyntaxRule() {
                        return AttributeRule.newBooleanRule(ba.name(), ba.optional(), ba.description());
                    }
                };
            }
        });

        registerXMLComponentFactory(new AnnotationXMLObjectParserFactory<DoubleArrayAttribute>(DoubleArrayAttribute.class) {
            @Override
            public Class getParsedType() {
                return double[].class;
            }
            @Override
            public AnnotationXMLObjectParser<double[]> newXMLObjectParser(final Class parameterType, final DoubleArrayAttribute daa) {
                return new AnnotationXMLObjectParser<double[]>() {
                    @Override
                    public double[] parse(XMLObject xo) throws XMLParseException {
                        final String name = daa.name();
                        return xo.hasAttribute(name) ? xo.getDoubleArrayAttribute(name) : null;
                    }
                    @Override
                    public XMLSyntaxRule getSyntaxRule() {
                        return AttributeRule.newDoubleArrayRule(daa.name(), daa.optional(), daa.description());
                    }
                };
            }
        });

        registerXMLComponentFactory(new AnnotationXMLObjectParserFactory<DoubleAttribute>(DoubleAttribute.class) {
            @Override
            public Class getParsedType() {
                return double.class;
            }
            @Override
            public AnnotationXMLObjectParser<Double> newXMLObjectParser(final Class parameterType, final DoubleAttribute da) {
                return new AnnotationXMLObjectParser<Double>() {
                    @Override
                    public Double parse(XMLObject xo) throws XMLParseException {
                        return xo.getAttribute(da.name(), da.defaultValue());
                    }
                    @Override
                    public XMLSyntaxRule getSyntaxRule() {
                        return AttributeRule.newDoubleRule(da.name(), da.optional(), da.description());
                    }
                };
            }
        });

        registerXMLComponentFactory(new AnnotationXMLObjectParserFactory<EnumAttribute>(EnumAttribute.class) {
            @Override
            public Class getParsedType() {
                return Enum.class;
            }
            @Override
            public boolean validate(Class c) {
                return c.isEnum();
            }
            @Override
            public AnnotationXMLObjectParser<Enum> newXMLObjectParser(Class parameterType, EnumAttribute ea) {
                return new AnnotationXMLObjectParser<Enum>() {
                    @Override
                    public Enum parse(XMLObject xo) throws XMLParseException {
                        if (!xo.hasAttribute(ea.name())) return null;
                        final String sa = xo.getStringAttribute(ea.name());
                        return Arrays.stream(((Enum[]) parameterType.getEnumConstants())).
                                filter(e -> e.toString().equalsIgnoreCase(sa))
                                .findFirst()
                                .orElseThrow(() -> new XMLParseException("Attribute \"" + ea.name() + "\" for " + xo + " must be one of {"
                                        + String.join(",", (Iterable<String>) Arrays.stream(parameterType.getEnumConstants()).map(Object::toString)::iterator)
                                        + "}."));
                    }
                    @Override
                    public XMLSyntaxRule getSyntaxRule() {
                        return AttributeRule.newStringRule(ea.name(), ea.optional(), ea.description());
                    }
                };
            }
        });

        registerXMLComponentFactory(new AnnotationXMLObjectParserFactory<IntegerArrayAttribute>(IntegerArrayAttribute.class) {
            @Override
            public Class getParsedType() {
                return int[].class;
            }
            @Override
            public AnnotationXMLObjectParser<int[]> newXMLObjectParser(final Class parameterType, final IntegerArrayAttribute iaa) {
                return new AnnotationXMLObjectParser<int[]>() {
                    @Override
                    public int[] parse(XMLObject xo) throws XMLParseException {
                        final String name = iaa.name();
                        return xo.hasAttribute(name) ? xo.getIntegerArrayAttribute(name) : null;
                    }
                    @Override
                    public XMLSyntaxRule getSyntaxRule() {
                        return AttributeRule.newIntegerArrayRule(iaa.name(), iaa.optional(), iaa.description());
                    }
                };
            }
        });

        registerXMLComponentFactory(new AnnotationXMLObjectParserFactory<IntegerAttribute>(IntegerAttribute.class) {
            @Override
            public Class getParsedType() {
                return int.class;
            }
            @Override
            public AnnotationXMLObjectParser<Integer> newXMLObjectParser(final Class parameterType, final IntegerAttribute ia) {
                return new AnnotationXMLObjectParser<Integer>() {
                    @Override
                    public Integer parse(XMLObject xo) throws XMLParseException {
                        return xo.getAttribute(ia.name(), ia.defaultValue());
                    }
                    @Override
                    public XMLSyntaxRule getSyntaxRule() {
                        return AttributeRule.newIntegerRule(ia.name(), ia.optional(), ia.description());
                    }
                };
            }
        });

        registerXMLComponentFactory(new AnnotationXMLObjectParserFactory<LongAttribute>(LongAttribute.class) {
            @Override
            public Class getParsedType() {
                return long.class;
            }
            @Override
            public AnnotationXMLObjectParser<Long> newXMLObjectParser(final Class parameterType, final LongAttribute la) {
                return new AnnotationXMLObjectParser<Long>() {
                    @Override
                    public Long parse(XMLObject xo) throws XMLParseException {
                        return xo.getAttribute(la.name(), la.defaultValue());
                    }
                    @Override
                    public XMLSyntaxRule getSyntaxRule() {
                        return AttributeRule.newLongRule(la.name(), la.optional(), la.description());
                    }
                };
            }
        });

        registerXMLComponentFactory(new AnnotationXMLObjectParserFactory<ObjectArrayElement>(ObjectArrayElement.class) {
            @Override
            public Class getParsedType() {
                return Object[].class;
            }
            @Override
            public boolean validate(Class c) {
                return c.isArray();
            }
            @Override
            public AnnotationXMLObjectParser<Object[]> newXMLObjectParser(final Class parameterType, final ObjectArrayElement oae) {
                return new AnnotationXMLObjectParser<Object[]>() {
                    @Override
                    public Object[] parse(XMLObject xo) throws XMLParseException {
                        final ArrayList arrayList = new ArrayList();
                        final String name = oae.name();
                        final Class componentType = parameterType.getComponentType();
                        xo.getChild(name).ifPresent(cxo ->
                                cxo.getProcessedChildren()
                                .filter(componentType::isInstance)
                                .forEach(arrayList::add));
                        try {
                            return arrayList.toArray((Object[]) Array.newInstance(componentType, arrayList.size()));
                        } catch (final NegativeArraySizeException ex) {
                            throw new XMLParseException(ex.getMessage());
                        }
                    }
                    @Override
                    public XMLSyntaxRule getSyntaxRule() {
                        return ElementRule.newElementRule(oae.name(), ElementRule.newElementRule(parameterType.getComponentType(), oae.min(), oae.max()), oae.min() == 0, oae.description());
                    }
                };
            }
        });

        registerXMLComponentFactory(new AnnotationXMLObjectParserFactory<ObjectElement>(ObjectElement.class) {
            @Override
            public Class getParsedType() {
                return Object.class;
            }
            @Override
            public boolean validate(Class c) {
                return true;
            }
            @Override
            public AnnotationXMLObjectParser<Object> newXMLObjectParser(final Class parameterType, final ObjectElement oe) {
                return new AnnotationXMLObjectParser<Object>() {
                    @Override
                    public Object parse(XMLObject xo) throws XMLParseException {
                        final String name = oe.name();
                        return xo.getChild(name).map(cxo -> cxo.getChild(parameterType)).get();
                    }
                    @Override
                    public XMLSyntaxRule getSyntaxRule() {
                        return ElementRule.newElementRule(oe.name(), parameterType, oe.optional(), oe.description());
                    }
                };
            }
        });

        registerXMLComponentFactory(new AnnotationXMLObjectParserFactory<StringArrayAttribute>(StringArrayAttribute.class) {
            @Override
            public Class getParsedType() {
                return String[].class;
            }
            @Override
            public AnnotationXMLObjectParser<String[]> newXMLObjectParser(final Class parameterType, final StringArrayAttribute saa) {
                return new AnnotationXMLObjectParser<String[]>() {
                    @Override
                    public String[] parse(XMLObject xo) throws XMLParseException {
                        final String name = saa.name();
                        return xo.hasAttribute(name) ? xo.getStringArrayAttribute(name) : null;
                    }
                    @Override
                    public XMLSyntaxRule getSyntaxRule() {
                        return AttributeRule.newStringArrayRule(saa.name(), saa.optional(), saa.description());
                    }
                };
            }
        });

        registerXMLComponentFactory(new AnnotationXMLObjectParserFactory<StringAttribute>(StringAttribute.class) {
            @Override
            public Class getParsedType() {
                return String.class;
            }
            @Override
            public AnnotationXMLObjectParser<String> newXMLObjectParser(final Class parameterType, final StringAttribute sa) {
                return new AnnotationXMLObjectParser<String>() {
                    @Override
                    public String parse(XMLObject xo) throws XMLParseException {
                        return xo.getAttribute(sa.name(), sa.defaultValue());
                    }
                    @Override
                    public XMLSyntaxRule getSyntaxRule() {
                        return AttributeRule.newStringRule(sa.name(), sa.optional(), sa.description());
                    }
                };
            }
        });
    }

    private static String camelCase(final String s) {
        final StringBuilder cc = new StringBuilder(s);
        for (int i = 0; i+1 < cc.length() && Character.isUpperCase(cc.charAt(i+1)); ++i)
            cc.setCharAt(i, Character.toLowerCase(cc.charAt(i)));
        return Introspector.decapitalize(cc.toString());
    }

    public static class ParserCreationException extends Exception {
        public ParserCreationException(final Constructor<?> constructor, final String msg) {
            super("Failed to create parser for constructor " + constructor.toGenericString() + ": "  + msg);
        }
    }
}
