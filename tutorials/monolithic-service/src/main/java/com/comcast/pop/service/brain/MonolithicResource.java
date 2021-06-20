package com.comcast.pop.service.brain;

import com.comcast.pop.api.Agenda;
import com.comcast.pop.api.AgendaTemplate;
import com.comcast.pop.api.facility.Customer;
import com.comcast.pop.api.facility.ResourcePool;
import com.comcast.pop.endpoint.api.DefaultServiceResponse;
import com.comcast.pop.endpoint.api.ErrorResponse;
import com.comcast.pop.endpoint.api.agenda.RunAgendaRequest;
import com.comcast.pop.endpoint.api.agenda.RunAgendaResponse;
import com.comcast.pop.endpoint.api.data.DefaultDataObjectResponse;
import com.comcast.pop.endpoint.api.resourcepool.CreateAgendaRequest;
import com.comcast.pop.endpoint.api.resourcepool.CreateAgendaResponse;
import com.comcast.pop.persistence.api.PersistenceException;
import com.comcast.pop.service.in_memory_endpoints.InMemoryPersisters;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collection;

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
    InMemoryPersisters persisters = InMemoryPersisters.getPersisters();

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

    // todo createAgenda from template ID and parameters; we can skip this for the moment



    // todo run agenda; this will entail that we integrate with the brain.  Cool!
    @GetMapping("/runAgenda")
    public RunAgendaResponse postAgendaTemplate(@RequestBody RunAgendaRequest runAgendaRequest)
    {
        RunAgendaResponse response = new RunAgendaResponse();
        try
        {
            // I need to see how much existing code we can use.

        }
        catch (Exception e)
        {
            ErrorResponse errorResponse = new ErrorResponse(e, 401, "todo cid");
            response.setErrorResponse(errorResponse);

        }
        return response;
    }


    // todo get agenda progress

}
