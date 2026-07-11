package com.jn.numrise.ui.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jn.numrise.audio.LocalSoundManager
import com.jn.numrise.ui.theme.NeonCyan
import com.jn.numrise.ui.theme.NeonYellow
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
fun NeonHeaderBar(
    title: String = "",
    coins: Int,
    onBack: (() -> Unit)? = null,
    onShop: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onBack != null) {
            NeonIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                onClick = onBack,
                tint = NeonCyan
            )
        } else {
            Spacer(modifier = Modifier.width(48.dp))
        }

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (title.isNotEmpty()) {
                NeonTitle(text = title, fontSize = 20)
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(end = 16.dp)
        ) {
            NeonText(text = "$coins", fontSize = 14, color = NeonYellow)
            Spacer(modifier = Modifier.width(8.dp))
            NeonIconButton(
                icon = Icons.Default.ShoppingCart,
                onClick = onShop,
                tint = NeonYellow
            )
        }
    }

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
    fontSize: Int = 16,
    autoResizeText: Boolean = true,
    maxLines: Int = 1
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
            NeonText(
                text = text,
                fontSize = fontSize,
                color = color,
                fontWeight = FontWeight.Bold,
                autoResize = autoResizeText,
                maxLines = maxLines,
                textAlign = TextAlign.Center
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
    fontSize: Int = 36,
    autoResize: Boolean = true
) {
    NeonText(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontWeight = FontWeight.Bold,
        autoResize = autoResize,
        maxLines = 1,
        textAlign = TextAlign.Center,
        hasShadow = true
    )
}

@Composable
fun NeonText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    fontSize: Int = 16,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    autoResize: Boolean = false,
    hasShadow: Boolean = false,
    softWrap: Boolean = true
) {
    if (autoResize) {
        AutoResizingNeonText(
            text = text,
            modifier = modifier,
            color = color,
            targetFontSize = fontSize,
            fontWeight = fontWeight,
            textAlign = textAlign,
            maxLines = maxLines,
            hasShadow = hasShadow,
            softWrap = softWrap
        )
    } else {
        Text(
            text = text,
            modifier = modifier,
            color = color,
            fontSize = fontSize.sp,
            fontWeight = fontWeight,
            fontFamily = PressStart2P,
            textAlign = textAlign,
            maxLines = maxLines,
            softWrap = softWrap,
            overflow = if (maxLines != Int.MAX_VALUE) TextOverflow.Ellipsis else TextOverflow.Clip,
            style = if (hasShadow) TextStyle(
                shadow = Shadow(
                    color = color,
                    blurRadius = 16f
                )
            ) else TextStyle.Default
        )
    }
}

@Composable
fun AutoResizingNeonText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    targetFontSize: Int = 16,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    hasShadow: Boolean = false,
    softWrap: Boolean = true
) {
    var resizedFontSize by remember { mutableStateOf(targetFontSize.sp) }

    Text(
        text = text,
        modifier = modifier,
        color = color,
        softWrap = softWrap,
        fontSize = resizedFontSize,
        fontWeight = fontWeight,
        fontFamily = PressStart2P,
        textAlign = textAlign,
        maxLines = maxLines,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.didOverflowWidth || textLayoutResult.didOverflowHeight) {
                if (resizedFontSize.value > 8f) {
                    resizedFontSize = (resizedFontSize.value - 1f).sp
                }
            }
        },
        style = if (hasShadow) TextStyle(
            shadow = Shadow(
                color = color,
                blurRadius = 16f
            )
        ) else TextStyle.Default
    )
}
