package com.comcast.pop.service.brain;

import com.comcast.pop.api.Agenda;
import com.comcast.pop.api.progress.AgendaProgress;
import com.comcast.pop.endpoint.agenda.AgendaRequestProcessor;
import com.comcast.pop.endpoint.agenda.factory.AgendaFactory;
import com.comcast.pop.endpoint.agenda.factory.DefaultAgendaFactory;
import com.comcast.pop.endpoint.agenda.service.RunAgendaServiceRequestProcessor;
import com.comcast.pop.endpoint.agendatemplate.AgendaTemplateRequestProcessor;
import com.comcast.pop.endpoint.api.DefaultServiceRequest;
import com.comcast.pop.endpoint.api.ServiceRequest;
import com.comcast.pop.endpoint.api.agenda.RunAgendaRequest;
import com.comcast.pop.endpoint.api.agenda.RunAgendaResponse;
import com.comcast.pop.endpoint.api.progress.ProgressSummaryRequest;
import com.comcast.pop.endpoint.api.progress.ProgressSummaryResponse;
import com.comcast.pop.endpoint.api.resourcepool.GetAgendaRequest;
import com.comcast.pop.endpoint.api.resourcepool.GetAgendaResponse;
import com.comcast.pop.endpoint.factory.RequestProcessorFactory;
import com.comcast.pop.endpoint.progress.service.ProgressSummaryRequestProcessor;
import com.comcast.pop.endpoint.resourcepool.service.GetAgendaServiceRequestProcessor;
import com.comcast.pop.endpoint.util.ServiceDataObjectRetriever;
import com.comcast.pop.endpoint.util.ServiceResponseFactory;
import com.comcast.pop.handler.executor.impl.progress.agenda.ResourcePoolServiceAgendaProgressReporter;
import com.comcast.pop.persistence.api.PersistenceException;
import com.comcast.pop.scheduling.queue.algorithm.AgendaSchedulerFactory;
import com.comcast.pop.service.in_memory_clients.InMemoryClient;
import com.comcast.pop.service.in_memory_endpoints.AbstractEndpointStore;
import com.comcast.pop.service.in_memory_endpoints.InMemoryPersistersFactory;
import com.comcast.pop.service.queues.MemoryQueueFactories;
import com.comcast.pop.service.resourcepool.ResourcePoolProcessIgniter;
import com.comcast.pop.service.stream.ResourcePoolProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.Scheduled;

/*
Note that this class can effectively be broken up into microservices; perhaps even one microservice for each major functional field.
Of course the persisters are a collection.  Each persister could be broken into a microservice.

The point of this class is not to undo the partitioning already achieved by POP, but rather to provide a resource for learning and experimentation.

It may also suggest how to architect POP to be more tech agnostic.

This class will need testing!
 */
@ComponentScan("com.comcast.pop.service")
public class Brain
{
    private final GetAgendaServiceRequestProcessor getAgendaProcessor;
    private final ResourcePoolServiceAgendaProgressReporter progressReporter;
    private ResourcePoolProcessor resourcePoolProcessor; // todo do we need this?
    private ProgressSummaryRequestProcessor progressRequestProcessor;

    private InMemoryClient<Agenda> agendaClient;
    private RequestProcessorFactory requestProcessorFactory = new RequestProcessorFactory();
    private ServiceResponseFactory<RunAgendaResponse> responseFactory = new ServiceResponseFactory<>(RunAgendaResponse.class);
    private RunAgendaServiceRequestProcessor requestProcessor;
    private AgendaTemplateRequestProcessor agendaTemplateRequestProcessor;
    private AgendaRequestProcessor agendaRequestProcessor;
    private ServiceDataObjectRetriever<RunAgendaResponse> dataObjectRetriever;
    private AgendaFactory agendaFactory;
    private AgendaSchedulerFactory agendaSchedulerFactory;
    private ResourcePoolProcessIgniter igniter;

    @Autowired
     public Brain(@Qualifier("persisters") InMemoryPersistersFactory persisters,
                  @Qualifier("queueFactories") MemoryQueueFactories memoryQueueFactories,
                  @Qualifier("progressRequestProcessor") ProgressSummaryRequestProcessor progressRequestProcessor,
                  @Qualifier("postProgress") ResourcePoolServiceAgendaProgressReporter progressReporter,
                  @Qualifier("getAgendaProcessor")GetAgendaServiceRequestProcessor getAgendaProcessor)
    {
        this.getAgendaProcessor = getAgendaProcessor;
        this.progressRequestProcessor = progressRequestProcessor;
        this.progressReporter = progressReporter;
        // todo review order of bean creation...seeing that persisters is null here!
        resourcePoolProcessor = new ResourcePoolProcessor().defaultResourcePoolProcessor(persisters, memoryQueueFactories);
        agendaClient = new InMemoryClient<>((AbstractEndpointStore<Agenda>) persisters.getAgendaPersister());
        agendaTemplateRequestProcessor = requestProcessorFactory.createAgendaTemplateRequestProcessor(persisters.getAgendaTemplatePersister());
        agendaRequestProcessor = requestProcessorFactory.createAgendaRequestProcessor(
                persisters.getAgendaPersister(),
                persisters.getAgendaProgressPersister(),
                persisters.getReadyAgendaPersister(),
                persisters.getOperationProgressPersister(),
                persisters.getInsightPersister(),
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
        agendaSchedulerFactory = new AgendaSchedulerFactory();
        igniter = new ResourcePoolProcessIgniter(resourcePoolProcessor, persisters);
    }


    // Todo what functionality do we expose here?  Take a look at our monolith service.  I think that we will want to
    // inject the brain into that; to begin its work.

    /*
    Let's call out the steps.
    1.  The inputs include a reference to an existing agenda template and also includes a payload that, applied
    to the agenda template is used to create an agenda - aaa.

     */
    public RunAgendaResponse runAgenda(ServiceRequest<RunAgendaRequest> request)
    {
        // Note this creates an agenda from an agenda template + parameter map.  The agenda is persisted.
        // A related agendaprogress instance is created and persisted.
        RunAgendaResponse response = requestProcessor.processPOST(request);

        // Note that readyagenda instances are populated to a queue by the checkResourcePools method (that we have set
        // up to be run on intervals - event driven might be better!).

        // Also note that agendaprogress is typically updated by an executor... the endpoint call for that is still pending.

        return response;
    }


    // This is just one way to to this. For some scenarios, an event-driven action (e.g. an agenda is newly ready to be executed, or perhaps a resource pool
    // signals that it has unused capacity - or maybe both) might be better.
    @Scheduled(fixedRate = 5000)
    public void checkResourcePools() throws PersistenceException
    {
        igniter.ignite();
    }

    // Note that this is primarily used by a puller to get agendas from a readyagenda queue.
    public GetAgendaResponse getAgendaRequest(GetAgendaRequest getAgendaRequest)
    {
        ServiceRequest<GetAgendaRequest> serviceRequest = new DefaultServiceRequest<>(getAgendaRequest);

        GetAgendaResponse response = getAgendaProcessor.processPOST(serviceRequest);

        // todo check for problems
        return response;
    }

    public ProgressSummaryResponse getAgendaProgress(ProgressSummaryRequest progressSummaryRequest)
    {
        ServiceRequest<ProgressSummaryRequest> serviceRequest = new DefaultServiceRequest<>(progressSummaryRequest);
        ProgressSummaryResponse response = progressRequestProcessor.processPOST(serviceRequest);

        // todo check for problems
        return response;
    }

    public void postProgress(AgendaProgress agendaProgress)
    {
        progressReporter.reportProgress(agendaProgress);
    }
}
