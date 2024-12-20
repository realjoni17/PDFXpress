package com.joni.pdfexpress

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.Image

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.PointerIcon.Companion.Text
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsProperties.Text
import androidx.compose.ui.text.input.KeyboardType.Companion.Text
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.net.URL

@Composable
fun PDFReaderScreen(fileUri: Uri, context: Context, onBack: () -> Unit) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp // Screen width in dp
    val screenHeight = configuration.screenHeightDp // Screen height in dp

    // Convert dp to pixels
    val density = LocalDensity.current
    val maxWidth = with(density) { screenWidth.dp.toPx().toInt() }
    val maxHeight = with(density) { screenHeight.dp.toPx().toInt() }
    Box(modifier = Modifier.fillMaxSize()) {
        // Back button
        TextButton(onClick = onBack, modifier = Modifier.align(Alignment.TopStart)) {
            Text("Back")
        }

        // Open PDF file with ContentResolver
        val fileDescriptor = context.contentResolver.openFileDescriptor(fileUri, "r")
        if (fileDescriptor != null) {
            PDFReader(fileDescriptor,maxWidth,maxHeight)
        } else {
            Text(
                text = "Failed to load PDF.",
                modifier = Modifier.align(Alignment.Center),
                color = Color.Red
            )
        }
    }
}

@Composable
fun PDFReaderFromUrl(url: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val localFile = remember { mutableStateOf<File?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Download the PDF when this composable is first launched
    DisposableEffect(url) {
        val job = coroutineScope.launch {
            localFile.value = downloadPdfFromUrl(context, url)
        }

        onDispose {
            job.cancel()
        }
    }

    if (localFile.value != null) {
        // Render the downloaded PDF
        PDFReaderScreen(fileUri = Uri.fromFile(localFile.value!!), context = context, onBack = onBack )
    } else {
        // Show a loading indicator while downloading
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

suspend fun downloadPdfFromUrl(context: Context, url: String): File? {
    return withContext(Dispatchers.IO) {
        try {
            // Create a temporary file
            val file = File(context.cacheDir, "temp.pdf")
            file.outputStream().use { output ->
                val connection = URL(url).openConnection()
                connection.getInputStream().use { input ->
                    input.copyTo(output)
                }
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
