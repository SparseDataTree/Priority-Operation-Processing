package com.comcast.pop.service.stream;

import com.comcast.pop.api.facility.Customer;
import com.comcast.pop.api.facility.Insight;
import com.comcast.pop.modules.queue.api.ItemQueueFactory;
import com.comcast.pop.persistence.api.ObjectPersisterFactory;
import com.comcast.pop.scheduling.api.ReadyAgenda;
import com.comcast.pop.scheduling.queue.InsightScheduleInfo;
import com.comcast.pop.scheduling.queue.monitor.QueueMonitor;
import com.comcast.pop.scheduling.queue.monitor.QueueMonitorFactory;
import com.comcast.pop.service.in_memory_clients.InMemoryClient;
import com.comcast.pop.service.in_memory_endpoints.AbstractEndpointStore;
import com.comcast.pop.service.in_memory_endpoints.InMemoryPersistersFactory;
import com.comcast.pop.service.in_memory_endpoints.BasePersisterFactory;
import com.comcast.pop.service.queues.MemoryQueueFactories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;

@ComponentScan("com.comcast.pop.service")
public class ResourcePoolProcessor
{
    public ResourcePoolProcessor()
    {
    }

    @Autowired
    public ResourcePoolProcessor defaultResourcePoolProcessor(@Qualifier("persisters")InMemoryPersistersFactory persisters,
                                                              @Qualifier("queueFactories")MemoryQueueFactories memoryQueueFactories )
    {
        ObjectPersisterFactory<InsightScheduleInfo>  insightScheduleInfoObjectPersisterFactory = new BasePersisterFactory(persisters.getInsightScheduleInfoPersister());
        ItemQueueFactory<ReadyAgenda> readyAgendaItemQueueFactory = memoryQueueFactories.getReadyAgendaMemoryQueueFactory();
        ObjectPersisterFactory<ReadyAgenda> readyAgendaObjectPersisterFactory = new BasePersisterFactory(persisters.getReadyAgendaPersister());
        InMemoryClient<Customer> customerClient = new InMemoryClient<>((AbstractEndpointStore<Customer>) persisters.getCustomerPersister());
        InMemoryClient<Insight> insightClient = new InMemoryClient<>((AbstractEndpointStore<Insight>) persisters.getInsightPersister());

        return new ResourcePoolProcessor(insightScheduleInfoObjectPersisterFactory,
                readyAgendaItemQueueFactory,
                readyAgendaObjectPersisterFactory,
                customerClient,
                insightClient
                );
    }

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private  ObjectPersisterFactory<InsightScheduleInfo> insightScheduleInfoPersisterFactory;
    private  ItemQueueFactory<ReadyAgenda> readyAgendaQueueFactory;
    private  ObjectPersisterFactory<ReadyAgenda> readyAgendaPersisterFactory;
    private  InMemoryClient<Customer> customerClient;
    private  InMemoryClient<Insight> insightClient;

    private QueueMonitorFactory queueMonitorFactory = new QueueMonitorFactory();

    public ResourcePoolProcessor(ObjectPersisterFactory<InsightScheduleInfo> insightScheduleInfoPersisterFactory, ItemQueueFactory<ReadyAgenda> readyAgendaQueueFactory, ObjectPersisterFactory<ReadyAgenda> readyAgendaPersisterFactory, InMemoryClient<Customer> customerClient, InMemoryClient<Insight> insightClient)
    {
        this.insightScheduleInfoPersisterFactory = insightScheduleInfoPersisterFactory;
        this.readyAgendaQueueFactory = readyAgendaQueueFactory;
        this.readyAgendaPersisterFactory = readyAgendaPersisterFactory;
        this.customerClient = customerClient;
        this.insightClient = insightClient;
    }

    public void processResourcePool(String resourcePoolId)
    {
        String readyAgendaTableName = "readyagenda";
        String scheduleInfoTableName = "insightscheduleinfo";

        logger.info("ReadyAgenda Table: {} ScheduleInfo Table: {}", readyAgendaTableName, scheduleInfoTableName);

        QueueMonitor queueMonitor = queueMonitorFactory.createQueueMonitor(
                readyAgendaQueueFactory,
                readyAgendaPersisterFactory.getObjectPersister(readyAgendaTableName),
                insightClient,
                customerClient,
                insightScheduleInfoPersisterFactory.getObjectPersister(scheduleInfoTableName));
        try
        {
            queueMonitor.processResourcePool(resourcePoolId);
        }
        catch(Throwable t)
        {
            throw new RuntimeException(String.format("Error processing resource pool: %1$s ", resourcePoolId), t);
        }
    }
}
