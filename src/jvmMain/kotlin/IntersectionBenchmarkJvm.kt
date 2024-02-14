package org.example

import kotlinx.benchmark.*
import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.infra.IterationParams
import org.openjdk.jmh.runner.IterationType

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@Warmup(iterations = 50, time = 100, timeUnit = BenchmarkTimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@State(Scope.Benchmark)
open class IntersectionWithPollutedReceiverTypeProfilerBenchmark : IntersectionBenchmarkBase() {
    private val types = listOf("Iter", "Set", "List")

    @Setup(Level.Trial)
    fun setup(params: IterationParams) {
        receiver = if (params.type == IterationType.WARMUP) {
            createIterable(receiverSize, types.random(), 0)
        } else {
            createIterable(receiverSize, receiverType, 0)
        }
        parameter = createIterable(parameterSize, parameterType, (intersectionRatio * (1 - receiverSize)).toInt())
    }
}
