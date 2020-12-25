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

class HashCodeTest {
	val record1 = Record.from(Foo::class.java, object : Foo {
		override val greeting: String = "Hello"
		override val name: String = "World"
	})
	val record2 = Record.from(Foo::class.java, object : Foo {
		override val greeting: String = "Hello"
		override val name: String = "World"
	})

	@Test
	fun `creates a non-zero hash code`() {
		val hashCode = record1.hashCode()

		assertThat(hashCode).isNotEqualTo(0)
	}

	@Test
	fun `same object has same hash code`() {
		val hashCode = record1.hashCode()

		assertThat(hashCode).isEqualTo(record1.hashCode())
	}

	@Test
	fun `similar object has same hash code`() {
		val hashCode = record1.hashCode()

		assertThat(hashCode).isEqualTo(record2.hashCode())
	}

	@Test
	fun `different object has different hash code`() {
		val record3 = record1.with { current, setter -> setter.set(current.name, "Kotlin")}

		val hashCode = record1.hashCode()

		assertThat(hashCode).isNotEqualTo(record3.hashCode())
	}
}