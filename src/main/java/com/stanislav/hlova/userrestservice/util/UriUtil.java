package com.stanislav.hlova.userrestservice.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class UriUtil {
    @Value("${server.port}")
    private int port;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Value("${server.scheme}")
    private String defaultScheme;
    @Value("${server.host}")
    private String host;

    public URI createBaseUri(String path) {
        return URI.create(String.format("%s://%s:%d%s%s", defaultScheme, host, port, contextPath, path));
    }
}
