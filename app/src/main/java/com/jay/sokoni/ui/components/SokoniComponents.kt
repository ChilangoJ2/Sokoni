package com.jay.sokoni.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jay.sokoni.ui.theme.SokoniBlack
import com.jay.sokoni.ui.theme.SokoniGold
import com.jay.sokoni.ui.theme.SokoniGreen
import com.jay.sokoni.ui.theme.SokoniRed
import com.jay.sokoni.ui.theme.SokoniTheme
import com.jay.sokoni.ui.theme.SokoniWhite

@Composable
fun SokoniBadge(
    text: String,
    color: Color = SokoniGold,
    textColor: Color = SokoniBlack
) {
    Surface(
        color = color,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SokoniCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (onClick != null) {
        ElevatedCard(
            onClick = onClick,
            modifier = modifier,
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = SokoniWhite),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
            content = content
        )
    } else {
        ElevatedCard(
            modifier = modifier,
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = SokoniWhite),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
            content = content
        )
    }
}

@Composable
fun SokoniLoading() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = SokoniGold)
    }
}

@Composable
fun SokoniEmptyState(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
    }
}

@Composable
fun SokoniErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, color = SokoniRed)
        Spacer(modifier = Modifier.height(16.dp))
        SokoniSecondaryButton(text = "Retry", onClick = onRetry)
    }
}

@Composable
fun SokoniStatusIndicator(
    status: String,
    isActive: Boolean
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(if (isActive) SokoniGreen else Color.Gray)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = status, style = MaterialTheme.typography.labelMedium)
    }
}

@Preview(showBackground = true)
@Composable
fun ComponentsPreview() {
    SokoniTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            SokoniBadge(text = "Verified")
            Spacer(modifier = Modifier.height(8.dp))
            SokoniStatusIndicator(status = "Active", isActive = true)
            Spacer(modifier = Modifier.height(8.dp))
            SokoniCard(modifier = Modifier.fillMaxWidth()) {
                Text("This is a Sokoni Card", modifier = Modifier.padding(16.dp))
            }
        }
    }
}
