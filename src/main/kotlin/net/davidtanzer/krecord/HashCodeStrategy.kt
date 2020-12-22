package net.davidtanzer.krecord

import java.util.*

typealias HashCodeStrategy = (values: Map<String, Any?>) -> Int

internal fun defaultHashCode(values: Map<String, Any?>): Int {
	return Objects.hash(*values.values.toTypedArray())
}
