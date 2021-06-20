package com.comcast.pop.service.in_memory_endpoints;

import com.comcast.pop.object.api.IdentifiedObject;
import com.comcast.pop.persistence.api.ObjectPersister;

import java.util.Date;

public interface EndpointStore<T extends IdentifiedObject> extends ObjectPersister<T>
{
    Date getAdded(String id);

    Date getUpdated(String id);
}
