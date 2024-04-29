package com.stanislav.hlova.userrestservice.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UriUtilTest {
    private final UriUtil uriUtil = new UriUtil();

    private static final int PORT = 9090;
    private static final String CONTEXT_PATH = "/application/path";
    private static final String DEFAULT_SCHEME = "http";
    private static final String HOST = "localhost";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(uriUtil, "port", PORT);
        ReflectionTestUtils.setField(uriUtil, "contextPath", CONTEXT_PATH);
        ReflectionTestUtils.setField(uriUtil, "defaultScheme", DEFAULT_SCHEME);
        ReflectionTestUtils.setField(uriUtil, "host", HOST);
    }

    @Test
    void shouldReturnCorrectUri() throws URISyntaxException {
        URI expectedUri = new URI("http://localhost:9090/application/path/test/path/2");

        URI actualUri = uriUtil.createBaseUri("/test/path/2");

        assertEquals(expectedUri, actualUri);
    }
}