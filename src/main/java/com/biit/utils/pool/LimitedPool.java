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


import com.biit.logger.BiitPoolLogger;

public abstract class LimitedPool<Type> extends BasePool<Integer, Type> {
    private int index = 0;

    public abstract int getMaxElements();

    public synchronized void addElement(Type element) throws FullPoolException {
        if (getElementsById().size() < getMaxElements()) {
            super.addElement(element, getElementsById().size());
            return;
        }
        BiitPoolLogger.debug(this.getClass(), "Max pool size '" + getMaxElements() + "' reached.");
        throw new FullPoolException();
    }

    public synchronized Type getNextElement() {
        cleanExpired();
        if (index >= getElementsById().size()) {
            if (index < getMaxElements()) {
                try {
                    addElement(createNewElement());
                } catch (FullPoolException e) {
                    index = 0;
                }
            } else {
                index = 0;
            }
        }
        try {
            return getElement(index);
        } finally {
            index++;
        }
    }

    public abstract Type createNewElement();

    @Override
    public synchronized void reset() {
        super.reset();
        index = 0;
    }

    @Override
    public synchronized Type removeElement(Integer elementId) {
        if (index >= getElementsById().size() - 1) {
            index = 0;
        }
        return super.removeElement(elementId);
    }

}
