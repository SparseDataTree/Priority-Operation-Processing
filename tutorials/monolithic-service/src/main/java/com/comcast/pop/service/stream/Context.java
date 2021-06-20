package com.comcast.pop.service.stream;

public interface Context
{
    Object get(String key, Class objectClass);
}
