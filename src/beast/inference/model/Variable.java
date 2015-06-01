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

import beast.inference.logging.Loggable;
import beast.xml.Identifiable;

import java.util.ArrayList;
import java.util.List;

/**
 * A generic random variable.
 *
 * @author Alexei Drummond
 * @author Arman Bilge
 */
public abstract class Variable<V> implements Identifiable, Loggable {

    private String id;
    private final String name;
    private final List<VariableListener> listeners = new ArrayList<>();

    public Variable(final String name) {
        this.name = name;
    }

    /**
     * @return the name of this variable.
     */
    public abstract String getVariableName();

    @Override
    public final String getId() {
        return id;
    }

    @Override
    public final void setId(final String id) {
        this.id = id;
    }

    public abstract V getValue(int index);

    public abstract void setValue(int index, V value);

    public abstract V[] getValues();

    /**
     * @return the size of this variable - i.e. the length of the vector
     */
    public abstract int getSize();

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

    protected final void fireVariableChanged() {
        listeners.forEach(l -> l.variableChangedEvent(new ChangeEvent(ChangeType.ALL_VALUES_CHANGED, -1)));
    }

    protected final void fireVariableChanged(final int index) {
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

    public enum ChangeType {
        VALUE_CHANGED,
        REMOVED,
        ADDED,
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
