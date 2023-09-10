package com.coolnexttech.freetimer.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.coolnexttech.freetimer.ui.theme.TertiaryColor

@Composable
fun RoundedBox(widthFraction: Float = 1f, action: (() -> Unit)? = {}, content: @Composable BoxScope.() -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth(widthFraction)
            .height(100.dp)
            .padding(16.dp)
            .clip(shape = RoundedCornerShape(30.dp))
            .background(TertiaryColor)
            .clickable(enabled = action != null) { action?.invoke() }
    ) {
        content()
    }
}