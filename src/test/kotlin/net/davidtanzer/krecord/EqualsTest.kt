package net.davidtanzer.krecord

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EqualsTest {
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
	fun `default equals strategy views objects as equal when they are the same reference`() {
		assertThat(customer.equals(customer)).isTrue()
	}
	@Test
	fun `default equals strategy views objects as unequal when they are not the same reference`() {
		val customer2 = customer.with { current, setter -> setter.set(current.fullName, "Max Mustermann") }

		assertThat(customer.equals(customer2)).isFalse()
	}
}