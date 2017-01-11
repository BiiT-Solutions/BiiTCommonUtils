package com.biit.utils.pool;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.biit.logger.BiitCommonLogger;

public abstract class SimplePool<ElementId, Type extends PoolElement<ElementId>> implements ISimplePool<ElementId, Type> {
	// Elements by id;
	private Map<ElementId, Long> elementsTime; // user id -> time.
	private Map<ElementId, Type> elementsById;

	public SimplePool() {
		reset();
	}

	@Override
	public void addElement(Type element) {
		elementsTime.put(element.getId(), System.currentTimeMillis());
		elementsById.put(element.getId(), element);
	}

	@Override
	public Set<Type> getAllPooledElements() {
		return new HashSet<>(elementsById.values());
	}

	/**
	 * Gets all previously stored elements of a user in a site.
	 * 
	 * @param siteId
	 * @param userId
	 * @return
	 */
	@Override
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
						if (elementsById.get(storedObjectId) != null) {
							// Remove not valid elements.
							if (isDirty(elementsById.get(storedObjectId))) {
								BiitCommonLogger.debug(this.getClass(), "Cache: " + storedObjectId.getClass().getName() + " is dirty! ");
								removeElement(storedObjectId);
							} else if (Objects.equals(storedObjectId, elementId)) {
								BiitCommonLogger.debug(this.getClass(), "Cache: " + storedObjectId.getClass().getName() + " store hit for " + elementId);
								return elementsById.get(storedObjectId);
							}
						}
					}
				}
			}
		}
		BiitCommonLogger.debug(this.getClass(), "Cache: Object with Id '" + elementId + "' - Miss ");
		return null;
	}

	@Override
	public Map<ElementId, Type> getElementsById() {
		return elementsById;
	}

	@Override
	public Map<ElementId, Long> getElementsTime() {
		return elementsTime;
	}

	@Override
	public void removeElement(ElementId elementId) {
		if (elementId != null) {
			elementsTime.remove(elementId);
			elementsById.remove(elementId);
		}
	}

	@Override
	public void reset() {
		elementsTime = new HashMap<ElementId, Long>();
		elementsById = new HashMap<ElementId, Type>();
	}

	/**
	 * An element is dirty if cannot be used by the pool any more.
	 * 
	 * @param elementId
	 * @return
	 */
	@Override
	public abstract boolean isDirty(Type element);

	@Override
	public abstract long getExpirationTime();
}
