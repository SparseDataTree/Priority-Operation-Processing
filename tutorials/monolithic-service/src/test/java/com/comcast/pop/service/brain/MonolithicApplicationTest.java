package com.comcast.pop.service.brain;
import com.comcast.pop.api.Agenda;
import com.comcast.pop.api.AgendaTemplate;
import com.comcast.pop.api.facility.Customer;
import com.comcast.pop.api.facility.Insight;
import com.comcast.pop.api.facility.ResourcePool;
import com.comcast.pop.api.progress.AgendaProgress;
import com.comcast.pop.endpoint.api.data.query.scheduling.ByCustomerId;
import com.comcast.pop.modules.queue.api.ItemQueue;
import com.comcast.pop.persistence.api.PersistenceException;
import com.comcast.pop.persistence.api.query.Query;
import com.comcast.pop.scheduling.api.ReadyAgenda;
import com.comcast.pop.service.MonolithicResource;
import com.comcast.pop.service.in_memory_endpoints.InMemoryPersistersFactory;
import com.comcast.pop.service.queues.MemoryQueueFactories;
import com.comcast.pop.service.queues.MemoryQueueFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ComponentScan(basePackages = "com.comcast.pop.service")
public class MonolithicApplicationTest
{


    private static ObjectMapper mapper = new ObjectMapper();

    private static final String PERSISTERS_BEAN_NAME = "persisters";
    private static final String QUEUE_FACTORIES = "queueFactories";
    private static final String CUSTOMER_ID = "testCustomerId";
    private static final String INSIGHT_ID = "testInsightId";


