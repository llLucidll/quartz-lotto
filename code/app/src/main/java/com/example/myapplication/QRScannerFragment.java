package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.*;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;

import com.example.myapplication.R;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.*;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QRScannerFragment extends Fragment {

    private static final String TAG = "QRScannerFragment";
    private PreviewView previewView;
    private ImageButton flashToggleButton;
    private boolean isFlashOn = false;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ExecutorService cameraExecutor;

    private ActivityResultLauncher<String> requestPermissionLauncher;

    private BarcodeScanner barcodeScanner;

    private Camera camera;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_scanner, container, false);

        //ui
        previewView = view.findViewById(R.id.previewView);
        flashToggleButton = view.findViewById(R.id.flash_toggle_button);


        cameraExecutor = Executors.newSingleThreadExecutor();

        //ML Kit's Barcode Scanner
        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                                Barcode.FORMAT_QR_CODE)
                        .build();
        barcodeScanner = BarcodeScanning.getClient(options);


        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());

        setupPermissionLauncher();

        //request camera permission if not granted
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }

        // Set up flash toggle button
        flashToggleButton.setOnClickListener(v -> toggleFlash());

        return view;
    }

    private void setupPermissionLauncher() {
        requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        //permission granted> Start camera
                        startCamera();
                    } else {
                        Toast.makeText(getContext(), "Camera permission is required to scan QR codes", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean allPermissionsGranted() {
        return ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void startCamera() {
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error starting camera", e);
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void bindCameraUseCases(ProcessCameraProvider cameraProvider) {


        Preview preview = new Preview.Builder()
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());


        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        imageAnalysis.setAnalyzer(cameraExecutor, imageProxy -> {
            processImageProxy(imageProxy);
        });


        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;


        cameraProvider.unbindAll();

        try {

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

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void processImageProxy(ImageProxy imageProxy) {
        Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            InputImage image =
                    InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());

            //pass Image to ML Kit's Barcode Scanner
            barcodeScanner.process(image)
                    .addOnSuccessListener(barcodes -> {
                        for (Barcode barcode : barcodes) {
                            String rawValue = barcode.getRawValue();
                            if (rawValue != null) {
                                Log.d(TAG, "Scanned QR Code: " + rawValue);
                                Toast.makeText(getContext(), "Scanned: " + rawValue, Toast.LENGTH_SHORT).show();
                                // Will handle qr code scanning here
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Barcode scanning failed", e);
                    })
                    .addOnCompleteListener(task -> {
                        imageProxy.close();
                    });
        }
    }

    private void toggleFlash() {
        if (camera == null) {
            Toast.makeText(getContext(), "Camera not initialized", Toast.LENGTH_SHORT).show();
            return;
        }

        if (camera.getCameraInfo().hasFlashUnit()) {
            camera.getCameraControl().enableTorch(!isFlashOn);
            isFlashOn = !isFlashOn;

            // Update Flash Icon
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
    public void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
        barcodeScanner.close();
    }
}
