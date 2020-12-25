# Simple Immutable Records for Kotlin

`krecord` is a library that makes it easier to implement record types that

* **are immutable**: Once created, a record cannot be changed anymore
* provide functionality to **create new records** based on existing ones,
where the new record object shares most of its structure with the existing
* **implmenent `toString`, `equals` and `hashCode`** and allows users to
change those default implementations
* can be used from **Kotlin** and **Java**

`krecord` is not "finished" yet, for whatever value of "finished" one
could imagine. I created `krecord` to practice writing Kotlin code. It
is currently a **proof-of-concept**, and I would love to hear your
feedback:

* If something does not work for you or you have an improvement idea,
open an issue
* If you have an improvement idea and know how to implement it, open
a pull request (please make sure that you add tests and write a
[meaningful commit message](#contributing))
* If you have other feedback, contact me [on Twitter](https://twitter.com/dtanzer)
(DMs open) or [via email](business@davidtanzer.net).

## The Problem

Immutable data cannot be changed once created. This leads to a simpler
application development model: Every piece of code can be sure that
no other code will change the data it is currently using. And when we
want to know whether a certain object has changed, we can simply compare
the references of two objects.

In Java, we can create a class that has only `get` Methods and never changes
its internal data. In Kotlin, we can even create a `data class` that has
only `val` properties.

But when data changes over time, like a user changing their password, our code
must create a new copy of the immutable object that reflects the changed data.
And this new object should share the structure of the old object: All the values
that did **not** change should have the same **reference** to enable the
comparison mentioned above.

In TypeScript, we could just write

```typescript
updatedUserData = { ...userData, password: newPassword }
```

and be done (it gets slightly more complicated with deeply structured objects).

When we write Java or Kotlin, the code to update the immutable data objects
becomes way more complicated and verbose. `krecord` tries to fix that.

## Where to Get It

`krecord` is hosted at `jcenter`.

Maven:

```xml
<dependency>
  <groupId>net.davidtanzer</groupId>
  <artifactId>krecord</artifactId>
  <version>0.0.1</version>
  <type>pom</type>
</dependency>
```

Gradle:

```
implementation 'net.davidtanzer:krecord:0.0.1'
```

## Create Immutable Objects

When using `krecord`, you define the structure of your immutable records
using interfaces:

```kotlin
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
```

Then, create immutable objects by calling `Record.from` and supplying the
initial data values:

```kotlin
val address1 = Record.from(Address::class.java, object : Address {
    override val streetAddress = Record.from(StreetAddress::class.java, object: StreetAddress {
        override val street = "Beethovengasse"
        override val streetNo = "13a"
    })
    override val city = "Wien"
    override val zipCode = "1010"
    override val country = "AT"
})
```

Now you have an immutable object in `address1`. But that looks much more
complicated than a simple `data class`, so why should you want to do that?
Because `krecord` gives you a way to "update" values in you data object
by creating a new object that shares most of the structure with the old
one:

## Update Immutable Objects

To "update" the data stored in an immutable object, one must create a new
object with the updated data and use that object instead of the old one.
`Record<T>` provides a method called `with` to accomplish that:

```kotlin
val customer2 = customer.with { current, setter -> setter.set(current.fullName, "Martina Musterfrau") }
val customer3 = customer2.with { current, setter -> setter
        .set(current.billingAddress.city, "Linz")
        .set(current.billingAddress.zipCode, "4040")
        .set(current.billingAddress.streetAddress.street, "Straussweg") }
```

`with` gets passed a function that describes how to create the new object
based on the old one. In this function, you can implement the "changes" you
want to make. A `setter` will collect all those changes (you can call `set`
multiple times), and your function must return that setter.

To "set" a new value in the newly created object, call `set` with the current
value as first parameter and the new value as the second. Make sure to use
the parameter `current` passed to your lambda function to access the current
value.

In the example above, `customer2` and `customer3` are new objects with the
new values set, that share all values from the old objects.

## Using krecord With Java

One can also use Java code to interact with `krecord`. Define a Java interface
with only getter methods that extends `Record<T>`. (The interfaces must be
`public`):

```java
public interface RecoveryData extends Record<RecoveryData> {
    String getPhoneNumber();
    String getPin();
}

public interface User extends Record<User> {
    String getUserName();
    String getEmailAddress();
    String getPassword();
    RecoveryData getRecoveryData();
}
```

Then use those interfaces to create record objects:

```java
RecoveryData recoveryData = Record.from(RecoveryData.class, new RecoveryData() {
    @Override public String getPhoneNumber() { return "+43-123-45 67 890"; }
    @Override public String getPin() { return "1234"; }
});
User user = Record.from(User.class, new User() {
    @Override public String getUserName() { return "jenny"; }
    @Override public String getEmailAddress() { return "jenny@example.com"; }
    @Override public String getPassword() { return "53cur3"; }
    @Override public RecoveryData getRecoveryData() { return recoveryData; }
});
```

To update values, call `with` and pass in a lambda function that returns the
setter. Use the setter to collect the "changes" to the data in your record.

```java
User user2 = user.with((current, setter) -> setter
        .set(current.getPassword(), "3v3nm0r353cur3")
        .set(current.getRecoveryData().getPin(), "123456"));
```

## Missing / Future Features

<a name="contributing"><h2>Contributing</h2></a>

### Tests

When you add or change functionality, please add automated tests that show
how the functionality has changed. The tests should be small and focused -
better write multiple tests than one with dozens of asserts.

Test names should describe the new current behavior and not contain any
redundant words (like `should`).

* Bad: ``@Test fun `deep property test`() `` - not descriptive
* Less bad: ``@Test fun `should update the immutable records when setting a deep property`() `` -
redundant word `should` **and** does not describe the current behavior, but a desired behavior
* Better: ``@Test fun `setting a deep property updates the immutable records`() ``

### Commit Messages

The **first line** of your commit messages should start with a lower-case verb
that describes the main focus of your commit, like `add`, `remove`, `refactor`,
`fix`, `document`, etc. . Then there should be a short headline / overview of
your change. The first line should not end with a period.

After the first line, there should be an empty line. After that, you can
add more paragraphs containing a detailed description. Please do **not** explain
what you did or how you did it there - instead, explain what the software does
differently now.

If you use very fine-grained commits during development, please **squash your commits**
so that each commit contains a coherent piece of functionality.

## License: MIT

```
Copyright (c) 2020 David Tanzer

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
```
