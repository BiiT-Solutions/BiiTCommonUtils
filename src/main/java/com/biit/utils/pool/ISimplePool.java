package com.biit.utils.pool;

import java.util.Collection;

public interface ISimplePool<ElementId, Type extends PoolElement<ElementId>> extends IBasePool<ElementId, Type> {

    void addElement(Type element);

    void addElements(Collection<Type> elements);

}
