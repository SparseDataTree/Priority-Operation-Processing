package com.comcast.pop.service.in_memory_endpoints;

import com.comcast.pop.object.api.IdentifiedObject;
import com.comcast.pop.persistence.api.ObjectPersister;
import com.comcast.pop.persistence.api.ObjectPersisterFactory;

public class BasePersisterFactory<T extends IdentifiedObject> implements ObjectPersisterFactory<T>
{

    private final ObjectPersister<T> objectPersister;

    public BasePersisterFactory(ObjectPersister<T> objectPersister)
    {
        this.objectPersister = objectPersister;
    }

    @Override
    public ObjectPersister<T> getObjectPersister(String containerName)
    {
       return objectPersister;
    }
}
