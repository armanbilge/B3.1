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

import java.util.stream.Stream;

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

    Stream<V> getUpperLimits();

    /**
     * @return the lower limit of this hypervolume in the given dimension.
     */
    V getLowerLimit(int dimension);

    Stream<V> getLowerLimits();

    /**
     * @return the dimensionality of this hypervolume.
     */
    int getDimension();

}
