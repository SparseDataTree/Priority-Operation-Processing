package com.comcast.pop.service.brain;
import com.comcast.pop.service.brain.HomeResource;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
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
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ComponentScan(basePackages = "com.comcast.pop.service")
public class MonolithicApplicationTest
{

    Path agendaFilePath = Paths.get("src/main/resources/json/Agenda.json");

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

    @Autowired
    private HomeResource controller;

    @Test
    public void contextLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    public void testAgenda() throws Exception {

        System.out.println("Sanity");
//        mockMvc.perform(post("/postAgenda", makeTestAgenda()));
        mockMvc.perform(post("/postAgenda").
                content(makeTestAgenda()).
                contentType(MediaType.APPLICATION_JSON).
                accept(MediaType.APPLICATION_JSON));

        System.out.println("Post?");

        Thread.sleep(100L);

        mockMvc.perform(get("/agenda")).andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value("theAgenda"));
    }

    private String makeTestAgenda() throws IOException
    {
        Stream<String> lines = Files.lines(agendaFilePath, StandardCharsets.UTF_8);
        StringBuffer b = new StringBuffer();
        lines.forEach(line -> b.append(line));
        return b.toString();
    }

}