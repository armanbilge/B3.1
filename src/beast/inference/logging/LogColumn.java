/*
 * LogColumn.java
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

/**
 * A column in a log.
 *
 * @author Andrew Rambaut
 * @author Alexei Drummond
 * @author Arman Bilge
 */
public abstract class LogColumn {

    private String label;
    private int minimumWidth;

    public LogColumn(final String label) {
        setLabel(label);
        setMinimumWidth(1);
    }

    public final void setLabel(final String label) {
        if (label == null)
            throw new IllegalArgumentException("Column label is null.");
        this.label = label;
    }

    private String format(final String str) {
        return String.format("%-" + getMinimumWidth() + "s", str);
    }

    public final String getLabel() {
        return format(label);
    }

    public final void setMinimumWidth(final int minimumWidth) {
        if (minimumWidth < 1)
            throw new IllegalArgumentException("Minimum width must be >= 1");
        this.minimumWidth = minimumWidth;
    }

    public final int getMinimumWidth() {
        return minimumWidth;
    }

    public final String getFormatted() {
        return format(getFormattedValue());
    }

    protected abstract String getFormattedValue();

    public static class Default extends LogColumn {

        private final Object object;

        public Default(final String label, final Object object) {
            super(label);
            this.object = object;
        }

        protected String getFormattedValue() {
            return object.toString();
        }
    }

}
