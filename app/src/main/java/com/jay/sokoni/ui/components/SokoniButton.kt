package com.jay.sokoni.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jay.sokoni.ui.theme.SokoniBlack
import com.jay.sokoni.ui.theme.SokoniGold
import com.jay.sokoni.ui.theme.SokoniWhite

@Composable
fun SokoniPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SokoniBlack,
            contentColor = SokoniWhite,
            disabledContainerColor = Color.Gray,
            disabledContentColor = SokoniWhite
        )
    ) {
        Text(text = text)
    }
}

@Composable
fun SokoniSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SokoniGold,
            contentColor = SokoniBlack,
            disabledContainerColor = Color.Gray,
            disabledContentColor = SokoniBlack
        )
    ) {
        Text(text = text)
    }
}
