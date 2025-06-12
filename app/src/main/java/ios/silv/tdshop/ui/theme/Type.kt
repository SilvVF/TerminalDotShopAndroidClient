package ios.silv.tdshop.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

import ios.silv.tdshop.R

private val DMMono = FontFamily(
    Font(R.font.dm_mono_regular, weight = FontWeight.Normal),
    Font(R.font.dm_mono_light, weight = FontWeight.Light),
    Font(R.font.dm_mono_medium, weight = FontWeight.Medium),
    Font(R.font.dm_mono_light_italic, weight = FontWeight.Light, style = FontStyle.Italic),
    Font(R.font.dm_mono_medium_italic, weight = FontWeight.Medium, style = FontStyle.Italic)
)

private val defaultTypography = Typography()

val Typography = defaultTypography.copy(
    displayLarge = defaultTypography.displayLarge.copy(
        fontFamily = DMMono
    ),
    displayMedium = defaultTypography.displayMedium.copy(
        fontFamily = DMMono
    ),
    displaySmall = defaultTypography.displaySmall.copy(
        fontFamily = DMMono
    ),
    headlineLarge = defaultTypography.headlineLarge.copy(
        fontFamily = DMMono
    ),
    headlineMedium = defaultTypography.headlineMedium.copy(
        fontFamily = DMMono
    ),
    headlineSmall = defaultTypography.headlineSmall.copy(
        fontFamily = DMMono
    ),
    titleLarge = defaultTypography.titleLarge.copy(
        fontFamily = DMMono
    ),
    titleMedium = defaultTypography.titleMedium.copy(
        fontFamily = DMMono
    ),
    titleSmall = defaultTypography.titleSmall.copy(
        fontFamily = DMMono
    ),
    bodyLarge = defaultTypography.bodyLarge.copy(
        fontFamily = DMMono
    ),
    bodyMedium = defaultTypography.bodyMedium.copy(
        fontFamily = DMMono
    ),
    bodySmall = defaultTypography.bodySmall.copy(
        fontFamily = DMMono
    ),
    labelLarge = defaultTypography.labelLarge.copy(
        fontFamily = DMMono
    ),
    labelMedium = defaultTypography.labelMedium.copy(
        fontFamily = DMMono
    ),
    labelSmall = defaultTypography.labelSmall.copy(
        fontFamily = DMMono
    ),
)