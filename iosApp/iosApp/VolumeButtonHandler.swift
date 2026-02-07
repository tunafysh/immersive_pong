import AVFoundation
import MediaPlayer
import UIKit

class VolumeButtonHandler: NSObject {
    static let shared = VolumeButtonHandler()
    
    private var audioSession: AVAudioSession?
    private var volumeView: MPVolumeView?
    private var initialVolume: Float = 0.5
    private var isMonitoring = false
    
    var onVolumeUp: (() -> Void)?
    var onVolumeDown: (() -> Void)?
    
    override init() {
        super.init()
        setupAudioSession()
    }
    
    private func setupAudioSession() {
        audioSession = AVAudioSession.sharedInstance()
        
        do {
            try audioSession?.setActive(true)
        } catch {
            print("Failed to activate audio session: \(error)")
        }
    }
    
    func startMonitoring() {
        guard !isMonitoring else { return }
        isMonitoring = true
        
        // Create hidden volume view to intercept volume changes
        volumeView = MPVolumeView(frame: CGRect(x: -1000, y: -1000, width: 1, height: 1))
        if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
           let window = windowScene.windows.first {
            window.addSubview(volumeView!)
        }
        
        // Get current volume
        if let audioSession = audioSession {
            initialVolume = audioSession.outputVolume
        }
        
        // Observe volume changes
        audioSession?.addObserver(self, forKeyPath: "outputVolume", options: [.new, .old], context: nil)
    }
    
    func stopMonitoring() {
        guard isMonitoring else { return }
        isMonitoring = false
        
        audioSession?.removeObserver(self, forKeyPath: "outputVolume")
        volumeView?.removeFromSuperview()
        volumeView = nil
    }
    
    override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
        if keyPath == "outputVolume" {
            guard let newValue = change?[.newKey] as? Float,
                  let oldValue = change?[.oldKey] as? Float else { return }
            
            // Detect direction
            if newValue > oldValue {
                onVolumeUp?()
                resetVolume()
            } else if newValue < oldValue {
                onVolumeDown?()
                resetVolume()
            }
        }
    }
    
    private func resetVolume() {
        // Reset volume to initial value to allow continuous presses
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) { [weak self] in
            guard let self = self else { return }
            let volumeSlider = self.volumeView?.subviews.first(where: { $0 is UISlider }) as? UISlider
            volumeSlider?.value = self.initialVolume
        }
    }
    
    deinit {
        stopMonitoring()
    }
}
