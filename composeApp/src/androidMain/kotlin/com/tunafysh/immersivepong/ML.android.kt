package com.tunafysh.immersivepong

import com.google.ai.edge.litert.Accelerator
import com.google.ai.edge.litert.CompiledModel
import com.google.ai.edge.litert.TensorBuffer

actual fun predictPaddleMove(inputs: List<Float>): String {
    // Load the compiled TFLite model
    val model = CompiledModel.create(
        "files/pong_model.tflite",
        CompiledModel.Options(Accelerator.CPU)
    )

    // Create input and output buffers
    val inputBuffers = model.createInputBuffers()
    val outputBuffers = model.createOutputBuffers()

    // Write all 5 input floats
    val inputBuffer = inputBuffers[0]
    inputBuffer.writeFloat(inputs.toFloatArray())

    // Run inference
    model.run(inputBuffers, outputBuffers)

    // Read output softmax vector (3 floats)
    val outputBuffer: TensorBuffer = outputBuffers[0]

    val output = outputBuffer.readFloat()

    // Pick the move with the highest probability
    val index = output.indices.maxByOrNull { output[it] } ?: 2

    return when (index) {
        0 -> "left"
        1 -> "right"
        else -> "stay"
    }
}
