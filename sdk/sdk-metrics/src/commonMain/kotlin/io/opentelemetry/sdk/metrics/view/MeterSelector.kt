/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.sdk.metrics.view

import io.opentelemetry.sdk.metrics.internal.view.StringPredicates

/**
 * Provides means for selecting one or more Meters. Used for selecting instruments when constructing
 * views.
 */
interface MeterSelector {
    /**
     * Returns the [Pattern] generated by the provided `regex` in the [Builder], or
     * `Pattern.compile(".*")` if none was specified.
     */
    val nameFilter: (String) -> Boolean

    /**
     * Returns the [Pattern] generated by the provided `regex` in the [Builder], or
     * `Pattern.compile(".*")` if none was specified.
     */
    val versionFilter: (String) -> Boolean

    /**
     * Returns the [Pattern] generated by the provided `regex` in the [Builder], or
     * `Pattern.compile(".*")` if none was specified.
     */
    val schemaUrlFilter: (String) -> Boolean

    /** Builder for [InstrumentSelector] instances. */
    data class Builder
    internal constructor(
        val nameFilter: (String) -> Boolean = StringPredicates.ALL,
        val versionFilter: (String) -> Boolean = StringPredicates.ALL,
        val schemaUrlFilter: (String) -> Boolean = StringPredicates.ALL
    ) {

        /**
         * Returns the [Pattern] generated by the provided `regex` in the [Builder], or
         * `Pattern.compile(".*")` if none was specified.
         */

        /**
         * Returns the [Pattern] generated by the provided `regex` in the [Builder], or
         * `Pattern.compile(".*")` if none was specified.
         */
        /**
         * Sets the [Predicate] for matching name.
         *
         * Note: The last provided of [.setNameFilter], [.setNamePattern] and [ ][.setName] is used.
         */
        fun setNameFilter(filter: (String) -> Boolean): Builder {
            return this.copy(nameFilter = filter)
        }

        /**
         * Sets the [Pattern] for matching name.
         *
         * Note: The last provided of [.setNameFilter], [.setNamePattern] and [ ][.setName] is used.
         */
        fun setNamePattern(pattern: Regex): Builder {
            return setNameFilter(StringPredicates.regex(pattern))
        }

        /**
         * Sets a specifier for selecting Instruments by name.
         *
         * Note: The last provided of [.setNameFilter], [.setNamePattern] and [ ][.setName] is used.
         */
        fun setName(name: String): Builder {
            return setNameFilter(StringPredicates.exact(name))
        }

        /**
         * Sets the [Predicate] for matching versions.
         *
         * Note: The last provided of [.setVersionFilter], [.setVersionPattern] and [.setVersion] is
         * used.
         */
        fun setVersionFilter(filter: (String) -> Boolean): Builder {
            return this.copy(versionFilter = filter)
        }

        /**
         * Sets the [Pattern] for matching versions.
         *
         * Note: The last provided of [.setVersionFilter], [.setVersionPattern] and [.setVersion] is
         * used.
         */
        fun setVersionPattern(pattern: Regex): Builder {
            return setVersionFilter(StringPredicates.regex(pattern))
        }

        /**
         * Sets a specifier for selecting Meters by version.
         *
         * Note: The last provided of [.setVersionFilter], [.setVersionPattern] and [.setVersion] is
         * used.
         */
        fun setVersion(version: String): Builder {
            return setVersionFilter(StringPredicates.exact(version))
        }

        /**
         * Sets the [Predicate] for matching schema urls.
         *
         * Note: The last provided of [.setSchemaUrlFilter], [.setSchemaUrlPattern] and
         * [.setSchemaUrl] is used.
         */
        fun setSchemaUrlFilter(filter: (String) -> Boolean): Builder {
            return this.copy(schemaUrlFilter = filter)
        }

        /**
         * Sets the [Pattern] for matching schema urls.
         *
         * Note: The last provided of [.setSchemaUrlFilter], [.setSchemaUrlPattern] and
         * [.setSchemaUrl] is used.
         */
        fun setSchemaUrlPattern(pattern: Regex): Builder {
            return setSchemaUrlFilter(StringPredicates.regex(pattern))
        }

        /**
         * Sets the schema url to match.
         *
         * Note: The last provided of [.setSchemaUrlFilter], [.setSchemaUrlPattern] and
         * [.setSchemaUrl] is used.
         */
        fun setSchemaUrl(url: String): Builder {
            return setSchemaUrlFilter(StringPredicates.exact(url))
        }

        /** Returns an InstrumentSelector instance with the content of this builder. */
        fun build(): MeterSelector {
            return object : MeterSelector {
                override val nameFilter: (String) -> Boolean
                    get() = this@Builder.nameFilter
                override val versionFilter: (String) -> Boolean
                    get() = this@Builder.versionFilter
                override val schemaUrlFilter: (String) -> Boolean
                    get() = this@Builder.schemaUrlFilter
            }
        }
    }

    companion object {
        /**
         * Returns a new [Builder] for [InstrumentSelector].
         *
         * @return a new [Builder] for [InstrumentSelector].
         */
        fun builder(): Builder {
            return Builder()
        }
    }
}
