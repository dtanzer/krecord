package net.davidtanzer.krecord

fun main(args: Array<String>) {
	val address1 = Record.from(Address::class.java, object : Address {
		override val streetAddress = Record.from(StreetAddress::class.java, object: StreetAddress {
			override val street = "Beethovengasse"
			override val streetNo = "13a"
		})
		override val city = "Wien"
		override val zipCode = "1010"
		override val country = "AT"
	})
	val address2 = Record.from(Address::class.java, object : Address {
		override val streetAddress = Record.from(StreetAddress::class.java, object: StreetAddress {
			override val street = "Mozartstraße"
			override val streetNo = "1/3/43"
		})
		override val city = "Salzburg"
		override val zipCode = "5020"
		override val country = "AT"
	})
	val address3 = Record.from(Address::class.java, object : Address {
		override val streetAddress = Record.from(StreetAddress::class.java, object: StreetAddress {
			override val street = "Leharstraße"
			override val streetNo = "4"
		})
		override val city = "Linz"
		override val zipCode = "4020"
		override val country = "AT"
	})
	val customer1 = Record.from(Customer::class.java, object: Customer {
		override val fullName = "Max Mustermann"
		override val billingAddress = address1
		override val shippingAddress = address2
	})
	val customer2 = customer1.with { current, setter -> setter.set(current.fullName, "Martina Musterfrau") }
	val customer3 = customer1.with { current, setter -> setter.set(current.shippingAddress, address3) }
	val customer4 = customer1.with { current, setter -> setter
			.set(current.billingAddress.streetAddress.street, "Straussweg")
			.set(current.billingAddress.streetAddress.streetNo, "42")
	}
	println(customer1)
	println(customer2)
	println(customer3)
	println(customer4)
}