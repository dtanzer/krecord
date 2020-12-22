package net.davidtanzer.krecord

typealias EqualsStrategy = (type: Class<*>, record1: Any, record2: Any) -> Boolean

internal fun defaultEquals(type: Class<*>, record1: Any, record2: Any): Boolean {
	return record1 === record2
}
