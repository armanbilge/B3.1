/*
 * Bounds.java
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

/**
 * Represents a multi-dimensional 'regular' boundary (a hypervolume)
 *
 * @author Alexei Drummond
 * @author Arman Bilge
 */
public interface Bounds<V> {

    /**
     * @return the upper limit of this hypervolume in the given dimension.
     */
    V getUpperLimit(int dimension);

    /**
     * @return the lower limit of this hypervolume in the given dimension.
     */
    V getLowerLimit(int dimension);

    /**
     * @return the dimensionality of this hypervolume.
     */
    int getBoundsDimension();

    final class IntersectionBounds<V> implements Bounds<V> {

        private final List<Bounds<V>> bounds = new ArrayList<>();
        private final Comparator<V> comparator;

        public IntersectionBounds(final Comparator<V> comparator, final Bounds<V> bounds) {
            this.comparator = comparator;
            this.bounds.add(bounds);
        }

        public void addBounds(final Bounds<V> bounds) {
            if (bounds.getBoundsDimension() != getBoundsDimension())
                throw new IllegalArgumentException("Bounds must have same number of dimensions.");
            this.bounds.add(bounds);
        }

        @Override
        public V getUpperLimit(int dimension) {
            return bounds.stream().map(b -> b.getUpperLimit(dimension)).min(comparator).get();
        }

        @Override
        public V getLowerLimit(int dimension) {
            return bounds.stream().map(b -> b.getUpperLimit(dimension)).max(comparator).get();
        }

        @Override
        public int getBoundsDimension() {
            return bounds.get(0).getBoundsDimension();
        }
    }

}
