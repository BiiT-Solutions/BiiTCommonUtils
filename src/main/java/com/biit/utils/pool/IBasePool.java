package com.biit.utils.pool;

import java.util.Map;
import java.util.Set;

public interface IBasePool<ElementId, Type> {

    void addElement(Type element, ElementId key);

    void reset();

    long getExpirationTime();

    Set<Type> getAllPooledElements();

    Set<ElementId> getAllPooledKeys();

    Map<ElementId, Long> getElementsTime();

    Map<ElementId, Type> getElementsById();

    Type removeElement(ElementId elementId);

    boolean isDirty(Type element);

    Type getElement(ElementId elementId);

    ElementId getKey(Type element);

}
