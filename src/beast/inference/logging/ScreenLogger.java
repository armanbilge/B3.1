/*
 * ScreenLogger.java
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

import java.text.NumberFormat;
import java.util.stream.Stream;

/**
 * @author Arman Bilge
 */
public class ScreenLogger extends AbstractLogger {

    private static final String TITLE = ""; // TODO

    private final int reportDelay;
    private final NumberFormat format;

    private boolean reportStarted = false;
    private long startState;
    private double startTime;

    {
        format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(2);
    }


    public ScreenLogger(final int logEvery, final Stream<LogColumn> columns, final int reportDelay) {
        super(TITLE, new TabDelimitedFormatter(System.out), logEvery, columns);
        this.reportDelay = reportDelay;
    }

    @Override
    public void log(final long state) {

        if (!reportStarted && state >= reportDelay) {
            startTime = System.currentTimeMillis();
            startState = state;
        }

        super.log(state);

        if (!reportStarted && state >= reportDelay)
            reportStarted = true;

    }

    @Override
    protected Stream<String> getValues() {

        final String report;

        if (reportStarted) {

            final long time = System.currentTimeMillis();

            final double hoursPerMillionStates = (time - startTime) / (3.6 * (double) (getCurrentState() - startState));

            final String hpm = format.format(hoursPerMillionStates);
            if (hpm.equals("0")) // test cases can run fast :)
                report = format.format(1000 * hoursPerMillionStates) + " hours/billion states";
            else
                report = hpm + " hours/million states";

        } else {

            report = "-";

        }

        return Stream.concat(super.getValues(), Stream.of(report));

    }

}
