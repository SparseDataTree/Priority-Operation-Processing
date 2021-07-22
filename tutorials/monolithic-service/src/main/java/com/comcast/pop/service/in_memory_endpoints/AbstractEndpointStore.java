package com.comcast.pop.service.in_memory_endpoints;

import com.comcast.pop.object.api.IdentifiedObject;
import com.comcast.pop.object.api.UUIDGenerator;
import com.comcast.pop.persistence.api.DataObjectFeed;
import com.comcast.pop.persistence.api.PersistenceException;
import com.comcast.pop.persistence.api.query.Query;
import com.comcast.pop.persistence.impl.QueryPredicate;

import java.util.*;

public class AbstractEndpointStore<T extends IdentifiedObject> implements EndpointStore<T>
{
    static UUIDGenerator generator = new UUIDGenerator();

//    T dummy = (T) new DummyIdentifiedObject();
//    Date dummyDate = new Date(0l);
    Map<String, AbstractPersisted<T>> endpointMap = new HashMap<>();
    @Override
    public T persist( T t)
    {
        if(t.getId() == null)
        {
            t.setId(generator.generate());
        }
        endpointMap.put(t.getId(), new AbstractPersisted<T>(t));
        return t;
    }

    // What is a fast way to do this lookup?  Users should order queries that are likely to eliminate more options sooner.
    // Other than that, we could have multiple keys into our objects, maybe.

    // Seems like we should be using java util predicates here.
    // But that is not what was done.  Take a look at the QueryPredicate field. For compatibility, we will do that for now.
    @Override
    public DataObjectFeed<T> retrieve(List<Query> queries) throws PersistenceException
    {
        Set<String> ids = new HashSet<>(endpointMap.keySet());
        QueryPredicate<T> queryPredicate = new QueryPredicate(queries);
            for(String id: ids)
            {
                if(!queryPredicate.evaluate(endpointMap.get(id).get()))
                {
                    ids.remove(id);
                }
            }
        DataObjectFeed<T> dataObjectFeed = new DataObjectFeed<>();
            for(String id: ids)
            {
                dataObjectFeed.add(endpointMap.get(id).get());
            }
        return dataObjectFeed;
    }

    @Override
    public T retrieve(String id)
    {
        if(endpointMap.containsKey(id))
        {
            return endpointMap.get(id).get();
        }
        return null;
    }

    @Override
    public T update(T t)
    {
        if(endpointMap.containsKey(t.getId()))
        {
            AbstractPersisted<T> apt = endpointMap.get(t.getId());
            apt.put(t);
            return t;
        }
        return null;
    }

    @Override
    public void delete(String id)
    {
        if(endpointMap.containsKey(id))
        {
            endpointMap.remove(id);
        }
    }

    @Override
    public Date getAdded(String id)
    {
        if(endpointMap.containsKey(id))
        {
            return endpointMap.get(id).getAddedTime();
        }
        return null;
    }

    @Override
    public Date getUpdated(String id)
    {
        if(endpointMap.containsKey(id))
        {
            return endpointMap.get(id).getUpdatedTime();
        }
        return null;
    }
}
