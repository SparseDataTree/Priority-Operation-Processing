package com.comcast.pop.service.stream;

import java.util.HashMap;
import java.util.Map;

/*
Thinking about what else we may want here.  Do we want immutable context instances?
Maybe for later.  For now, we are working to build a set of code that can be modified for various demos.
 */
public class MutableContext implements Context
{
    private Map<String, Object> context = new HashMap<>();
    private static final String keyTemplate = "Context does not have key: %s";
    private static final ErrorResponse noKeyFound = new ErrorResponse(keyTemplate);
    private static final String classTemplate = "Context Object type for key %s does not match class: %s";
    private static final ErrorResponse classMismatch = new ErrorResponse(classTemplate);

    public void put(String key, Object value)
    {
        context.put(key, value);
    }

    @Override
    public Object get(String key, Class objectClass)
    {
        if(!context.keySet().contains(key))
        {
            return noKeyFound.create(key);
        }
        Object value = context.get(key);
        if(!value.getClass().isAssignableFrom(objectClass))
        {
            return classMismatch.create(key, value.getClass().getName());
        }
        return value;
    }
}
class ErrorResponse implements Message
{
    private final String s;

    public ErrorResponse(String s)
    {
        this.s = s;
    }

    public ErrorResponse create(String... v)
    {
        ErrorResponse response = new ErrorResponse(String.format(s,v));
        return response;
    }

    public String getMessage()
    {
        return s;
    }
}
