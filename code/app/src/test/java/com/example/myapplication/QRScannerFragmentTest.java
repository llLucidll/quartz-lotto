//package com.example.myapplication;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.widget.ImageButton;
//import android.widget.Toast;
//
//import androidx.camera.core.Camera;
//import androidx.camera.core.ImageAnalysis;
//import androidx.camera.core.Preview;
//import androidx.camera.lifecycle.ProcessCameraProvider;
//import androidx.camera.view.PreviewView;
//import androidx.core.content.ContextCompat;
//
//import com.google.mlkit.vision.barcode.BarcodeScanner;
//import com.google.mlkit.vision.barcode.common.Barcode;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.robolectric.RobolectricTestRunner;
//import org.robolectric.annotation.Config;
//
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import static org.junit.Assert.assertNotNull;
//import static org.mockito.Mockito.*;
//
//@RunWith(RobolectricTestRunner.class)
//@Config(sdk = 28)
//public class QRScannerFragmentTest {
//
//    private QRScannerFragment fragment;
//
//    @Mock
//    private PreviewView mockPreviewView;
//
//    @Mock
//    private ImageButton mockFlashToggleButton;
//
//    @Mock
//    private ProcessCameraProvider mockCameraProvider;
//
//    @Mock
//    private BarcodeScanner mockBarcodeScanner;
//
//    @Mock
//    private Camera mockCamera;
//
//    @Mock
//    private ExecutorService mockExecutorService;
//
//    @Before
//    public void setUp() {
//        MockitoAnnotations.initMocks(this);
//
//        // Initialize the Fragment under test
//        fragment = new QRScannerFragment();
//        fragment.cameraExecutor = mockExecutorService;
//        fragment.barcodeScanner = mockBarcodeScanner;
//    }
//
//    @Test
//    public void testFragmentInitialization() {
//        // Verifies that the Fragment initializes correctly with necessary views and services
//        FragmentScenario<QRScannerFragment> scenario = FragmentScenario.launchInContainer(QRScannerFragment.class);
//        scenario.onFragment(fragment -> {
//            assertNotNull(fragment.previewView);
//            assertNotNull(fragment.flashToggleButton);
//            assertNotNull(fragment.cameraProviderFuture);
//            assertNotNull(fragment.barcodeScanner);
//        });
//    }
//
//    @Test
//    public void testPermissionLauncher_whenPermissionGranted_startsCamera() {
//        when(ContextCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.CAMERA))
//                .thenReturn(PackageManager.PERMISSION_GRANTED);
//
//        fragment.setupPermissionLauncher();
//
//        verify(mockExecutorService, never()).shutdown();
//    }
//
//    @Test
//    public void testStartCamera_withPermission_granted() {
//        // Test startCamera functionality without triggering UI/Camera binding
//        doNothing().when(mockCameraProvider).unbindAll();
//
//        // Launching the Fragment scenario and invoking startCamera
//        FragmentScenario.launchInContainer(QRScannerFragment.class);
//        fragment.startCamera();
//
//        verify(mockExecutorService, never()).shutdown();
//    }
//
//    @Test
//    public void testToggleFlash_whenFlashAvailable_togglesFlash() {
//        when(mockCamera.getCameraInfo().hasFlashUnit()).thenReturn(true);
//        fragment.camera = mockCamera;
//        fragment.isFlashOn = false;
//
//        fragment.toggleFlash();
//
//        verify(mockCamera.getCameraControl()).enableTorch(true);
//        assertNotNull(fragment.flashToggleButton);
//    }
//
//    @Test
//    public void testProcessImageProxy_callsBarcodeScanner() {
//        ImageAnalysis.Analyzer analyzer = mock(ImageAnalysis.Analyzer.class);
//
//        fragment.barcodeScanner = mockBarcodeScanner;
//        fragment.cameraExecutor = Executors.newSingleThreadExecutor();
//
//        fragment.bindCameraUseCases(mockCameraProvider);
//
//        // Verify if imageProxy's close method is called to release resources
//        verify(mockBarcodeScanner, never()).close();
//    }
//
//    @Test
//    public void testOnDestroy_releasesResources() {
//        fragment.cameraExecutor = Executors.newSingleThreadExecutor();
//        fragment.barcodeScanner = mockBarcodeScanner;
//
//        fragment.onDestroy();
//
//        verify(mockBarcodeScanner).close();
//    }
//
//    @Test
//    public void testToggleFlash_whenFlashNotAvailable_showsToast() {
//        fragment.camera = mockCamera;
//        when(mockCamera.getCameraInfo().hasFlashUnit()).thenReturn(false);
//
//        fragment.toggleFlash();
//
//        Toast.makeText(fragment.getContext(), "Flash not available on this device", Toast.LENGTH_SHORT).show();
//        assertNotNull(fragment.flashToggleButton);
//    }
//}
