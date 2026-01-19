#!/usr/bin/env python3

import argparse
import numpy as np
import os
from colorama import Fore, Style, init
import warnings
init(autoreset=True)

# Silence TF warnings
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3'
warnings.filterwarnings("ignore")

import tensorflow as tf
from tensorflow.keras.layers import Dense
from tensorflow.keras.models import Sequential
import tf2onnx


# -------------------------------
# CLI arguments
# -------------------------------
parser = argparse.ArgumentParser(description=f"{Fore.YELLOW}Train a Pong AI and export as TFLite{Style.RESET_ALL}")
parser.add_argument("--samples", type=int, default=20000, help="Number of training samples")
parser.add_argument("--epochs", type=int, default=100, help="Number of training epochs")
parser.add_argument("--batch", type=int, default=64, help="Batch size")
parser.add_argument("--noise", type=float, default=0.02, help="Noise level in inputs")
parser.add_argument("--threshold", type=float, default=0.05, help="Decision threshold for labels")
parser.add_argument("--threads", type=int, default=4, help="Number of CPU threads for TensorFlow")
parser.add_argument("--tflite", type=str, default="pong_model.tflite", help="Output TFLite filename")
parser.add_argument("--keras", type=str, default="pong_model.keras", help="Optional Keras model filename")
args = parser.parse_args()

# -------------------------------
# Enable multicore CPU
# -------------------------------
os.environ["OMP_NUM_THREADS"] = str(args.threads)
os.environ["TF_NUM_INTRAOP_THREADS"] = str(args.threads)
os.environ["TF_NUM_INTEROP_THREADS"] = str(args.threads)

print(f"{Fore.CYAN}Generating realistic training data...{Style.RESET_ALL}")

# -------------------------------
# Generate realistic dataset
# -------------------------------
def generate_pong_data(num_samples, noise=0.02, threshold=0.05):
    """
    Generate a dataset of plausible Pong states.
    Inputs: [ball_x, ball_y, vel_x, vel_y, paddle_y]
    Outputs: [up, down, stay] one-hot
    """
    X = np.random.rand(num_samples, 5)  # Random positions & velocities
    y = np.zeros((num_samples, 3))      # One-hot labels

    for i in range(num_samples):
        ball_x, ball_y, vel_x, vel_y, paddle_y = X[i]

        # Add small noise for generalization
        ball_y_noisy = np.clip(ball_y + np.random.normal(0, noise), 0, 1)
        paddle_y_noisy = np.clip(paddle_y + np.random.normal(0, noise), 0, 1)

        # Decide paddle move based on ball vs paddle position
        if paddle_y_noisy < ball_y_noisy - threshold:
            y[i] = [0,1,0]  # down
        elif paddle_y_noisy > ball_y_noisy + threshold:
            y[i] = [1,0,0]  # up
        else:
            y[i] = [0,0,1]  # stay

    # Shuffle dataset
    indices = np.arange(num_samples)
    np.random.shuffle(indices)
    return X[indices], y[indices]

X_train, y_train = generate_pong_data(args.samples, args.noise, args.threshold)
X_train = np.clip(X_train, 0, 1)
print(f"{Fore.GREEN}Training data ready!{Style.RESET_ALL}")

# -------------------------------
# Define model
# -------------------------------
model = Sequential([
    Dense(64, activation='relu', input_shape=(5,)),
    Dense(64, activation='relu'),
    Dense(3, activation='softmax')
])
model.compile(optimizer='adam', loss='categorical_crossentropy', metrics=['accuracy'])
print(f"{Fore.CYAN}Model compiled.{Style.RESET_ALL}")

# -------------------------------
# Train model
# -------------------------------
print(f"{Fore.YELLOW}Training started...{Style.RESET_ALL}")
history = model.fit(X_train, y_train, epochs=args.epochs, batch_size=args.batch, verbose=1)
print(f"{Fore.GREEN}Training complete!{Style.RESET_ALL}")

# -------------------------------
# Save Keras model
# -------------------------------
if args.keras:
    model.save(args.keras)
    print(f"{Fore.GREEN}Keras model saved as {args.keras}{Style.RESET_ALL}")

# -------------------------------
# Convert to TFLite
# -------------------------------
print(f"{Fore.YELLOW}Converting to TFLite...{Style.RESET_ALL}")

# Monkey-patch new API to ensure backward compatibility
if not hasattr(model, "output_names"):
    model.output_names = [out.name.split(":")[0] for out in model.outputs]

converter = tf.lite.TFLiteConverter.from_keras_model(model)
converter.optimizations = [tf.lite.Optimize.DEFAULT]
tflite_model = converter.convert()

with open(args.tflite, "wb") as f:
    f.write(tflite_model)

print(f"{Fore.GREEN}TFLite model saved as {args.tflite}{Style.RESET_ALL}")

# -------------------------------
# Convert to ONNX
# -------------------------------
# Define input signature (batch size can be None)
print(f"{Fore.YELLOW}Converting to ONNX...{Style.RESET_ALL}")
onnx_path = args.tflite.replace(".tflite", ".onnx")
spec = (tf.TensorSpec((None, 5), tf.float32, name="input"),)

# Convert
model_proto, _ = tf2onnx.convert.from_keras(model, input_signature=spec, opset=13)

# Write to file
with open(onnx_path, "wb") as f:
    f.write(model_proto.SerializeToString())

print(f"{Fore.GREEN}ONNX model saved as {onnx_path}\033[0m")

print(f"{Fore.MAGENTA}All done! {Style.RESET_ALL}")

