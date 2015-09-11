/*
 * OperatorFailedException.java
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

/**
 * This exception provides a fast-fail mechanism for operators.
 * It represents the situation where an operator realises rejection
 * is guaranteed to occur during acceptance phase.
 *
 * @author Alexei Drummond
 */
public class OperatorFailedException extends Exception {

	private static final long serialVersionUID = 1;

	public OperatorFailedException(final String message) {
		super(message);
	}
}

