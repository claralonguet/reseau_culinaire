package com.example.culinar.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with

val TypographyScreenTitle = Typography(



    bodyLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 50.sp,
        letterSpacing = 0.5.sp,
        textAlign = TextAlign.Center,
        color = mediumGreen

    )

)

val Typography = Typography(
    // For app titles
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Cursive,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    // For top navbar menu items
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        letterSpacing = 2.sp,
    ),
    // Body text or description, small
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 19.sp,
        letterSpacing = 0.7.sp,
    ),
    // Body text or description, slightly bolder/bigger
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 21.sp,
        letterSpacing = 1.sp,
    ),
    // For screens titles
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 50.sp,
        letterSpacing = 1.sp,
        textAlign = TextAlign.Center,
        color = mediumGreen,
        ),
    // For subsections titles
    titleMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Normal,
        fontSize = 25.sp,
        lineHeight = 26.sp,
        letterSpacing = 1.5.sp
    ),
    // For full-text function buttons (add, modify, delete))
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        letterSpacing = 3.sp
    )

)