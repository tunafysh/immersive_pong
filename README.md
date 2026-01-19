# Immersive Pong

Welcome to the Immersive Pong project! This application combines classic gaming with modern web technologies to create an engaging experience.

## ⚠️ Deprecation Notice


> ### **Note**
> The Tauri version of this project will not be maintained. Please consider using the Flutter or web versions for the latest features and updates.

## Project Structure
- **AI/**: Contains the machine learning models and Python scripts for game logic.
- **flutter/**: Contains the Flutter application for mobile deployment.
- **tauri/**: Contains the Tauri application for desktop deployment.
- **build/**: Contains build artifacts for various platforms.

## Getting Started

### Prerequisites
- Python 3.x
- Node.js
- Flutter SDK
- Tauri CLI

### Installation
1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd immersive_pong
   ```

2. Install Python dependencies:
   ```bash
   pip install -r AI/requirements.txt
   ```

3. Install Node.js dependencies:
   ```bash
   cd tauri
   npm install
   ```

### Running the Project
- For the AI backend:
  ```bash
  python AI/main.py
  ```

- For the Flutter app:
  ```bash
  cd flutter
  flutter run
  ```

- For the Tauri app:
  ```bash
  cd tauri
  tauri dev
  ```

### Note
The Tauri version of this project will not be maintained. Please consider using the web or mobile versions for the latest features and updates.

## Contributing
We welcome contributions! Please read our [contributing guidelines](CONTRIBUTING.md) for more information.

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.