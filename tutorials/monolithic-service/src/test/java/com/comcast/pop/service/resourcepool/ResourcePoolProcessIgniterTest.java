package com.comcast.pop.service.resourcepool;

import com.comcast.pop.api.facility.ResourcePool;
import com.comcast.pop.persistence.api.PersistenceException;
import com.comcast.pop.service.config.Config;
import com.comcast.pop.service.config.TestConfig;
import com.comcast.pop.service.in_memory_endpoints.InMemoryPersistersFactory;
import com.comcast.pop.service.stream.ResourcePoolProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.testng.Assert.*;

@SpringBootTest
public class ResourcePoolProcessIgniterTest
{
    Config config = new Config();

    @Test
    public void testRetrievePools() throws PersistenceException
    {
        InMemoryPersistersFactory persisters = config.makeDefaultInMemoryPeristersFactory();
        ResourcePoolProcessor resourcePoolProcessor = config.getResourcePoolProcessorSingleton(persisters, config.memoryQueueFactoriesSingleton());
        ResourcePoolProcessIgniter igniter = new ResourcePoolProcessIgniter(resourcePoolProcessor, persisters);

        ResourcePool pool1 = new ResourcePool();
        pool1.setId("pool1_id");
        persisters.getResourcePoolPersister().persist(pool1);

        ResourcePool pool2 = new ResourcePool();
        pool2.setId("pool2_id");
        persisters.getResourcePoolPersister().persist(pool2);

        List<ResourcePool> pools = igniter.getAllPools();


        assertThat(pools.size()).isEqualTo(2);
    }
}