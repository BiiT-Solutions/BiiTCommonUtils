package com.biit.utils.pool;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class SimplePool<ElementId, Type extends PoolElement<ElementId>> {
	// Elements by id;
	private Map<ElementId, Long> elementsTime; // user id -> time.
	private Map<ElementId, Type> elementsById;

	// Classification by string.
	private Map<String, Long> elementsTagTime; // tag -> time.
	private Map<String, Set<Type>> elementsByTag;

	public SimplePool() {
		reset();
	}

	public void addElement(Type element) {
		elementsTime.put(element.getId(), System.currentTimeMillis());
		elementsById.put(element.getId(), element);
	}

	public void addElementByTag(Set<Type> elements, String tag) {
		if (tag != null && elements != null) {
			elementsTagTime.put(tag, System.currentTimeMillis());
			Set<Type> existingGroups = elementsByTag.get(tag);
			if (existingGroups == null) {
				existingGroups = new HashSet<Type>();
			}
			existingGroups.addAll(elements);
			elementsByTag.put(tag, existingGroups);
		}
	}

	public void addElementByTag(Type element, String tag) {
		if (tag != null && element != null) {
			addElement(element);
			elementsTagTime.put(tag, System.currentTimeMillis());
			Set<Type> elements = elementsByTag.get(tag);
			if (elements == null) {
				elements = new HashSet<Type>();
			}
			elements.add(element);
			elementsByTag.put(tag, elements);
		}
	}

	/**
	 * Gets all previously stored elements of a user in a site.
	 * 
	 * @param siteId
	 * @param userId
	 * @return
	 */
	public Type getElement(ElementId elementId) {
		if (elementId != null) {
			long now = System.currentTimeMillis();
			ElementId storedObjectId = null;
			if (elementsTime.size() > 0) {
				Iterator<ElementId> groupsIds = new HashMap<ElementId, Long>(elementsTime).keySet().iterator();
				while (groupsIds.hasNext()) {
					storedObjectId = groupsIds.next();
					if (elementsTime.get(storedObjectId) != null && (now - elementsTime.get(storedObjectId)) > getExpirationTime()) {
						// object has expired
						removeElement(elementId);
						storedObjectId = null;
					} else {
						if (elementsById.get(storedObjectId) != null && storedObjectId.equals(elementId)) {
							return elementsById.get(storedObjectId);
						}
					}
				}
			}
		}
		return null;
	}

	public Map<ElementId, Type> getElementsById() {
		return elementsById;
	}

	public Set<Type> getElementsByTag(String tag) {
		long now = System.currentTimeMillis();
		String nextGroupTag = null;
		if (elementsTagTime.size() > 0) {
			Iterator<String> e = new HashMap<String, Set<Type>>(elementsByTag).keySet().iterator();
			while (e.hasNext()) {
				nextGroupTag = e.next();
				if (elementsTagTime.get(nextGroupTag) != null && (now - elementsTagTime.get(nextGroupTag)) > getExpirationTime()) {
					// object has expired
					removeElementsByTag(nextGroupTag);
					nextGroupTag = null;
				} else {
					if (tag.equals(nextGroupTag)) {
						return elementsByTag.get(nextGroupTag);
					}
				}
			}
		}
		return null;
	}

	public Map<ElementId, Long> getElementsTime() {
		return elementsTime;
	}

	public void removeElement(ElementId elementId) {
		if (elementId != null) {
			elementsTime.remove(elementId);
			elementsById.remove(elementId);
		}
	}

	public void removeElementsByTag(String tag) {
		if (tag != null) {
			elementsByTag.remove(tag);
			elementsTagTime.remove(tag);
		}
	}

	public void removeElementsByTag(String tag, PoolElement<ElementId> element) {
		if (tag != null) {
			if (elementsByTag.get(tag) != null) {
				elementsByTag.get(tag).remove(element);
			}
		}
	}

	public void reset() {
		elementsTime = new HashMap<ElementId, Long>();
		elementsById = new HashMap<ElementId, Type>();
		elementsTagTime = new HashMap<String, Long>();
		elementsByTag = new HashMap<String, Set<Type>>();
	}

	public abstract long getExpirationTime();
}
