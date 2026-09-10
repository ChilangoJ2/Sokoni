package com.jay.sokoni.ui.vendor.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jay.sokoni.domain.model.Order
import com.jay.sokoni.domain.model.OrderStatus
import com.jay.sokoni.ui.components.*
import com.jay.sokoni.ui.theme.SokoniTheme

@Composable
fun VendorOrderDetailScreen(
    orderId: String,
    onNavigateBack: () -> Unit,
    viewModel: VendorOrderDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(orderId) {
        viewModel.loadOrder(orderId)
    }

    VendorOrderDetailContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onAccept = { viewModel.acceptOrder() },
        onConfirmItem = { productId, variant, isConfirmed -> viewModel.confirmItem(productId, variant, isConfirmed) },
        onMarkReady = { viewModel.markAsReady() },
        onDispatch = { name, phone, plate -> viewModel.dispatchOrder(name, phone, plate) },
        onComplete = { viewModel.completeOrder() },
        onCancel = { viewModel.cancelOrder(it) },
        onClearError = { viewModel.clearError() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorOrderDetailContent(
    uiState: VendorOrderDetailUiState,
    onNavigateBack: () -> Unit,
    onAccept: () -> Unit,
    onConfirmItem: (String, String, Boolean) -> Unit,
    onMarkReady: () -> Unit,
    onDispatch: (String, String, String) -> Unit,
    onComplete: () -> Unit,
    onCancel: (String) -> Unit,
    onClearError: () -> Unit
) {
    var showCancelDialog by remember { mutableStateOf(false) }
    var cancelReason by remember { mutableStateOf("") }
    var showDispatchDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            SokoniLoading()
        } else if (uiState.order == null) {
            SokoniEmptyState(message = "Order not found")
        } else {
            val order = uiState.order
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                OrderHeaderInfo(order)
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(text = "Items Checklist", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                order.items.forEach { item ->
                    ItemChecklistRow(
                        item = item,
                        isEnabled = order.status == OrderStatus.ACCEPTED,
                        onCheck = { onConfirmItem(item.productId, item.variantName, it) }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                StatusActions(
                    status = order.status,
                    isDelivery = order.isDelivery,
                    allChecked = order.items.all { it.isConfirmedByVendor },
                    onAccept = onAccept,
                    onMarkReady = onMarkReady,
                    onDispatch = { showDispatchDialog = true },
                    onComplete = onComplete,
                    onCancelClick = { showCancelDialog = true }
                )
            }
        }
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Order") },
            text = {
                Column {
                    Text("Select a reason for cancellation:")
                    Spacer(modifier = Modifier.height(8.dp))
                    SokoniTextField(value = cancelReason, onValueChange = { cancelReason = it }, label = "Reason")
                }
            },
            confirmButton = {
                TextButton(onClick = { 
                    onCancel(cancelReason)
                    showCancelDialog = false 
                }, enabled = cancelReason.isNotBlank()) {
                    Text("Cancel Order")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (showDispatchDialog) {
        var riderName by remember { mutableStateOf("") }
        var riderPhone by remember { mutableStateOf("") }
        var plateNumber by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showDispatchDialog = false },
            title = { Text("Assign Rider") },
            text = {
                Column {
                    SokoniTextField(value = riderName, onValueChange = { riderName = it }, label = "Rider Name")
                    Spacer(modifier = Modifier.height(8.dp))
                    SokoniTextField(value = riderPhone, onValueChange = { riderPhone = it }, label = "Rider Phone")
                    Spacer(modifier = Modifier.height(8.dp))
                    SokoniTextField(value = plateNumber, onValueChange = { plateNumber = it }, label = "Vehicle Plate")
                }
            },
            confirmButton = {
                TextButton(onClick = { 
                    onDispatch(riderName, riderPhone, plateNumber)
                    showDispatchDialog = false 
                }) {
                    Text("Dispatch")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDispatchDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (uiState.error != null) {
        AlertDialog(
            onDismissRequest = onClearError,
            title = { Text("Error") },
            text = { Text(uiState.error) },
            confirmButton = {
                TextButton(onClick = onClearError) { Text("OK") }
            }
        )
    }
}

@Composable
fun OrderHeaderInfo(order: Order) {
    SokoniCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Order #${order.orderId.takeLast(6)}", style = MaterialTheme.typography.headlineSmall)
                SokoniBadge(text = order.status.name)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Total: KSh ${order.totalAmount}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Method: ${if (order.isDelivery) "Delivery" else "Pickup"}")
            if (order.isDelivery) {
                Text(text = "To: ${order.deliveryLocation?.address}")
            }
        }
    }
}

@Composable
fun ItemChecklistRow(
    item: com.jay.sokoni.domain.model.OrderItem,
    isEnabled: Boolean,
    onCheck: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.isConfirmedByVendor,
            onCheckedChange = onCheck,
            enabled = isEnabled
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = "${item.quantity}x ${item.productName}", style = MaterialTheme.typography.bodyLarge)
            Text(text = item.variantName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun StatusActions(
    status: OrderStatus,
    isDelivery: Boolean,
    allChecked: Boolean,
    onAccept: () -> Unit,
    onMarkReady: () -> Unit,
    onDispatch: () -> Unit,
    onComplete: () -> Unit,
    onCancelClick: () -> Unit
) {
    Column {
        when (status) {
            OrderStatus.PENDING -> {
                SokoniPrimaryButton(text = "Accept Order", onClick = onAccept)
            }
            OrderStatus.ACCEPTED -> {
                SokoniPrimaryButton(text = "Mark as Ready", onClick = onMarkReady, enabled = allChecked)
                if (!allChecked) {
                    Text(
                        text = "Check all items to mark as ready",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            OrderStatus.READY -> {
                if (isDelivery) {
                    SokoniPrimaryButton(text = "Dispatch Rider", onClick = onDispatch)
                } else {
                    SokoniPrimaryButton(text = "Mark Collected", onClick = onComplete)
                }
            }
            OrderStatus.OUT_FOR_DELIVERY -> {
                SokoniPrimaryButton(text = "Mark Delivered", onClick = onComplete)
            }
            else -> Unit
        }
        
        if (status != OrderStatus.COMPLETED && status != OrderStatus.CANCELLED) {
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(
                onClick = onCancelClick,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel Order")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VendorOrderDetailPreview() {
    SokoniTheme {
        VendorOrderDetailContent(
            uiState = VendorOrderDetailUiState(
                order = Order(
                    orderId = "ABCDEF",
                    vendorName = "MJC Store",
                    items = listOf(
                        com.jay.sokoni.domain.model.OrderItem(productName = "20L Water", variantName = "Refill", quantity = 1, isConfirmedByVendor = true),
                        com.jay.sokoni.domain.model.OrderItem(productName = "Milk", variantName = "500ml", quantity = 2, isConfirmedByVendor = false)
                    ),
                    status = OrderStatus.ACCEPTED,
                    isDelivery = true,
                    totalAmount = 650.0
                )
            ),
            onNavigateBack = {},
            onAccept = {},
            onConfirmItem = { _, _, _ -> },
            onMarkReady = {},
            onDispatch = { _, _, _ -> },
            onComplete = {},
            onCancel = {},
            onClearError = {}
        )
    }
}
