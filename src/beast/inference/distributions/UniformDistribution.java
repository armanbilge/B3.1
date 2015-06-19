/*
 * UniformDistribution.java
 *
 * BEAST: Bayesian Evolutionary Analysis by Sampling Trees
 * Copyright (C) 2015 BEAST Developers
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

package beast.inference.distributions;

import beast.inference.model.Variable;

/**
 * uniform distribution.
 *
 * @author Andrew Rambaut
 * @author Alexei Drummond
 * @author Arman Bilge
 */
public final class UniformDistribution extends Distribution {

    private final Variable<Double> lower;
    private final Variable<Double> upper;

    public UniformDistribution(final Variable<Double> lower, final Variable<Double> upper) {
        super("UniformDistribution");
        this.lower = lower;
        this.upper = upper;
    }

    @Override
    public double pdf(final double x) {
        final double lower = getLower();
        final double upper = getUpper();
        return (x >= lower && x <= upper ? 1.0 / (upper - lower) : 0.0);
    }

    @Override
    public double logPDF(final double x) {
        final double lower = getLower();
        final double upper = getUpper();
        if (x < lower || x > upper)
            return Double.NEGATIVE_INFINITY;
        // improve numerical stability
        return - Math.log(upper - lower);

    }

    @Override
    public double cdf(final double x) {
        final double lower = getLower();
        final double upper = getUpper();
        if (x < lower) return 0.0;
        if (x > upper) return 1.0;
        return (x - lower) / (upper - lower);
    }

    @Override
    public double logCDF(final double x) {
        final double lower = getLower();
        final double upper = getUpper();
        if (x < lower) return Double.NEGATIVE_INFINITY;
        if (x > upper) return 0.0;
        return Math.log(x - lower) - Math.log(upper - lower);
    }

    @Override
    public double quantile(final double y) {
        final double lower = getLower();
        final double upper = getUpper();
        if (!(0.0 <= y && y <= 1.0))
            throw new IllegalArgumentException("y must in range [0,1]");
        return (y * (upper - lower)) + lower;
    }

    @Override
    public double mean() {
        return (getUpper() - getLower()) / 2;
    }

    @Override
    public double variance() {
        final double lower = getLower();
        final double upper = getUpper();
        return (upper - lower) * (upper - lower) / 12;
    }

    public double getLower() {
        return lower.getValue(0);
    }

    public double getUpper() {
        return upper.getValue(0);
    }

}
