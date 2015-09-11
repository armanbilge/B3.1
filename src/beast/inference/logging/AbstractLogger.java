/*
 * MCLogger.java
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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A class for a general purpose logger.
 *
 * @author Andrew Rambaut
 * @author Alexei Drummond
 */
public abstract class AbstractLogger implements Logger {

    private final String title;
    private final int logEvery;
    private final LogFormatter formatter;
    private final List<LogColumn> columns;

    private long currentState;

    /**
     * Constructor. Will log every logEvery.
     *
     * @param formatter the formatter of this logger
     * @param logEvery  logging frequency
     * @param columns log columns
     */
    public AbstractLogger(final LogFormatter formatter, final int logEvery, final Stream<LogColumn> columns) {
        this(null, formatter, logEvery, columns);
    }

    /**
     * Constructor. Will log every logEvery.
     *
     * @param title title
     * @param formatter the formatter of this logger
     * @param logEvery  logging frequency
     * @param columns log columns
     */
    public AbstractLogger(final String title, final LogFormatter formatter, final int logEvery, final Stream<LogColumn> columns) {
        this.title = title;
        this.logEvery = logEvery;
        this.formatter = formatter;
        this.columns = Collections.unmodifiableList(columns.collect(Collectors.toList()));
    }

    protected long getCurrentState() {
        return currentState;
    }

    public void startLogging() {

        formatter.startLogging();

        if (title != null)
            formatter.logHeading(title);

        if (logEvery > 0) {
            final Stream<String> columnLabels = columns.stream().map(LogColumn::toString);
            final Stream<String> labels = Stream.concat(Stream.of("state"), columnLabels);
            formatter.logLabels(labels);
        }
    }

    public void log(final long state) {

        currentState = state;

        if (logEvery > 0 && state % logEvery == 0) {
            final Stream<String> values = Stream.concat(Stream.of(Long.toString(state)), getValues());
            formatter.logValues(values);
        }

    }

    protected Stream<String> getValues() {
        return columns.stream().map(LogColumn::getFormatted);
    }

    public void stopLogging() {
        formatter.stopLogging();
    }

}
