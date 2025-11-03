package com.example.newsreader.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.newsreader.data.model.Article
import com.example.newsreader.data.model.Source
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NewsCard(
    article: Article,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            article.urlToImage?.let { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = article.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
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
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = formatDate(article.publishedAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                article.description?.let { description ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
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

        val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        date?.let { formatter.format(it) } ?: dateString
    } catch (_: Exception) {
        dateString
    }
}

@Preview(showBackground = true)
@Composable
fun NewsCardPreview() {
    MaterialTheme {
        NewsCard(
            article = Article(
                source = Source(id = "techcrunch", name = "TechCrunch"),
                author = "John Doe",
                title = "Breaking: New AI Technology Revolutionizes Mobile Development",
                description = "A groundbreaking new artificial intelligence system has been developed that promises to transform how developers create mobile applications, making the process faster and more efficient than ever before.",
                url = "https://example.com/article",
                urlToImage = "https://picsum.photos/seed/news1/800/600",
                publishedAt = "2025-11-03T10:30:00Z",
                content = "Full article content..."
            ),
            onClick = { /* Preview action */ }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NewsCardWithoutImagePreview() {
    MaterialTheme {
        NewsCard(
            article = Article(
                source = Source(id = "bbc-news", name = "BBC News"),
                author = "Jane Smith",
                title = "Global Markets Show Strong Recovery Signs",
                description = "Financial markets worldwide are showing positive indicators as economic conditions improve across major economies.",
                url = "https://example.com/article2",
                urlToImage = null,
                publishedAt = "2025-11-02T15:45:00Z",
                content = "Full article content..."
            ),
            onClick = { /* Preview action */ }
        )
    }
}