    Path agendaFilePath = Paths.get("src/test/resources/json/Agenda.json");
    Path agendaTemplateFilePath = Paths.get("src/test/resources/json/AgendaTemplate.json");
    Path runAgendaFilePath = Paths.get("src/test/resources/json/RunAgenda.json");
    Path customerFilePath = Paths.get("src/test/resources/json/TestCustomer.json");
    Path resourcePoolFilePath = Paths.get("src/test/resources/json/ResourcePool.json");
    Path insightFilePath = Paths.get("src/test/resources/json/Insight.json");

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        assertThat(mockMvc).isNotNull();
    }

    @Test
    public void contextLoadsBlank() {
    }

    @Test
    public void beanTest()
    {
        assertThat(webApplicationContext.containsBean(PERSISTERS_BEAN_NAME)).isTrue();
    }

    @Autowired
    private MonolithicResource controller;

    @Test
    public void contextLoads()
    {
        assertThat(controller).isNotNull();
    }

    /*
        This test:
        - illustrates how POP takes customer input defining a workflow;
        - looks for authorized and capable resources to execute the workflow;
        - queues up the work to be performed, and
        - provides visibility into the progress of the work.
     */
    @Test
    public void testAgendaTemplateAndRun() throws Exception, PersistenceException
    {
        InMemoryPersistersFactory persisters = (InMemoryPersistersFactory) webApplicationContext.getBean(PERSISTERS_BEAN_NAME);

        /*
            First, we define some endpoints needed to illustrate the core functionality of POP.  These include:
            - an agendatemplate: this endpoint holds a key-value pair template of an agenda.  It can be used to build \
              agendas via referencing the template id and also supplying the key-value pairs to feed into the template.
            - a customer: the endpoint is referenced by other endpoints to establish rights.
            - a resourcepool: this endpoint references insights and one customer.
               One example of a resource pool would be a Kubernetes cluster.
            - insight: this endpoint connects resourcepools with queues.  It also provided mappers of allowed operations.
               It also references a set of customers (potentially allowing multiple customers permission to one resourcepool - I think)

               Once we have persisted these happy-path endpoints, we can proceed with kicking off a workflow request.

         */
        assertThat(postAgendaTemplate(persisters)).isTrue();
        assertThat(postCustomer(persisters)).isTrue();
        assertThat(postResourcePool(persisters)).isTrue();
        assertThat(postInsight(persisters)).isTrue();


        /*
            Now we are ready to run an agenda.  More details in the "runAgenda" method.
         */
        assertThat(runAgenda(persisters)).isTrue();

        /*
        Finally, let's check our queues.  We expect that we will have a readyagenda instance queued up for a resourcepool
        to pull in and execute.  In a fully integrated system, our resourcepool would likely be a Kubernetes cluster.
        For this test, we will either finish the test confirming that our readyagenda queue has the expected readyagenda
        instance.

        We might also consider making a mock test resource pool that pulls the readyagenda instance from the queue and then
         pushes back an agendaprogress instance to its own queue.

         */

        Thread.sleep(6000); // give resourcepool checker time to check for readyagendas to put on the queue.

        MemoryQueueFactories queues = (MemoryQueueFactories) webApplicationContext.getBean(QUEUE_FACTORIES);
        MemoryQueueFactory<ReadyAgenda> readyAgendaItemQueue =  queues.getReadyAgendaMemoryQueueFactory();
        ItemQueue<ReadyAgenda> queue = readyAgendaItemQueue.createItemQueue("test queue");
        assertThat(queue).isNotNull();
        assertThat(queue.size().getMessage()).isEqualTo("1");

    }

    private boolean runAgenda(InMemoryPersistersFactory persisters) throws Exception, PersistenceException
    {
        System.out.println("Run Agenda");

        mockMvc.perform(post("/runAgenda").
                content(makeTestObject(runAgendaFilePath)).
                contentType(MediaType.APPLICATION_JSON).
                accept(MediaType.APPLICATION_JSON));

        /*
        For this "Run Agenda" step, three new endpoint types were created by the POP brain (with autogenerated ids):
        - Agenda,
        - ReadyAgenda, and
        - AgendaProgress

        Since we do not know the ids of these endpoints, we query for them by customer id, which we do know.
         */

        List<Query> queryList = new LinkedList<>();
        Query byCustomer = new ByCustomerId(CUSTOMER_ID);
        queryList.add(byCustomer);
        Agenda agenda = persisters.getAgendaPersister().retrieve(queryList).getAll().get(0);
        assertThat(agenda).isNotNull();

        ReadyAgenda readyAgenda = persisters.getReadyAgendaPersister().retrieve(queryList).getAll().get(0);
        assertThat(readyAgenda).isNotNull();

        AgendaProgress agendaProgress = persisters.getAgendaProgressPersister().retrieve(queryList).getAll().get(0);
        assertThat(agendaProgress).isNotNull();
        return true;
    }

    private boolean postInsight(InMemoryPersistersFactory persisters) throws Exception, PersistenceException
    {
        System.out.println("Post insight");

        mockMvc.perform(post("/postInsight").
                content(makeTestObject(insightFilePath)).
                contentType(MediaType.APPLICATION_JSON).
                accept(MediaType.APPLICATION_JSON));

        Insight insight = persisters.getInsightPersister().retrieve("testInsight");
        assertThat(insight).isNotNull();
        return true;
    }

    private boolean postResourcePool(InMemoryPersistersFactory persisters) throws Exception, PersistenceException
    {
        System.out.println("Post resource pool");

        mockMvc.perform(post("/postResourcePool").
                content(makeTestObject(resourcePoolFilePath)).
                contentType(MediaType.APPLICATION_JSON).
                accept(MediaType.APPLICATION_JSON));

        ResourcePool resourcePool = persisters.getResourcePoolPersister().retrieve("testResourcePoolId");
        assertThat(resourcePool).isNotNull();
        assertThat(resourcePool.getInsightIds()).contains(INSIGHT_ID);
        return true;
    }

    private boolean postCustomer(InMemoryPersistersFactory persisters) throws Exception, PersistenceException
    {
        System.out.println("Post customer");

        mockMvc.perform(post("/postCustomer").
                content(makeTestObject(customerFilePath)).
                contentType(MediaType.APPLICATION_JSON).
                accept(MediaType.APPLICATION_JSON));

        Customer customer = persisters.getCustomerPersister().retrieve("testCustomerId");

        assertThat(customer).isNotNull();
        assertThat(customer.getCustomerId()).isEqualTo(CUSTOMER_ID);
        return true;
    }

    private boolean postAgendaTemplate(InMemoryPersistersFactory persisters) throws Exception, PersistenceException
    {
        System.out.println("Post agenda template");

        mockMvc.perform(post("/postAgendaTemplate").
                content(makeTestObject(agendaTemplateFilePath)).
                contentType(MediaType.APPLICATION_JSON).
                accept(MediaType.APPLICATION_JSON));

        AgendaTemplate agendaTemplate = persisters.getAgendaTemplatePersister().retrieve("theAgendaTemplate");
        assertThat(agendaTemplate).isNotNull();
        assertThat(agendaTemplate.getAllowedCustomerIds()).contains(CUSTOMER_ID.toLowerCase(Locale.ROOT)); // note that there is also an undefined default customer id field.
        return true;
    }

    private String makeTestObject(Path objectFilePath) throws IOException
    {
        Stream<String> lines = Files.lines(objectFilePath, StandardCharsets.UTF_8);
        StringBuffer b = new StringBuffer();
        lines.forEach(line -> b.append(line));
        return b.toString();
    }

    private String makeTestAgendaRequest() throws IOException
    {
        Stream<String> lines = Files.lines(runAgendaFilePath, StandardCharsets.UTF_8);
        StringBuffer b = new StringBuffer();
        lines.forEach(line -> b.append(line));
        return b.toString();
    }

    private String makeTestAgenda() throws IOException
    {
        Stream<String> lines = Files.lines(agendaFilePath, StandardCharsets.UTF_8);
        StringBuffer b = new StringBuffer();
        lines.forEach(line -> b.append(line));
        return b.toString();
    }

}