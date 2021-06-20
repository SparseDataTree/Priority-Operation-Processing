package com.comcast.pop.persistence.impl;

import com.comcast.pop.object.api.IdentifiedObject;
import com.comcast.pop.object.api.UUIDGenerator;
import com.comcast.pop.persistence.api.DataObjectFeed;
import com.comcast.pop.persistence.api.ObjectPersister;
import com.comcast.pop.persistence.api.query.Query;

import java.util.*;

public class MemoryObjectPersister<T extends IdentifiedObject> implements ObjectPersister<T>
{
    private static UUIDGenerator uuidGenerator = new UUIDGenerator();
    protected Map<String, T> objectPersistenceMap;

    public MemoryObjectPersister()
    {
        objectPersistenceMap = new LinkedHashMap<>();
    }

    @Override
    public DataObjectFeed<T> retrieve(List<Query> queries)
    {
        throw new RuntimeException();
    }

    @Override
    public T retrieve(String identifier)
    {
        return objectPersistenceMap.get(identifier);
    }

    @Override
    public T persist(T object)
    {
        if(object.getId() == null)
        {
           object.setId(uuidGenerator.generate());
        }
        objectPersistenceMap.put(object.getId(), object);
        return object;
    }

    /**
     * Uses the persist method to simply overwrite the object.
     * @param object The object to update
     */
    @Override
    public T update(T object)
    {
        persist(object);
        return retrieve(object.getId());
    }

    @Override
    public void delete(String identifier)
    {
        objectPersistenceMap.remove(identifier);
    }
}
