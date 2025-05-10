package com.example.culinar.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

// Définition des couleurs globales
val GreenPrimary = Color(0xFF69F0AE)
val PurplePrimary = Purple40

// Jeu de couleurs en mode sombre
private val DarkColorScheme = darkColorScheme(
    primary = PurplePrimary, 
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

// Jeu de couleurs en mode clair
private val LightColorScheme = lightColorScheme(
    primary = PurplePrimary, 
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color.White,
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

// Définition des formes globales
private val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(16.dp)
)

@Composable
fun CulinarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
