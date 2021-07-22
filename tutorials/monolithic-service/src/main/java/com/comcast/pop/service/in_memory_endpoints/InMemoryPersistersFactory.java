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
import com.comcast.pop.scheduling.queue.InsightScheduleInfo;

public class InMemoryPersistersFactory
{
    private final ObjectPersister<Agenda> agendaPersister;
    private final ObjectPersister<AgendaTemplate> agendaTemplatePersister;
    private final ObjectPersister<AgendaProgress> agendaProgressPersister;
    private final ObjectPersister<ReadyAgenda> readyAgendaPersister;
    private final ObjectPersister<OperationProgress> operationProgressPersister;
    private final ObjectPersister<Insight> insightPersister;
    private final ObjectPersister<Customer> customerPersister;
    private final ObjectPersister<ResourcePool> resourcePoolPersister;
    private final ObjectPersister<InsightScheduleInfo> insightScheduleInfoPersister;

     public InMemoryPersistersFactory(ObjectPersister<Agenda> agendaPersister, ObjectPersister<AgendaTemplate> agendaTemplatePersister, ObjectPersister<AgendaProgress> agendaProgressPersister, ObjectPersister<ReadyAgenda> readyAgendaPersister, ObjectPersister<OperationProgress> operationProgressPersister, ObjectPersister<Insight> insightPersister, ObjectPersister<Customer> customerPersister, ObjectPersister<ResourcePool> resourcePoolPersister, ObjectPersister<InsightScheduleInfo> insightScheduleInfoPersister)
    {
        this.agendaPersister = agendaPersister;
        this.agendaTemplatePersister = agendaTemplatePersister;
        this.agendaProgressPersister = agendaProgressPersister;
        this.readyAgendaPersister = readyAgendaPersister;
        this.operationProgressPersister = operationProgressPersister;
        this.insightPersister = insightPersister;
        this.customerPersister = customerPersister;
        this.resourcePoolPersister = resourcePoolPersister;
        this.insightScheduleInfoPersister = insightScheduleInfoPersister;
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

    public ObjectPersister<InsightScheduleInfo> getInsightScheduleInfoPersister()
    {
        return insightScheduleInfoPersister;
    }
}
