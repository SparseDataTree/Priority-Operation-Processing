# In memory store.

Given that we can abstract everything, might we want to put all these enpoints into an enum?

See subclasses of DefaultEndpointDataObject.  We don't have everything yet.

Hm.  I think we need to look at the MemoryObjectPersister class; and ObjectPersister interface.  These appear to do what most of this is doing.

So, as much as possible, we want to work with what has already been made.

Maybe we don't need any of this.

In some ways, I like my approach better (esp separating out the persistence logic).  But I don't want to mess up the system.

Compare EndpointStore to ObjectPersister.   Let's see what we can do to reconcile those.



The point of this is illustration, not reinvention.

Of course, I could always build something from scratch.

Well?

I could go half way, and build a new brain, while keeping the kubernetes part of it.

Let's try that.


