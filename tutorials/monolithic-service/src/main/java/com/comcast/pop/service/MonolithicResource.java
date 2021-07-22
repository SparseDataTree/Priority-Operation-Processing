package com.comcast.pop.service;

import com.comcast.pop.api.Agenda;
import com.comcast.pop.api.AgendaTemplate;
import com.comcast.pop.api.facility.Customer;
import com.comcast.pop.api.facility.Insight;
import com.comcast.pop.api.facility.ResourcePool;
import com.comcast.pop.api.progress.AgendaProgress;
import com.comcast.pop.endpoint.api.DefaultServiceRequest;
import com.comcast.pop.endpoint.api.ErrorResponse;
import com.comcast.pop.endpoint.api.ServiceRequest;
import com.comcast.pop.endpoint.api.agenda.RunAgendaRequest;
import com.comcast.pop.endpoint.api.agenda.RunAgendaResponse;
import com.comcast.pop.endpoint.api.data.DefaultDataObjectResponse;
import com.comcast.pop.endpoint.api.progress.ProgressSummaryRequest;
import com.comcast.pop.endpoint.api.progress.ProgressSummaryResponse;
import com.comcast.pop.endpoint.api.resourcepool.GetAgendaRequest;
import com.comcast.pop.endpoint.api.resourcepool.GetAgendaResponse;
import com.comcast.pop.endpoint.api.resourcepool.UpdateAgendaProgressRequest;
import com.comcast.pop.endpoint.api.resourcepool.UpdateAgendaProgressResponse;
import com.comcast.pop.persistence.api.PersistenceException;
import com.comcast.pop.service.brain.Brain;
import com.comcast.pop.service.in_memory_endpoints.InMemoryPersistersFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/*
We are starting light, only exposing part of the full POP API, but enough to
run some happy path demos

To start, we are exposing 5 APIs:
   - post agendas;
   - post agenda templates;
   - create agendas via template and parameters;
   - run agendas; and
   - get agenda progress

   Things to do after happy path ...
   - authenticate,
   - authorize.
   - Wire up cid logic
   - more APIs to create and retrieve and update and delete endpoints

 */

@RestController
@ComponentScan(basePackages = "com.comcast.pop.service")
public class MonolithicResource
{
    @Autowired
    @Qualifier("persisters")
    InMemoryPersistersFactory persisters;

    @Autowired
    @Qualifier("brainSingleton")
    Brain brain;

    @PostMapping("/postAgenda")
    public DefaultDataObjectResponse postAgenda(@RequestBody Agenda agenda)
    {
        DefaultDataObjectResponse response = new DefaultDataObjectResponse();
            try
            {
                response.add(persisters.getAgendaPersister().persist(agenda));
            }
            catch (Exception | PersistenceException e)
            {
                ErrorResponse errorResponse = new ErrorResponse(e, 401, agenda.getCid());
                response.setErrorResponse(errorResponse);

            }
        return response;
    }

    @PostMapping("/postCustomer")
    public DefaultDataObjectResponse postCustomer(@RequestBody Customer customer)
    {
        DefaultDataObjectResponse response = new DefaultDataObjectResponse();
        try
        {
            response.add(persisters.getCustomerPersister().persist(customer));
        }
        catch (Exception | PersistenceException e)
        {
            ErrorResponse errorResponse = new ErrorResponse(e, 401, customer.getCid());
            response.setErrorResponse(errorResponse);

        }
        return response;
    }

    @PostMapping("/postResourcePool")
    public DefaultDataObjectResponse postResourcePool(@RequestBody ResourcePool resourcePool)
    {
        DefaultDataObjectResponse response = new DefaultDataObjectResponse();
        try
        {
            response.add(persisters.getResourcePoolPersister().persist(resourcePool));
        }
        catch (Exception | PersistenceException e)
        {
            ErrorResponse errorResponse = new ErrorResponse(e, 401, resourcePool.getCid());
            response.setErrorResponse(errorResponse);

        }
        return response;
    }

