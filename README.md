# PDFExpress Library

PDFExpress is a Kotlin library designed to render PDF files efficiently in Android applications. It allows for downloading, rendering, and displaying PDFs with enhanced features like fixed page sizes, high-quality rendering, and support for remote URLs.

---

## Features

- Render PDFs from local files or remote URLs.
- High-quality bitmap rendering for pages.
- Fixed maximum page size for consistent layouts.
- Easy-to-use Composable integration with Jetpack Compose.
- Cache handling for optimized performance.

---

## Setup

### Permissions

Add the following permissions to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

---

## Usage

### Rendering PDFs from Local Files

```kotlin
@Composable
fun PDFReaderScreen(fileUri: Uri, context: Context) {
    val file = File(fileUri.path ?: return)
    PDFReader(file = file)
}

@Composable
fun PDFReader(file: File) {
    Box(modifier = Modifier.fillMaxSize()) {
        val pdfRender = remember {
            PdfRender(fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY))
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(count = pdfRender.pageCount) { index ->
                Image(
                    bitmap = pdfRender.getPageBitmap(index).asImageBitmap(),
                    contentDescription = "PDF page $index",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                pdfRender.close()
            }
        }
    }
}
```

### Rendering PDFs from a URL

```kotlin
@Composable
fun PDFReaderFromUrl(url: String) {
    val context = LocalContext.current
    val localFile = remember { mutableStateOf<File?>(null) }
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(url) {
        val job = coroutineScope.launch {
            try {
                val file = downloadPdfFromUrl(context, url)
                localFile.value = file
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        onDispose {
            job.cancel()
        }
    }

    if (localFile.value != null) {
        PDFReaderScreen(fileUri = Uri.fromFile(localFile.value!!), context = context)
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

suspend fun downloadPdfFromUrl(context: Context, url: String): File {
    return withContext(Dispatchers.IO) {
        val file = File(context.cacheDir, "temp.pdf")
        try {
            val connection = URL(url).openConnection()
            connection.connectTimeout = 10000
            connection.readTimeout = 15000
            connection.getInputStream().use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            throw IOException("Failed to download file: ${e.message}")
        }
    }
}
```

---

## Helper Classes

### PdfRender

```kotlin
internal class PdfRender(
    private val fileDescriptor: ParcelFileDescriptor
) {
    private val pdfRenderer = PdfRenderer(fileDescriptor)
    val pageCount get() = pdfRenderer.pageCount

    fun getPageBitmap(index: Int): Bitmap {
        val page = pdfRenderer.openPage(index)
        val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        page.close()
        return bitmap
    }

    fun close() {
        pdfRenderer.close()
        fileDescriptor.close()
    }
}
```

---

## Debugging Tips

1. **Ensure Network Permissions**: Validate the `INTERNET` permission is added.
2. **Test URLs**: Check the provided URL in a browser to confirm accessibility.
3. **Log Errors**: Use `Log.e` to print exceptions during downloads or rendering.

---

## License

This library is open-source and free to use under the MIT License. See the LICENSE file for details.
