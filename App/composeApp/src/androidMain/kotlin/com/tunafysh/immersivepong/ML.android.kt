package com.tunafysh.immersivepong

import android.content.Context
import com.google.ai.edge.litert.Accelerator
import com.google.ai.edge.litert.CompiledModel
import com.google.ai.edge.litert.TensorBuffer
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContentProviderCompat.requireContext
import okio.FileSystem
import java.io.File

private var cachedModel: CompiledModel? = null
private var liteRtAvailable = true  // assume available until proven otherwise

/**
 * Returns the best supported accelerator on the device: NPU > GPU > CPU
 */
fun checkSupportedAccelerators(ctx: Context, modelPath: String = "files/model/pong_model.tflite"): Accelerator {
    // Try NPU first
    try {
        val model = CompiledModel.create(
            ctx.assets,
            modelPath,
            CompiledModel.Options(Accelerator.NPU)
        )
        model.close()
        return Accelerator.NPU
    } catch (_: Exception) { /* NPU not supported */ }

    // Try GPU
    try {
        val model = CompiledModel.create(
            ctx.assets,
            modelPath,
            CompiledModel.Options(Accelerator.GPU)
        )
        model.close()
        return Accelerator.GPU
    } catch (_: Exception) { /* GPU not supported */ }

    // Fallback to CPU
    return Accelerator.CPU
}

actual fun predictPaddleMove(inputs: List<Float>): String {
    // Fallback heuristic function
    fun heuristic(): String {
        val targetX = inputs[0] // ballX
        return when {
            targetX < inputs.last() -> "left"
            targetX > inputs.last() -> "right"
            else -> "stay"
        }
    }

    if (!liteRtAvailable) return heuristic()


    return try {
        val context = getContext()
        // Load model once and cache it
        if (cachedModel == null) {
            cachedModel = CompiledModel.create(
                context.assets,
                "files/model/pong_model.tflite",
                CompiledModel.Options(Accelerator.CPU)
            )
        }

        val model = cachedModel ?: return heuristic()

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

    } catch (e: UnsatisfiedLinkError) {
        // LiteRT library missing, fallback permanently to heuristic
        liteRtAvailable = false
        heuristic()
    } catch (e: Exception) {
        // Any other runtime error, fallback temporarily
        heuristic()
    }
}