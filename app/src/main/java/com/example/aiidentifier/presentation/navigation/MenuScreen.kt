package com.example.aiidentifier.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MenuScreen(
    onNavigateToTextReader: () -> Unit,
    onNavigateToObjectIdentifier: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F0C1B), // Dark midnight violet
                        Color(0xFF15102A), // Medium dark violet
                        Color(0xFF06040A)  // Near black
                    )
                )
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp)
        ) {
            // Header
            Text(
                text = "AI Vision",
                fontSize = 38.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            
            Text(
                text = "Choose a tool to analyze your environment",
                fontSize = 15.sp,
                color = Color.LightGray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Text Recognizer Card
            MenuCard(
                title = "Text Recognizer",
                subtitle = "Read, extract and copy text from any image or document instantly.",
                gradientColors = listOf(Color(0xFF6366F1), Color(0xFFA855F7)), // Indigo to Purple
                onClick = onNavigateToTextReader
            )

            // Object Identifier Card
            MenuCard(
                title = "Object Identifier",
                subtitle = "Detect and label real-world objects with precision bounding boxes.",
                gradientColors = listOf(Color(0xFF0EA5E9), Color(0xFF10B981)), // Blue to Emerald/Teal
                onClick = onNavigateToObjectIdentifier
            )
        }
    }
}

@Composable
fun MenuCard(
    title: String,
    subtitle: String,
    gradientColors: List<Color>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(gradientColors))
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    lineHeight = 20.sp
                )
            }
        }
    }
}
