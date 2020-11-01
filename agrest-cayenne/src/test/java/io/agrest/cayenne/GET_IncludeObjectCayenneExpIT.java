package io.agrest.cayenne;

import io.agrest.Ag;
import io.agrest.DataResponse;
import io.agrest.cayenne.cayenne.main.E2;
import io.agrest.cayenne.cayenne.main.E3;
import io.agrest.cayenne.cayenne.main.E4;
import io.agrest.cayenne.cayenne.main.E5;
import io.agrest.cayenne.unit.AgCayenneTester;
import io.agrest.cayenne.unit.DbTest;
import io.bootique.junit5.BQTestTool;
import org.junit.jupiter.api.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

@Deprecated
public class GET_IncludeObjectCayenneExpIT extends DbTest {

    @BQTestTool
    static final AgCayenneTester tester = tester(Resource.class)
            .entitiesAndDependencies(E2.class, E3.class, E4.class, E5.class)
            .build();

    @Test
    public void testMapBy_ToMany_WithCayenneExp() {

        // see LF-294 - filter applied too late may cause a AgException

        tester.e2().insertColumns("id_", "name").values(1, "xxx").exec();
        tester.e3().insertColumns("id_", "name", "e2_id")
                .values(8, "aaa", 1)
                .values(9, "zzz", 1)
                .values(7, "aaa", 1)
                .values(6, null, 1).exec();

        tester.target("/e2")
                .queryParam("include", "{\"path\":\"e3s\",\"mapBy\":\"name\", \"cayenneExp\":{\"exp\":\"name != NULL\"}}")
                .queryParam("include", "id")
                .get().wasOk()
                .bodyEquals(1, "{\"id\":1,\"e3s\":{"
                        + "\"aaa\":[{\"id\":7,\"name\":\"aaa\",\"phoneNumber\":null},{\"id\":8,\"name\":\"aaa\",\"phoneNumber\":null}],"
                        + "\"zzz\":[{\"id\":9,\"name\":\"zzz\",\"phoneNumber\":null}]}}");
    }

    @Test
    public void testToMany_CayenneExp() {

        tester.e2().insertColumns("id_", "name").values(1, "xxx").exec();

        tester.e3().insertColumns("id_", "name", "e2_id")
                .values(8, "a", 1)
                .values(9, "z", 1)
                .values(7, "a", 1).exec();

        tester.target("/e2")
                .queryParam("include", "{\"path\":\"e3s\",\"cayenneExp\":{\"exp\":\"name = $n\", \"params\":{\"n\":\"a\"}}}")
                .queryParam("include", "id")
                .get().wasOk()
                .bodyEquals(1, "{\"id\":1,\"e3s\":["
                        + "{\"id\":7,\"name\":\"a\",\"phoneNumber\":null},"
                        + "{\"id\":8,\"name\":\"a\",\"phoneNumber\":null}]}");
    }

    @Test
    public void testToMany_CayenneExpById() {

        tester.e5().insertColumns("id", "name")
                .values(545, "B")
                .values(546, "A").exec();

        tester.e2().insertColumns("id_", "name").values(51, "xxx").exec();

        tester.e3().insertColumns("id_", "name", "e2_id", "e5_id")
                .values(58, "s", 51, 545)
                .values(59, "z", 51, 545)
                .values(57, "b", 51, 546).exec();

        tester.target("/e2")
                .queryParam("include", "{\"path\":\"e3s\",\"cayenneExp\":{\"exp\":\"e5 = $id\", \"params\":{\"id\":546}}}")
                .queryParam("include", "id")
                .get().wasOk()
                .bodyEquals(1, "{\"id\":51,\"e3s\":[{\"id\":57,\"name\":\"b\",\"phoneNumber\":null}]}");
    }

    @Path("")
    public static class Resource {

        @Context
        private Configuration config;

        @GET
        @Path("e2")
        public DataResponse<E2> getE2(@Context UriInfo uriInfo) {
            return Ag.service(config).select(E2.class).uri(uriInfo).get();
        }
    }
}
