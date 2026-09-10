package com.jay.sokoni.ui.vendor.orders

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
import com.jay.sokoni.domain.model.OrderItem
import com.jay.sokoni.domain.model.OrderStatus
import com.jay.sokoni.ui.components.SokoniBadge
import com.jay.sokoni.ui.components.SokoniCard
import com.jay.sokoni.ui.components.SokoniLoading
import com.jay.sokoni.ui.theme.SokoniTheme

@Composable
fun VendorOrderListScreen(
    onOrderClick: (String) -> Unit,
    viewModel: VendorOrderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    VendorOrderListContent(
        uiState = uiState,
        onOrderClick = onOrderClick,
        onUpdateStatus = { id, status -> viewModel.updateStatus(id, status) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorOrderListContent(
    uiState: VendorOrderUiState,
    onOrderClick: (String) -> Unit,
    onUpdateStatus: (String, OrderStatus) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Store Orders") })
        }
    ) { padding ->
        if (uiState.isLoading) {
            SokoniLoading()
        } else if (uiState.orders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("No orders received yet")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.orders) { order ->
                    VendorOrderItem(order = order, onClick = { onOrderClick(order.orderId) }, onUpdateStatus = onUpdateStatus)
                }
            }
        }
    }
}

@Composable
fun VendorOrderItem(order: Order, onClick: () -> Unit, onUpdateStatus: (String, OrderStatus) -> Unit) {
    SokoniCard(onClick = onClick) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Order #${order.orderId.takeLast(6)}", style = MaterialTheme.typography.titleMedium)
                SokoniBadge(text = order.status.name)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            order.items.forEach { item ->
                Text(text = "${item.quantity}x ${item.productName} (${item.variantName})", style = MaterialTheme.typography.bodySmall)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                when (order.status) {
                    OrderStatus.PENDING -> {
                        Button(onClick = { onUpdateStatus(order.orderId, OrderStatus.ACCEPTED) }) {
                            Text("Accept")
                        }
                    }
                    OrderStatus.ACCEPTED -> {
                        Button(onClick = { onUpdateStatus(order.orderId, OrderStatus.PREPARING) }) {
                            Text("Start Preparing")
                        }
                    }
                    OrderStatus.PREPARING -> {
                        Button(onClick = { onUpdateStatus(order.orderId, OrderStatus.READY) }) {
                            Text("Mark Ready")
                        }
                    }
                    OrderStatus.READY -> {
                        Button(onClick = { onUpdateStatus(order.orderId, OrderStatus.COMPLETED) }) {
                            Text("Mark Completed")
                        }
                    }
                    else -> Unit
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VendorOrderListPreview() {
    SokoniTheme {
        VendorOrderListContent(
            uiState = VendorOrderUiState(
                orders = listOf(
                    Order(
                        orderId = "123",
                        items = listOf(OrderItem(productName = "20L Water", quantity = 2, variantName = "Refill")),
                        status = OrderStatus.PENDING
                    )
                )
            ),
            onOrderClick = {},
            onUpdateStatus = { _, _ -> }
        )
    }
}
