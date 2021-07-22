package com.comcast.pop.service.in_memory_clients;

import com.comcast.pop.endpoint.api.ErrorResponse;
import com.comcast.pop.endpoint.api.data.DataObjectResponse;
import com.comcast.pop.endpoint.api.data.DefaultDataObjectResponse;
import com.comcast.pop.endpoint.client.ObjectClient;
import com.comcast.pop.object.api.IdentifiedObject;
import com.comcast.pop.persistence.api.DataObjectFeed;
import com.comcast.pop.persistence.api.PersistenceException;
import com.comcast.pop.persistence.api.query.Query;
import com.comcast.pop.service.in_memory_endpoints.AbstractEndpointStore;
import org.apache.commons.lang3.NotImplementedException;

import java.util.LinkedList;
import java.util.List;

public class InMemoryClient<T extends IdentifiedObject> implements ObjectClient<T>
{
    private final AbstractEndpointStore<T> endpointStore;

    public InMemoryClient(AbstractEndpointStore<T> endpointStore)
    {
        this.endpointStore = endpointStore;
    }

    @Override
    public DataObjectResponse<T> getObjects(String queryParams)
    {
        throw new NotImplementedException();
    }

    @Override
    public DataObjectResponse<T> getObjects(List<Query> queries)
    {
        DataObjectResponse<T> response = new DefaultDataObjectResponse();
        List<T> objects = new LinkedList<>(); // hm.  store will treat this query param string as an id.  OK?
        try
        {
            DataObjectFeed<T> feed = endpointStore.retrieve(queries);
            objects.addAll(feed.getAll());
        } catch (PersistenceException e)
        {
            e.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse(e, 401, "todo get cid");
            response.setErrorResponse(errorResponse);
        }
        response.addAll(objects);

        return response;
    }

    @Override
    public DataObjectResponse<T> getObject(String id)
    {
        DataObjectResponse<T> response = new DefaultDataObjectResponse();
        T object = endpointStore.retrieve(id);
        response.add(object);

        return response;
    }

    @Override
    public DataObjectResponse<T> persistObject(T object)
    {
        DataObjectResponse<T> response = new DefaultDataObjectResponse();
        T objectOut = endpointStore.persist(object);
        response.add(objectOut);

        return response;
    }

    @Override
    public DataObjectResponse<T> updateObject(T object, String id)
    {
        DataObjectResponse<T> response = new DefaultDataObjectResponse();
        T objectOut = endpointStore.update(object);
        response.add(objectOut);

        return response;
    }

    @Override
    public DataObjectResponse<T> deleteObject(String id)
    {
        DataObjectResponse<T> response = new DefaultDataObjectResponse();
        endpointStore.delete(id);

        return response;
    }
}
