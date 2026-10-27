package com.qusai.tarneebscoretracker.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = Color(0xFF3A6351),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBDECCF),
    onPrimaryContainer = Color(0xFF082117),
    secondary = Color(0xFF53635A),
    secondaryContainer = Color(0xFFD6E8DC),
    tertiary = Color(0xFF3D6374),
    error = Color(0xFFBA1A1A),
    background = Color(0xFFF7FAF7),
    surface = Color(0xFFF7FAF7),
    surfaceVariant = Color(0xFFDEE5DF),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFA2D0B5),
    onPrimary = Color(0xFF0C3726),
    primaryContainer = Color(0xFF244E3B),
    onPrimaryContainer = Color(0xFFBDECCF),
    secondary = Color(0xFFBACCC0),
    secondaryContainer = Color(0xFF3C4B42),
    tertiary = Color(0xFFA5CDDF),
    error = Color(0xFFFFB4AB),
    background = Color(0xFF101512),
    surface = Color(0xFF101512),
    surfaceVariant = Color(0xFF414943),
)

@Composable
fun TarneebScoreTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && darkTheme ->
            dynamicDarkColorScheme(context)
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            dynamicLightColorScheme(context)
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TarneebTypography,
        content = content,
    )
}
