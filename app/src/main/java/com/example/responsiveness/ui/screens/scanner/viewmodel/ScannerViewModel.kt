package com.example.responsiveness.ui.screens.scanner.viewmodel

import androidx.camera.core.Camera
import androidx.camera.core.ImageCapture
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for Scanner screen. Manages camera state, permissions, and flash/camera toggling.
 */
class ScannerViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _hasPermissions = MutableStateFlow(false)
    val hasPermissions: StateFlow<Boolean> = _hasPermissions.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _imageCapture = MutableStateFlow<ImageCapture?>(null)
    val imageCapture: StateFlow<ImageCapture?> = _imageCapture.asStateFlow()

    private val _camera = MutableStateFlow<Camera?>(null)
    val camera: StateFlow<Camera?> = _camera.asStateFlow()

    private val _isFlashOn = MutableStateFlow(false)
    val isFlashOn: StateFlow<Boolean> = _isFlashOn.asStateFlow()

    private val _isUsingBackCamera = MutableStateFlow(true)
    val isUsingBackCamera: StateFlow<Boolean> = _isUsingBackCamera.asStateFlow()

    /** Called when camera permissions are granted. */
    fun onPermissionsGranted() { _hasPermissions.value = true; _isLoading.value = false }
    /** Called when camera permissions are denied. */
    fun onPermissionsDenied() { _hasPermissions.value = false; _isLoading.value = false; _errorMessage.value = "Camera permission is required" }
    /** Called when camera is ready. */
    fun onCameraReady(capture: ImageCapture?, cam: Camera?) { _imageCapture.value = capture; _camera.value = cam; _isLoading.value = false }
    /** Called when an error occurs. */
    fun onError(message: String?) { _errorMessage.value = message; _isLoading.value = false }
    /** Switches between front and back camera. */
    fun onSwitchCamera() { _isUsingBackCamera.value = !_isUsingBackCamera.value }
    /** Toggles the flash state. */
    fun onToggleFlash() { _isFlashOn.value = !_isFlashOn.value }
    /** Sets the flash state explicitly. */
    fun setFlashState(newState: Boolean) { _isFlashOn.value = newState }
}
