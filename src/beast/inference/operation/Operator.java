/*
 * MCMCOperator.java
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

package beast.inference.operation;

import beast.util.Identifiable;

/**
 * An MCMC operator.
 *
 * @author Alexei Drummond
 * @author Andrew Rambaut
 * @author Arman Bilge
 */
public abstract class Operator implements Identifiable {

    private final String name;
    private final double weight;

    private String id;

    private int acceptCount = 0;
    private int rejectCount = 0;

    private double sumDeviation = 0.0;

    private boolean operateAllowed = true;

    private long totalEvaluationTime = 0;

    protected Operator(final String name, final double weight) {
        this.name = name;
        if (weight > 0)
            this.weight = weight;
        else
            throw new IllegalArgumentException("Weight must be a positive real, but tried to set weight to " + weight + ".");
    }

    /**
     * operates on the model.
     *
     * @return the log hastings ratio of this operator.
     * @throws OperatorFailedException if the operator failed and should be rejected
     */
    public abstract double operate() throws OperatorFailedException;

    /**
     * Called to tell operator that operation was accepted
     *
     * @param deviation the log ratio accepted on
     */
    public final void accept(final double deviation) {
        if (!operateAllowed) {
            operateAllowed = true;
            ++acceptCount;
            sumDeviation += deviation;
        } else {
            throw new RuntimeException("Accept/reject methods called twice without operate called in between!");
        }
        handleAccept(deviation);
    }

    protected void handleAccept(final double deviation) {
        // Nothing by default
    }

    /**
     * Called to tell operator that operation was rejected
     */
    public final void reject() {
        if (!operateAllowed) {
            operateAllowed = true;
            ++rejectCount;
        } else {
            throw new RuntimeException("Accept/reject methods called twice without operate called in between!");
        }
        handleReject();
    }

    protected void handleReject() {
        // Do nothing by default
    }

    /**
     * Reset operator acceptance records.
     */
    public final void reset() {
        operateAllowed = true;
        acceptCount = 0;
        rejectCount = 0;
        sumDeviation = 0.0;
    }

    public final int getOperationCount() {
        return getAcceptCount() + getRejectCount();
    }

    /**
     * @return the number of acceptances since last call to reset().
     */
    public final int getAcceptCount() {
        return acceptCount;
    }

    /**
     * Set the number of acceptances since last call to reset(). This is used
     * to restore the state of the operator
     *
     * @param acceptCount number of acceptances
     */
    public final void setAcceptCount(final int acceptCount) {
        this.acceptCount = acceptCount;
    }

    /**
     * @return the number of rejections since last call to reset().
     */
    public final int getRejectCount() {
        return this.rejectCount;
    }

    /**
     * Set the number of rejections since last call to reset(). This is used
     * to restore the state of the operator
     *
     * @param rejectCount number of rejections
     */
    public final void setRejectCount(int rejectCount) {
        this.rejectCount = rejectCount;
    }

    /**
     * @return the mean deviation in log posterior per accepted operations.
     */
    public final double getMeanDeviation() {
        return sumDeviation / acceptCount;
    }

    public final double getSumDeviation() {
        return sumDeviation;
    }

    public final void setSumDeviation(double sumDeviation) {
        this.sumDeviation = sumDeviation;
    }

    /**
     * @return the optimal acceptance probability
     */
    public double getTargetAcceptanceProbability() {
        return 0.234;
    }

    /**
     * @return the minimum acceptable acceptance probability
     */
    public double getMinimumAcceptanceLevel() {
        return 0.05;
    }

    /**
     * @return the maximum acceptable acceptance probability
     */
    public double getMaximumAcceptanceLevel() {
        return 0.5;
    }

    /**
     * @return the minimum good acceptance probability
     */
    public double getMinimumGoodAcceptanceLevel() {
        return 0.1;
    }

    /**
     * @return the maximum good acceptance probability
     */
    public double getMaximumGoodAcceptanceLevel() {
        return 0.4;
    }

    /**
     * @return a short descriptive message of the performance of this operator.
     */
    public abstract String getPerformanceSuggestion();

    /**
     * @return the relative weight of this operator.
     */
    public final double getWeight() {
        return weight;
    }

    /**
     * @return the name of this operator
     */
    public final String getName() {
        return name;
    }

    public double getMeanEvaluationTime() {
        return totalEvaluationTime / (double) getOperationCount();
    }

    public final void addEvaluationTime(final long time) {
        totalEvaluationTime += time;
    }

    public final long getTotalEvaluationTime() {
        return totalEvaluationTime;
    }

    public final double getAcceptanceProbability() {
        return getAcceptCount() / (double) getOperationCount();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(final String id) {
        this.id = id;
    }
}
