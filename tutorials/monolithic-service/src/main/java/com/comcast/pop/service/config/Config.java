package com.comcast.pop.service.config;

import com.comcast.pop.api.Agenda;
import com.comcast.pop.api.AgendaTemplate;
import com.comcast.pop.api.facility.Customer;
import com.comcast.pop.api.facility.Insight;
import com.comcast.pop.api.facility.ResourcePool;
import com.comcast.pop.api.progress.AgendaProgress;
import com.comcast.pop.api.progress.OperationProgress;
import com.comcast.pop.endpoint.client.ResourcePoolServiceClient;
import com.comcast.pop.endpoint.progress.service.ProgressSummaryRequestProcessor;
import com.comcast.pop.endpoint.resourcepool.service.GetAgendaServiceRequestProcessor;
import com.comcast.pop.handler.executor.impl.progress.agenda.ResourcePoolServiceAgendaProgressReporter;
import com.comcast.pop.http.api.HttpURLConnectionFactory;
import com.comcast.pop.http.api.NoAuthHTTPUrlConnectionFactory;
import com.comcast.pop.persistence.api.ObjectPersister;
import com.comcast.pop.scheduling.api.ReadyAgenda;
import com.comcast.pop.scheduling.queue.InsightScheduleInfo;
import com.comcast.pop.service.brain.Brain;
import com.comcast.pop.service.in_memory_endpoints.*;
import com.comcast.pop.service.queues.MemoryQueueFactories;
import com.comcast.pop.service.stream.ResourcePoolProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan("com.comcast.pop.service")
public class Config
{
    @Autowired
    @Bean("resourcePoolProcessorSingleton")
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ResourcePoolProcessor getResourcePoolProcessorSingleton(@Qualifier("persisters")InMemoryPersistersFactory persisters, @Qualifier("queueFactories")MemoryQueueFactories queueFactories)
    {
        return new ResourcePoolProcessor().defaultResourcePoolProcessor(persisters, queueFactories);
    }

    @Autowired
    @Bean("postProgress")
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ResourcePoolServiceAgendaProgressReporter postProgress(@Qualifier("resourcePoolServiceClientSingleton") ResourcePoolServiceClient resourcePoolServiceClient)
    {

        return new ResourcePoolServiceAgendaProgressReporter(resourcePoolServiceClient);
    }

    @Autowired
    @Bean("resourcePoolServiceClientSingleton")
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ResourcePoolServiceClient getResourcePoolServiceClient()
    {
        String path = "/postProgress"; // todo confirm ... or do we need localhost:8080 ... etc?  Aiming for POC ATM.
        HttpURLConnectionFactory connectionFactory = new NoAuthHTTPUrlConnectionFactory();
        return new ResourcePoolServiceClient(path, connectionFactory);
    }

    @Autowired
    @Bean("progressRequestProcessor")
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ProgressSummaryRequestProcessor getProgressSummaryRequestProcessorSingleton(@Qualifier("persisters")InMemoryPersistersFactory persisters)
    {
        return new ProgressSummaryRequestProcessor(persisters.getAgendaProgressPersister(),
                persisters.getOperationProgressPersister(),
                persisters.getAgendaPersister());
    }


    @Autowired
    @Bean("brainSingleton")
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public Brain brainSingleton(@Qualifier("persisters")InMemoryPersistersFactory persisters,
                                @Qualifier("queueFactories")MemoryQueueFactories memoryQueueFactories,
                                @Qualifier("progressRequestProcessor") ProgressSummaryRequestProcessor progressRequestProcessor,
                                @Qualifier("postProgress") ResourcePoolServiceAgendaProgressReporter progressReporter,
                                @Qualifier("getAgendaProcessor")GetAgendaServiceRequestProcessor getAgendaProcessor)
    {
        return new Brain(persisters,memoryQueueFactories, progressRequestProcessor,  progressReporter, getAgendaProcessor);
    }

