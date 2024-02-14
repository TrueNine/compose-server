package net.yan100.compose.core.http;

import java.util.List;

public interface Methods {
    String GET = "GET";
    String POST = "POST";
    String PUT = "PUT";
    String DELETE = "DELETE";
    String OPTIONS = "OPTIONS";
    String PATCH = "PATCH";
    String HEAD = "HEAD";
    String TRACE = "TRACE";

    static String[] all() {
        return List.of(GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD, TRACE).toArray(String[]::new);
    }
}
