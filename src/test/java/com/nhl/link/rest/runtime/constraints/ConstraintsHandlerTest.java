package com.nhl.link.rest.runtime.constraints;

import static com.nhl.link.rest.constraints.ConstraintsBuilder.excludeAll;
import static org.apache.cayenne.exp.ExpressionFactory.exp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.nhl.link.rest.DataResponse;
import com.nhl.link.rest.EntityConstraint;
import com.nhl.link.rest.ResourceEntity;
import com.nhl.link.rest.SizeConstraints;
import com.nhl.link.rest.constraints.ConstraintsBuilder;
import com.nhl.link.rest.it.fixture.cayenne.E1;
import com.nhl.link.rest.it.fixture.cayenne.E2;
import com.nhl.link.rest.it.fixture.cayenne.E3;
import com.nhl.link.rest.it.fixture.cayenne.E4;
import com.nhl.link.rest.it.fixture.cayenne.E5;
import com.nhl.link.rest.meta.DefaultLrAttribute;
import com.nhl.link.rest.meta.LrEntity;
import com.nhl.link.rest.meta.LrRelationship;
import com.nhl.link.rest.runtime.meta.IMetadataService;

public class ConstraintsHandlerTest {

	private ConstraintsHandler constraintHandler;

	private LrEntity<E1> lre0;
	private LrEntity<E2> lre1;
	private LrEntity<E3> lre2;
	private LrEntity<E4> lre3;
	private LrEntity<E5> lre4;


