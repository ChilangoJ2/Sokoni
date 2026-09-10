package com.jay.sokoni.ui.customer.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jay.sokoni.domain.model.Order
import com.jay.sokoni.domain.model.OrderStatus
import com.jay.sokoni.domain.model.PaymentStatus
import com.jay.sokoni.ui.components.SokoniBadge
import com.jay.sokoni.ui.components.SokoniCard
import com.jay.sokoni.ui.components.SokoniLoading
import com.jay.sokoni.ui.theme.SokoniTheme
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrderListScreen(
    onOrderClick: (String) -> Unit,
    viewModel: OrderListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    OrderListContent(
        uiState = uiState,
        onOrderClick = onOrderClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListContent(
    uiState: OrderListUiState,
    onOrderClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Orders") })
        }
    ) { padding ->
        if (uiState.isLoading) {
            SokoniLoading()
        } else if (uiState.orders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("No orders yet")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.orders) { order ->
                    OrderItem(order = order, onClick = { onOrderClick(order.orderId) })
                }
            }
        }
    }
}

@Composable
fun OrderItem(order: Order, onClick: () -> Unit) {
    val date = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date(order.createdAt))
    
    SokoniCard(onClick = onClick) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Order #${order.orderId.takeLast(6)}", style = MaterialTheme.typography.titleMedium)
                Text(text = "KSh ${order.totalAmount}", style = MaterialTheme.typography.titleMedium)
            }
            Text(text = order.vendorName, style = MaterialTheme.typography.bodyMedium)
            Text(text = date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SokoniBadge(text = order.status.name)
                if (order.paymentStatus == PaymentStatus.PAID) {
                    SokoniBadge(text = "PAID", color = MaterialTheme.colorScheme.tertiary, textColor = MaterialTheme.colorScheme.onTertiary)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderListPreview() {
    SokoniTheme {
        OrderListContent(
            uiState = OrderListUiState(
                orders = listOf(
                    Order(orderId = "123456", vendorName = "MJC Water", totalAmount = 450.0, status = OrderStatus.PREPARING, paymentStatus = PaymentStatus.PAID, createdAt = System.currentTimeMillis())
                )
            ),
            onOrderClick = {}
        )
    }
}
