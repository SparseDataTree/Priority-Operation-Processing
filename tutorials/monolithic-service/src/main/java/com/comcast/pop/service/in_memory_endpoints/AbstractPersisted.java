package com.comcast.pop.service.in_memory_endpoints;

import java.util.Date;

public class AbstractPersisted<T> implements Persisted
{
    Date addedTime;
    Date updatedTime;
    T t;

    public AbstractPersisted(T t) // todo consider making a factory
    {
        this.t = t;
        addedTime = new Date(System.currentTimeMillis());
        updatedTime = addedTime;
    }

    T get()
    {
        return t;
    }

    void put(T t)
    {
        this.t = t;
        updatedTime = new Date(System.currentTimeMillis());
    }

    @Override
    public Date getAddedTime()
    {
        return addedTime;
    }

    @Override
    public Date getUpdatedTime()
    {
        return updatedTime;
    }
}
