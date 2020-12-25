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

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

interface Foo : Record<Foo> {
	val greeting: String
	val name: String
}

class RecordTest {
	@Test
	fun `creates an immutable record with default values`() {
		val record = Record.from(Foo::class.java, object : Foo {
			override val greeting: String = "Hello"
			override val name: String = "World"
		})

		assertThat(record.greeting).isEqualTo("Hello")
		assertThat(record.name).isEqualTo("World")
	}

	@Test
	fun `creates a new immutable record when setting the values`() {
		val record = Record.from(Foo::class.java, object : Foo {
			override val greeting: String = "Hello"
			override val name: String = "World"
		})

		val record2 = record.with { current, setter -> setter.set(current.greeting, "Hallo").set(current.name, "Welt") }

		assertThat(record2).isNotSameAs(record)
		assertThat(record2.greeting).isEqualTo("Hallo")
		assertThat(record2.name).isEqualTo("Welt")
	}

	@Test
	fun `creates a new immutable record where a function sets name to a different value`() {
		val record = Record.from(Foo::class.java, object : Foo {
			override val greeting: String = "Hello"
			override val name: String = "World"
		})

		val record2 = record.with{ current, setter -> setter.set(current.name) { c -> if(c == "World") "Kotlin" else c } }

		assertThat(record2).isNotSameAs(record)
		assertThat(record2.greeting).isEqualTo("Hello")
		assertThat(record2.name).isEqualTo("Kotlin")
	}

	@Test
	fun `creates a new immutable record where a function sets name to the same value`() {
		val record = Record.from(Foo::class.java, object : Foo {
			override val greeting: String = "Hallo"
			override val name: String = "Welt"
		})

		val record2 = record.with{ current, setter -> setter.set(current.name) { c -> if(c == "World") "Kotlin" else c } }

		assertThat(record2).isNotSameAs(record)
		assertThat(record2.greeting).isEqualTo("Hallo")
		assertThat(record2.name).isEqualTo("Welt")
	}
}