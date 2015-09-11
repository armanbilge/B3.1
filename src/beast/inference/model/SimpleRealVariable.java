/*
 * SimpleRealVariable.java
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

package beast.inference.model;

import beast.inference.distributions.Distribution;
import beast.xml.AbstractXMLObjectParser;
import beast.xml.AndRule;
import beast.xml.AttributeRule;
import beast.xml.ElementRule;
import beast.xml.XMLObject;
import beast.xml.XMLObjectParser;
import beast.xml.XMLParseException;
import beast.xml.XMLSyntaxRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.IntToDoubleFunction;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

/**
 * @author Arman Bilge
 */
public final class SimpleRealVariable extends RealVariable {

    private double[] values;
    private double[] storedValues;

    public SimpleRealVariable(final String name, final int dimension) {
        super(name, dimension);
        values = new double[dimension];
        storedValues = new double[dimension];
    }

    public SimpleRealVariable(final String name, final int dimension, final double value) {
        this(name, dimension);
        fill(value);
    }

    public SimpleRealVariable(final String name, final double... values) {
        this(name, values.length);
        setValues(values);
    }

    @Override
    public double getDoubleValue(final int index) {
        return values[index];
    }

    @Override
    public DoubleStream getDoubleValues() {
        return Arrays.stream(values);
    }

    @Override
    protected void setVariableValue(final int index, final Double value) {
        values[index] = value;
    }

    @Override
    protected void setVariableValues(final Stream<Double> values) {
        this.values = values.mapToDouble(Double::doubleValue).toArray();
        if (this.values.length != getDimension())
            throw new IllegalArgumentException("Wrong number of new values");
    }

    @Override
    public void storeValues() {
        System.arraycopy(values, 0, storedValues, 0, getDimension());
    }

    @Override
    public void restoreValues() {
        final double[] tmp = values;
        values = storedValues;
        storedValues = values;
    }

    /**
     * Parses a multi-dimensional continuous parameter.
     *
     * @author Alexei Drummond
     * @author Andrew Rambaut
     * @author Arman Bilge
     */
    public static final XMLObjectParser<SimpleRealVariable> PARSER = new AbstractXMLObjectParser<SimpleRealVariable>() {

        private static final String DIMENSION = "dimension";
        private static final String VALUE = "value";
        private static final String LOWER = "lower";
        private static final String UPPER = "upper";
        private static final String RANDOMIZE = "randomize";

        private final XMLSyntaxRule rule = AndRule.newAndRule(
                AttributeRule.newIntegerRule(DIMENSION, true),
                AttributeRule.newDoubleArrayRule(VALUE, true),
                AttributeRule.newDoubleArrayRule(LOWER, true),
                AttributeRule.newDoubleArrayRule(UPPER, true),
                ElementRule.newElementRule(RANDOMIZE, Distribution.class, true)
        );

        @Override
        public Class<SimpleRealVariable> getReturnType() {
            return SimpleRealVariable.class;
        }

        @Override
        public String getName() {
            return "variable";
        }

        @Override
        public Set<String> getNames() {
            final Set<String> names = new HashSet<>(super.getNames());
            names.add("realVariable");
            names.add("simpleRealVariable");
            return Collections.unmodifiableSet(names);
        }

        @Override
        public String getDescription() {
            return "A real-valued variable of one or more dimensions.";
        }

        @Override
        public XMLSyntaxRule getSyntaxRule() {
            return rule;
        }

        @Override
        protected SimpleRealVariable parseXMLObject(final XMLObject xo) throws XMLParseException {

            final int givenDimension = xo.getAttribute(DIMENSION, 0);
            final double[] value = xo.getAttribute(VALUE, new double[0]);

            final int dimension = Math.max(givenDimension, value.length);

            if (dimension < 1)
                throw new XMLParseException("Variable must have at least one dimension.");

            if (givenDimension > 0 && value.length > 1 && givenDimension != value.length)
                throw new XMLParseException("Variable dimension and values must be the same.");

            final SimpleRealVariable variable;
            if (value.length == 1)
                variable = new SimpleRealVariable(xo.getId(), dimension, value[0]);
            else if (value.length > 1)
                variable = new SimpleRealVariable(xo.getId(), value);
            else
                variable = new SimpleRealVariable(xo.getId(), dimension);

            final double[] lower = xo.getAttribute(LOWER, new double[dimension]);
            final double[] upper = xo.getAttribute(UPPER, new double[dimension]);
            if (!xo.hasAttribute(UPPER))
                Arrays.fill(upper, Double.POSITIVE_INFINITY);

            variable.addBounds(variable.new RealBounds(lower, upper));

            if (xo.hasAttribute(RANDOMIZE)) {
                final Distribution distribution = xo.getChild(RANDOMIZE).get().getChild(Distribution.class).get();
                final Bounds<Double> bounds = variable.getBounds();
                variable.setAll((IntToDoubleFunction) i -> {
                    double v;
                    do {
                        v = distribution.sample();
                    } while (!bounds.inBounds(i, v));
                    return v;
                });
            }

            return variable;
        }

    };

}
