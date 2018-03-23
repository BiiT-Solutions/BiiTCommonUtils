package com.biit.utils.pool;

public interface ISimplePool<ElementId, Type extends PoolElement<ElementId>> extends IBasePool<ElementId, Type> {

	void addElement(Type element);

}
