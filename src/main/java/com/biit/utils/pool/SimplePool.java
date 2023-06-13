package com.biit.utils.pool;

import java.util.Collection;

public abstract class SimplePool<ElementId, Type extends PoolElement<ElementId>> extends BasePool<ElementId, Type>
        implements ISimplePool<ElementId, Type> {

    public SimplePool() {
        reset();
    }

    @Override
    public void addElement(Type element) {
        addElement(element, element.getUniqueId());
    }

    @Override
    public void addElements(Collection<Type> elements) {
        for (final Type element : elements) {
            addElement(element);
        }
    }
}
