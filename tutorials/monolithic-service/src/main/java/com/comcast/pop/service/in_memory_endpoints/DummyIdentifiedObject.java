package com.comcast.pop.service.in_memory_endpoints;

import com.comcast.pop.object.api.IdentifiedObject;

public class DummyIdentifiedObject implements IdentifiedObject
{
    @Override
    public String getId()
    {
        return "";
    }

    @Override
    public void setId(String id)
    {

    }

    @Override
    public String getCustomerId()
    {
        return "";
    }

    @Override
    public void setCustomerId(String customerId)
    {

    }
}
