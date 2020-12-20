package net.davidtanzer.krecord

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

interface Setter<T: Record<T>> {
	fun <U> set(currentValue: U, newValue: U): Setter<T>
	fun <U> set(currentValue: U, newValueProducer: (c: U) -> U): Setter<T>
}
private class SetterInvocationHandler<T: Record<T>>(currentValues: Map<String, Any?>, setter: RecordSetter<T>) : InvocationHandler {
	private val currentValues = currentValues
	private val setter = setter

	@Throws(Throwable::class)
	override operator fun invoke(proxy: Any?, method: Method, args: Array<Any>?): Any? {
		setter.currentProperty = method.name
		return this.currentValues[method.name]
	}
}
private class RecordSetter<T: Record<T>>(type: Class<T>, values: Map<String, Any?>) : Setter<T> {
	val type = type
	val values = values
	val newValues = values.toMutableMap()
	var currentProperty: String? = null

	override fun <U> set(currentValue: U, newValue: U): Setter<T>  {
		//FIXME: What if current property is null?
		newValues[currentProperty!!] = newValue
		return this
	}
	override fun <U> set(currentValue: U, newValueProducer: (c: U) -> U): Setter<T> {
		newValues[currentProperty!!] = newValueProducer(currentValue)
		return this
	}

	fun proxiedValue(): T {
		return Proxy.newProxyInstance(
				Record::class.java.classLoader,
				arrayOf<Class<*>>(this.type),
				SetterInvocationHandler(this.values, this)) as T
	}

	fun createNewValue(): T {
		return Proxy.newProxyInstance(
				Record::class.java.classLoader,
				arrayOf<Class<*>>(type),
				DynamicInvocationHandler(type, this.newValues)) as T
	}
}
private class DynamicInvocationHandler<T: Record<T>> : InvocationHandler {
	private val type: Class<T>
	private val values: Map<String, Any?>

	constructor(type: Class<T>, values: Map<String, Any?>) {
		this.type = type
		this.values = values
	}
	constructor(type: Class<T>, value: T)  {
		this.type = type

		values = type.declaredMethods.fold(mutableMapOf()) { acc, m ->
			acc[m.name] = m.invoke(value)
			acc
		}
	}
	@Throws(Throwable::class)
	override operator fun invoke(proxy: Any?, method: Method, args: Array<Any>?): Any? {
		if(method.name == "with") {
			val setter = RecordSetter(this.type, values)
			val mutator = args!![0] as (current: T, setter: Setter<T>) -> Setter<T>
			val newSetter = mutator(setter.proxiedValue(), setter) as RecordSetter

			return Proxy.newProxyInstance(
					Record::class.java.classLoader,
					arrayOf<Class<*>>(this.type),
					DynamicInvocationHandler(type, newSetter.createNewValue())) as T
		}
		return values[method.name]
	}
}

interface Record<T: Record<T>> {
	fun with(mutator: (current: T, setter: Setter<T>) -> Setter<T>): T { throw IllegalStateException("Always overridden by default") }
	companion object {
		@JvmStatic fun <T: Record<T>> from(type: Class<T>, initialValue: T): T {
			return Proxy.newProxyInstance(
					Record::class.java.classLoader,
					arrayOf<Class<*>>(type),
					DynamicInvocationHandler(type, initialValue)) as T
		}
	}

}