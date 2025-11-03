package com.example.newsreader.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.newsreader.data.model.Article
import com.example.newsreader.data.model.Source
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(
    article: Article,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Article Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            article.urlToImage?.let { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = article.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = article.source.name,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = formatDate(article.publishedAt),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = article.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                article.author?.let { author ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "By $author",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                article.description?.let { description ->
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                article.content?.let { content ->
                    Text(
                        text = content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        parser.timeZone = TimeZone.getTimeZone("UTC")
        val date = parser.parse(dateString)

        val formatter = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
        date?.let { formatter.format(it) } ?: dateString
    } catch (_: Exception) {
        dateString
    }
}

// Preview 函數
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NewsDetailScreenPreview() {
    MaterialTheme {
        NewsDetailScreen(
            article = Article(
                source = Source(
                    id = "techcrunch",
                    name = "TechCrunch"
                ),
                author = "John Doe",
                title = "Breaking: Revolutionary AI Technology Transforms Mobile Development",
                description = "A comprehensive look at how new artificial intelligence tools are reshaping the way developers build mobile applications, making it faster and more efficient than ever before.",
                url = "https://example.com/article",
                urlToImage = "https://picsum.photos/800/400",
                publishedAt = "2025-11-03T10:30:00Z",
                content = "The world of mobile development is experiencing a significant transformation with the introduction of advanced AI-powered tools. These innovations are not only streamlining the development process but also enhancing the quality and performance of mobile applications across all platforms..."
            ),
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NewsDetailScreenWithoutImagePreview() {
    MaterialTheme {
        NewsDetailScreen(
            article = Article(
                source = Source(
                    id = "bbc-news",
                    name = "BBC News"
                ),
                author = null,
                title = "Global Climate Summit Reaches Historic Agreement",
                description = "World leaders have come together to sign a groundbreaking climate accord.",
                url = "https://example.com/article",
                urlToImage = null,
                publishedAt = "2025-11-02T15:45:00Z",
                content = "After weeks of intense negotiations, representatives from over 190 countries have reached a consensus on new climate action targets. The agreement includes commitments to reduce carbon emissions and invest in renewable energy infrastructure."
            ),
            onBackClick = {}
        )
    }
}

