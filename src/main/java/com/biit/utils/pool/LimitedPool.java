package com.biit.utils.pool;


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
