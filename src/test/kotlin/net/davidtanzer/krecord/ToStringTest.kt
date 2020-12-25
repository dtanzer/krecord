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