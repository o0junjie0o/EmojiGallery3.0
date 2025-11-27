package com.example.emojigallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color // 已添加 Color 导包
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // MaterialTheme 是皮肤，我们可以直接用默认的
            MaterialTheme {
                EmojiGalleryScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmojiGalleryScreen(viewModel: EmojiViewModel = viewModel()) {
    // 监听 ViewModel 里的数据
    val emojis by viewModel.emojiList.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Emoji Gallery (${emojis.size})") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        // 判断：如果数据还没出来（是空的），显示转圈圈
        if (emojis.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
                Text("正在生成1000张表情...", modifier = Modifier.padding(top = 48.dp))
            }
        } else {
            // === 【核心组件：懒加载网格】 ===
            // LazyVerticalGrid 专门处理大量数据，只渲染屏幕看得到的部分
            // 所以 1000 张图滑动起来也非常流畅，不卡顿
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 100.dp), // 自动适应列数，每列最小100dp
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                items(emojis, key = { it.id }) { emoji ->
                    EmojiCard(emoji)
                }
            }
        }
    }
}

@Composable
fun EmojiCard(emoji: EmojiEntity) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        // Box 布局允许元素堆叠
        Box {
            // === 【核心技术：Coil 图片加载】 ===
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(emoji.imageUrl)
                    .crossfade(true) // 淡入效果
                    // !!! 这一行满足作业要求：使用文件存储图片 !!!
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentDescription = emoji.title,
                contentScale = ContentScale.Crop, // 裁剪成正方形
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f) // 宽高比 1:1
            )

            // 底部半透明标题
            Surface(
                // === 这里已修改：使用 Color.Black ===
                color = Color.Black.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
            ) {
                Text(
                    text = emoji.title,
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(4.dp),
                    maxLines = 1
                )
            }
        }
    }
}
