package com.biit.utils.pool;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.biit.logger.BiitPoolLogger;

public abstract class BasePool<ElementId, Type> implements IBasePool<ElementId, Type> {
	// Elements by id;
	private Map<ElementId, Long> elementsTime; // user id -> time.
	private Map<ElementId, Type> elementsById;

	public BasePool() {
		reset();
	}

	@Override
	public void reset() {
		BiitPoolLogger.debug(this.getClass(), "Reseting all pool.");
		elementsTime = new ConcurrentHashMap<ElementId, Long>();
		elementsById = new ConcurrentHashMap<ElementId, Type>();
	}

	@Override
	public synchronized void addElement(Type element, ElementId key) {
		BiitPoolLogger.debug(this.getClass(), "Adding element '" + element + "' with key '" + key + "'.");
		if (getExpirationTime() > 0) {
			elementsTime.put(key, System.currentTimeMillis());
			elementsById.put(key, element);
		}
	}

	/**
	 * Gets all previously stored elements of a user in a site.
	 * 
	 * @param elementId
	 *            element key for the pool.
	 * @return the element that has the selected key.
	 */
	@Override
	public synchronized Type getElement(ElementId elementId) {
		if (elementId != null && getExpirationTime() > 0) {
			long now = System.currentTimeMillis();
			ElementId storedObjectId = null;
			if (elementsTime.size() > 0) {
				BiitPoolLogger.debug(this.getClass(), "Elements on cache: " + elementsTime.size() + ".");
				Map<ElementId, Long> elementsByTimeChecked = new ConcurrentHashMap<>(elementsTime);
				Map<ElementId, Type> elementsByIdChecked = new ConcurrentHashMap<>(elementsById);
				Iterator<ElementId> elementByTime = elementsByTimeChecked.keySet().iterator();
				while (elementByTime.hasNext()) {
					storedObjectId = elementByTime.next();
					if (elementsByTimeChecked.get(storedObjectId) != null && (now - elementsByTimeChecked.get(storedObjectId)) > getExpirationTime()) {
						BiitPoolLogger.debug(this.getClass(), "Element '" + elementsByTimeChecked.get(storedObjectId) + "' has expired (elapsed time: '"
								+ (now - elementsByTimeChecked.get(storedObjectId)) + "' > '" + getExpirationTime() + "'.)");
						// object has expired
						removeElement(storedObjectId);
						storedObjectId = null;
					} else {
						if (elementsByIdChecked.get(storedObjectId) != null) {
							// Remove not valid elements.
							if (isDirty(elementsByIdChecked.get(storedObjectId))) {
								BiitPoolLogger.debug(this.getClass(), "Cache: " + elementsByIdChecked.get(storedObjectId).getClass().getName() + " is dirty! ");
								removeElement(storedObjectId);
							} else if (Objects.equals(storedObjectId, elementId)) {
								BiitPoolLogger.info(this.getClass(), "Cache: " + elementsByIdChecked.get(storedObjectId).getClass().getName()
										+ " store hit for " + elementId);
								return elementsByIdChecked.get(storedObjectId);
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
	public synchronized ElementId getKey(Type element) {
		if (element != null && getExpirationTime() > 0) {
			long now = System.currentTimeMillis();
			ElementId storedObjectId = null;
			if (elementsTime.size() > 0) {
				BiitPoolLogger.debug(this.getClass(), "Elements on cache: " + elementsTime.size() + ".");
				Iterator<ElementId> groupsIds = new ConcurrentHashMap<ElementId, Long>(elementsTime).keySet().iterator();
				while (groupsIds.hasNext()) {
					storedObjectId = groupsIds.next();
					if (elementsTime.get(storedObjectId) != null && (now - elementsTime.get(storedObjectId)) > getExpirationTime()) {
						BiitPoolLogger.debug(this.getClass(), "Element '" + elementsTime.get(storedObjectId) + "' has expired (elapsed time: '"
								+ (now - elementsTime.get(storedObjectId)) + "' > '" + getExpirationTime() + "'.)");
						// object has expired
						removeElement(storedObjectId);
						storedObjectId = null;
					} else {
						if (elementsById.get(storedObjectId) != null) {
							// Remove not valid elements.
							if (isDirty(elementsById.get(storedObjectId))) {
								BiitPoolLogger.debug(this.getClass(), "Cache: " + elementsById.get(storedObjectId).getClass().getName() + " is dirty! ");
								removeElement(storedObjectId);
							} else if (Objects.equals(elementsById.get(storedObjectId), element)) {
								BiitPoolLogger.info(this.getClass(), "Cache: " + elementsById.get(storedObjectId).getClass().getName() + " store hit for "
										+ element);
								return storedObjectId;
							}
						}
					}
				}
			}
		}
		BiitPoolLogger.debug(this.getClass(), "Object '" + element + "' - Cache Miss.");
		return null;
	}

	@Override
	public abstract long getExpirationTime();

	@Override
	public Set<Type> getAllPooledElements() {
		return new HashSet<>(elementsById.values());
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
	public synchronized void removeElement(ElementId elementId) {
		if (elementId != null) {
			BiitPoolLogger.debug(this.getClass(), "Removing element '" + elementId + "'.");
			elementsTime.remove(elementId);
			elementsById.remove(elementId);
		}
	}

	/**
	 * An element is dirty if cannot be used by the pool any more.
	 * 
	 * @param element
	 *            element to check
	 * @return if it is dirty or not.
	 */
	@Override
	public abstract boolean isDirty(Type element);
}
