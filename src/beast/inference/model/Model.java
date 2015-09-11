/*
 * Model.java
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

import beast.xml.Identifiable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * An interface that describes a model of some data.
 *
 * @author Alexei Drummond
 * @author Andrew Rambaut
 * @author Arman Bilge
 */
public abstract class Model<C> implements Identifiable, ModelChangeListener<Object>, VariableListener {

    private String id;

    private final String name;

    private final List<Model<?>> models = new ArrayList<>();
    private final List<Variable<?>> variables = new ArrayList<>();

    private final List<ModelChangeListener<C>> changeListeners = new ArrayList<>();
    private final List<ModelStoreListener> storeListeners = new ArrayList<>();

    /**
     * @param name Model name
     */
    public Model(final String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    public final boolean isUsed() {
        return changeListeners.size() + storeListeners.size() > 0;
    }

    /**
     * Adds a sub-model to this model. If the model is already in the
     * list then it does nothing.
     */
    public final void addModel(final Model model) {
        if (!models.contains(model)) {
            models.add(model);
            model.addChangeListener(this);
        }
    }

    public final void addModels(final Model... models) {
        Arrays.stream(models).forEach(this::addModel);
    }

    public final void removeModel(final Model model) {
        models.remove(model);
        model.removeChangeListener(this);
    }

    public final List<Model> getModels() {
        return Collections.unmodifiableList(models);
    }

    public final void addVariable(final Variable variable) {
        if (!hasVariable(variable)) {
            variables.add(variable);
            variable.addVariableListener(this);
        }
    }

    public final void removeVariable(final Variable var) {
        variables.remove(var);
        var.removeVariableListener(this);
    }

    public final List<Variable<?>> getVariables() {
        return Collections.unmodifiableList(variables);
    }

    /**
     * @param var
     * @return true of the given variable is contained in this model
     */
    public final boolean hasVariable(final Variable var) {
        return variables.contains(var) || models.stream().anyMatch(m -> m.hasVariable(var));
    }

    /**
     * Adds a model listener.
     */
    public final void addChangeListener(final ModelChangeListener<C> listener) {
        if (!changeListeners.contains(listener))
            changeListeners.add(listener);
    }

    /**
     * remove a model listener.
     */
    public final void removeChangeListener(final ModelChangeListener listener) {
        changeListeners.remove(listener);
    }

    public final void addStoreListener(final ModelStoreListener listener) {
        if (!storeListeners.contains(listener))
            storeListeners.add(listener);
    }

    public final void removeModelStoreListener(final ModelStoreListener listener) {
        storeListeners.remove(listener);
    }

    /**
     * Fires a model changed event.
     */
    protected final void fireModelChanged() {
        changeListeners.forEach(ModelChangeListener::modelChangedEvent);
    }

    protected final void fireModelChanged(final C change) {
        changeListeners.forEach(l -> l.modelChangedEvent(change));
    }

    @Override
    public final void modelChangedEvent(final Object change) {
        fireModelChanged();
    }

    public final void storeState() {
        models.forEach(Model::storeState);
        variables.forEach(Variable::storeValues);
        storeModelState();
        storeListeners.forEach(ModelStoreListener::modelStored);
    }

    /**
     * Additional state information, outside of the sub-model is stored by this call.
     */
    protected abstract void storeModelState();

    public final void restoreState() {
        models.forEach(Model::restoreState);
        variables.forEach(Variable::restoreValues);
        restoreModelState();
        storeListeners.forEach(ModelStoreListener::modelRestored);
    }

    /**
     * After this call the model is guaranteed to have returned its extra state information to
     * the values coinciding with the last storeState call.
     * Sub-models are handled automatically and do not need to be considered in this method.
     */
    protected abstract void restoreModelState();

    @Override
    public final void setId(String id) {
        this.id = id;
    }

    @Override
    public final String getId() {
        return id;
    }

    @Override
    public String toString() {
        return getName() + (getId() != null ? "[" + getId() + "]" : "");
    }

}
