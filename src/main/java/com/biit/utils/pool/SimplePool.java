package com.biit.utils.pool;

public abstract class SimplePool<ElementId, Type extends PoolElement<ElementId>> extends BasePool<ElementId, Type> implements ISimplePool<ElementId, Type> {

	public SimplePool() {
		reset();
	}

	@Override
	public void addElement(Type element) {
		addElement(element, element.getUniqueId());
	}

}
