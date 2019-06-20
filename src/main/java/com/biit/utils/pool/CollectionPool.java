package com.biit.utils.pool;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.biit.logger.BiitPoolLogger;

public abstract class CollectionPool<ElementId, Type> implements ICollectionPool<ElementId, Type> {
	// Elements by id;
	private Map<ElementId, Long> elementsTime; // user id -> time.
	private Map<ElementId, Collection<Type>> elementsById;

	public CollectionPool() {
		reset();
	}

	@Override
	public synchronized void reset() {
		BiitPoolLogger.debug(this.getClass(), "Reseting all pool.");
		elementsTime = new ConcurrentHashMap<ElementId, Long>();
		elementsById = new ConcurrentHashMap<ElementId, Collection<Type>>();
	}

	@Override
	public synchronized void addElement(Type element, ElementId key) {
		BiitPoolLogger.debug(this.getClass(), "Adding element '" + element + "' with key '" + key + "'.");
		if (getExpirationTime() > 0) {
			elementsTime.put(key, System.currentTimeMillis());
			if (elementsById.get(key) == null) {
				elementsById.put(key, new HashSet<Type>());
			}
			elementsById.get(key).add(element);
		}
	}

	@Override
	public synchronized void addElements(Collection<Type> elements, ElementId key) {
		BiitPoolLogger.debug(this.getClass(), "Adding collection '" + elements + "' with key '" + key + "'.");
		if (getExpirationTime() > 0) {
			elementsTime.put(key, System.currentTimeMillis());
			if (elementsById.get(key) == null) {
				elementsById.put(key, new HashSet<Type>());
			}
			elementsById.get(key).addAll(elements);
		}
	}

	@Override
	public synchronized void setElements(Collection<Type> element, ElementId key) {
		BiitPoolLogger.debug(this.getClass(), "Setting collection '" + element + "' with key '" + key + "'.");
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
	public synchronized Collection<Type> getElement(ElementId elementId) {
		if (elementId != null && getExpirationTime() > 0) {
			final long now = System.currentTimeMillis();
			ElementId storedObjectId = null;
			if (elementsTime.size() > 0) {
				BiitPoolLogger.debug(this.getClass(), "Elements on cache: " + elementsTime.size() + ".");
				final Map<ElementId, Long> elementsByTimeChecked = new ConcurrentHashMap<>(elementsTime);
				final Map<ElementId, Collection<Type>> elementsByIdChecked = new ConcurrentHashMap<>(elementsById);
				final Iterator<ElementId> elementByTime = elementsByTimeChecked.keySet().iterator();

				for (final Entry<ElementId, Long> elementsByTimeEntry : elementsByTimeChecked.entrySet()) {
					storedObjectId = elementByTime.next();
					if (elementsByTimeEntry.getValue() != null
							&& (now - elementsByTimeEntry.getValue()) > getExpirationTime()) {
						BiitPoolLogger.debug(this.getClass(), "Element '" + elementsByTimeEntry.getValue()
								+ "' has expired (elapsed time: '" + (now - elementsByTimeEntry.getValue()) + "' > '"
								+ getExpirationTime() + "'.)");
						// object has expired
						removeElement(storedObjectId);
						storedObjectId = null;
					} else {
						if (elementsByIdChecked.get(storedObjectId) != null) {
							// Remove not valid elements.
							if (isDirty(elementsByIdChecked.get(storedObjectId))) {
								BiitPoolLogger.debug(this.getClass(),
										"Cache: " + elementsByIdChecked.get(storedObjectId).getClass().getName()
												+ " is dirty! ");
								removeElement(storedObjectId);
							} else if (Objects.equals(storedObjectId, elementId)) {
								BiitPoolLogger.info(this.getClass(), "Cache: "
										+ elementsByIdChecked.get(storedObjectId).getClass().getName()
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
	public abstract long getExpirationTime();

	@Override
	public Set<Collection<Type>> getAllPooledElements() {
		return new HashSet<>(elementsById.values());
	}

	@Override
	public Map<ElementId, Collection<Type>> getElementsById() {
		return elementsById;
	}

	@Override
	public Map<ElementId, Long> getElementsTime() {
		return elementsTime;
	}

	@Override
	public synchronized Collection<Type> removeElement(ElementId elementId) {
		if (elementId != null) {
			BiitPoolLogger.debug(this.getClass(), "Removing element '" + elementId + "'.");
			elementsTime.remove(elementId);
			return elementsById.remove(elementId);
		}
		return null;
	}

	@Override
	public synchronized boolean removeElement(ElementId elementId, Type element) {
		if (elementId != null && element != null) {
			BiitPoolLogger.debug(this.getClass(), "Removing element '" + element + "' of '" + elementId + "'.");
			return elementsById.get(elementId).remove(element);
		}
		return false;
	}

	/**
	 * An element is dirty if cannot be used by the pool any more.
	 * 
	 * @param element
	 *            element to check
	 * @return if it is dirty or not.
	 */
	@Override
	public abstract boolean isDirty(Collection<Type> element);
}
