package com.comcast.pop.service.queues;

import com.comcast.pop.api.Agenda;
import com.comcast.pop.modules.queue.api.ItemQueue;
import com.comcast.pop.modules.queue.api.QueueResult;

import java.util.*;

// todo do we care about asynchronous access?  If so, then we will need to adjust things here.
public class MemoryQueue<T> implements ItemQueue<T>
{
    QueueResult<T> noDataResult = new QueueResult<>(true, Collections.EMPTY_LIST, "one item added");

    Queue<T> queue = new LinkedList<>(); // todo consider if this has the functionality we need.
    @Override
    public QueueResult<T> add(T item)
    {
        queue.add(item);
        return noDataResult;
    }

    @Override
    public QueueResult<T> add(Collection<T> items)
    {
        queue.addAll(items);
        return new QueueResult<>(true, Collections.EMPTY_LIST, String.format("%d items added", items.size()));
    }

    @Override
    public QueueResult<T> peek()
    {
        T item = queue.peek();
        return new QueueResult<>(true, Collections.singletonList(item), "");
    }

    @Override
    public QueueResult<T> poll()
    {
        T item = queue.poll();
        return new QueueResult<>(true, Collections.singletonList(item), "");
    }

    @Override
    public QueueResult<T> poll(int maxPollCount)
    {
        List<T> items = new LinkedList<>();
        while(queue.size() > 0 && maxPollCount > 0)
        {
            items.add(queue.poll());
            maxPollCount--;
        }
        return new QueueResult<>(true, items, "");
    }

    @Override
    public QueueResult<T> size()
    {
        return new QueueResult<>(true, null, String.format("%d",queue.size()));
    }
}
