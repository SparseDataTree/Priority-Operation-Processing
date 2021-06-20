package com.comcast.pop.service.brain;

import com.comcast.pop.api.Agenda;
import com.comcast.pop.service.in_memory_endpoints.InMemoryPersisters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

//@RestController
//@ComponentScan(basePackages = "com.comcast.pop.service")
public class HomeResource
{
    // todo gut this; and expose the POP endpoints.  These include direct
    // todo ... interaction will all endpoints, as well as two additional APIs;
    // todo ... one to create agendas from a template and a payload; and
    // todo ... a final one for tracking agenda progress

    // todo correction.. well, also endpoints to rerun an agenda; and maybe more?  But we will not
    // todo ... be concerned about that presently

    InMemoryPersisters persisters = InMemoryPersisters.getPersisters();

    private Agenda agenda;

    @GetMapping("/")
    public String home()
    {
        return("<h1>Welcome</h1>");
    }

    @GetMapping("/agenda")
    public String agenda()
    {
        return("<h1>" + agenda.getTitle()+"</h1>");
    }
    /*
    For example:
    $ curl -X POST localhost:8080/postAgenda -H 'Content-type:application/json' -d '{"title": "my agenda"}'
     */
    @PostMapping("/postAgenda")
    public void postAgenda(@RequestBody Agenda newAgenda)
    {
        this.agenda = newAgenda;
    }
}
