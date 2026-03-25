import UIKit
import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        let controller = MainViewControllerKt.MainViewController()
        
        // Setup volume button monitoring
        VolumeButtonHandler.shared.onVolumeUp = {
            VolumeButtonStateKt.setVolumeButtonPressed(button: .up)
        }
        VolumeButtonHandler.shared.onVolumeDown = {
            VolumeButtonStateKt.setVolumeButtonPressed(button: .down)
        }
        VolumeButtonHandler.shared.startMonitoring()
        
        return controller
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea()
            .onDisappear {
                VolumeButtonHandler.shared.stopMonitoring()
            }
    }
}
