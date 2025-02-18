package com.biit.utils.pool;

import com.biit.logger.BiitPoolLogger;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BasePool<ElementId, Type> implements IBasePool<ElementId, Type> {
    // Elements by id;
    private Map<ElementId, Long> elementsTime; // user id -> time.
    private Map<ElementId, Type> elementsById;

    public BasePool() {
        reset();
    }

    @Override
    public synchronized void reset() {
        BiitPoolLogger.debug(this.getClass(), "Reseting all pools.");
        elementsTime = new ConcurrentHashMap<>();
        elementsById = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized void addElement(Type element, ElementId key) {
        addElement(element, key, System.currentTimeMillis());
    }

    @Override
    public synchronized void addElement(Type element, ElementId key, Long expirationTime) {
        BiitPoolLogger.debug(this.getClass(), "Adding element '{}' with key '{}' that expires at '{}'.",
                element, key, expirationTime);
        elementsTime.put(key, expirationTime);
        elementsById.put(key, element);
    }

    /**
     * Gets all previously stored elements of a user in a site.
     *
     * @param elementId element key for the pool.
     * @return the element that has the selected key.
     */
    @Override
    public synchronized Type getElement(ElementId elementId) {
        if (elementId != null && getExpirationTime() > 0) {
            final long now = System.currentTimeMillis();
            ElementId storedObjectId;
            if (!elementsTime.isEmpty()) {
                BiitPoolLogger.debug(this.getClass(), "Elements on cache '{}'.", elementsTime.size());
                final Map<ElementId, Long> elementsByTimeChecked = new ConcurrentHashMap<>(elementsTime);
                final Map<ElementId, Type> elementsByIdChecked = new ConcurrentHashMap<>(elementsById);
                final Iterator<ElementId> elementByTime = elementsByTimeChecked.keySet().iterator();

                for (final Entry<ElementId, Long> elementsByTimeEntry : elementsByTimeChecked.entrySet()) {
                    storedObjectId = elementByTime.next();
                    if (elementsByTimeEntry.getValue() != null
                            && (now - elementsByTimeEntry.getValue()) > getExpirationTime()) {
                        BiitPoolLogger.debug(this.getClass(), "Element '{}' has expired (elapsed time: '{}' > '{}'.)",
                                elementsByTimeEntry.getValue(), (now - elementsByTimeEntry.getValue()));
                        // object has expired
                        removeElement(storedObjectId);
                    } else {
                        if (elementsByIdChecked.get(storedObjectId) != null) {
                            // Remove not valid elements.
                            if (isDirty(elementsByIdChecked.get(storedObjectId))) {
                                BiitPoolLogger.debug(this.getClass(),
                                        "Cache '{}' is dirty! ",
                                        elementsByIdChecked.get(storedObjectId).getClass().getName());
                                removeElement(storedObjectId);
                            } else if (Objects.equals(storedObjectId, elementId)) {
                                BiitPoolLogger.info(this.getClass(), "Cache '{}' store hit for '{}'.",
                                        elementsByIdChecked.get(storedObjectId).getClass().getName(), elementId);
                                return elementsByIdChecked.get(storedObjectId);
                            }
                        }
                    }
                }
            }
        }
        BiitPoolLogger.debug(this.getClass(), "Object with Id '{}' - Cache Miss.", elementId);
        return null;
    }

    protected synchronized void cleanExpired() {
        final long now = System.currentTimeMillis();
        for (final ElementId elementId : new ConcurrentHashMap<>(elementsTime).keySet()) {
            if (elementsTime.get(elementId) != null
                    && (now - elementsTime.get(elementId)) > getExpirationTime()) {
                BiitPoolLogger.debug(this.getClass(), "Element '{}' has expired (elapsed time: '{}' > '{}'.)",
                        elementsTime.get(elementId), (now - elementsTime.get(elementId)), getExpirationTime());
                // object has expired
                removeElement(elementId);
            } else {
                if (elementsById.get(elementId) != null) {
                    // Remove not valid elements.
                    if (isDirty(elementsById.get(elementId))) {
                        BiitPoolLogger.debug(this.getClass(), "Cache '{}' is dirty! ", elementsById.get(elementId).getClass().getName());
                        removeElement(elementId);
                    }
                }
            }
        }
    }

    @Override
    public synchronized ElementId getKey(Type element) {
        if (element != null && getExpirationTime() > 0) {
            final long now = System.currentTimeMillis();
            ElementId storedObjectId;
            if (!elementsTime.isEmpty()) {
                BiitPoolLogger.debug(this.getClass(), "Elements on cache '{}'.", elementsTime.size());
                for (ElementId elementId : new ConcurrentHashMap<>(elementsTime).keySet()) {
                    storedObjectId = elementId;
                    if (elementsTime.get(storedObjectId) != null
                            && (now - elementsTime.get(storedObjectId)) > getExpirationTime()) {
                        BiitPoolLogger.debug(this.getClass(), "Element '{}' has expired (elapsed time: '{}' > '{}'.)",
                                elementsTime.get(storedObjectId), (now - elementsTime.get(storedObjectId)), getExpirationTime());
                        // object has expired
                        removeElement(storedObjectId);
                    } else {
                        if (elementsById.get(storedObjectId) != null) {
                            // Remove not valid elements.
                            if (isDirty(elementsById.get(storedObjectId))) {
                                BiitPoolLogger.debug(this.getClass(), "Cache '{}' is dirty! ", elementsById.get(storedObjectId).getClass().getName());
                                removeElement(storedObjectId);
                            } else if (Objects.equals(elementsById.get(storedObjectId), element)) {
                                BiitPoolLogger.info(this.getClass(), "Cache '{}' store hit for '{}'. ",
                                        elementsById.get(storedObjectId).getClass().getName(), element);
                                return storedObjectId;
                            }
                        }
                    }
                }
            }
        }
        BiitPoolLogger.debug(this.getClass(), "Object '{}' - Cache Miss.", element);
        return null;
    }

    @Override
    public abstract long getExpirationTime();

    @Override
    public Set<Type> getAllPooledElements() {
        return new HashSet<>(elementsById.values());
    }

    @Override
    public Set<ElementId> getAllPooledKeys() {
        return new HashSet<>(elementsById.keySet());
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
    public synchronized Type removeElement(ElementId elementId) {
        if (elementId != null) {
            BiitPoolLogger.debug(this.getClass(), "Removing element '{}'.", elementId);
            elementsTime.remove(elementId);
            return elementsById.remove(elementId);
        }
        return null;
    }

    /**
     * An element is dirty if cannot be used by the pool any more.
     *
     * @param element element to check
     * @return if it is dirty or not.
     */
    @Override
    public abstract boolean isDirty(Type element);

    public int size() {
        return elementsById.size();
    }
}
