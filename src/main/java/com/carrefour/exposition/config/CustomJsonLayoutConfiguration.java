package com.carrefour.exposition.config;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.contrib.json.classic.JsonLayout;
import org.slf4j.MDC;

import java.util.Map;


public class CustomJsonLayoutConfiguration extends JsonLayout {

    public static final String CORRELATION_ID_ATTR_NAME = "correlation_id";

    @Override
	protected void addCustomDataToJsonMap(Map<String, Object> map, ILoggingEvent event) {
		map.put(CORRELATION_ID_ATTR_NAME, MDC.getCopyOfContextMap() != null ? MDC.getCopyOfContextMap().get(CORRELATION_ID_ATTR_NAME) : null);
	}
}
