package io.agrest.swagger.api.v1.service;

import io.agrest.cayenne.cayenne.main.E5;

import io.agrest.AgRequest;
import io.agrest.DataResponse;

import java.util.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import io.agrest.Ag;
import io.agrest.SimpleResponse;

@Path("/")
public class E5Resource {

    @Context
    private Configuration config;

    @POST
    @Path("/v1/e5")
    @Consumes({ "application/json" })
    public DataResponse<E5> createE5(String E5) {

        AgRequest agRequest = Ag.request(config)
                .build();

        return Ag.create(E5.class, config)
                 .request(agRequest)
                 .syncAndSelect(E5);
    }

    @GET
    @Path("/v1/e5")
    @Produces({ "application/json" })
    public DataResponse<E5> getAllE5() {

        AgRequest agRequest = Ag.request(config)
                .build();

        return Ag.select(E5.class, config)
                 .request(agRequest)
                 .get();
    }

}
