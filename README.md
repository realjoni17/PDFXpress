# PDFExpress Library

PDFExpress is a Kotlin library designed to render PDF files efficiently in Android applications. It allows for downloading, rendering, and displaying PDFs with enhanced features like fixed page sizes, high-quality rendering, and support for remote URLs.

---
[![](https://jitpack.io/v/realjoni17/PDFXpress.svg)](https://jitpack.io/#realjoni17/PDFXpress)

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

### Rendering PDFs from URI

```kotlin
@Composable
fun PDFScreen(fileUri: Uri, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        TextButton(onClick = onBack) {
            Text("Back")
        }
        PDFReaderScreen(fileUri = fileUri, context = LocalContext.current, onBack = onBack)
    }
}
```

### Rendering PDFs from a URL

```kotlin
@Composable
fun PdfFromUrl(url: String, onBack: () -> Unit) {
    Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PDFReaderFromUrl(url = url, onBack = onBack)
            }
        }

```

---

## Dependencies

### Add this to root build.gradle

```kotlin
	dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}

```
### Add this to build.gradle(app)
	dependencies {
	        implementation 'com.github.realjoni17:PDFXpress:1.0.0'
	}

---

## Debugging Tips

1. **Ensure Network Permissions**: Validate the `INTERNET` permission is added.
2. **Test URLs**: Check the provided URL in a browser to confirm accessibility.
3. **Log Errors**: Use `Log.e` to print exceptions during downloads or rendering.

---

## License

This library is open-source and free to use under the MIT License. See the LICENSE file for details.
