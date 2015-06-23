/*
 * Random.java
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

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.SynchronizedRandomGenerator;

/**
 * Synchronized random number generation.
 *
 * @author Arman Bilge
 */
public final class Random {

    private static long seed = System.currentTimeMillis();
    private static final RandomGenerator RANDOM = new SynchronizedRandomGenerator(new MersenneTwister(seed));

    public static void setSeed(final long seed) {
        Random.seed = seed;
        RANDOM.setSeed(seed);
    }

    public static long getSeed() {
        return seed;
    }

    public static void nextBytes(byte[] bytes) {
        RANDOM.nextBytes(bytes);
    }

    public static int nextInt() {
        return RANDOM.nextInt();
    }

    public static int nextInt(final int n) {
        return RANDOM.nextInt(n);
    }

    public static long nextLong() {
        return RANDOM.nextLong();
    }

    public static boolean nextBoolean() {
        return RANDOM.nextBoolean();
    }

    public static float nextFloat() {
        return RANDOM.nextFloat();
    }

    public static double nextDouble() {
        return RANDOM.nextDouble();
    }

    public static double nextGaussian() {
        return RANDOM.nextGaussian();
    }

    public static double nextUniform(final double lower, final double upper) {
        return lower + nextDouble() * (upper - lower);
    }

    private Random() {}

}
