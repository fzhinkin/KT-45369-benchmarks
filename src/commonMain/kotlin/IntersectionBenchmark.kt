package org.example

import kotlinx.benchmark.*

public infix fun <T> Iterable<T>.intersectOptimized(other: Iterable<T>): Set<T> {
    if (other is Set) {
        val set = mutableSetOf<T>()
        for (e in this) {
            if (other.contains(e)) {
                set.add(e)
            }
        }
        return set
    }
    val set = this.toMutableSet()
    set.retainAll(other)
    return set
}

public infix fun <T> Iterable<T>.intersectOptimized2(other: Iterable<T>): Set<T> {
    if (other is Set) {
        val set = mutableSetOf<T>()
        for (e in this) {
            if (other.contains(e)) {
                set.add(e)
            }
        }
        return set
    }
    if (this is Collection && other is Collection) {
        val thisSize = this.size
        val otherSize = other.size
        if (thisSize < otherSize) {
            return this.toMutableSet().apply { retainAll(other) }
        } else {
            val set = mutableSetOf<T>()
            val otherAsSet = other.toMutableSet()
            for (e in this) {
                if (otherAsSet.contains(e)) {
                    set.add(e)
                }
            }
            return set
        }
    }
    val set = this.toMutableSet()
    set.retainAll(other)
    return set
}

public infix fun <T> Iterable<T>.intersectOptimized3(other: Iterable<T>): Set<T> {
    if (this is Collection && other is Collection) {
        val thisSize = this.size
        val otherSize = other.size
        if (thisSize < otherSize) {
            return this.toMutableSet().apply { retainAll(other) }
        } else {
            val set = mutableSetOf<T>()
            val otherAsSet = when (other) {
                is Set -> other
                else -> other.toSet()
            }
            for (e in this) {
                if (otherAsSet.contains(e)) {
                    set.add(e)
                }
            }
            return set
        }
    }
    if (other is Set) {
        val set = mutableSetOf<T>()
        for (e in this) {
            if (other.contains(e)) {
                set.add(e)
            }
        }
        return set
    }
    val set = this.toMutableSet()
    set.retainAll(other)
    return set
}

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@State(Scope.Benchmark)
abstract class IntersectionBenchmarkBase {
    @Param("0", "10", "1000")
    var receiverSize: Int = 0

    @Param("0", "10", "1000")
    var parameterSize: Int = 0

    @Param("Set", "List")
    var receiverType: String = ""

    @Param("Set", "List")
    var parameterType: String = ""

    @Param("0.1", "0.5", "0.9")
    var intersectionRatio: Double = 0.0

    var receiver: Iterable<Int> = emptySet()

    var parameter: Iterable<Int> = emptySet()

    protected fun createIterable(size: Int, typeName: String, startFrom: Int): Iterable<Int> {
        val seq = startFrom until (size + startFrom)
        return when(typeName) {
            "Set" -> seq.toSet()
            "List" -> seq.toList()
            "Iter" -> seq.asIterable()
            else -> throw UnsupportedOperationException("typeName = $typeName")
        }
    }

    @Benchmark
    fun baseline(): Set<Int> = receiver.intersect(parameter)

    @Benchmark
    fun optimized(): Set<Int> = receiver.intersectOptimized(parameter)

    @Benchmark
    fun optimized2(): Set<Int> = receiver.intersectOptimized2(parameter)

    @Benchmark
    fun optimized3(): Set<Int> = receiver.intersectOptimized3(parameter)
}

@State(Scope.Benchmark)
open class SimpleIntersectionBenchmark : IntersectionBenchmarkBase() {
    @Setup
    fun setup() {
        receiver = createIterable(receiverSize, receiverType, 0)
        parameter = createIterable(parameterSize, parameterType, (intersectionRatio * (1 - receiverSize)).toInt())
    }
}


