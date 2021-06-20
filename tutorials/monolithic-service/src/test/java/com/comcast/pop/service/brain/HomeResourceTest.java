package com.comcast.pop.service.brain;


import com.comcast.pop.api.Agenda;
import com.comcast.pop.service.TestUtil;
import com.comcast.pop.service.brain.HomeResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class HomeResourceTest
{

    HomeResource hr;

    @BeforeEach
    public void init()
    {
        hr = new HomeResource();
    }

    @Test
    public void testHome()
    {
        String homeMessage = hr.home();
        assertThat(homeMessage).isEqualTo("<h1>Welcome</h1>");
    }

    @Test
    public void testPostAgenda() throws IOException // todo this tests limited functionality; will break, when that is changed
    {
        hr.postAgenda(TestUtil.makeTestAgenda());

        Agenda agenda = hr.agenda();
        System.out.println(agenda.getId());

        assertThat(agenda).isNotNull();
    }
}