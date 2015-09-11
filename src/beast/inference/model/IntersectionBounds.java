/*
 * IntersectionBounds.java
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Arman Bilge
 */
public final class IntersectionBounds<V> implements Bounds<V> {

    private final int dimension;
    private final Comparator<V> comparator;
    private final V lower;
    private final V upper;
    private final List<Bounds<V>> bounds = new ArrayList<>();

    public IntersectionBounds(final int dimension, final Comparator<V> comparator, final V lower, final V upper) {
        this.dimension = dimension;
        this.comparator = comparator;
        this.lower = lower;
        this.upper = upper;
    }

    public void addBounds(final Bounds<V> bounds) {
        if (bounds.getBoundsDimension() != getBoundsDimension())
            throw new IllegalArgumentException("Bounds must have same number of dimensions.");
        this.bounds.add(bounds);
    }

    @Override
    public V getLowerLimit(final int dimension) {
        return bounds.stream().map(b -> b.getLowerLimit(dimension)).max(comparator).orElse(lower);
    }

    @Override
    public Stream<V> getLowerLimits() {
        return IntStream.range(0, getBoundsDimension())
                .mapToObj(this::getLowerLimit);
    }

    @Override
    public V getUpperLimit(final int dimension) {
        return bounds.stream().map(b -> b.getUpperLimit(dimension)).min(comparator).orElse(upper);
    }

    @Override
    public Stream<V> getUpperLimits() {
        return IntStream.range(0, getBoundsDimension())
                .mapToObj(this::getUpperLimit);
    }

    @Override
    public int getBoundsDimension() {
        return dimension;
    }

    @Override
    public String toString() {
        return "lower=["
                + getLowerLimits().map(V::toString).collect(Collectors.joining(", "))
                + "] upper=["
                + getUpperLimits().map(V::toString).collect(Collectors.joining(", "))
                + "]";
    }

}
