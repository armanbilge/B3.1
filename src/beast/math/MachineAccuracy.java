/*
 * MachineAccuracy.java
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

package beast.math;


/**
 * Determines machine accuracy
 *
 * @author Korbinian Strimmer
 * @author Alexei Drummond
 */
public final class MachineAccuracy {

    /** machine accuracy constant */
    public static final double EPSILON;// = 2.220446049250313E-16;
    public static final double SQRT_EPSILON;// = 1.4901161193847656E-8;

    static {
        EPSILON = computeEpsilon();
        SQRT_EPSILON = Math.sqrt(EPSILON);
    }

    /** Compute EPSILON from scratch */
    public static double computeEpsilon() {
        double eps = 1.0;
        while (eps + 1.0 != 1.0) eps /= 2.0;
        eps *= 2.0;
        return eps;
    }

    /**
     * @return true if the relative difference between the two parameters
     * is smaller than SQRT_EPSILON.
     */
    public static boolean same(double a, double b) {
        return Math.abs((a/b)-1.0) <= SQRT_EPSILON;
    }
}
