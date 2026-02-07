#!/usr/bin/env python3
import argparse
import numpy as np
import torch
import torch.nn as nn
import torch.optim as optim
from colorama import Fore, Style, init
from torch.utils.data import TensorDataset, DataLoader

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


def generate_pong_data(num_samples, noise=0.02, threshold=0.05):
    X = np.random.rand(num_samples, 5).astype(np.float32)
    y = np.zeros((num_samples,), dtype=np.int64)

    for i in range(num_samples):
        ball_x, ball_y, vel_x, vel_y, paddle_y = X[i]

        ball_y_noisy = np.clip(ball_y + np.random.normal(0, noise), 0, 1)
        paddle_y_noisy = np.clip(paddle_y + np.random.normal(0, noise), 0, 1)

        if paddle_y_noisy < ball_y_noisy - threshold:
            y[i] = 1  # down
        elif paddle_y_noisy > ball_y_noisy + threshold:
            y[i] = 0  # up
        else:
            y[i] = 2  # stay

    idx = np.random.permutation(num_samples)
    return X[idx], y[idx]

class PongNet(nn.Module):
    def __init__(self):
        super().__init__()
        self.net = nn.Sequential(
            nn.Linear(5, 64),
            nn.ReLU(),
            nn.Linear(64, 64),
            nn.ReLU(),
            nn.Linear(64, 3)
        )

    def forward(self, x):
        return self.net(x)

def train(model, loader, epochs, device):
    model.train()
    criterion = nn.CrossEntropyLoss()
    optimizer = optim.Adam(model.parameters(), lr=1e-3)
    for epoch in range(epochs):
        total_loss = 0
        correct = 0
        for x, y in loader:
            x, y = x.to(device), y.to(device)
            optimizer.zero_grad()
            logits = model(x)
            loss = criterion(logits, y)
            loss.backward()
            optimizer.step()
            total_loss += loss.item()
            correct += (logits.argmax(1) == y).sum().item()
        acc = correct / len(loader.dataset)
        print(f"Epoch {epoch+1}: loss={total_loss:.3f}, acc={acc:.3f}")

device = "cuda" if torch.cuda.is_available() else "cpu"

X, y = generate_pong_data(args.samples, args.noise, args.threshold)
dataset = TensorDataset(torch.from_numpy(X), torch.from_numpy(y))
loader = DataLoader(dataset, batch_size=args.batch, shuffle=True)

model = PongNet().to(device)
train(model, loader, args.epochs, device)

torch.save(model.state_dict(), "pong_model.pth")

dummy_input = torch.randn(1, 5)

torch.onnx.export(
    model.cpu(),
    dummy_input,
    "pong_model.onnx",
    input_names=["input"],
    output_names=["output"],
    opset_version=17,
    dynamic_axes={
        "input": {0:"batch"},
        "output": {0:"batch"}
    }
)
