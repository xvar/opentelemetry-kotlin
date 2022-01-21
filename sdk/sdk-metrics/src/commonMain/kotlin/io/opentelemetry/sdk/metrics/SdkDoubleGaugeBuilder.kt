/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.sdk.metrics

import io.opentelemetry.api.metrics.DoubleGaugeBuilder
import io.opentelemetry.api.metrics.LongGaugeBuilder
import io.opentelemetry.api.metrics.ObservableDoubleMeasurement
import io.opentelemetry.sdk.metrics.common.InstrumentType
import io.opentelemetry.sdk.metrics.internal.state.MeterProviderSharedState
import io.opentelemetry.sdk.metrics.internal.state.MeterSharedState

internal class SdkDoubleGaugeBuilder(
    meterProviderSharedState: MeterProviderSharedState,
    sharedState: MeterSharedState,
    name: String,
    description: String,
    unit: String
) :
    AbstractInstrumentBuilder<SdkDoubleGaugeBuilder>(
        meterProviderSharedState,
        sharedState,
        name,
        description,
        unit
    ),
    DoubleGaugeBuilder {
    constructor(
        meterProviderSharedState: MeterProviderSharedState,
        meterSharedState: MeterSharedState,
        name: String
    ) : this(meterProviderSharedState, meterSharedState, name, "", "1")

    override val `this`: SdkDoubleGaugeBuilder
        protected get() = this

    override fun ofLongs(): LongGaugeBuilder {
        return swapBuilder {
            meterProviderSharedState: MeterProviderSharedState,
            sharedState: MeterSharedState,
            name: String,
            description: String,
            unit: String ->
            SdkLongGaugeBuilder(meterProviderSharedState, sharedState, name, description, unit)
        }
    }

    override fun buildWithCallback(callback: (ObservableDoubleMeasurement) -> Unit) {
        registerDoubleAsynchronousInstrument(InstrumentType.OBSERVABLE_GAUGE, callback)
    }
}
