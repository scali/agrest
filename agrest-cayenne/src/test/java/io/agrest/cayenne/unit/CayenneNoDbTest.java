package io.agrest.cayenne.unit;

import io.agrest.NestedResourceEntity;
import io.agrest.ResourceEntity;
import io.agrest.RootResourceEntity;
import io.agrest.cayenne.compiler.CayenneEntityCompiler;
import io.agrest.cayenne.persister.ICayennePersister;
import io.agrest.meta.AgEntity;
import io.agrest.meta.compiler.AgEntityCompiler;
import io.agrest.meta.compiler.PojoEntityCompiler;
import io.agrest.meta.parser.IResourceParser;
import io.agrest.meta.parser.ResourceParser;
import io.agrest.runtime.meta.*;
import io.agrest.runtime.processor.select.SelectContext;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.DataSourceFactory;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.di.Module;
import org.apache.cayenne.map.ObjEntity;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * A superclass of Cayenne-aware test cases that do not need to access the DB, but need to work with EntityResolver
 * and higher levels of the stack.
 */
public abstract class CayenneNoDbTest {

    protected static ServerRuntime runtime;

    protected ICayennePersister mockCayennePersister;
    protected IMetadataService metadataService;
    protected IResourceMetadataService resourceMetadataService;
    protected IResourceParser resourceParser;

    @BeforeAll
    public static void setUpClass() {
        Module module = binder -> {
            DataSourceFactory dsf = mock(DataSourceFactory.class);
            binder.bind(DataSourceFactory.class).toInstance(dsf);
        };

        runtime = ServerRuntime
                .builder()
                .addConfig("cayenne-agrest-tests.xml")
                .addModule(module)
                .build();
    }

    @AfterAll
    public static void tearDownClass() {
        runtime.shutdown();
        runtime = null;
    }

    @BeforeEach
    public void initAgDataMap() {

        ObjectContext sharedContext = runtime.newContext();

        this.mockCayennePersister = mock(ICayennePersister.class);
        when(mockCayennePersister.entityResolver()).thenReturn(runtime.getChannel().getEntityResolver());
        when(mockCayennePersister.sharedContext()).thenReturn(sharedContext);
        when(mockCayennePersister.newContext()).thenReturn(runtime.newContext());

        this.metadataService = new MetadataService(createEntityCompilers());
        this.resourceParser = new ResourceParser(metadataService);
        this.resourceMetadataService = createResourceMetadataService();
    }

    protected List<AgEntityCompiler> createEntityCompilers() {

        AgEntityCompiler c1 = new CayenneEntityCompiler(
                mockCayennePersister,
                Collections.emptyMap());

        AgEntityCompiler c2 = new PojoEntityCompiler(Collections.emptyMap());

        return asList(c1, c2);
    }

    protected IResourceMetadataService createResourceMetadataService() {
        return new ResourceMetadataService(resourceParser, BaseUrlProvider.forUrl(Optional.empty()));
    }

    protected <T> SelectContext<T> prepareContext(MultivaluedMap<String, String> params, Class<T> type) {
        SelectContext<T> context = new SelectContext<>(type);

        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getQueryParameters()).thenReturn(params);

        context.setUriInfo(uriInfo);
        return context;
    }

    protected <T> AgEntity<T> getAgEntity(Class<T> type) {
        return metadataService.getAgEntity(type);
    }

    protected ObjEntity getEntity(Class<?> type) {
        return runtime.getChannel().getEntityResolver().getObjEntity(type);
    }

    protected <T> RootResourceEntity<T> getResourceEntity(Class<T> type) {
        return new RootResourceEntity<>(getAgEntity(type), null);
    }

    protected <T> NestedResourceEntity<T> getChildResourceEntity(Class<T> type, ResourceEntity<?> parent, String incoming) {
        return new NestedResourceEntity<>(getAgEntity(type), null, parent, parent.getAgEntity().getRelationship(incoming));
    }
}
