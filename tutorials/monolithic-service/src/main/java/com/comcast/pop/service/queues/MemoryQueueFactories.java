package com.comcast.pop.service.queues;

import com.comcast.pop.api.progress.AgendaProgress;
import com.comcast.pop.scheduling.api.ReadyAgenda;

/*
    This class is aimed at demo'ing and experimenting with the POP framework.  It is a bit light in that
    the queue factories only reference a single queue.

    If it is interesting, the functionality to create and retrieve queues of specific names could be supported

    It would also be interesting to integrate with queues such as in Kafka, etc.
 */
public class MemoryQueueFactories
{
    private static MemoryQueueFactories memoryQueueFactories = new MemoryQueueFactories();

    private MemoryQueueFactory<ReadyAgenda> readyAgendaMemoryQueueFactory = new MemoryQueueFactory<>();
    private MemoryQueueFactory<AgendaProgress> agendaProgressMemoryQueueFactory = new MemoryQueueFactory<>();

    private MemoryQueueFactories()
    {
    }

    public MemoryQueueFactory<ReadyAgenda> getReadyAgendaMemoryQueueFactory()
    {
        return readyAgendaMemoryQueueFactory;
    }

    public MemoryQueueFactory<AgendaProgress> getAgendaProgressMemoryQueueFactory()
    {
        return agendaProgressMemoryQueueFactory;
    }

    public static MemoryQueueFactories getMemoryQueueFactories()
    {
        return memoryQueueFactories;
    }

}
