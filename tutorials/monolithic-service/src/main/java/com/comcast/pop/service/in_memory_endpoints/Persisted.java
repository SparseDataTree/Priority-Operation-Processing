package com.comcast.pop.service.in_memory_endpoints;

import java.util.Date;

// We see these values in DefaultEndpointDataObject.  But I think I prefer to break this out into an interface.
// An argument can be made that the persistence attributes of an endpoint are a separate concern from the endpoint itself.
public interface Persisted
{
    Date getAddedTime();
    Date getUpdatedTime();
     // Note that we have not exposed setters.  I'm thinking that these should not be exposed.
}
