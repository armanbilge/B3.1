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

import beast.inference.logging.LogColumn;
import beast.inference.logging.RealNumberColumn;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Arman Bilge
 */
public abstract class RealVariable extends Variable<Double> {

    private final IntersectionBounds<Double> bounds;

    public RealVariable(final String name, final int dimension) {
        super(name, dimension);
        bounds = new IntersectionBounds<>(getDimension(), Double::compare, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    @Override
    public Bounds<Double> getBounds() {
        return bounds;
    }

    protected IntersectionBounds<Double> getIntersectionBounds() {
        return bounds;
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
        public Stream<Double> getUpperLimits() {
            return IntStream.range(0, getDimension()).mapToObj(i -> upper);
        }

        @Override
        public Double getLowerLimit(int dimension) {
            return lower;
        }

        @Override
        public Stream<Double> getLowerLimits() {
            return IntStream.range(0, getDimension()).mapToObj(i -> lower);
        }

        @Override
        public int getBoundsDimension() {
            return getDimension();
        }
    }

    @Override
    public Collection<LogColumn> getColumns() {
        return Collections.unmodifiableList(
                IntStream.range(0, getDimension())
                        .mapToObj(Column::new)
                        .collect(Collectors.toList()));
    }

    private class Column extends RealNumberColumn {

        final int dimension;

        public Column(final int dimension) {
            super(getVariableName() + "[" + dimension + "]");
            this.dimension = dimension;
        }

        @Override
        protected Double getValue() {
            return RealVariable.this.getValue(dimension);
        }
    }

}
