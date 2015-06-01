/*
 * Likelihood.java
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
import beast.inference.logging.NumberColumn;
import beast.math.MachineAccuracy;
import beast.xml.Identifiable;

/**
 * A simple abstract base class for Likelihood functions.
 *
 * @author Alexei Drummond
 * @author Andrew Rambaut
 * @author Arman Bilge
 */
public abstract class Likelihood implements Identifiable, Loggable, ModelChangeListener<Object>, ModelStoreListener {

    private String id;
    private boolean used;
    private boolean likelihoodKnown = false;
    private boolean storedLikelihoodKnown;
    private double logLikelihood;
    private double storedLogLikelihood;

    private final Model<Object> model = new CompoundModel(getClass().getSimpleName() + "Model");

    {
        model.addChangeListener(this);
        model.addStoreListener(this);
    }

    public Likelihood(final Model model, final Model<?>... models) {
        model.addModel(model);
        model.addModels(models);
    }

	/**
	 * Get the model.
	 * @return the model.
	 */
	public final Model getModel() {
        return model;
    }

	/**
	 * Get the log likelihood.
	 * @return the log likelihood.
	 */
	public final double getLogLikelihood() {
        if (!getLikelihoodKnown()) {
            logLikelihood = calculateLogLikelihood();
            likelihoodKnown = true;
        }
        return logLikelihood;
    }

    protected abstract double calculateLogLikelihood();

    public final boolean getLikelihoodKnown() {
        return likelihoodKnown;
    }

    private void setLikelihoodUnknown() {
        likelihoodKnown = false;
    }

    public double differentiate(final Variable<Double> var, final int index) {
        if (getModel().hasVariable(var)) { // Default to numerical differentiation

            final double epsilon = MachineAccuracy.SQRT_EPSILON * Math.max(var.getValue(index), 1);

            final Bounds<Double> bounds = var.getBounds();
            final double upper = bounds.getUpperLimit(index);
            final double lower = bounds.getLowerLimit(index);

            final double x = var.getValue(index);

            final double xpe = x + epsilon;
            final double b = xpe <= upper ? xpe : upper;
            var.setValue(index, b);
            final double fb = getLogLikelihood();

            final double xme = x - epsilon;
            final double a = xme >= lower ? xme : lower;
            var.setValue(index, a);
            final double fa = getLogLikelihood();

            var.setValue(index, x);

            return (fb - fa) / (b - a);

        } else {
            return 0;
        }
    }

	/**
	 * Forces a complete recalculation of the likelihood next time getLikelihood is called
	 */
	public void makeLikelihoodDirty() {
        makeDirty();
    }

    protected abstract void makeDirty();

    /**
     * @return true if this likelihood should be evaluated early (if for example it may return a zero likelihood
     * and could terminate the evaluation early.
     */
    public boolean evaluateEarly() {
        return false;
    }

    /**
     * @return is the likelihood used in the MCMC?
     */
    public final boolean isUsed() {
        return used;
    }

    public final void setUsed() {
        used = true;
    }

    @Override
    public final void modelChangedEvent(final Object change) {
        setLikelihoodUnknown();
    }

    @Override
    public final void modelChangedEvent(final Variable.ChangeEvent change) {
        setLikelihoodUnknown();
    }


    @Override
    public final void modelStored() {
        storedLikelihoodKnown = likelihoodKnown;
        storedLogLikelihood = logLikelihood;
        storeCalculations();
    }

    protected abstract void storeCalculations();

    @Override
    public final void modelRestored() {
        likelihoodKnown = storedLikelihoodKnown;
        logLikelihood = storedLogLikelihood;
        restoreCalculations();
    }

    protected abstract void restoreCalculations();

    @Override
    public final LogColumn[] getColumns() {
        return new LogColumn[] {
                new NumberColumn(getId()) {
                    @Override
                    public double getDoubleValue() {
                        return getLogLikelihood();
                    }
                }
        };
    }

    @Override
    public final void setId(final String id) { this.id = id; }

    @Override
    public final String getId() { return id; }

    @Override
    public String toString() {
        // don't call any "recalculating" stuff like getLogLikelihood()
        return getClass().getSimpleName()
                + (getId() != null ? "[" + getId() + "]" : "")
                + "(" + (getLikelihoodKnown() ? logLikelihood : "??") + ")";
    }

}
