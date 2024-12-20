package com.joni.pdfexpress

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.graphics.createBitmap
import java.io.File


internal class PdfRender(
    private val fileDescriptor: ParcelFileDescriptor
) {
    private val pdfRenderer = PdfRenderer(fileDescriptor)
    val pageCount: Int get() = pdfRenderer.pageCount

    val pageLists: List<Page> = List(pageCount) {
        Page(it, pdfRenderer)
    }

    fun close() {
        pageLists.forEach { it.recycle() }
        pdfRenderer.close()
        fileDescriptor.close()
    }

    class Page(
        val index: Int,
        private val pdfRenderer: PdfRenderer
    ) {
        val pageContent: Bitmap by lazy { createBitmap() }

        private fun createBitmap(): Bitmap {
            pdfRenderer.openPage(index).use { page ->
                val bitmap = Bitmap.createBitmap(
                    page.width,
                    page.height,
                    Bitmap.Config.ARGB_8888
                )
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                return bitmap
            }
        }

        fun recycle() {
            if (!pageContent.isRecycled) {
                pageContent.recycle()
            }
        }
    }
}
