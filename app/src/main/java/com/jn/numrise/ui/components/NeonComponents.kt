package com.jn.numrise.ui.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jn.numrise.audio.LocalSoundManager
import com.jn.numrise.ui.theme.NeonCyan
import com.jn.numrise.ui.theme.PressStart2P

/**
 * Custom modifier that automatically plays a tap sound when clicked.
 */
fun Modifier.neonClickable(
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    val soundManager = LocalSoundManager.current
    this.clickable(
        enabled = enabled,
        interactionSource = remember { MutableInteractionSource() },
        indication = LocalIndication.current,
        onClick = {
            soundManager?.play("tap")
            onClick()
        }
    )
}

@Composable
fun NeonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = NeonCyan,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    height: Int = 64,
    fontSize: Int = 16
) {
    val soundManager = LocalSoundManager.current
    
    Button(
        onClick = {
            soundManager?.play("tap")
            onClick()
        },
        modifier = modifier
            .height(height.dp)
            .border(2.dp, color, RoundedCornerShape(50)),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = color.copy(alpha = 0.15f),
            contentColor = color,
            disabledContainerColor = color.copy(alpha = 0.05f),
            disabledContentColor = color.copy(alpha = 0.3f)
        ),
        enabled = enabled
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = fontSize.sp,
                    fontFamily = PressStart2P
                ),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun NeonIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    tint: Color = NeonCyan
) {
    val soundManager = LocalSoundManager.current
    IconButton(
        onClick = {
            soundManager?.play("tap")
            onClick()
        },
        modifier = modifier
    ) {
        Icon(icon, contentDescription = contentDescription, tint = tint)
    }
}

@Composable
fun NeonTitle(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = NeonCyan,
    fontSize: Int = 36
) {
    Text(
        text = text,
        modifier = modifier,
        style = TextStyle(
            fontFamily = PressStart2P,
            color = color,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold,
            shadow = Shadow(
                color = color,
                blurRadius = 16f
            )
        )
    )
}

@Composable
fun NeonText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    fontSize: Int = 16,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = fontSize.sp,
        fontWeight = fontWeight,
        fontFamily = PressStart2P
    )
}
