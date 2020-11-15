package io.agrest.openapi;

import io.agrest.DataResponse;
import org.example.entity.NonAgP1;
import io.agrest.openapi.unit.OpenAPIBuilder;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.junit.jupiter.api.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParamsTest {

    static final OpenAPI oapi = new OpenAPIBuilder()
            .addClass(Resource.class)
            .addPackage(NonAgP1.class)
            .build();

    @Test
    public void testUriInfoParameters() {
        PathItem pi = oapi.getPaths().get("/r/uri-info");
        Operation get = pi.getGet();

        assertEquals(9, get.getParameters().size());
        Map<String, Parameter> paramsMap = new HashMap<>();
        get.getParameters().forEach(p -> paramsMap.put(p.getName(), p));

        assertEquals(
                new HashSet<>(asList("cayenneExp", "dir", "exclude", "exp", "include", "limit", "mapBy", "sort", "start")),
                paramsMap.keySet());

        // TODO assert the actual parameters
    }

    @Test
    public void testExplicitParameters() {
        PathItem pi = oapi.getPaths().get("/r/explicit-params");
        Operation get = pi.getGet();

        assertEquals(2, get.getParameters().size());
        Map<String, Parameter> paramsMap = new HashMap<>();
        get.getParameters().forEach(p -> paramsMap.put(p.getName(), p));

        assertEquals(
                new HashSet<>(asList("exp", "sort")),
                paramsMap.keySet());

        // TODO assert the actual parameters
    }

    @Test
    public void testMixedParameters() {
        PathItem pi = oapi.getPaths().get("/r/mixed-params");
        Operation get = pi.getGet();

        assertEquals(10, get.getParameters().size());
        Map<String, Parameter> paramsMap = new HashMap<>();
        get.getParameters().forEach(p -> paramsMap.put(p.getName(), p));

        // TODO: Agrest should define the names enum publicly somewhere
        assertEquals(
                new HashSet<>(asList("x", "cayenneExp", "dir", "exclude", "exp", "include", "limit", "mapBy", "sort", "start")),
                paramsMap.keySet());

        // TODO assert the actual parameters
    }

    @Path("r")
    public static class Resource {

        @Context
        private Configuration config;

        @GET
        @Path("uri-info")
        public DataResponse<NonAgP1> uriInfo(@Context UriInfo uriInfo) {
            throw new UnsupportedOperationException("endpoint logic is irrelevant for the test");
        }

        @GET
        @Path("explicit-params")
        public DataResponse<NonAgP1> explicitParams(@QueryParam("exp") String exp, @QueryParam("sort") String sort) {
            throw new UnsupportedOperationException("endpoint logic is irrelevant for the test");
        }

        @GET
        @Path("mixed-params")
        public DataResponse<NonAgP1> mixedParams(@QueryParam("x") String x, @Context UriInfo uriInfo) {
            throw new UnsupportedOperationException("endpoint logic is irrelevant for the test");
        }
    }
}
