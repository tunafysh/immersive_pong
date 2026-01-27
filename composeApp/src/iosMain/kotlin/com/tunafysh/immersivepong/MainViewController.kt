package com.tunafysh.immersivepong

import androidx.compose.ui.window.ComposeUIViewController
import androidx.compose.runtime.Composable
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIViewController
import platform.UIKit.UIViewAutoresizingFlexibleWidth
import platform.UIKit.UIViewAutoresizingFlexibleHeight
import platform.UIKit.addChildViewController
import platform.UIKit.didMoveToParentViewController
import platform.UIKit.setNeedsUpdateOfHomeIndicatorAutoHidden

class ImmersiveGameViewController : UIViewController(nibName = null, bundle = null) {

    var isImmersive: Boolean = true
        set(value) {
            field = value
            setNeedsStatusBarAppearanceUpdate()
            setNeedsUpdateOfHomeIndicatorAutoHidden()
        }

    override fun prefersStatusBarHidden(): Boolean = isImmersive

    fun prefersHomeIndicatorAutoHidden(): Boolean = isImmersive

    @OptIn(ExperimentalForeignApi::class)
    override fun viewDidLoad() {
        super.viewDidLoad()

        // Create Compose view controller
        val composeViewController = ComposeUIViewController {
            App()
        }

        // Add as child view controller
        addChildViewController(composeViewController)
        view.addSubview(composeViewController.view)

        // Access frame using proper interop
        composeViewController.view.setFrame(view.bounds)
        composeViewController.view.autoresizingMask =
            UIViewAutoresizingFlexibleWidth or UIViewAutoresizingFlexibleHeight

        composeViewController.didMoveToParentViewController(this)
    }

    companion object {
        var instance: ImmersiveGameViewController? = null
    }

    override fun viewDidAppear(animated: Boolean) {
        super.viewDidAppear(animated)
        instance = this
    }
}

@Composable
actual fun ImmersiveMode(enabled: Boolean) {
    ImmersiveGameViewController.instance?.isImmersive = enabled
}

fun MainViewController() = ImmersiveGameViewController()