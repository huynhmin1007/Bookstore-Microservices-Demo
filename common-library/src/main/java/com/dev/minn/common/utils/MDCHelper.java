package com.dev.minn.common.utils;

import org.slf4j.MDC;

public class MDCHelper {

    public static String getTraceId() {
        return MDC.get("traceId");
    }
}
