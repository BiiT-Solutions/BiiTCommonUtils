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

import java.util.Map;
import java.util.Set;

public interface IBasePool<ElementId, Type> {

    void addElement(Type element, ElementId key);

    void reset();

    long getExpirationTime();

    Set<Type> getAllPooledElements();

    Set<ElementId> getAllPooledKeys();

    Map<ElementId, Long> getElementsTime();

    Map<ElementId, Type> getElementsById();

    Type removeElement(ElementId elementId);

    boolean isDirty(Type element);

    void addElement(Type element, ElementId key, Long expirationTime);

    Type getElement(ElementId elementId);

    ElementId getKey(Type element);

}
