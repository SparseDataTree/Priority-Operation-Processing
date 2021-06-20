package com.comcast.pop.service.brain;

import com.comcast.pop.api.progress.AgendaProgress;
import com.comcast.pop.endpoint.agenda.AgendaRequestProcessor;
import com.comcast.pop.endpoint.agenda.factory.AgendaFactory;
import com.comcast.pop.endpoint.agenda.factory.DefaultAgendaFactory;
import com.comcast.pop.endpoint.agenda.service.RunAgendaServiceRequestProcessor;
import com.comcast.pop.endpoint.agendatemplate.AgendaTemplateRequestProcessor;
import com.comcast.pop.endpoint.api.agenda.RunAgendaResponse;
import com.comcast.pop.endpoint.factory.RequestProcessorFactory;
import com.comcast.pop.endpoint.util.ServiceDataObjectRetriever;
import com.comcast.pop.endpoint.util.ServiceResponseFactory;
import com.comcast.pop.scheduling.queue.algorithm.AgendaScheduler;
import com.comcast.pop.scheduling.queue.algorithm.FIFOAgendaScheduler;
import com.comcast.pop.service.in_memory_endpoints.InMemoryPersisters;
import com.comcast.pop.service.queues.MemoryQueueFactories;
import com.comcast.pop.service.queues.MemoryQueueFactory;
import org.springframework.beans.factory.annotation.Autowired;
/*
Note that this class can effectively be broken up into microservices; perhaps even one microservice for each major functional field.
Of course the persisters are a collection.  Each persister could be broken into a microservice.

The point of this class is not to undo the partitioning already achieved by POP, but rather to provide a resource for learning and experimentation.

It may also suggest how to architect POP to be more tech agnostic.

This class will need testing!
 */
public class Brain
{
    private static Brain brain = new Brain();

    private InMemoryPersisters persisters = InMemoryPersisters.getPersisters(); // singleton

    private MemoryQueueFactories memoryQueueFactories = MemoryQueueFactories.getMemoryQueueFactories(); // singleton

    private RequestProcessorFactory requestProcessorFactory = new RequestProcessorFactory();
    private ServiceResponseFactory<RunAgendaResponse> responseFactory = new ServiceResponseFactory<>(RunAgendaResponse.class);
    private RunAgendaServiceRequestProcessor requestProcessor;
    private AgendaTemplateRequestProcessor agendaTemplateRequestProcessor;
    private AgendaRequestProcessor agendaRequestProcessor;
    private ServiceDataObjectRetriever<RunAgendaResponse> dataObjectRetriever;
    private AgendaFactory agendaFactory;
    private AgendaScheduler agendaScheduler;
    private void Brain()
    {
        agendaTemplateRequestProcessor = requestProcessorFactory.createAgendaTemplateRequestProcessor(persisters.getAgendaTemplatePersister());
        agendaRequestProcessor = requestProcessorFactory.createAgendaRequestProcessor(
                persisters.getAgendaPersister(),
                persisters.getAgendaProgressPersister(),
                persisters.getReadyAgendaPersister(),
                persisters.getOperationProgressPersister(), persisters.getInsightPersister(),
                persisters.getCustomerPersister());
        agendaFactory = new DefaultAgendaFactory();
        dataObjectRetriever = new ServiceDataObjectRetriever<>(responseFactory);
        requestProcessor = new RunAgendaServiceRequestProcessor(
                persisters.getInsightPersister(),
                persisters.getAgendaPersister(),
                persisters.getCustomerPersister(),
                persisters.getAgendaProgressPersister(),
                persisters.getOperationProgressPersister(),
                persisters.getReadyAgendaPersister(),
                persisters.getAgendaTemplatePersister());
        requestProcessor.setRequestProcessorFactory(requestProcessorFactory);
        requestProcessor.setAgendaFactory(agendaFactory);
        requestProcessor.setDataObjectRetriever(dataObjectRetriever);
        agendaScheduler = new FIFOAgendaScheduler(persisters.getReadyAgendaPersister()); // todo we could aim to support the roundrobin scheduler
    }




    public static Brain getBrain()
    {
        return brain;
    }

}
