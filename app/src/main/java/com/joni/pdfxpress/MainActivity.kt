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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.joni.pdfexpress.PDFReader
import com.joni.pdfexpress.PDFReaderFromUrl
import com.joni.pdfexpress.PDFReaderScreen
import com.joni.pdfxpress.ui.theme.PDFXpressTheme
import java.io.File

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PDFXpressTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
              //App()
                    val url = "https://s29.q4cdn.com/175625835/files/doc_downloads/test.pdf"
                    val uri = Uri.parse(url)
                   PDFReaderFromUrl(url = url, { onBackPressedDispatcher.onBackPressed() })


                }
            }
        }
    }
}
@Composable
fun App() {
    val currentScreen = remember { mutableStateOf<Screen>(Screen.FilePicker) }

    when (currentScreen.value) {
        Screen.FilePicker -> FilePickerScreen(
            onFileSelected = { uri ->
                currentScreen.value = Screen.PDFReader(uri)
            }
        )
        is Screen.PDFReader -> PDFScreen(
            fileUri = (currentScreen.value as Screen.PDFReader).uri,
            onBack = { currentScreen.value = Screen.FilePicker }
        )
    }
}

sealed class Screen {
    object FilePicker : Screen()
    data class PDFReader(val uri: Uri) : Screen()
}

@Composable
fun FilePickerScreen(onFileSelected: (Uri) -> Unit) {
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
    }
}

@Composable
fun PDFScreen(fileUri: Uri, onBack: () -> Unit) {
    val context = LocalContext.current
    Box(modifier = Modifier.fillMaxSize()) {
        TextButton(onClick = onBack) {
            Text("Back")
        }
       PDFReaderScreen(fileUri = fileUri, context, onBack)
    }
}
