package com.nhl.link.rest.runtime.parser.filter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.cayenne.exp.Expression;

import com.fasterxml.jackson.databind.JsonNode;
import com.nhl.link.rest.ResourceEntity;
import com.nhl.link.rest.meta.LrEntity;
import com.nhl.link.rest.parser.converter.JsonValueConverter;
import com.nhl.link.rest.parser.converter.UtcDateConverter;
import com.nhl.link.rest.runtime.jackson.IJacksonService;
import com.nhl.link.rest.runtime.parser.cache.IPathCache;

class CayenneExpProcessor {

	private IJacksonService jsonParser;
	private Map<String, JsonValueConverter> converters;
	private IPathCache pathCache;

	CayenneExpProcessor(IJacksonService jsonParser, IPathCache pathCache) {
		this.jsonParser = jsonParser;
		this.pathCache = pathCache;

		// TODO: instead of manually assembling converters we must switch to
		// IJsonValueConverterFactory already used by DataObjectProcessor.
		// The tricky part is the "id" attribute that is converted to DbPath
		// during CayenneExpProcessorWorker traversal, so its type can not be
		// mapped with existing tools

		this.converters = new HashMap<>();
		this.converters.put(Date.class.getName(), UtcDateConverter.converter());
		this.converters.put(java.sql.Date.class.getName(), UtcDateConverter.converter());
		this.converters.put(java.sql.Time.class.getName(), UtcDateConverter.converter());
		this.converters.put(java.sql.Timestamp.class.getName(), UtcDateConverter.converter());
	}

	void process(ResourceEntity<?> resourceEntity, String cayenneExp) {
		
		if (cayenneExp == null || cayenneExp.length() == 0) {
			return;
		}

		Expression exp = worker(resourceEntity).exp(cayenneExp);
		resourceEntity.andQualifier(exp);
	}

	void process(ResourceEntity<?> resourceEntity, JsonNode cayenneExp) {

		if (cayenneExp == null) {
			return;
		}

		Expression exp = worker(resourceEntity).exp(cayenneExp);
		resourceEntity.andQualifier(exp);
	}

	private CayenneExpProcessorWorker worker(ResourceEntity<?> resourceEntity) {
		LrEntity<?> entity = resourceEntity.getLrEntity();
		return new CayenneExpProcessorWorker(jsonParser, converters, pathCache, entity);
	}
}
