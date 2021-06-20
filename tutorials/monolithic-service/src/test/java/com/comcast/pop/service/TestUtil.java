package com.comcast.pop.service;

import com.comcast.pop.api.Agenda;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class TestUtil
{

    private static ObjectMapper mapper = new ObjectMapper();

    private static Path agendaFilePath = Paths.get("src/main/resources/json/Agenda.json");

    public static String makeTestAgendaText() throws IOException
    {
        Stream<String> lines = Files.lines(agendaFilePath, StandardCharsets.UTF_8);
        StringBuffer b = new StringBuffer();
        lines.forEach(line -> b.append(line));
        return b.toString();
    }

    public static Agenda makeTestAgenda() throws IOException
    {
        return deserializeAgenda(makeTestAgendaText());
    }


    public static Agenda deserializeAgenda(String agendaText) throws JsonProcessingException
    {
        return mapper.readValue(agendaText, Agenda.class);
    }

    public static String asJsonString(final Object obj) {
        try {
            final String jsonContent = mapper.writeValueAsString(obj);
            return jsonContent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
