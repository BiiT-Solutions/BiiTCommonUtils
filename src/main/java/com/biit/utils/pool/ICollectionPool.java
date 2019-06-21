package com.biit.utils.pool;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface ICollectionPool<ElementId, Type> {

	void reset();

	Collection<Type> getElement(ElementId elementId);

	long getExpirationTime();

	public Map<ElementId, Map<ElementId, Type>> getElementsById();

	public Map<ElementId, Type> removeElement(ElementId elementId);

	boolean isDirty(Collection<Type> element);

	Map<ElementId, Long> getElementsTime();

	void addElements(Map<ElementId, Type> elements, ElementId key);

	Type removeElement(ElementId elementId, Type element);

	void addElement(Type element, ElementId key);

	void setElements(Map<ElementId, Type> elements, ElementId key);

	void addElements(Collection<Type> elements, ElementId key);

	void setElements(Collection<Type> elements, ElementId key);

	Set<Map<ElementId, Type>> getAllPooledElements();

	Type removeElementById(ElementId elementId, ElementId collectedElementId);

	/**
	 * Removes an element inside of the mapped collection.
	 * 
	 * @param collectedElementId
	 */
	void removeCollectedElementById(ElementId collectedElementId);

	/**
	 * Updates an element inside of a mapped collection.
	 * 
	 * @param collectedItem
	 */
	void update(Type collectedItem);

}
