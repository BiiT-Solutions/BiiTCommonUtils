package com.biit.utils.pool;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface ICollectionPool<ElementId, Type> {

	void reset();

	Collection<Type> getElement(ElementId elementId);

	long getExpirationTime();

	Map<ElementId, Collection<Type>> getElementsById();

	Set<Collection<Type>> getAllPooledElements();

	Collection<Type> removeElement(ElementId elementId);

	boolean isDirty(Collection<Type> element);

	Map<ElementId, Long> getElementsTime();

	void addElements(Collection<Type> element, ElementId key);

	boolean removeElement(ElementId elementId, Type element);

	void addElement(Type element, ElementId key);

	void setElements(Collection<Type> element, ElementId key);

}
