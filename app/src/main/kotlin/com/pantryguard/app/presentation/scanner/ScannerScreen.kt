package com.pantryguard.app.presentation.scanner

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ScannerScreen(
    viewModel: ScannerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(
            onBarcodeScanned = { barcode ->
                viewModel.onBarcodeScanned(barcode)
            }
        )

        when (val state = uiState) {
            is ScannerUiState.Scanning -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is ScannerUiState.Success -> {
                LaunchedEffect(state) {
                    snackbarHostState.showSnackbar("Found: ${state.productName}")
                }
            }
            is ScannerUiState.Error -> {
                LaunchedEffect(state) {
                    snackbarHostState.showSnackbar(state.message)
                }
            }
            ScannerUiState.Idle -> {}
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
