package com.biit.utils.pool;

import java.util.Map;
import java.util.Set;

public interface ISimplePool<ElementId, Type extends PoolElement<ElementId>> {

	boolean isDirty(Type element);

	void addElement(Type element);

	Set<Type> getAllPooledElements();

	Type getElement(ElementId elementId);

	Map<ElementId, Type> getElementsById();

	Map<ElementId, Long> getElementsTime();

	void removeElement(ElementId elementId);

	void reset();

	long getExpirationTime();

	void addElement(Type element, ElementId key);

}
