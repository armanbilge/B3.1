/*
 * CompoundModel.java
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

import java.util.stream.Collectors;

/**
 * @author Arman Bilge
 */
public final class CompoundModel extends Model<Object> {

    /**
     * @param name Model name
     */
    public CompoundModel(final String name, final Model<?>... models) {
        super(name);
        addModels(models);
    }

    /**
     * Additional state information, outside of the sub-model is stored by this call.
     */
    @Override
    protected void storeModelState() {
        // Nothing to do
    }

    /**
     * After this call the model is guaranteed to have returned its extra state information to
     * the values coinciding with the last storeState call.
     * Sub-models are handled automatically and do not need to be considered in this method.
     */
    @Override
    protected void restoreModelState() {
        // Nothing to do
    }

    @Override
    public String toString() {
        return getName() + "{" + getModels().stream().map(Model::toString).collect(Collectors.joining(", ")) + "}";
    }

}
