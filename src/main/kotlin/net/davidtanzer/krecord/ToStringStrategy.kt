package net.davidtanzer.krecord

typealias ToStringStrategy = (className: String, values: Map<String, Any?>) -> String

fun simplePropertyName(name: String): String {
	if(name.startsWith("get")) {
		return name[3].toLowerCase() + name.substring(4)
	} else if(name.startsWith("is")) {
		return name[2].toLowerCase() + name.substring(3)
	}
	return name
}

internal fun defaultToString(className: String, values: Map<String, Any?>): String {
	var result = "{ \"__type__\": \"${className}\""
	values.forEach { (name, value) ->
		result += ", \"${simplePropertyName(name)}\": \"${value}\""
	}
	result += " }"

	return result
}