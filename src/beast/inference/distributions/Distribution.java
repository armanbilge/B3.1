/*
 * Distribution.java
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

package beast.inference.distributions;

import beast.inference.model.Model;

/**
 * an interface for a distribution.
 *
 * @author Alexei Drummond
 * @author Andrew Rambaut
 * @author Arman Bilge
 */
public abstract class Distribution extends Model {

    /**
     * @param name Model name
     */
    public Distribution(final String name) {
        super(name);
    }

    /**
     * probability density function of the distribution
     *
     * @param x argument
     * @return pdf value
     */
    public abstract double pdf(double x);

    /**
     * derivative of probability density function of the distribution
     *
     * @param x argument
     * @return pdf value
     */
    public abstract double differentiatePDF(double x);

    /**
     * the natural log of the probability density function of the distribution
     *
     * @param x argument
     * @return log pdf value
     */
    public abstract double logPDF(double x);

    /**
     * derivative of the natural log of the probability density function of the distribution
     *
     * @param x argument
     * @return log pdf value
     */
    public abstract double differentiateLogPDF(double x);

    /**
     * cumulative density function of the distribution
     *
     * @param x argument
     * @return cdf value
     */
    public abstract double cdf(double x);

    /**
     * the natural log of the cumulative density function of the distribution
     *
     * @param x argument
     * @return cdf value
     */
    public abstract double logCDF(double x);

    /**
     * quantile (inverse cumulative density function) of the distribution
     *
     * @param y argument
     * @return icdf value
     */
    public abstract double quantile(double y);

    /**
     * mean of the distribution
     *
     * @return mean
     */
    public abstract double mean();

    /**
     * variance of the distribution
     *
     * @return variance
     */
    public abstract double variance();

    @Override
    public void storeModelState() {
        // Nothing to do
    }

    @Override
    public void restoreModelState() {
        // Nothing to do
    }


}
