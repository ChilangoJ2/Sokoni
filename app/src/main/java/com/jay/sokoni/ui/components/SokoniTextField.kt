package com.jay.sokoni.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jay.sokoni.ui.theme.SokoniBlack
import com.jay.sokoni.ui.theme.SokoniGold

@Composable
fun SokoniTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        modifier = modifier.fillMaxWidth(),
        isError = isError,
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedLabelColor = SokoniGold,
            unfocusedLabelColor = SokoniBlack,
            focusedIndicatorColor = SokoniGold,
            unfocusedIndicatorColor = SokoniBlack
        ),
        supportingText = errorMessage?.let { { Text(text = it) } }
    )
}