    @PostMapping("/postAgendaTemplate")
    public DefaultDataObjectResponse postAgendaTemplate(@RequestBody AgendaTemplate agendaTemplate)
    {
        DefaultDataObjectResponse response = new DefaultDataObjectResponse();
        try
        {
            response.add(persisters.getAgendaTemplatePersister().persist(agendaTemplate));
        }
        catch (Exception | PersistenceException e)
        {
            ErrorResponse errorResponse = new ErrorResponse(e, 401, agendaTemplate.getCid());
            response.setErrorResponse(errorResponse);

        }
        return response;
    }

    @PostMapping("/postInsight")
    public DefaultDataObjectResponse postInsight(@RequestBody Insight insight)
    {
        DefaultDataObjectResponse response = new DefaultDataObjectResponse();
        try
        {
            response.add(persisters.getInsightPersister().persist(insight));
        }
        catch (Exception | PersistenceException e)
        {
            ErrorResponse errorResponse = new ErrorResponse(e, 401, insight.getCid());
            response.setErrorResponse(errorResponse);

        }
        return response;
    }

    // todo createAgenda from template ID and parameters; we can skip this for the moment



    // todo run agenda; this will entail that we integrate with the brain.  Cool!
    @PostMapping("/runAgenda")
    public RunAgendaResponse runAgenda(@RequestBody RunAgendaRequest runAgendaRequest)
    {
        RunAgendaResponse response = new RunAgendaResponse();
        ServiceRequest<RunAgendaRequest> request = new DefaultServiceRequest<>(runAgendaRequest);
        try
        {
            // todo ... don't we want to return an agendaprogress id?  Pending.
            response = brain.runAgenda(request);

        }
        catch (Exception e)
        {
            ErrorResponse errorResponse = new ErrorResponse(e, 401, "todo cid");
            response.setErrorResponse(errorResponse);

        }
        return response;
    }


    // todo get agenda progress... this will call out to a service that encapsulates an agendaprogress queue; this will be used by puller and executor pods
    //  todo ... in k8, as well as by customers.

    @GetMapping("/agendaProgress")
    public ProgressSummaryResponse agendaProgress(ProgressSummaryRequest progressSummaryRequest)
    {
        ProgressSummaryResponse response = brain.getAgendaProgress(progressSummaryRequest);

        // todo check for issues

        return response;
    }

    // todo we need a service to post to agenda/operation progress - see the executor class; as this is needed there.

    @PostMapping("/postProgress")
    public UpdateAgendaProgressResponse postProgress(UpdateAgendaProgressRequest updateAgendaProgressRequest)
    {
        UpdateAgendaProgressResponse response = new UpdateAgendaProgressResponse();
        try
        {
            brain.postProgress(updateAgendaProgressRequest.getAgendaProgress());
        }
        catch (Exception e)
        {
            ErrorResponse errorResponse = new ErrorResponse();
            int responseCode = 412; // todo
            errorResponse.setResponseCode(responseCode);
            errorResponse.setDescription(e.getMessage());
            errorResponse.setCorrelationId(updateAgendaProgressRequest.getAgendaProgress().getCid());
            // todo more information?
            response.setErrorResponse(errorResponse);
        }
        return response;
    }


    /*
    Still sussing this out.  See GenericPOPClient, and ResourcePoolServiceClient and PullerProcessor.  Some of these
    classes are really focused on the SQS tech; so we will need to make somethings comparable, for our monolith.

    Another observation here is that the request for agendas (as is done with AWS) is actually a POST.  Hm.  The
    GetAgendaRequest results in a POST ...  what is posted?  where is it posted?
    The GetAgendaRequest object is posted.  Note that this has two fields: 1. an insight id; 2 the number of agenda instances to retrieve.

    Where is it posted?

    See the GetAgendaServiceRequestProcessor class.
     */
    @GetMapping("/readyAgenda")
    public GetAgendaResponse readyAgenda(GetAgendaRequest getAgendaRequest)
    {
        GetAgendaResponse response = brain.getAgendaRequest(getAgendaRequest);

        // todo check for problems

        return response;
    }
}
