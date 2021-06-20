package com.comcast.pop.service.in_memory_endpoints;

public class EndpointUtil
{
    public static AgendaStore agendaStore = new AgendaStore();
    public static AgendaProgressStore agendaProgressStore = new AgendaProgressStore();
    public static CustomerStore customerStore = new CustomerStore();
    public static InsightStore insightStore = new InsightStore();
    public static ResourcePoolStore resourcePoolStore = new ResourcePoolStore();
    public static InsightScheduleInfoStore insightScheduleInfoStore = new InsightScheduleInfoStore();

    // todo what about the various queues?
}
