package com.comcast.pop.service.queues;

import com.comcast.pop.modules.queue.api.ItemQueue;
import com.comcast.pop.modules.queue.api.ItemQueueFactory;
/*
 A very simple in-memory queuing class; mainly intended for illustration.  We would expand this to include a map of queues.
 We could consider making a queuefactory to support other technologies, like Kafka.
 */
public class MemoryQueueFactory<T> implements ItemQueueFactory<T>
{
    private ItemQueue<T> queue = new MemoryQueue<>();
    @Override
    public ItemQueue<T> createItemQueue(String name)
    {
        return queue;
    }
}
