/*
 * ObjectStore.java
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

package beast.xml;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Arman Bilge
 */
public class ObjectStore {

    private final Map<String,XMLObject> store = new HashMap<>();

    public XMLObject getObject(final String id) throws ObjectNotFoundException {
        return store.get(id);
    }

    public boolean hasObject(final String id) {
        return store.containsKey(id);
    }

    public Set<String> getIdSet() {
        return Collections.unmodifiableSet(store.keySet());
    }

    public Collection<Identifiable> getObjects() {
        return Collections.unmodifiableCollection(store.values());
    }

    public void addObject(final XMLObject xo) {
        final String id = xo.getId();
        if (id == null)
            throw new IllegalArgumentException("Object must have a non-null identifier.");
        else if (store.containsKey(id))
            throw new IllegalArgumentException("Object must have a unique identifier.");
        store.put(id, xo);
    }

}
