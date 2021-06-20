package com.comcast.pop.scheduling.queue.algorithm;

import com.comcast.pop.api.facility.Insight;
import com.comcast.pop.persistence.api.DataObjectFeed;
import com.comcast.pop.persistence.api.ObjectPersister;
import com.comcast.pop.persistence.api.PersistenceException;
import com.comcast.pop.persistence.impl.MemoryObjectPersister;
import com.comcast.pop.persistence.impl.MemoryObjectPersisterRetriever;
import com.comcast.pop.scheduling.api.ReadyAgenda;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class FIFOAgendaSchedulerInMemorytest extends BaseAgendaSchedulerTest
{
    private FIFOAgendaScheduler scheduler;

    @BeforeMethod
    public void setup()
    {
        insight.setId(UUID.randomUUID().toString());
        mockReadyAgendaPersister = new MemoryObjectPersisterRetriever<>();
        scheduler = new FIFOAgendaScheduler(mockReadyAgendaPersister);
        insight = new Insight();
        insight.setId(UUID.randomUUID().toString());
    }

    @Test
    public void testScheduleNoReadyAgendas()
    {
        List<ReadyAgenda> readyAgendas = scheduler.schedule(1, insight, insightScheduleInfo);
        Assert.assertNotNull(readyAgendas);
        Assert.assertEquals(readyAgendas.size(), 0);
    }

    @Test
    public void testScheduleOneReadyAgenda()
    {
        List<String> customerIds = IntStream.range(0, 5).mapToObj(i -> Integer.toString(i)).collect(Collectors.toList());
        List<ReadyAgenda> readyAgendaList = new LinkedList<>();
        customerIds.forEach(id ->{
            readyAgendaList.addAll(createReadyAgendas(id,  1));
        });
        readyAgendaList.forEach(readyAgenda -> {
            try
            {
                readyAgenda.setInsightId(insight.getId());
                mockReadyAgendaPersister.persist(readyAgenda);
            } catch (PersistenceException e)
            {
                e.printStackTrace();
            }
        });


        List<ReadyAgenda> readyAgendas = scheduler.schedule(1, insight, insightScheduleInfo);
        Assert.assertNotNull(readyAgendas);
        Assert.assertEquals(readyAgendas.size(), 1);
        Assert.assertEquals(readyAgendas.get(0).getCustomerId(), customerIds.get(0));
    }

    @Test
    public void testScheduleExcessReadyAgendas()
    {
        List<String> customerIds = new ArrayList<>();
        List<String> expectedIds = IntStream.range(0, 5).mapToObj(i -> Integer.toString(i)).collect(Collectors.toList());
        customerIds.addAll(expectedIds);
        customerIds.addAll(IntStream.range(5, 10).mapToObj(i -> Integer.toString(i)).collect(Collectors.toList()));

        List<ReadyAgenda> readyAgendaList = new LinkedList<>();
        customerIds.forEach(id ->{
            readyAgendaList.addAll(createReadyAgendas(id,  1));
        });
        readyAgendaList.forEach(readyAgenda -> {
            try
            {
                readyAgenda.setInsightId(insight.getId());
                mockReadyAgendaPersister.persist(readyAgenda);
            } catch (PersistenceException e)
            {
                e.printStackTrace();
            }
        });


        List<ReadyAgenda> readyAgendas = scheduler.schedule(5, insight, insightScheduleInfo);
        Assert.assertNotNull(readyAgendas);
        Assert.assertEquals(readyAgendas.size(), 5);
        Assert.assertTrue(
                readyAgendas.stream().map(ReadyAgenda::getCustomerId).collect(Collectors.toList())
                        .containsAll(expectedIds)
        );
    }
}
