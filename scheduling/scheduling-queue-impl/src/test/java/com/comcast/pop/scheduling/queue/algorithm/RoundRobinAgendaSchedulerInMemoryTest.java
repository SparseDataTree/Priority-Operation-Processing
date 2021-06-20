package com.comcast.pop.scheduling.queue.algorithm;

import com.comcast.pop.api.Agenda;
import com.comcast.pop.api.facility.Customer;
import com.comcast.pop.api.facility.Insight;
import com.comcast.pop.endpoint.api.ErrorResponse;
import com.comcast.pop.endpoint.api.data.DataObjectRequest;
import com.comcast.pop.endpoint.api.data.DataObjectResponse;
import com.comcast.pop.endpoint.api.data.DefaultDataObjectResponse;
import com.comcast.pop.endpoint.base.DataObjectRequestProcessor;
import com.comcast.pop.endpoint.base.validation.DataObjectValidator;
import com.comcast.pop.endpoint.client.DataObjectRequestProcessorClient;
import com.comcast.pop.endpoint.client.ObjectClient;
import com.comcast.pop.persistence.api.ObjectPersister;
import com.comcast.pop.persistence.api.PersistenceException;
import com.comcast.pop.persistence.impl.MemoryObjectPersisterRetriever;
import com.comcast.pop.scheduling.api.ReadyAgenda;
import com.comcast.pop.scheduling.queue.InsightScheduleInfo;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.*;

public class RoundRobinAgendaSchedulerInMemoryTest extends BaseAgendaSchedulerTest
{

    private RoundRobinAgendaScheduler scheduler;
    private ObjectClient<Customer> mockCustomerClient;
    private ObjectPersister<Customer> customerPersister;

    @BeforeMethod
    public void setup()
    {
        insight = new Insight();
        insight.setId(UUID.randomUUID().toString());
        insight.setResourcePoolId(UUID.randomUUID().toString());
        customerPersister = new MemoryObjectPersisterRetriever<>();;

        insightScheduleInfo = new InsightScheduleInfo();
        mockReadyAgendaPersister = new MemoryObjectPersisterRetriever<>();
        DataObjectValidator<Customer, DataObjectRequest<Customer>> validator = new DataObjectValidator<>();
        DataObjectRequestProcessor<Customer> requestProcessor = new DataObjectRequestProcessor<>(customerPersister, validator);
        mockCustomerClient = new DataObjectRequestProcessorClient<>(requestProcessor);
        scheduler = new RoundRobinAgendaScheduler(mockReadyAgendaPersister, mockCustomerClient);
    }

    @Test
    public void testGetPendingCustomerIdsNoPendingCustomers()
    {
        // also no customers
        List<String> result = scheduler.getPendingCustomerIds(insightScheduleInfo, insight).getPendingCustomerIds();
        Assert.assertEquals(result.size(), 0);
    }

    @Test
    public void testGetPendingCustomerIdsClientError() throws PersistenceException
    {
//        DataObjectResponse<Customer> customerFeed = new DefaultDataObjectResponse<>();
//        customerFeed.setErrorResponse(new ErrorResponse());
//        doReturn(customerFeed).when(mockCustomerClient).getObjects(anyList());

        // todo looks like we need to persist some agendas and customers, with a problem; perhaps an agenda that does not
        // align with a customer?
        ReadyAgenda readyAgenda = new ReadyAgenda();
        Agenda agenda = new Agenda();
        agenda.setId("0");
        readyAgenda.setAgendaId(agenda.getId());
        mockReadyAgendaPersister.persist(readyAgenda);
        List<String> result = scheduler.getPendingCustomerIds(insightScheduleInfo, insight).getPendingCustomerIds();
        Assert.assertEquals(result.size(), 0);
//        verify(mockCustomerClient, times(1)).getObjects(anyList());
    }

}
