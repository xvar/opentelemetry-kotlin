/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.sdk.metrics.internal.state

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.context.Context
import io.opentelemetry.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.sdk.metrics.common.InstrumentType
import io.opentelemetry.sdk.metrics.common.InstrumentValueType
import io.opentelemetry.sdk.metrics.exemplar.ExemplarFilter
import io.opentelemetry.sdk.metrics.exemplar.mock.AttributesProcessorMock
import io.opentelemetry.sdk.metrics.internal.descriptor.InstrumentDescriptor
import io.opentelemetry.sdk.metrics.internal.export.CollectionHandle
import io.opentelemetry.sdk.metrics.internal.export.CollectionInfo
import io.opentelemetry.sdk.metrics.internal.view.ViewRegistry
import io.opentelemetry.sdk.metrics.mock.MetricReaderMock
import io.opentelemetry.sdk.metrics.view.Aggregation
import io.opentelemetry.sdk.metrics.view.InstrumentSelector
import io.opentelemetry.sdk.metrics.view.View
import io.opentelemetry.sdk.resources.Resource
import io.opentelemetry.sdk.trace.TestClock
import kotlin.test.Test

class AsynchronousMetricStorageTest {
    private val testClock: TestClock = TestClock.create()
    private val meterSharedState = MeterSharedState.create(InstrumentationLibraryInfo.empty())
    private val spyAttributesProcessor = AttributesProcessorMock()
    private val reader = MetricReaderMock()
    private val meterProviderSharedState: MeterProviderSharedState
    private val view: View
    private val handle: CollectionHandle
    private val all: MutableSet<CollectionHandle>

    init {
        view =
            View.builder()
                .setAggregation(Aggregation.lastValue())
                .setAttributesProcessor(spyAttributesProcessor)
                .build()
        val viewRegistry =
            ViewRegistry.builder()
                .addView(
                    InstrumentSelector.builder()
                        .setInstrumentType(InstrumentType.OBSERVABLE_GAUGE)
                        .build(),
                    view
                )
                .build()
        meterProviderSharedState =
            MeterProviderSharedState.create(
                testClock,
                Resource.empty(),
                viewRegistry,
                ExemplarFilter.sampleWithTraces()
            )
        handle = CollectionHandle.createSupplier().get()
        all = CollectionHandle.mutableSet()
        all.add(handle)
    }

    @Test
    fun doubleAsynchronousAccumulator_AttributesProcessor_used() {
        AsynchronousMetricStorage.doubleAsynchronousAccumulator<Any>(
                view,
                InstrumentDescriptor.create(
                    "name",
                    "description",
                    "unit",
                    InstrumentType.OBSERVABLE_GAUGE,
                    InstrumentValueType.DOUBLE
                )
            ) { value -> value.observe(1.0, Attributes.empty()) }
            .collectAndReset(
                CollectionInfo.create(handle, all, reader),
                meterProviderSharedState.resource,
                meterSharedState.instrumentationLibraryInfo,
                0,
                testClock.now(),
                false
            )
        assertSoftly(spyAttributesProcessor.calls.last()) {
            incoming shouldBe Attributes.empty()
            context shouldBe Context.current()
        }
    }

    @Test
    fun longAsynchronousAccumulator_AttributesProcessor_used() {
        AsynchronousMetricStorage.longAsynchronousAccumulator<Any>(
                view,
                InstrumentDescriptor.create(
                    "name",
                    "description",
                    "unit",
                    InstrumentType.OBSERVABLE_GAUGE,
                    InstrumentValueType.LONG
                )
            ) { value -> value.observe(1, Attributes.empty()) }
            .collectAndReset(
                CollectionInfo.create(handle, all, reader),
                meterProviderSharedState.resource,
                meterSharedState.instrumentationLibraryInfo,
                0,
                testClock.nanoTime(),
                false
            )
        assertSoftly(spyAttributesProcessor.calls.last()) {
            incoming shouldBe Attributes.empty()
            context shouldBe Context.current()
        }
    }
}
