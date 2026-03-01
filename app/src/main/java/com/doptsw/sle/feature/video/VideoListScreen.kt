@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.doptsw.sle.feature.video

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import java.util.Locale

@Composable
fun VideoListScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val videos = remember { VIDEO_ITEMS }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("도움이 되는 영상") },
                navigationIcon = { TextButton(onClick = onBack) { Text("<") } }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(videos) { video ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { openInYoutubeApp(context, video.url) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = video.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = video.url,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF5E6A7C)
                        )
                    }
                }
            }
        }
    }
}

private data class VideoItem(
    val title: String,
    val url: String
)

private val VIDEO_ITEMS: List<VideoItem> = listOf(
    VideoItem(
        title = "How to Stop Overthinking and Anxiety",
        url = "https://www.youtube.com/shorts/KFVO2_Ej9Eg"
    ),
    VideoItem(
        title = "4 tips to combat chronic stress",
        url = "https://www.youtube.com/shorts/q0DfRGcUDio"
    ),
    VideoItem(
        title = "How to improve your mental health",
        url = "https://www.youtube.com/shorts/h_Hz74BLSnM"
    ),
    VideoItem(
        title = "How to overcome anxiety WITHOUT medication",
        url = "https://www.youtube.com/shorts/sLkpNdzLIwQ"
    ),
    VideoItem(
        title = "How To Stress Relief",
        url = "https://www.youtube.com/shorts/SIkz7QvzpHI"
    ),
    VideoItem(
        title = "Tips to managing stress.",
        url = "https://www.youtube.com/shorts/3TPfi3t7HcE"
    )
)

private fun openInYoutubeApp(context: Context, url: String) {
    val appIntent = Intent(Intent.ACTION_VIEW).apply {
        setPackage("com.google.android.youtube")
        data = Uri.parse(url)
    }

    try {
        context.startActivity(appIntent)
    } catch (_: ActivityNotFoundException) {
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try {
            context.startActivity(webIntent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(context, "영상을 열 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}

private fun extractYoutubeVideoId(url: String): String? {
    val uri = Uri.parse(url)
    val host = uri.host?.lowercase(Locale.US).orEmpty()
    val segments = uri.pathSegments

    if (host.contains("youtube.com") && segments.size >= 2 && segments[0] == "shorts") {
        return segments[1]
    }
    if (host == "youtu.be" && segments.isNotEmpty()) {
        return segments[0]
    }
    if (host.contains("youtube.com")) {
        return uri.getQueryParameter("v")
    }
    return null
}
