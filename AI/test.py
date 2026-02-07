#!/usr/bin/env python3

import argparse
import numpy as np
import os
import warnings
from colorama import Fore, Style, init
import onnxruntime as ort
init(autoreset=True)

# Silence TF noise
os.environ["TF_CPP_MIN_LOG_LEVEL"] = "3"
warnings.filterwarnings("ignore")

import tensorflow as tf
from tensorflow.keras.models import load_model

# -------------------------------
# CLI
# -------------------------------
parser = argparse.ArgumentParser(description="Test a trained Pong AI")
parser.add_argument("--keras", type=str, help="Keras model file (.keras)")
parser.add_argument("--tflite", type=str, help="TFLite model file (.tflite)")
parser.add_argument("--onnx", type=str, help="ONNX model file (.onnx)")
parser.add_argument("--samples", type=int, default=1000, help="Test samples")
parser.add_argument("--noise", type=float, default=0.0, help="Noise level")
parser.add_argument("--threshold", type=float, default=0.05)
parser.add_argument("--show", type=int, default=10, help="Show N examples")
args = parser.parse_args()

assert args.keras or args.tflite or args.onnx, "Provide --keras, --tflite, or --onnx"

# -------------------------------
# Generate realistic test data
# -------------------------------
def generate_pong_data(n, noise, threshold):
    X = np.random.rand(n, 5)
    y = np.zeros((n, 3))

    for i in range(n):
        _, ball_y, _, _, paddle_y = X[i]
        ball_y += np.random.normal(0, noise)
        paddle_y += np.random.normal(0, noise)

        if paddle_y < ball_y - threshold:
            y[i] = [0,1,0]  # down
        elif paddle_y > ball_y + threshold:
            y[i] = [1,0,0]  # up
        else:
            y[i] = [0,0,1]  # stay

    return X.astype(np.float32), y

X_test, y_test = generate_pong_data(args.samples, args.noise, args.threshold)
true_labels = np.argmax(y_test, axis=1)

# -------------------------------
# Load model
# -------------------------------
if args.keras:
    print(f"{Fore.CYAN}Loading Keras model...{Style.RESET_ALL}")
    model = load_model(args.keras)
    preds = model.predict(X_test, verbose=0)
elif args.onnx:
    print(f"{Fore.CYAN}Loading ONNX model...{Style.RESET_ALL}")

    sess = ort.InferenceSession(
        args.onnx,
        providers=["CPUExecutionProvider"]
    )

    input_name = sess.get_inputs()[0].name
    output_name = sess.get_outputs()[0].name

    preds = sess.run(
        [output_name],
        {input_name: X_test}
    )[0]


else:
    print(f"{Fore.CYAN}Loading TFLite model...{Style.RESET_ALL}")
    interpreter = tf.lite.Interpreter(model_path=args.tflite)
    interpreter.allocate_tensors()
    inp = interpreter.get_input_details()[0]["index"]
    out = interpreter.get_output_details()[0]["index"]

    preds = []
    for x in X_test:
        interpreter.set_tensor(inp, x.reshape(1,5))
        interpreter.invoke()
        preds.append(interpreter.get_tensor(out)[0])
    preds = np.array(preds)

pred_labels = np.argmax(preds, axis=1)

# -------------------------------
# Accuracy
# -------------------------------
acc = np.mean(pred_labels == true_labels)
print(f"\n{Fore.GREEN}Test Accuracy: {acc*100:.2f}%{Style.RESET_ALL}\n")

# -------------------------------
# Show examples
# -------------------------------
names = ["LEFT", "RIGHT", "STAY"]

for i in range(min(args.show, args.samples)):
    correct = pred_labels[i] == true_labels[i]
    color = Fore.GREEN if correct else Fore.RED

    print(
        f"{color}"
        f"BallY={X_test[i][1]:.2f} PaddleY={X_test[i][4]:.2f} | "
        f"Pred={names[pred_labels[i]]} "
        f"True={names[true_labels[i]]}"
        f"{Style.RESET_ALL}"
    )

