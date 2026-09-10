package com.jay.sokoni.ui.admin.financials

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jay.sokoni.domain.model.FinancialLedgerEntry
import com.jay.sokoni.ui.components.SokoniCard
import com.jay.sokoni.ui.theme.SokoniTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialLedgerScreen(
    ledgerEntries: List<FinancialLedgerEntry>
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Financial Ledger") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(ledgerEntries) { entry ->
                LedgerItem(entry)
            }
        }
    }
}

@Composable
fun LedgerItem(entry: FinancialLedgerEntry) {
    SokoniCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Order #${entry.orderId.takeLast(6)}", style = MaterialTheme.typography.titleMedium)
                Text(text = "KSh ${entry.customerTotal}", style = MaterialTheme.typography.titleMedium)
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            FinancialRow(label = "Product Subtotal", value = entry.productSubtotal)
            FinancialRow(label = "Delivery Fee", value = entry.deliveryFee)
            FinancialRow(label = "Commission (${entry.commissionRateAtOrder}%)", value = -entry.commissionAmount, color = Color.Red)
            FinancialRow(label = "Delivery Margin", value = -entry.deliveryMarginAmount, color = Color.Red)
            FinancialRow(label = "Payment Fee", value = -entry.paymentFee, color = Color.Red)
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Vendor Net", style = MaterialTheme.typography.titleSmall, color = Color(0xFF2E7D32))
                Text(text = "KSh ${entry.vendorNetSettlement}", style = MaterialTheme.typography.titleSmall, color = Color(0xFF2E7D32))
            }
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Sokoni Revenue", style = MaterialTheme.typography.bodySmall)
                Text(text = "KSh ${entry.sokoniRevenue}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun FinancialRow(label: String, value: Double, color: Color = Color.Unspecified) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, style = MaterialTheme.typography.bodySmall)
        Text(text = "${if (value < 0) "-" else ""} KSh ${Math.abs(value)}", style = MaterialTheme.typography.bodySmall, color = color)
    }
}

@Preview(showBackground = true)
@Composable
fun LedgerPreview() {
    SokoniTheme {
        FinancialLedgerScreen(
            ledgerEntries = listOf(
                FinancialLedgerEntry(
                    orderId = "1001",
                    productSubtotal = 1000.0,
                    deliveryFee = 60.0,
                    customerTotal = 1060.0,
                    commissionRateAtOrder = 5.0,
                    commissionAmount = 50.0,
                    deliveryMarginAmount = 1.80,
                    paymentFee = 15.0,
                    vendorNetSettlement = 933.20,
                    sokoniRevenue = 51.80
                )
            )
        )
    }
}
