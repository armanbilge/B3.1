/*
 * NumberFormatter.java
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

import org.apache.commons.math3.util.ArithmeticUtils;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The world's most intelligent number formatter with the following features :-)
 * <P>
 * It guarantee's the display of a user-specified number of significant figures, sf <BR>
 * It displays decimal format for numbers with absolute values between 1 and 10^(sf-1) <BR>
 * It displays scientific notation for all other numbers (i.e. really big and really small absolute values) <BR>
 * <b>note</b>: Its display integers for doubles with integer value <BR>
 *
 * @author Alexei Drummond
 * @author Arman Bilge
 */
public final class NumberFormatter implements Serializable {

    private final DecimalFormat decimalFormat;
    private DecimalFormat scientificFormat;

    private int sf;
    private int fieldWidth;
    private boolean isPadding = false;

    private double upperCutoff;
    private double[] cutoffTable;

    {
        decimalFormat = new DecimalFormat();
        decimalFormat.setGroupingUsed(false);
    }

    public NumberFormatter(final int sf) {
        setSignificantFigures(sf);
    }

    public NumberFormatter(final int sf, final int fieldWidth) {
        setSignificantFigures(sf);
        setPadding(true);
        setFieldWidth(fieldWidth);
    }

    public void setSignificantFigures(int sf) {
        this.sf = sf;

        upperCutoff = ArithmeticUtils.pow(10, sf - 1);
        cutoffTable = IntStream.rangeClosed(1, sf)
                .mapToLong(i -> ArithmeticUtils.pow(10L, i))
                .asDoubleStream()
                .toArray();

        decimalFormat.setMinimumIntegerDigits(1);
        decimalFormat.setMaximumFractionDigits(sf - 1);
        decimalFormat.setMinimumFractionDigits(sf - 1);

        scientificFormat = getScientificFormat();

        fieldWidth = sf;
    }

    public void setPadding(final boolean padding) {
        isPadding = padding;
    }

    public int getFieldWidth() { return fieldWidth; }

    public void setFieldWidth(final int fw) {
        if (fw < sf + 4)
            throw new IllegalArgumentException("Field width must be at least s.f. + 4.");
        fieldWidth = fw;
    }

    /**
     * This method formats a number 'nicely': <BR>
     * It guarantee's the display of a user-specified total significant figures, sf <BR>
     * It displays decimal format for numbers with absolute values between 1 and 10^(sf-1) <BR>
     * It displays scientific notation for all other numbers (i.e. really big and really small absolute values) <BR>
     * <b>note</b>: Its display integers for doubles with integer value <BR>
     * @return a nicely formatted number.
     */
    public String format(final double value) {

        final StringBuilder builder = new StringBuilder(fieldWidth);

        final double absValue = Math.abs(value);

        if (absValue > upperCutoff || (absValue < 0.1 && absValue != 0.0)) {

            builder.append(scientificFormat.format(value));

        } else {

            final int numFractionDigits;
            if (value != (int) value)
                numFractionDigits = getNumFractionDigits(value);
            else
                numFractionDigits = 0;

            builder.append(formatDecimal(value, numFractionDigits));

        }

        if (isPadding) {
            final int size = fieldWidth - builder.length();
            for (int i = 0; i < size; i++)
                builder.append(' ');
        }

        return builder.toString();
    }

    /**
     * @return the given value formatted to have exactly then number of
     * fraction digits specified.
     */
    private String formatDecimal(final double value, final int numFractionDigits) {
        decimalFormat.setMaximumFractionDigits(numFractionDigits);
        decimalFormat.setMinimumFractionDigits(Math.min(numFractionDigits, 1));
        return decimalFormat.format(value);
    }

    private int getNumFractionDigits(final double value) {
        final double absValue = Math.abs(value);
        final int i = IntStream.range(0, cutoffTable.length)
                .filter(j -> absValue < cutoffTable[j])
                .findFirst()
                .orElseGet(() -> 0);
        return sf - i - 1;
    }

    private DecimalFormat getScientificFormat() {
        final String format = "0." + IntStream.rangeClosed(0, sf - 1).mapToObj(i -> "#").collect(Collectors.joining()) + "E0";
        return new DecimalFormat(format);
    }
}
