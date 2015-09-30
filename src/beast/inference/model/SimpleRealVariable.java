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

import java.util.Arrays;
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

}
