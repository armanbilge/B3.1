/*
 * CoercableMCMCOperator.java
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

package beast.inference.operation;

/**
 * An MCMC operator that can be coerced to produce a target acceptance probability.
 *
 * @author Alexei Drummond
 * @author Andrew Rambaut
 */
public interface CoercableOperator extends Operator {

    String AUTO_OPTIMIZE = "autoOptimize";

    /**
     * A coercable parameter must have a range from -infinity to +infinity with a preference for
     * small numbers.
     * <p/>
     * If operator acceptance is too high, BEAST increases the parameter; if operator acceptance is
     * too low, BEAST decreases the parameter.
     * <p/>
     * From MarkovChain.coerceAcceptanceProbability:
     * <p/>
     * new parameter = old parameter + 1/(1+N) * (current-step acceptance probability - target probabilitity),
     * <p/>
     * where N is some function of the number of operator trials.
     *
     * @return a "coercable" parameter
     */
    double getCoercableParameter();

    /**
     * Sets the coercable parameter value. A coercable parameter must have a range from -infinity to +infinity with a preference for
     * small numbers.
     *
     * @param value the value to set the coercible parameter to
     */
    void setCoercableParameter(double value);

    /**
     * @return the underlying tuning parameter value
     */
    double getRawParameter();

    /**
     * @return the mode of this operator.
     */
    CoercionMode getMode();

}
