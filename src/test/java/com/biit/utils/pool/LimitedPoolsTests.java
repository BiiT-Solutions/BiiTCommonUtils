package com.biit.utils.pool;

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
