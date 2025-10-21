package com.biit.utils.pool;

/*-
 * #%L
 * Generic utilities used in all Biit projects.
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Stores a collection of elements in a key.
 *
 * @param <KeyId>     The key type to store and retrieve the collection
 * @param <ElementId> The id type for each element
 * @param <Type>      The elements type to be stored.
 */
public interface ICollectionPool<KeyId, ElementId, Type> {

    void reset();

    Collection<Type> getElements(KeyId elementId);

    long getExpirationTime();

    Map<KeyId, Map<ElementId, Type>> getElementsByKey();

    Map<ElementId, Type> removeElement(KeyId elementId);

    boolean isDirty(Collection<Type> element);

    Map<KeyId, Long> getElementsTime();

    void addElements(Map<ElementId, Type> elements, KeyId key);

    Type removeElement(KeyId key, Type element);

    void addElement(Type element, KeyId key);

    void setElements(Map<ElementId, Type> elements, KeyId key);

    void addElements(Collection<Type> elements, KeyId key);

    void setElements(Collection<Type> elements, KeyId key);

    Set<Map<ElementId, Type>> getAllPooledElements();

    Type removeElementByKey(KeyId key, ElementId collectedElementId);

    /**
     * Removes an element inside of the mapped collection.
     *
     * @param collectedElementId
     */
    void removeCollectedElementById(ElementId collectedElementId);

    /**
     * Updates an element inside a mapped collection.
     *
     * @param collectedItem
     */
    void update(Type collectedItem);

}
