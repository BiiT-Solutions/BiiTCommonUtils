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

import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = {"limitedPoolsTests"})
public class LimitedPoolsTests {
    private static final int MAX_ELEMENTS = 10;

    private ElementLimitedPool elementLimitedPool = new ElementLimitedPool();

    class Element {

    }

    class ElementLimitedPool extends LimitedPool<Element> {

        @Override
        public long getExpirationTime() {
            return 100000;
        }

        @Override
        public boolean isDirty(Element element) {
            return false;
        }

        @Override
        public int getMaxElements() {
            return MAX_ELEMENTS;
        }

        @Override
        public Element createNewElement() {
            return new Element();
        }
    }

    public void checkMaxElements() {
        elementLimitedPool.reset();
        for (int i = 0; i < MAX_ELEMENTS * 30; i++) {
            elementLimitedPool.getNextElement();
        }
        Assert.assertEquals(MAX_ELEMENTS, elementLimitedPool.size());
    }

    @Test(expectedExceptions = FullPoolException.class)
    public void noMoreElementsAllowed() throws FullPoolException {
        elementLimitedPool.reset();
        for (int i = 0; i < MAX_ELEMENTS + 1; i++) {
            elementLimitedPool.addElement(new Element());
        }
    }

    @Test
    public void removeElementsAllowed() throws FullPoolException {
        elementLimitedPool.reset();
        for (int i = 0; i < MAX_ELEMENTS; i++) {
            elementLimitedPool.addElement(new Element());
        }
        elementLimitedPool.removeElement(3);
        //No exception thrown.
        elementLimitedPool.addElement(new Element());
    }
}
