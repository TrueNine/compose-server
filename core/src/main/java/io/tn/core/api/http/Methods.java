package io.tn.core.api.http;

import java.util.List;

public class Methods {
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    public static final String OPTIONS = "OPTIONS";
    public static final String PATCH = "PATCH";
    public static final String HEAD = "HEAD";
    public static final String TRACE = "TRACE";

    public static String[] all() {
        return List.of(GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD, TRACE).toArray(String[]::new);
    }
}
