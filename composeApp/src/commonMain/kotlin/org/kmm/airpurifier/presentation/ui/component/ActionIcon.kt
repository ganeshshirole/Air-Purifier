package org.kmm.airpurifier.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun ActionIcon(
    onClick: () -> Unit,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    iconVector: ImageVector? = null,
    iconPainter: Painter? = null,
    contentDescription: String? = null,
    tint: Color = Color.White
) {
    require(!(iconVector == null && iconPainter == null)) {
        "Either iconVector or iconPainter must be provided"
    }

    IconButton(
        onClick = onClick,
        modifier = modifier
            .background(backgroundColor)
    ) {
        when {
            iconVector != null -> Icon(
                imageVector = iconVector,
                contentDescription = contentDescription,
                tint = tint
            )
            iconPainter != null -> Icon(
                painter = iconPainter,
                contentDescription = contentDescription,
                tint = tint
            )
        }
    }
}