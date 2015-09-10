/*
 * Variable.java
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

package beast.inference.model;

import beast.inference.logging.LogColumn;
import beast.inference.logging.Loggable;
import beast.xml.Identifiable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A generic random variable.
 *
 * @author Alexei Drummond
 * @author Arman Bilge
 */
public abstract class Variable<V> implements Identifiable, Loggable {

    private String id;
    private final String name;
    private final int dimension;
    private final List<VariableListener> listeners = new ArrayList<>();

    public Variable(final String name, final int dimension) {
        this.name = name;
        this.dimension = dimension;
    }

    /**
     * @return the name of this variable.
     */
    public final String getVariableName() {
        return name;
    }

    /**
     * @return the dimension of this variable - i.e. the length of the vector
     */
    public final int getDimension() {
        return dimension;
    }

    public abstract V getValue(int index);

    public abstract Stream<V> getValues();

    public final void setValue(final int index, final V value) {
        setVariableValue(index, value);
        fireVariableChanged(index);
    }

    public final void setValues(final Stream<V> values) {
        setVariableValues(values);
        fireVariableChanged();
    }

    @SafeVarargs
    public final void setValues(final V... values) {
        setValues(Arrays.stream(values));
    }

    public final void fill(final V value) {
        setAll(i -> value);
    }

    public final void setAll(final IntFunction<V> generator) {
        setValues(IntStream.range(0, getDimension()).mapToObj(generator));
    }

    protected abstract void setVariableValue(int index, V value);

    protected abstract void setVariableValues(Stream<V> values);

    /**
     * adds a parameter listener that is notified when this parameter changes.
     *
     * @param listener the listener
     */
    public final void addVariableListener(final VariableListener listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }

    /**
     * removes a parameter listener.
     *
     * @param listener the listener
     */
    public final void removeVariableListener(VariableListener listener) {
        listeners.remove(listener);
    }

    public final void fireVariableChanged() {
        listeners.forEach(l -> l.variableChangedEvent(new ChangeEvent(ChangeType.ALL_VALUES_CHANGED, -1)));
    }

    private void fireVariableChanged(final int index) {
        listeners.forEach(l -> l.variableChangedEvent(new ChangeEvent(ChangeType.VALUE_CHANGED, index)));
    }

    /**
     * stores the state of this parameter for subsequent restore
     */
    public abstract void storeVariableValues();

    /**
     * restores the stored state of this parameter
     */
    public abstract void restoreVariableValues();

    /**
     * @return the bounds on this parameter
     */
    public abstract Bounds<V> getBounds();

    public abstract void addBounds(Bounds<V> bounds);

    @Override
    public final String getId() {
        return id;
    }

    @Override
    public final void setId(final String id) {
        this.id = id;
    }

    @Override
    public Collection<LogColumn> getColumns() {
        return IntStream.range(0, getDimension())
                .mapToObj(VariableLogColumn::new)
                .collect(Collectors.toList());
    }

    private final class VariableLogColumn extends LogColumn<V> {

        private final int dimension;

        VariableLogColumn(final int dimension) {
            super(getVariableName() + "[" + dimension + "]");
            this.dimension = dimension;
        }

        @Override
        protected V getValue() {
            return Variable.this.getValue(dimension);
        }
    }

    public enum ChangeType {
        VALUE_CHANGED,
        ALL_VALUES_CHANGED
    }

    public class ChangeEvent {

        private final ChangeType type;
        private final int index;

        protected ChangeEvent(final ChangeType type, final int index) {
            this.type = type;
            this.index = index;
        }

        public Variable<V> getVariable() {
            return Variable.this;
        }

        public ChangeType getType() {
            return type;
        }

        public int getIndex() {
            return index;
        }
    }
}
