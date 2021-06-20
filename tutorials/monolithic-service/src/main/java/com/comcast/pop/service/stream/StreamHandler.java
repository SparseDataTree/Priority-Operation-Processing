package com.comcast.pop.service.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface StreamHandler
{
    void handleRequest(InputStream var1, OutputStream var2, Context var3) throws IOException;
}
