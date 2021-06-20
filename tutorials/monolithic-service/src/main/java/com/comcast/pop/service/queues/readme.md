# POP queues have been configured in AWS.  

So we will roll our own.

What APIs do we have to follow?

We know that they have string ids and that they can tell their size.

Presumably we can be fairly standard about adding and removing items.

At the least, we can have a FIFO queue.

POP also has a (by customer) round robin queue, but we can allow for more control.

We also know that it has to have a certain interface for a generic scheduler to handle.  What do we get from that?

Take a look at AgendaScheduler.  This has some helpful information.

See it's implementations.  See FIFOAgendaScheduler and also ObjectPersister.

See also MemoryObjectPersister.

In case we want to expand, look at the AgendaSchedulerFactory.  It is perhaps not
the best architecture for expansion, but it is a start.

See also the QueueMonitor.

Of particular interest is the ItemQueueFactory.  Presently, this is only wired up to AWS.






