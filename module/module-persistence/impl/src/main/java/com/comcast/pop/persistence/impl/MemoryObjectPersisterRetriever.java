package com.comcast.pop.persistence.impl;

import com.comcast.pop.object.api.IdentifiedObject;
import com.comcast.pop.persistence.api.DataObjectFeed;
import com.comcast.pop.persistence.api.query.Query;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
Lightweight in-memory object persister with functional query.
 */
public class MemoryObjectPersisterRetriever<T extends IdentifiedObject> extends MemoryObjectPersister<T>
{
    @Override
    public DataObjectFeed<T> retrieve(List<Query> queries)
    {
        QueryPredicate<T> queryPredicate = new QueryPredicate<>(queries);
        DataObjectFeed<T> feed = new DataObjectFeed<>();
        List<T> objects = doRetrieve(queryPredicate);
        feed.addAll(objects);
        return feed;
    }

    private List<T> doRetrieve(QueryPredicate<T> queryPredicate)
    {
        List<T> objectList = new LinkedList<>();
        for(Map.Entry<String, T> entry: objectPersistenceMap.entrySet())
        {
            if(queryPredicate.evaluate(entry.getValue()))
            {
                objectList.add(entry.getValue());
            }
        }
        return objectList;
    }

}
