package com.comcast.pop.persistence.impl;

import com.comcast.pop.persistence.api.DataObjectFeed;
import com.comcast.pop.persistence.api.query.Query;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;

public class MemoryDataStoreRetrieveTest
{

    private MemoryObjectPersisterRetriever<PersistenceTestObject> memoryDataStore;

    @BeforeMethod
    public void setup()
    {
        memoryDataStore = new MemoryObjectPersisterRetriever<>();
    }

    @Test
    public void testQueryAction()
    {
        final String key = "theKey";
        final String value = "theValue";
        PersistenceTestObject testObject = new PersistenceTestObject();
        testObject.setId(key);
        testObject.setVal(value);

        Assert.assertNull(memoryDataStore.retrieve(key));
        memoryDataStore.persist(testObject);

        List<Query> queries = new LinkedList<>();
        Query<String> query = new Query<>("val", value);
        queries.add(query);
        DataObjectFeed<PersistenceTestObject> feed = memoryDataStore.retrieve(queries);
        Assert.assertEquals(feed.getAll().remove(0).getVal(), value);
        memoryDataStore.delete(key);
        Assert.assertNull(memoryDataStore.retrieve(key));
    }

}
