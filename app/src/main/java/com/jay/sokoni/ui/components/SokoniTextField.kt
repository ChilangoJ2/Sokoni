package com.jay.sokoni.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jay.sokoni.ui.theme.SokoniBlack
import com.jay.sokoni.ui.theme.SokoniGold
import com.jay.sokoni.ui.theme.SokoniTheme

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

@Preview(showBackground = true)
@Composable
fun TextFieldPreview() {
    SokoniTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            SokoniTextField(value = "", onValueChange = {}, label = "Enter Email")
            Spacer(modifier = Modifier.height(16.dp))
            SokoniTextField(value = "Invalid input", onValueChange = {}, label = "Error field", isError = true, errorMessage = "Required")
        }
    }
}