    @Bean("persisters")
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public InMemoryPersistersFactory makeDefaultInMemoryPeristersFactory()
    {
        ObjectPersister<Agenda> agendaPersister_local = new AgendaStore();
        ObjectPersister<AgendaTemplate> agendaTemplatePersister_local = new AgendaTemplateStore();
        ObjectPersister<AgendaProgress> agendaProgressPersister_local = new AgendaProgressStore();
        ObjectPersister<ReadyAgenda> readyAgendaPersister_local = new ReadyAgendaStore();
        ObjectPersister<OperationProgress> operationProgressPersister_local = new OperationProgressStore();
        ObjectPersister<Insight> insightPersister_local = new InsightStore();
        ObjectPersister<Customer> customerPersister_local = new CustomerStore();
        ObjectPersister<ResourcePool> resourcePoolPersister_local = new ResourcePoolStore();
        ObjectPersister<InsightScheduleInfo> insightScheuleInfo_local = new InsightScheduleInfoStore();
        InMemoryPersistersFactory persisters = new InMemoryPersistersFactory(agendaPersister_local, agendaTemplatePersister_local, agendaProgressPersister_local, readyAgendaPersister_local,operationProgressPersister_local,insightPersister_local, customerPersister_local, resourcePoolPersister_local, insightScheuleInfo_local);
        return persisters;
    }

    @Bean("queueFactories")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public MemoryQueueFactories memoryQueueFactoriesSingleton()
    {
        return new MemoryQueueFactories();
    }

    @Bean("getAgendaProcessor")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public GetAgendaServiceRequestProcessor getAgendaServiceRequestProcessor()
    {
        return new GetAgendaServiceRequestProcessor(memoryQueueFactoriesSingleton().getAgendaInfoMemoryQueueFactory(),
                makeDefaultInMemoryPeristersFactory().getInsightPersister(),
                makeDefaultInMemoryPeristersFactory().getAgendaPersister(),
                makeDefaultInMemoryPeristersFactory().getAgendaProgressPersister(),
                makeDefaultInMemoryPeristersFactory().getOperationProgressPersister());
    }
//    @Bean("resourcePoolProcessorSingleton")
//    @DependsOn({"persisters","queueFactories"})
//    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
//    public ResourcePoolProcessor getResourcePoolProcessorSingleton()
//    {
//        return new ResourcePoolProcessor().defaultResourcePoolProcessor(makeDefaultInMemoryPeristersFactory(), memoryQueueFactoriesSingleton());
//    }
//
//    @Bean("brainSingleton")
//    @DependsOn("persisters")
//    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
//    public Brain brainSingleton()
//    {
//        return new Brain(makeDefaultInMemoryPeristersFactory());
//    }
//
//    @Bean("persisters")
//    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
//    InMemoryPersistersFactory makeDefaultInMemoryPeristersFactory()
//    {
//        ObjectPersister<Agenda> agendaPersister_local = new AgendaStore();
//        ObjectPersister<AgendaTemplate> agendaTemplatePersister_local = new AgendaTemplateStore();
//        ObjectPersister<AgendaProgress> agendaProgressPersister_local = new AgendaProgressStore();
//        ObjectPersister<ReadyAgenda> readyAgendaPersister_local = new ReadyAgendaStore();
//        ObjectPersister<OperationProgress> operationProgressPersister_local = new OperationProgressStore();
//        ObjectPersister<Insight> insightPersister_local = new InsightStore();
//        ObjectPersister<Customer> customerPersister_local = new CustomerStore();
//        ObjectPersister<ResourcePool> resourcePoolPersister_local = new ResourcePoolStore();
//        ObjectPersister<InsightScheduleInfo> insightScheuleInfo_local = new InsightScheduleInfoStore();
//        InMemoryPersistersFactory persisters = new InMemoryPersistersFactory(agendaPersister_local, agendaTemplatePersister_local, agendaProgressPersister_local, readyAgendaPersister_local,operationProgressPersister_local,insightPersister_local, customerPersister_local, resourcePoolPersister_local, insightScheuleInfo_local);
//        return persisters;
//    }
//
//    @Bean("queueFactories")
//    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
//    MemoryQueueFactories memoryQueueFactoriesSingleton()
//    {
//        return new MemoryQueueFactories();
//    }
}
