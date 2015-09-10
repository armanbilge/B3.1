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

/**
 * An interface for a numerical column in a log.
 *
 * @author Andrew Rambaut
 * @author Alexei Drummond
 * @author Arman Bilge
 */
public abstract class RealNumberColumn extends LogColumn<Double> {

    private final NumberFormatter formatter = new NumberFormatter(1);

    public RealNumberColumn(final String label) {
        super(label);
    }

    @Override
    protected final String formatValue(final Double value) {
        return formatter.format(value);
    }

    public NumberFormatter getFormatter() {
        return formatter;
    }

}
