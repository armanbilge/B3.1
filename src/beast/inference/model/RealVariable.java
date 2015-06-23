/*
 * RealVariable.java
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

import beast.inference.model.Bounds.IntersectionBounds;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @author Arman Bilge
 */
public final class RealVariable extends Variable<Double> {

    private final int dimension;
    private double[] values;
    private double[] storedValues;
    private final IntersectionBounds<Double> bounds;

    {
        bounds = new IntersectionBounds<>(Double::compare, new RealBounds(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY));
    }

    public RealVariable(final String name, final int dimension) {
        this(name, new double[dimension]);
    }

    public RealVariable(final String name, final int dimension, final double value) {
        this(name, dimension);
        fill(value);
    }

    public RealVariable(final String name, final double... values) {
        super(name);
        dimension = values.length;
        this.values = values;
        storedValues = new double[dimension];
    }

    @Override
    public Double getValue(final int index) {
        return values[index];
    }

    @Override
    public void setVariableValue(final int index, final Double value) {
        values[index] = value;
    }

    @Override
    public void setVariableValues(final Double... values) {
        if (values.length != getDimension())
            throw new IllegalArgumentException("Wrong number of dimensions.");
        Arrays.setAll(this.values, i -> values[i]);
    }

    @Override
    public Stream<Double> getValues() {
        return Arrays.stream(values).boxed();
    }

    @Override
    public int getDimension() {
        return dimension;
    }

    @Override
    public void storeVariableValues() {
        System.arraycopy(values, 0, storedValues, 0, dimension);
    }

    @Override
    public void restoreVariableValues() {
        final double[] tmp = values;
        values = storedValues;
        storedValues = tmp;
    }

    @Override
    public Bounds<Double> getBounds() {
        return null;
    }

    @Override
    public void addBounds(final Bounds<Double> bounds) {
        this.bounds.addBounds(bounds);
    }

    public final class RealBounds implements Bounds<Double> {

        final double lower;
        final double upper;

        private RealBounds(final double lower, final double upper) {
            this.lower = lower;
            this.upper = upper;
        }

        @Override
        public Double getUpperLimit(int dimension) {
            return upper;
        }

        @Override
        public Double getLowerLimit(int dimension) {
            return lower;
        }

        @Override
        public int getBoundsDimension() {
            return getDimension();
        }
    }

}
