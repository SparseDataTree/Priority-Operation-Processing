package com.comcast.pop.service.in_memory_endpoints;

import com.comcast.pop.api.Agenda;
import com.comcast.pop.api.AgendaTemplate;
import com.comcast.pop.api.facility.Customer;
import com.comcast.pop.api.facility.Insight;
import com.comcast.pop.api.facility.ResourcePool;
import com.comcast.pop.api.progress.AgendaProgress;
import com.comcast.pop.api.progress.OperationProgress;
import com.comcast.pop.persistence.api.ObjectPersister;
import com.comcast.pop.scheduling.api.ReadyAgenda;

public class InMemoryPersisters
{

    private static InMemoryPersisters singletonPersisters = InMemoryPersisters.makeDefault();

    private final ObjectPersister<Agenda> agendaPersister;
    private final ObjectPersister<AgendaTemplate> agendaTemplatePersister;
    private final ObjectPersister<AgendaProgress> agendaProgressPersister;
    private final ObjectPersister<ReadyAgenda> readyAgendaPersister;
    private final ObjectPersister<OperationProgress> operationProgressPersister;
    private final ObjectPersister<Insight> insightPersister;
    private final ObjectPersister<Customer> customerPersister;
    private final ObjectPersister<ResourcePool> resourcePoolPersister;


//     @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
     private InMemoryPersisters(ObjectPersister<Agenda> agendaPersister, ObjectPersister<AgendaTemplate> agendaTemplatePersister, ObjectPersister<AgendaProgress> agendaProgressPersister, ObjectPersister<ReadyAgenda> readyAgendaPersister, ObjectPersister<OperationProgress> operationProgressPersister, ObjectPersister<Insight> insightPersister, ObjectPersister<Customer> customerPersister, ObjectPersister<ResourcePool> resourcePoolPersister)
    {
        this.agendaPersister = agendaPersister;
        this.agendaTemplatePersister = agendaTemplatePersister;
        this.agendaProgressPersister = agendaProgressPersister;
        this.readyAgendaPersister = readyAgendaPersister;
        this.operationProgressPersister = operationProgressPersister;
        this.insightPersister = insightPersister;
        this.customerPersister = customerPersister;
        this.resourcePoolPersister = resourcePoolPersister;
    }

    private static InMemoryPersisters makeDefault()
    {
        ObjectPersister<Agenda> agendaPersister_local = new AgendaStore();
        ObjectPersister<AgendaTemplate> agendaTemplatePersister_local = new AgendaTemplateStore();
        ObjectPersister<AgendaProgress> agendaProgressPersister_local = new AgendaProgressStore();
        ObjectPersister<ReadyAgenda> readyAgendaPersister_local = new ReadyAgendaStore();
        ObjectPersister<OperationProgress> operationProgressPersister_local = new OperationProgressStore();
        ObjectPersister<Insight> insightPersister_local = new InsightStore();
        ObjectPersister<Customer> customerPersister_local = new CustomerStore();
        ObjectPersister<ResourcePool> resourcePoolPersister_local = new ResourcePoolStore();

        return new InMemoryPersisters(agendaPersister_local, agendaTemplatePersister_local, agendaProgressPersister_local, readyAgendaPersister_local,operationProgressPersister_local,insightPersister_local, customerPersister_local, resourcePoolPersister_local);
    }

    public static InMemoryPersisters getPersisters()
    {
        return singletonPersisters;
    }

    public ObjectPersister<Agenda> getAgendaPersister()
    {
        return agendaPersister;
    }

    public ObjectPersister<AgendaTemplate> getAgendaTemplatePersister()
    {
        return agendaTemplatePersister;
    }

    public ObjectPersister<AgendaProgress> getAgendaProgressPersister()
    {
        return agendaProgressPersister;
    }

    public ObjectPersister<ReadyAgenda> getReadyAgendaPersister()
    {
        return readyAgendaPersister;
    }

    public ObjectPersister<OperationProgress> getOperationProgressPersister()
    {
        return operationProgressPersister;
    }

    public ObjectPersister<Insight> getInsightPersister()
    {
        return insightPersister;
    }

    public ObjectPersister<Customer> getCustomerPersister()
    {
        return customerPersister;
    }

    public ObjectPersister<ResourcePool> getResourcePoolPersister()
    {
        return resourcePoolPersister;
    }
}