	@SuppressWarnings("unchecked")
	@Before
	public void before() {

		lre0 = mock(LrEntity.class);
		when(lre0.getType()).thenReturn(E1.class);
		LrRelationship r1 = mock(LrRelationship.class);
		when(lre0.getRelationship("r1")).thenReturn(r1);
		when(r1.getName()).thenReturn("r1");
		when(r1.getTargetEntityType()).then(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				return E2.class;
			}
		});

		LrRelationship r2 = mock(LrRelationship.class);
		when(lre0.getRelationship("r2")).thenReturn(r2);
		when(r2.getName()).thenReturn("r2");
		when(r2.getTargetEntityType()).then(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				return E4.class;
			}
		});

		lre1 = mock(LrEntity.class);
		when(lre1.getType()).thenReturn(E2.class);

		LrRelationship r11 = mock(LrRelationship.class);
		when(lre1.getRelationship("r11")).thenReturn(r11);
		when(r11.getName()).thenReturn("r11");
		when(r11.getTargetEntityType()).then(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				return E3.class;
			}
		});

		lre2 = mock(LrEntity.class);
		when(lre2.getType()).thenReturn(E3.class);

		lre3 = mock(LrEntity.class);
		when(lre3.getType()).thenReturn(E4.class);

		lre4 = mock(LrEntity.class);
		when(lre4.getType()).thenReturn(E5.class);

		IMetadataService mockMDService = mock(IMetadataService.class);
		when(mockMDService.getLrEntity(E1.class)).thenReturn(lre0);
		when(mockMDService.getLrEntity(E2.class)).thenReturn(lre1);
		when(mockMDService.getLrEntity(E3.class)).thenReturn(lre2);
		when(mockMDService.getLrEntity(E4.class)).thenReturn(lre3);
		when(mockMDService.getLrEntity(E5.class)).thenReturn(lre4);

		List<EntityConstraint> r = Collections.emptyList();
		List<EntityConstraint> w = Collections.emptyList();
		this.constraintHandler = new ConstraintsHandler(r, w, mockMDService);
	}

	@Test
	public void testApply_FetchOffset() {

		SizeConstraints s1 = new SizeConstraints().fetchOffset(5);
		SizeConstraints s2 = new SizeConstraints().fetchOffset(0);

		DataResponse<?> t1 = DataResponse.forType(Object.class).withFetchOffset(0);
		constraintHandler.constrainResponse(t1, s1, null);
		assertEquals(0, t1.getFetchOffset());
		assertEquals(5, s1.getFetchOffset());

		DataResponse<?> t2 = DataResponse.forType(Object.class).withFetchOffset(3);
		constraintHandler.constrainResponse(t2, s1, null);
		assertEquals(3, t2.getFetchOffset());
		assertEquals(5, s1.getFetchOffset());

		DataResponse<?> t3 = DataResponse.forType(Object.class).withFetchOffset(6);
		constraintHandler.constrainResponse(t3, s1, null);
		assertEquals(5, t3.getFetchOffset());
		assertEquals(5, s1.getFetchOffset());

		DataResponse<?> t4 = DataResponse.forType(Object.class).withFetchOffset(6);
		constraintHandler.constrainResponse(t4, s2, null);
		assertEquals(6, t4.getFetchOffset());
		assertEquals(0, s2.getFetchOffset());
	}

	@Test
	public void testApply_FetchLimit() {

		SizeConstraints s1 = new SizeConstraints().fetchLimit(5);
		SizeConstraints s2 = new SizeConstraints().fetchLimit(0);

		DataResponse<?> t1 = DataResponse.forType(Object.class).withFetchLimit(0);
		constraintHandler.constrainResponse(t1, s1, null);
		assertEquals(0, t1.getFetchLimit());
		assertEquals(5, s1.getFetchLimit());

		DataResponse<?> t2 = DataResponse.forType(Object.class).withFetchLimit(3);
		constraintHandler.constrainResponse(t2, s1, null);
		assertEquals(3, t2.getFetchLimit());
		assertEquals(5, s1.getFetchLimit());

		DataResponse<?> t3 = DataResponse.forType(Object.class).withFetchLimit(6);
		constraintHandler.constrainResponse(t3, s1, null);
		assertEquals(5, t3.getFetchLimit());
		assertEquals(5, s1.getFetchLimit());

		DataResponse<?> t4 = DataResponse.forType(Object.class).withFetchLimit(6);
		constraintHandler.constrainResponse(t4, s2, null);
		assertEquals(6, t4.getFetchLimit());
		assertEquals(0, s2.getFetchLimit());
	}

	@Test
	public void testApply_ResourceEntity_NoTargetRel() {

		ConstraintsBuilder<E1> tc1 = ConstraintsBuilder.excludeAll(E1.class).attributes("a", "b");

		ResourceEntity<E1> te1 = new ResourceEntity<>(lre0);
		appendAttribute(te1, "c");
		appendAttribute(te1, "b");

		ResourceEntity<?> te11 = new ResourceEntity<>(lre2);
		appendAttribute(te11, "a1");
		appendAttribute(te11, "b1");
		te1.getChildren().put("d", te11);

		DataResponse<E1> t1 = DataResponse.forType(E1.class).resourceEntity(te1);

		constraintHandler.constrainResponse(t1, null, tc1);
		assertEquals(1, t1.getEntity().getAttributes().size());
		assertTrue(t1.getEntity().getAttributes().containsKey("b"));
		assertTrue(t1.getEntity().getChildren().isEmpty());
	}

	@Test
	public void testApply_ResourceEntity_TargetRel() {

		ConstraintsBuilder<E1> tc1 = ConstraintsBuilder.excludeAll(E1.class).attributes("a", "b")
				.path("r1", ConstraintsBuilder.excludeAll(E2.class).attributes("n", "m"))
				.path("r1.r11", ConstraintsBuilder.excludeAll(E3.class).attributes("p", "r"))
				.path("r2", ConstraintsBuilder.excludeAll(E4.class).attributes("k", "l"));

		ResourceEntity<E1> te1 = new ResourceEntity<>(lre0);
		appendAttribute(te1, "c");
		appendAttribute(te1, "b");

		ResourceEntity<?> te11 = new ResourceEntity<>(lre1);
		appendAttribute(te11, "m");
		appendAttribute(te11, "z");
		te1.getChildren().put("r1", te11);

		ResourceEntity<?> te21 = new ResourceEntity<>(lre4);
		appendAttribute(te21, "p");
		appendAttribute(te21, "z");
		te1.getChildren().put("r3", te21);

		DataResponse<E1> t1 = DataResponse.forType(E1.class).resourceEntity(te1);

		constraintHandler.constrainResponse(t1, null, tc1);
		assertEquals(1, t1.getEntity().getAttributes().size());
		assertTrue(t1.getEntity().getAttributes().containsKey("b"));
		assertEquals(1, t1.getEntity().getChildren().size());

		ResourceEntity<?> mergedTe11 = t1.getEntity().getChildren().get("r1");
		assertNotNull(mergedTe11);
		assertTrue(mergedTe11.getChildren().isEmpty());
		assertEquals(1, mergedTe11.getAttributes().size());
		assertTrue(mergedTe11.getAttributes().containsKey("m"));
	}

	@Test
	public void testMerge_ResourceEntity_Id() {

		ConstraintsBuilder<E1> tc1 = ConstraintsBuilder.excludeAll(E1.class).excludeId();
		ConstraintsBuilder<E1> tc2 = ConstraintsBuilder.excludeAll(E1.class).includeId();

		ResourceEntity<E1> te1 = new ResourceEntity<>(lre0);
		te1.includeId();
		DataResponse<E1> t1 = DataResponse.forType(E1.class).resourceEntity(te1);
		constraintHandler.constrainResponse(t1, null, tc1);
		assertFalse(t1.getEntity().isIdIncluded());

		ResourceEntity<E1> te2 = new ResourceEntity<>(lre0);
		te2.includeId();
		DataResponse<E1> t2 = DataResponse.forType(E1.class).resourceEntity(te2);
		constraintHandler.constrainResponse(t2, null, tc2);
		assertTrue(t2.getEntity().isIdIncluded());

		ResourceEntity<E1> te3 = new ResourceEntity<>(lre0);
		te3.excludeId();
		DataResponse<E1> t3 = DataResponse.forType(E1.class).resourceEntity(te3);
		constraintHandler.constrainResponse(t3, null, tc2);
		assertFalse(t3.getEntity().isIdIncluded());
	}

	@Test
	public void testMerge_CayenneExp() {

		Expression q1 = exp("a = 5");

		ConstraintsBuilder<E1> tc1 = ConstraintsBuilder.excludeAll(E1.class).and(q1);

		ResourceEntity<E1> te1 = new ResourceEntity<>(lre0);
		DataResponse<E1> t1 = DataResponse.forType(E1.class).resourceEntity(te1);
		constraintHandler.constrainResponse(t1, null, tc1);
		assertEquals(exp("a = 5"), t1.getEntity().getQualifier());

		ResourceEntity<E1> te2 = new ResourceEntity<>(lre0);
		te2.andQualifier(exp("b = 'd'"));
		DataResponse<E1> t2 = DataResponse.forType(E1.class).resourceEntity(te2);
		constraintHandler.constrainResponse(t2, null, tc1);
		assertEquals(exp("b = 'd' and a = 5"), t2.getEntity().getQualifier());
	}

	@Test
	public void testMerge_MapBy() {

		ConstraintsBuilder<E1> tc1 = excludeAll(E1.class).path("r1",
				ConstraintsBuilder.excludeAll(E2.class).attribute("a"));

		ResourceEntity<E1> te1MapByTarget = new ResourceEntity<>(lre0);
		appendAttribute(te1MapByTarget, "b");

		ResourceEntity<E2> te1MapBy = new ResourceEntity<>(lre1);
		te1MapBy.getChildren().put("r1", te1MapByTarget);

		ResourceEntity<E1> te1 = new ResourceEntity<>(lre0);
		te1.mapBy(te1MapBy, "r1.b");

		DataResponse<E1> t1 = DataResponse.forType(E1.class).resourceEntity(te1);
		constraintHandler.constrainResponse(t1, null, tc1);
		assertNull(t1.getEntity().getMapBy());
		assertNull(t1.getEntity().getMapByPath());

		ResourceEntity<E2> te2MapByTarget = new ResourceEntity<>(lre1);
		appendAttribute(te2MapByTarget, "a");

		ResourceEntity<E1> te2MapBy = new ResourceEntity<>(lre0);
		te1MapBy.getChildren().put("r1", te2MapByTarget);

		ResourceEntity<E1> te2 = new ResourceEntity<>(lre0);
		te2.mapBy(te2MapBy, "r1.a");

		DataResponse<E1> t2 = DataResponse.forType(E1.class).resourceEntity(te2);
		constraintHandler.constrainResponse(t2, null, tc1);
		assertSame(te2MapBy, t2.getEntity().getMapBy());
		assertEquals("r1.a", t2.getEntity().getMapByPath());
	}

	@Test
	public void testMerge_MapById_Exclude() {

		ConstraintsBuilder<E1> tc1 = excludeAll(E1.class).path("r1", excludeAll(E2.class).excludeId());

		ResourceEntity<E1> te1MapByTarget = new ResourceEntity<>(lre0);
		te1MapByTarget.includeId();

		ResourceEntity<E2> te1MapBy = new ResourceEntity<>(lre1);
		te1MapBy.getChildren().put("r1", te1MapByTarget);

		ResourceEntity<E1> te1 = new ResourceEntity<>(lre0);
		te1.mapBy(te1MapBy, "r1");

		DataResponse<E1> t1 = DataResponse.forType(E1.class).resourceEntity(te1);
		constraintHandler.constrainResponse(t1, null, tc1);
		assertNull(t1.getEntity().getMapBy());
		assertNull(t1.getEntity().getMapByPath());

	}

	@Test
	public void testMerge_MapById_Include() {

		ConstraintsBuilder<E1> tc1 = excludeAll(E1.class).path("r1", excludeAll(E2.class).includeId());

		ResourceEntity<E2> te1MapByTarget = new ResourceEntity<>(lre1);
		te1MapByTarget.includeId();

		ResourceEntity<E1> te1MapBy = new ResourceEntity<>(lre0);
		te1MapBy.getChildren().put("r1", te1MapByTarget);

		ResourceEntity<E1> te1 = new ResourceEntity<>(lre0);
		te1.mapBy(te1MapBy, "r1");

		DataResponse<E1> t1 = DataResponse.forType(E1.class).resourceEntity(te1);
		constraintHandler.constrainResponse(t1, null, tc1);
		assertSame(te1MapBy, t1.getEntity().getMapBy());
		assertEquals("r1", t1.getEntity().getMapByPath());
	}

	protected void appendAttribute(ResourceEntity<?> entity, String name) {
		entity.getAttributes().put(name, new DefaultLrAttribute(name, String.class.getName()));
	}
}
