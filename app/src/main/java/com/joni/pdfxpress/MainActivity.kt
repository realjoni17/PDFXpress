package com.joni.pdfxpress

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.joni.pdfexpress.PDFReader
import com.joni.pdfexpress.PDFReaderFromUrl
import com.joni.pdfexpress.PDFReaderScreen
import com.joni.pdfxpress.ui.theme.PDFXpressTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        val url = "https://webhome.phy.duke.edu/~rgb/Class/intro_physics_1/intro_physics_1.pdf"
        val onBack = onBackPressedDispatcher
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PDFXpressTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    Column {
                        App(url)


                    }
                }
            }
        }
    }
}
@Composable
fun App(url: String) {
    val currentScreen = remember { mutableStateOf<Screen>(Screen.FilePicker) }

    when (val screen = currentScreen.value) {
        Screen.FilePicker -> FilePickerScreen(
            onFileSelected = { uri -> currentScreen.value = Screen.PDFReader(uri) },
            onUrlSelected = { currentScreen.value = Screen.PDFUrlReader(url) }
        )
        is Screen.PDFReader -> PDFScreen(
            fileUri = screen.uri,
            onBack = { currentScreen.value = Screen.FilePicker }
        )
        is Screen.PDFUrlReader -> PDFReaderFromUrl(
            url = screen.url,
            onBack = { currentScreen.value = Screen.FilePicker }
        )
    }
}

sealed class Screen {
    object FilePicker : Screen()
    data class PDFReader(val uri: Uri) : Screen()
    data class PDFUrlReader(val url: String) : Screen()
}

@Composable
fun FilePickerScreen(onFileSelected: (Uri) -> Unit, onUrlSelected: () -> Unit) {
    val context = LocalContext.current
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let { onFileSelected(it) }
        }
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            filePickerLauncher.launch(arrayOf("application/pdf"))
        }) {
            Text("Pick a PDF")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onUrlSelected) {
            Text("Open PDF from URL")
        }
    }
}

@Composable
fun PDFScreen(fileUri: Uri, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        TextButton(onClick = onBack) {
            Text("Back")
        }
        PDFReaderScreen(fileUri = fileUri,
            context = LocalContext.current,
            onBack = onBack)
    }
}

