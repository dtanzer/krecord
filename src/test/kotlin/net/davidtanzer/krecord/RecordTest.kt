package net.davidtanzer.krecord

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

interface Foo : Record<Foo> {
	val greeting: String;
	val name: String;
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
		/*
		val f3 = f2.with{ current, setter -> setter.set(current.name) { c -> if(c == "Welt") "Kotlin" else c } }
		println(f3.greeting+" "+f3.name)
		val f4 = f.with{ current, setter -> setter.set(current.name) { c -> if(c == "Welt") "Kotlin" else c } }
		println(f4.greeting+" "+f4.name)

		 */
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