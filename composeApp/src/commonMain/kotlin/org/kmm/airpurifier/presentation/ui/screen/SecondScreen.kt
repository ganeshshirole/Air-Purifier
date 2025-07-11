package org.kmm.airpurifier.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SecondScreen(popBackStack: () -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ImagePagerWithIndicator(
                listOf(
                    "https://fastly.picsum.photos/id/778/800/400.jpg?hmac=ImrjaCrV1avloQNJtl2a7-5QNfhqrzctCC10ye8qte0",
                    "https://fastly.picsum.photos/id/103/800/400.jpg?hmac=LUxcXxT-06T0hr-KDkeeXaDIsB1CQevHG7AzLdF3XQU",
                    "https://fastly.picsum.photos/id/1056/800/400.jpg?hmac=YL-PEjQ3s9IjZbheiIL04rg1ygp4hfKFjxMgcobJWrM"
                )
            )
        }
    }
}


@Composable
fun ImagePagerWithIndicator(imageUrls: List<String>) {
    val pagerState = rememberPagerState { imageUrls.size }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentPadding = PaddingValues(horizontal = 32.dp), // gap on left & right
            pageSpacing = 16.dp // spacing between pages
        ) { page ->
            val url = imageUrls[page]
            AsyncImage(
                model = url,
                contentDescription = "Image $page",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .shadow(4.dp, RoundedCornerShape(16.dp))
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(imageUrls.size) { index ->
                val selected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (selected) 10.dp else 8.dp)
                        .background(
                            color = if (selected) Color.Blue else Color.Gray,
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

@Preview
@Composable
fun SecondScreenPreview() {
    SecondScreen {}
}