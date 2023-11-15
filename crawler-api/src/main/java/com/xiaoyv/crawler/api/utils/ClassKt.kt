package com.xiaoyv.crawler.api.utils

import android.util.Log
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

val typeMapping: Map<String, Class<*>> = mapOf(
    "int" to Int::class.java,
    "long" to Long::class.java,
    "short" to Short::class.java,
    "byte" to Byte::class.java,
    "float" to Float::class.java,
    "double" to Double::class.java,
    "char" to Char::class.java,
    "boolean" to Boolean::class.java
)

/**
 * 兼容 Kotlin 挂起函数的获取方法
 */
fun Class<*>.getDeclaredMethodCompat(
    methodName: String,
    types: List<Class<*>>
): Pair<Method, Boolean> {
    val javaMethod =
        runCatching { getDeclaredMethod(methodName, *types.toTypedArray()) }.getOrNull()
    if (javaMethod != null) return javaMethod to false

    val kotlinTypes = types.toMutableList()

    kotlinTypes.add(Continuation::class.java)

    val suspendMethod =
        runCatching { getDeclaredMethod(methodName, *kotlinTypes.toTypedArray()) }.getOrNull()
    if (suspendMethod != null) return suspendMethod to true

    throw NoSuchMethodException("Class: $name,methodName: $methodName, params: ${types.map { it.name }}")
}

/**
 * 兼容 Kotlin 挂起函数反射传参数的调用
 */
inline fun Method.invokeCompat(
    instance: Any,
    values: List<Any?>,
    isKotlinSuspend: Boolean,
    crossinline invokeResult: (any: Any?) -> Unit
) {
    Continuation::class.java to object : Continuation<Any> {
        override val context: CoroutineContext
            get() = EmptyCoroutineContext

        override fun resumeWith(result: Result<Any>) {
            Log.e("Plugin", "Init result! ${result.getOrNull()}")
        }
    }

    // 调用方法
    val result = if (Modifier.isStatic(modifiers)) {
        invoke(null, *compatMethodValues(values, isKotlinSuspend, invokeResult))
    } else {
        invoke(instance, *compatMethodValues(values, isKotlinSuspend, invokeResult))
    }

    // 返回结果不是挂起类型，直接手动回调数据
    if (result == null || result.javaClass.name != "kotlin.coroutines.intrinsics.CoroutineSingletons") {
        invokeResult(result)
    }
}

/**
 * 兼容 Kotlin 挂起函数反射传参数
 */
inline fun compatMethodValues(
    values: List<Any?>,
    isKotlinSuspend: Boolean,
    crossinline invokeResult: (any: Any?) -> Unit
): Array<Any?> {
    if (isKotlinSuspend.not()) return values.toTypedArray()
    val suspendValues = values.toMutableList()
    suspendValues.add(object : Continuation<Any> {
        override val context: CoroutineContext
            get() = EmptyCoroutineContext

        override fun resumeWith(result: Result<Any>) {
            invokeResult(result.getOrNull())
        }
    })
    return suspendValues.toTypedArray()
}

fun classFromName(
    name: String,
    pluginClassLoader: ClassLoader,
    parentClassLoader: ClassLoader
): Class<*> {
    if (typeMapping.keys.contains(name)) return requireNotNull(typeMapping[name])

    var clazz = runCatching { pluginClassLoader.loadClass(name) }.getOrNull()
    if (clazz != null) return clazz
    clazz = runCatching { parentClassLoader.loadClass(name) }.getOrNull()
    if (clazz != null) return clazz
    throw NoClassDefFoundError("Class($name) not found!")
}