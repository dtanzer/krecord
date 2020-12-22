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