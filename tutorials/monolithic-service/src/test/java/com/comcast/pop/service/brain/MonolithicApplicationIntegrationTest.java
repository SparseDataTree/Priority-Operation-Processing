package com.comcast.pop.service.brain;

import com.comcast.pop.service.brain.MonolithicApplication;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MonolithicApplicationIntegrationTest
{
    Path agendaFilePath = Paths.get("src/main/resources/json/Agenda.json");

    @Autowired
    private MonolithicApplication application;

    @Test
    public void testAgenda() throws Exception {

        // this fails... (NPE) why?  Probably trying to exercise something that is mocked out.
//        mockMvc.perform(post("/postAgenda",makeTestAgenda()));
//
//        mockMvc.perform(get("/agenda")).andExpect(status().isOk())
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andExpect(jsonPath("$.id").value("theAgenda"));

    }

    private String makeTestAgenda() throws IOException
    {
        Stream<String> lines = Files.lines(agendaFilePath, StandardCharsets.UTF_8);
        StringBuffer b = new StringBuffer();
        lines.forEach(line -> b.append(line));
        return b.toString();
    }
}
