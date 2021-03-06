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

interface StreetAddress: Record<StreetAddress> {
	val street: String
	val streetNo: String
}
interface Address: Record<Address> {
	val streetAddress: StreetAddress
	val city: String
	val zipCode: String
	val country: String
}
interface Customer : Record<Customer> {
	val fullName: String
	val billingAddress: Address
	val shippingAddress: Address
}

class DeepRecordTest {
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
	fun `setting name does not affect the addresses`() {
		val customer2 = customer.with { current, setter -> setter.set(current.fullName, "Martina Musterfrau") }

		assertThat(customer2.billingAddress).isSameAs(address1)
		assertThat(customer2.shippingAddress).isSameAs(address2)
	}

	@Test
	fun `setting a record property updates the whole record`() {
		val customer2 = customer.with { current, setter -> setter.set(current.billingAddress, address2) }

		assertThat(customer2.billingAddress).isSameAs(address2)
	}

	@Test
	fun `setting a deep property updates the immutable records`() {
		val customer2 = customer.with { current, setter -> setter.set(current.billingAddress.streetAddress.street, "Straussweg") }

		assertThat(customer2.billingAddress).isNotSameAs(customer.billingAddress)
		assertThat(customer2.billingAddress.streetAddress.street).isEqualTo("Straussweg")
	}

	@Test
	fun `setting a deep property causes everything on the path to change`() {
		val customer2 = customer.with { current, setter -> setter.set(current.billingAddress.streetAddress.street, "Straussweg") }

		assertThat(customer2.billingAddress.streetAddress).isNotSameAs(customer.billingAddress.streetAddress)
		assertThat(customer2.billingAddress).isNotSameAs(customer.billingAddress)
		assertThat(customer2).isNotSameAs(customer)
	}

	@Test
	fun `does not change the rest of the billing address object`() {
		val customer2 = customer.with { current, setter -> setter.set(current.billingAddress.streetAddress.street, "Straussweg") }

		assertThat(customer2.billingAddress.streetAddress.streetNo).isSameAs(customer.billingAddress.streetAddress.streetNo)
		assertThat(customer2.billingAddress.city).isSameAs(customer.billingAddress.city)
		assertThat(customer2.billingAddress.zipCode).isSameAs(customer.billingAddress.zipCode)
		assertThat(customer2.billingAddress.country).isSameAs(customer.billingAddress.country)
	}

	@Test
	fun `does not change the rest of the customer object`() {
		val customer2 = customer.with { current, setter -> setter.set(current.billingAddress.streetAddress.street, "Straussweg") }

		assertThat(customer2.fullName).isSameAs(customer.fullName)
		assertThat(customer2.shippingAddress).isSameAs(customer.shippingAddress)
	}
}