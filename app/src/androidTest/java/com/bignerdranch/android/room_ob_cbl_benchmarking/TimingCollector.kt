package com.bignerdranch.android.room_ob_cbl_benchmarking

import java.io.File

class TimingCollector(iterations: Int, repetitions: Int) {

    val array = Array(iterations) {
        LongArray(repetitions)
    }

    fun store(iterPos: Int, repPos: Int, elapsedNs: Long) {
        array[iterPos][repPos] = elapsedNs
    }

    fun saveTsv(file: File) {
        file.bufferedWriter().use { writer ->

            // Header
            writer.appendLine(
                "Iteration\t" +
                        (1..array[0].size).joinToString("\t") { "Rep_${it}_ns" }
            )

            // One row per iteration
            array.forEachIndexed { index, row ->
                writer.appendLine(
                    "${index + 1}\t" + row.joinToString("\t")
                )
            }
        }
    }
}


