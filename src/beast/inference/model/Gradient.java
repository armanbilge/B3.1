/*
 * Gradient.java
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

/**
 * @author Arman Bilge
 */
public final class Gradient {

    private final Map<RealVariable, double[]> gradient = new ConcurrentHashMap<>();
    private final Function<RealVariable, double[]> mapping = var -> new double[var.getDimension()];

    public final void addDerivative(final RealVariable var, final int index, final double deriv) {
        final double[] gradient = new double[var.getDimension()];
        gradient[index] = deriv;
        addGradient(var, gradient);
    }

    public final void addGradient(final RealVariable var, final double[] gradient) {
        this.gradient.merge(var, gradient, (x, y) -> {
            for (int i = 0; i < x.length; ++i)
                y[i] += x[i];
            return y;
        });
    }

    public final DoubleStream getGradient(final Stream<RealVariable> vars) {
        return vars.map(v -> gradient.getOrDefault(v, new double[v.getDimension()]))
                .flatMapToDouble(Arrays::stream);
    }

}
