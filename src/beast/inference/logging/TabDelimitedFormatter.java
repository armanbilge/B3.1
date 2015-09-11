/*
 * TabDelimitedFormatter.java
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

import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A class that writes a log in tab delimited format.
 *
 * @author Andrew Rambaut
 * @author Alexei Drummond
 * @author Arman Bilge
 */
public final class TabDelimitedFormatter implements LogFormatter {

    protected final PrintWriter printWriter;

    public TabDelimitedFormatter(final PrintWriter printWriter) {
        this.printWriter = printWriter;
    }

    public TabDelimitedFormatter(final OutputStream out) {
        this(new PrintWriter(out));
    }

    public void startLogging() {
        // Nothing to do
    }

    public void logLine(String line) {
        printWriter.println(line);
        printWriter.flush();
    }

    public void logHeading(final String heading) {
        new BufferedReader(new StringReader(heading))
                .lines()
                .map(l -> "# " + l)
                .forEach(this::logLine);
    }

    public void logLabels(final Stream<String> labels) {
        logStrings(labels);
    }

    public void logValues(final Stream<String> values) {
        logStrings(values);
    }

    private void logStrings(final Stream<String> strings) {
        logLine(strings.collect(Collectors.joining("\t")));
    }

    public void stopLogging() {
        // Nothing to do
    }

}
