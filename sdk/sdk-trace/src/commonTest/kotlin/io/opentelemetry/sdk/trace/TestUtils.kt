/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.sdk.trace

import com.benasher44.uuid.uuid4
import io.opentelemetry.api.common.AttributeKey.Companion.stringKey
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.api.common.normalizeToNanos
import io.opentelemetry.api.trace.SpanContext
import io.opentelemetry.api.trace.SpanKind
import io.opentelemetry.sdk.testing.trace.TestSpanData
import io.opentelemetry.sdk.trace.data.SpanData
import io.opentelemetry.sdk.trace.data.StatusData
import kotlinx.datetime.DateTimeUnit

/** Common utilities for unit tests. */
object TestUtils {
    /**
     * Generates some random attributes used for testing.
     *
     * @return some [io.opentelemetry.api.common.Attributes]
     */
    fun generateRandomAttributes(): Attributes {
        return Attributes.of(stringKey(uuid4().toString()), uuid4().toString())
    }

    /**
     * Create a very basic SpanData instance, suitable for testing. It has the bare minimum viable
     * data.
     *
     * @return A SpanData instance.
     */
    fun makeBasicSpan(): SpanData {
        return TestSpanData.builder()
            .setHasEnded(true)
            .setSpanContext(SpanContext.invalid)
            .setName("span")
            .setKind(SpanKind.SERVER)
            .setStartEpochNanos(DateTimeUnit.SECOND.normalizeToNanos(100) + 100)
            .setStatus(StatusData.ok())
            .setEndEpochNanos(DateTimeUnit.SECOND.normalizeToNanos(200) + 200)
            .setTotalRecordedLinks(0)
            .setTotalRecordedEvents(0)
            .build()
    }
}
