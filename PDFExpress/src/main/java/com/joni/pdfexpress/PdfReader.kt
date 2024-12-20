package com.joni.pdfexpress

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.io.File

@Composable
fun PDFReader(fileDescriptor: ParcelFileDescriptor, maxWidth: Int, maxHeight: Int) {
    val pdfRenderer = PdfRenderer(fileDescriptor)

    LazyColumn(modifier = Modifier.fillMaxSize().offset(y = -8.dp)) {
        items(count = pdfRenderer.pageCount) { pageIndex ->
            val page = pdfRenderer.openPage(pageIndex)

            // Calculate the scale factor to fit the page within maxWidth and maxHeight
            val scaleFactor = minOf(
                maxWidth.toFloat() / page.width,
                maxHeight.toFloat() / page.height
            )

            // New dimensions based on the scaling factor
            val scaledWidth = (page.width * scaleFactor).toInt()
            val scaledHeight = (page.height * scaleFactor).toInt()

            // Create a scaled Bitmap
            val bitmap = Bitmap.createBitmap(
                scaledWidth,
                scaledHeight,
                Bitmap.Config.ARGB_8888
            )

            // Set up a Canvas with a white background
            val canvas = Canvas(bitmap)
            canvas.drawColor(Color.WHITE) // Avoid transparency issues

            // Render the page onto the scaled Bitmap
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            page.close() // Always close the page to free resources

            // Display the Bitmap
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "PDF page number: $pageIndex",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }

    // Clean up resources when the composable is disposed
    DisposableEffect(Unit) {
        onDispose {
            pdfRenderer.close()
            fileDescriptor.close()
        }
    }
}
