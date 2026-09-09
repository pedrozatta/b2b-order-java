package br.com.zattaz.common;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public final class BasicAuthTestClientSupport {

    public static final String USERNAME = "admin";
    public static final String PASSWORD = "admin123";

    private BasicAuthTestClientSupport() {}

    public static ResultActions putJson(MockMvc mockMvc, String uri, String body) throws Exception {
        return mockMvc.perform(authenticated(put(uri).contentType(MediaType.APPLICATION_JSON).content(body)));
    }

    public static ResultActions getJson(MockMvc mockMvc, String uri) throws Exception {
        return mockMvc.perform(authenticated(get(uri).accept(MediaType.APPLICATION_JSON)));
    }

    public static ResultActions postEmpty(MockMvc mockMvc, String uri) throws Exception {
        return mockMvc.perform(authenticated(post(uri).accept(MediaType.APPLICATION_JSON)));
    }

    public static ResultActions deleteResource(MockMvc mockMvc, String uri) throws Exception {
        return mockMvc.perform(authenticated(delete(uri)));
    }

    private static MockHttpServletRequestBuilder authenticated(MockHttpServletRequestBuilder builder) {
        return builder.with(httpBasic(USERNAME, PASSWORD));
    }
}
