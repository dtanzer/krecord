package net.davidtanzer.krecord

import org.junit.jupiter.api.Test

interface Foo : Record<Foo> {
	val greeting: String;
	val name: String;
}

class RecordTest {
	@Test
	fun `creates an immutable record with default values`() {
		val f = Record.from(Foo::class.java, object : Foo {
			override val greeting: String = "Hello"
			override val name: String = "World"
		})

		println(f.greeting+" "+f.name)
		val f2 = f.with { current, setter -> setter.set(current.greeting, "Hallo").set(current.name, "Welt") }
		println(f2.greeting+" "+f2.name)
		val f3 = f2.with{ current, setter -> setter.set(current.name) { c -> if(c == "Welt") "Kotlin" else c } }
		println(f3.greeting+" "+f3.name)
		val f4 = f.with{ current, setter -> setter.set(current.name) { c -> if(c == "Welt") "Kotlin" else c } }
		println(f4.greeting+" "+f4.name)
	}
}