package com.biit.utils.pool;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.biit.logger.BiitPoolLogger;

public abstract class SimplePool<ElementId, Type extends PoolElement<ElementId>> implements ISimplePool<ElementId, Type> {
	// Elements by id;
	private Map<ElementId, Long> elementsTime; // user id -> time.
	private Map<ElementId, Type> elementsById;

	public SimplePool() {
		reset();
	}

	@Override
	public void addElement(Type element) {
		BiitPoolLogger.debug(this.getClass(), "Adding element '" + element.getUniqueId() + "'.");
		if (getExpirationTime() > 0) {
			elementsTime.put(element.getUniqueId(), System.currentTimeMillis());
			elementsById.put(element.getUniqueId(), element);
		}
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
	public synchronized Type getElement(ElementId elementId) {
		if (elementId != null && getExpirationTime() > 0) {
			long now = System.currentTimeMillis();
			ElementId storedObjectId = null;
			if (elementsTime.size() > 0) {
				BiitPoolLogger.debug(this.getClass(), "Elements on cache: " + elementsTime.size() + ".");
				Iterator<ElementId> groupsIds = new HashMap<ElementId, Long>(elementsTime).keySet().iterator();
				while (groupsIds.hasNext()) {
					storedObjectId = groupsIds.next();
					if (elementsTime.get(storedObjectId) != null && (now - elementsTime.get(storedObjectId)) > getExpirationTime()) {
						BiitPoolLogger.debug(this.getClass(),
								"Element '" + elementId + "' has expired (elapsed time: '" + (now - elementsTime.get(storedObjectId)) + "' > '"
										+ getExpirationTime() + "'.)");
						// object has expired
						removeElement(elementId);
						storedObjectId = null;
					} else {
						if (elementsById.get(storedObjectId) != null) {
							// Remove not valid elements.
							if (isDirty(elementsById.get(storedObjectId))) {
								BiitPoolLogger.debug(this.getClass(), "Cache: " + elementsById.get(storedObjectId).getClass().getName() + " is dirty! ");
								removeElement(storedObjectId);
							} else if (Objects.equals(storedObjectId, elementId)) {
								BiitPoolLogger.info(this.getClass(), "Cache: " + elementsById.get(storedObjectId).getClass().getName() + " store hit for "
										+ elementId);
								return elementsById.get(storedObjectId);
							}
						}
					}
				}
			}
		}
		BiitPoolLogger.debug(this.getClass(), "Object with Id '" + elementId + "' - Cache Miss.");
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
			BiitPoolLogger.debug(this.getClass(), "Removing element '" + elementId + "'.");
			elementsTime.remove(elementId);
			elementsById.remove(elementId);
		}
	}

	@Override
	public void reset() {
		BiitPoolLogger.debug(this.getClass(), "Reseting all pool.");
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
