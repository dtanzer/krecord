package net.davidtanzer.krecord

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

private data class RecordPath(val propertyName: String, val recordValues: MutableMap<String, Any?>?, val type: Class<*>?)

interface Setter<T: Record<T>> {
	fun <U> set(currentValue: U, newValue: U): Setter<T>
	fun <U> set(currentValue: U, newValueProducer: (c: U) -> U): Setter<T>
}
private class SetterInvocationHandler<T: Record<T>>(currentValues: Map<String, Any?>, setter: RecordSetter<T>, path: List<RecordPath>) : InvocationHandler {
	private val currentValues = currentValues
	private val setter = setter
	private val path = path

	@Throws(Throwable::class)
	override operator fun invoke(proxy: Any?, method: Method, args: Array<Any>?): Any? {
		val newPath = path.toMutableList()

		val value = this.currentValues[method.name]
		println("current ${method.name} -> ${value?.javaClass}")
		if(value is Record<*>) {
			println("Value ${method.name} is a Record, interfaces: ${value.javaClass.interfaces.map { it.name }}!")

			val type = value.javaClass.interfaces[0]
			val recordValues: MutableMap<String, Any?> = type.declaredMethods.fold(mutableMapOf()) { acc, m ->
				println("collecting ${m.name}")
				if(m.name != "with") {
					acc[m.name] = m.invoke(value)
				}
				acc
			}
			newPath.add(RecordPath(method.name, recordValues, type))

			return Proxy.newProxyInstance(
					Record::class.java.classLoader,
					arrayOf<Class<*>>(type),
					SetterInvocationHandler(recordValues, setter, newPath))
		} else {
			newPath.add(RecordPath(method.name, null, null))
		}

		setter.currentProperty = method.name
		setter.currentPath = newPath
		return value
	}
}
private class RecordSetter<T: Record<T>>(type: Class<T>, values: Map<String, Any?>) : Setter<T> {
	val type = type
	val values = values
	val newValues = values.toMutableMap()
	var currentProperty: String? = null
	var currentPath: List<RecordPath>? = null

	override fun <U> set(currentValue: U, newValue: U): Setter<T>  {
		return set(currentValue) { newValue }
	}
	override fun <U> set(currentValue: U, newValueProducer: (c: U) -> U): Setter<T> {
		//FIXME: What if current property is null?
		var values = newValues
		currentPath!!.forEachIndexed { index, recordPath ->
			if(index == currentPath!!.size-1) {
				values[recordPath.propertyName] = newValueProducer(currentValue)
			} else {
				values[recordPath.propertyName] = recordPath
				values = recordPath.recordValues!!
			}
		}
		println(values)
		return this
	}

	fun proxiedValue(): T {
		return Proxy.newProxyInstance(
				Record::class.java.classLoader,
				arrayOf<Class<*>>(this.type),
				SetterInvocationHandler(this.values, this, listOf())) as T
	}

	fun createNewValue(): T {
		println("creating new value!")
		val values = this.newValues.mapValues(this::mapRecordValues)

		println("returning new mapped value")
		return Proxy.newProxyInstance(
				Record::class.java.classLoader,
				arrayOf<Class<*>>(type),
				DynamicInvocationHandler(type, values)) as T
	}

	private fun mapRecordValues(entry: Map.Entry<String, Any?>): Any? {
		return if (entry.value is RecordPath) {
			println("mapping ${entry.key} as record")
			val recordValues = (entry.value as RecordPath).recordValues!!.mapValues(this::mapRecordValues)
			Proxy.newProxyInstance(
					Record::class.java.classLoader,
					arrayOf((entry.value as RecordPath).type!!),
					DynamicInvocationHandler((entry.value as RecordPath).type as Class<Record<*>>, recordValues))
		} else {
			println("mapping ${entry.key} to ${entry.value}")
			entry.value
		}
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
		println("--- creating ${type} from ${value}")

		values = type.declaredMethods.fold(mutableMapOf()) { acc, m ->
			println("getting ${m.name}")
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