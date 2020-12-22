package net.davidtanzer.krecord

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ToStringTest {
	private val address1 = Record.from(Address::class.java, object : Address {
		override val streetAddress = Record.from(StreetAddress::class.java, object: StreetAddress {
			override val street = "Beethovengasse"
			override val streetNo = "13a"
		})
		override val city = "Wien"
		override val zipCode = "1010"
		override val country = "AT"
	})
	private val address2 = Record.from(Address::class.java, object : Address {
		override val streetAddress = Record.from(StreetAddress::class.java, object: StreetAddress {
			override val street = "Mozartstraße"
			override val streetNo = "1/3/43"
		})
		override val city = "Salzburg"
		override val zipCode = "5020"
		override val country = "AT"
	})
	private val customer = Record.from(Customer::class.java, object: Customer {
		override val fullName = "Max Mustermann"
		override val billingAddress = address1
		override val shippingAddress = address2
	})

	@Test
	fun `string representation contains properties on all levels`() {
		val stringRepresentation = customer.toString()
		println(stringRepresentation)

		assertThat(stringRepresentation).contains("Max Mustermann")
		assertThat(stringRepresentation).contains("Wien")
		assertThat(stringRepresentation).contains("Beethovengasse")
	}

	@Test
	fun `string representation of updated customer contains updated properties`() {
		val customer2 = customer.with { current, setter ->
			setter.set(current.billingAddress.streetAddress.street, "Straussweg")
					.set(current.fullName, "Martina Musterfrau")
		}

		val stringRepresentation = customer2.toString()
		println(stringRepresentation)

		assertThat(stringRepresentation).contains("Martina Musterfrau")
		assertThat(stringRepresentation).contains("Straussweg")
	}
}