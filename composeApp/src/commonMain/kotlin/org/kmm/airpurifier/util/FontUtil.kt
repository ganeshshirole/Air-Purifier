package org.kmm.airpurifier.util

import airpurifier.composeapp.generated.resources.Res
import airpurifier.composeapp.generated.resources.Roboto_Regular
import airpurifier.composeapp.generated.resources.Roboto_Thin
import airpurifier.composeapp.generated.resources.ostrich_sans_inline_regular
import airpurifier.composeapp.generated.resources.ostrich_sans_rounded_medium
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font

@Composable
fun OstrichSansFontFamily() = FontFamily(
    Font(Res.font.ostrich_sans_inline_regular, FontWeight.Normal),
    Font(Res.font.ostrich_sans_rounded_medium, FontWeight.Medium),
)

@Composable
fun RobotoFontFamily() = FontFamily(
    Font(Res.font.Roboto_Thin, FontWeight.Light),
    Font(Res.font.Roboto_Regular, FontWeight.Normal),
)