/* Copyright (c) 2020 David Tanzer

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

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
		val delimiter = if(value is Record<*>) "" else "\""
		result += ", \"${simplePropertyName(name)}\": ${delimiter}${value}${delimiter}"
	}
	result += " }"

	return result
}