package com.comcast.pop.service.resourcepool;

import com.comcast.pop.api.facility.ResourcePool;
import com.comcast.pop.persistence.api.DataObjectFeed;
import com.comcast.pop.persistence.api.ObjectPersister;
import com.comcast.pop.persistence.api.PersistenceException;
import com.comcast.pop.persistence.api.query.Query;
import com.comcast.pop.service.in_memory_endpoints.InMemoryPersistersFactory;
import com.comcast.pop.service.stream.ResourcePoolProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

public class ResourcePoolProcessIgniter
{
    private final ResourcePoolProcessor resourcePoolProcessor;
    private final ObjectPersister<ResourcePool> resourcePoolPersister;

    @Autowired
    public ResourcePoolProcessIgniter(
            @Qualifier("resourcePoolProcessorSingleton") ResourcePoolProcessor resourcePoolProcessor,
            @Qualifier("persisters") InMemoryPersistersFactory persisters)
    {
        this.resourcePoolProcessor = resourcePoolProcessor;
        resourcePoolPersister = persisters.getResourcePoolPersister();
    }

    public void ignite() throws PersistenceException
    {
        List<ResourcePool> pools = getAllPools();

        for(ResourcePool pool: pools)
        {
            resourcePoolProcessor.processResourcePool(pool.getId());
        }
    }

    protected List<ResourcePool> getAllPools() throws PersistenceException
    {
        /*
        We want to select all.  Given that we expect to never have very many resource pools (less that 20?),
         this is a reasonable approach.  Of course, with an event-driven approach, we could avoid this issue entirely.

         One extension here would be to make an interface and then inject an instance, so that this igniter class is agnostic to the
         details of obtaining resourcepool instances of interest.
            */
        List<Query> queryList = null; // select all.

        DataObjectFeed<ResourcePool> pools = resourcePoolPersister.retrieve(queryList);
        return pools.getAll();
    }
}
