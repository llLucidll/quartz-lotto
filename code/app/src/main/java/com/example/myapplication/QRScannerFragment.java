package com.example.myapplication;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.*;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Fragment responsible for scanning QR codes to navigate entrants to the Event Signup page.
 */
public class QRScannerFragment extends Fragment {

    private static final String TAG = "QRScannerFragment";
    private PreviewView previewView;
    private ImageButton flashToggleButton;
    private boolean isFlashOn = false;
    private boolean isScanning = true;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ExecutorService cameraExecutor;

    private BarcodeScanner barcodeScanner;

    private Camera camera;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_scanner, container, false);

        // Initialize UI components
        previewView = view.findViewById(R.id.previewView);
        flashToggleButton = view.findViewById(R.id.flash_toggle_button);

        cameraExecutor = Executors.newSingleThreadExecutor();

        // Configure ML Kit's Barcode Scanner for QR codes
        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                                Barcode.FORMAT_QR_CODE)
                        .build();
        barcodeScanner = BarcodeScanning.getClient(options);

        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());

        // Start the camera
        startCamera();

        // Set up flash toggle button listener
        flashToggleButton.setOnClickListener(v -> toggleFlash());

        return view;
    }

    /**
     * Initializes and binds the camera use cases.
     */
    private void startCamera() {
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error starting camera", e);
                Toast.makeText(getContext(), "Error starting camera.", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    /**
     * Binds the camera use cases: Preview and ImageAnalysis.
     *
     * @param cameraProvider The camera provider.
     */
    private void bindCameraUseCases(ProcessCameraProvider cameraProvider) {

        // Preview Use Case
        Preview preview = new Preview.Builder()
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // Image Analysis Use Case
        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        imageAnalysis.setAnalyzer(cameraExecutor, imageProxy -> {
            processImageProxy(imageProxy);
        });

        // Select back camera as default
        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

        // Unbind all use cases before rebinding
        cameraProvider.unbindAll();

        try {
            // Bind use cases to camera lifecycle
            camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalysis);
        } catch (Exception e) {
            Log.w(TAG, "Back camera not available, trying front camera.", e);
            try {
                cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
                camera = cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageAnalysis);
            } catch (Exception ex) {
                Log.e(TAG, "No available camera found.", ex);
                Toast.makeText(getContext(), "No available camera found on this device.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Processes each frame from the camera to detect QR codes.
     *
     * @param imageProxy The image proxy containing the frame.
     */
    @OptIn(markerClass = ExperimentalGetImage.class)
    private void processImageProxy(ImageProxy imageProxy) {
        if (!isScanning) {
            imageProxy.close();
            return;
        }

        Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());

            barcodeScanner.process(image)
                    .addOnSuccessListener(barcodes -> {
                        for (Barcode barcode : barcodes) {
                            String rawValue = barcode.getRawValue();
                            if (rawValue != null) {
                                Log.d(TAG, "Scanned QR Code: " + rawValue);
                                isScanning = false; // Stop further scanning
                                handleScannedData(rawValue);
                                break;
                            }
                        }
                        imageProxy.close();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Barcode scanning failed", e);
                        imageProxy.close();
                    });
        }
    }

    /**
     * Handles the scanned QR code data by parsing the eventId and navigating to EventSignupActivity.
     *
     * @param data The raw data from the scanned QR code.
     */
    private void handleScannedData(String data) {
        Log.d(TAG, "Scanned Data: " + data);
        if (data.startsWith("eventapp://event/")) {
            String eventId = data.substring(data.lastIndexOf('/') + 1);
            Log.d(TAG, "Parsed Event ID: " + eventId);

            // Open EventSignupActivity with the parsed eventId
            Intent intent = new Intent(getActivity(), EventSignupActivity.class);
            intent.putExtra("eventId", eventId);
            startActivity(intent);
        } else {
            Log.e(TAG, "Invalid QR code format.");
            Toast.makeText(getContext(), "Invalid QR code", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Toggles the camera flash on or off.
     */
    private void toggleFlash() {
        if (camera == null) {
            Toast.makeText(getContext(), "Camera not initialized", Toast.LENGTH_SHORT).show();
            return;
        }

        if (camera.getCameraInfo().hasFlashUnit()) {
            camera.getCameraControl().enableTorch(!isFlashOn);
            isFlashOn = !isFlashOn;

            if (isFlashOn) {
                flashToggleButton.setImageResource(R.drawable.ic_flash_on);
            } else {
                flashToggleButton.setImageResource(R.drawable.ic_flash_off);
            }
        } else {
            Toast.makeText(getContext(), "Flash not available on this device", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isScanning = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
        barcodeScanner.close();
    }
}
