/*
 * ModelChangeListener.java
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

/**
 * An interface that provides a listener on a model.
 *
 * @author Alexei Drummond
 * @author Andrew Rambaut
 * @author Arman Bilge
 */
@FunctionalInterface
public interface ModelChangeListener<C> {

    default void modelChangedEvent() {
        modelChangedEvent(null);
    }

    /**
     * The model has changed. The model firing the event can optionally
     * supply a reference to an object and an index if appropriate. Use
     * of this extra information will be contingent on recognising what
     * model it was that fired the event.
     */
    void modelChangedEvent(C change);

}
