/*
 * NumberColumn.java
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

package beast.inference.logging;

import beast.math.NumberFormatter;

import java.util.function.Supplier;

/**
 * An interface for a numerical column in a log.
 *
 * @author Andrew Rambaut
 * @author Alexei Drummond
 * @author Arman Bilge
 */
public final class RealNumberColumn extends LogColumn<Double> {

    private final NumberFormatter formatter;

    public RealNumberColumn(final String label, final Supplier<Double> value) {
        this(label, new NumberFormatter(1), value);
    }

    private RealNumberColumn(final String label, final NumberFormatter formatter, final Supplier<Double> value) {
        super(label, formatter::format, value);
        this.formatter = formatter;
    }

    public NumberFormatter getFormatter() {
        return formatter;
    }

}
