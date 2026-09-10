package com.jay.sokoni.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object CustomerHome : Screen("customer_home")
    object Search : Screen("search")
    object VendorStore : Screen("vendor_store/{vendorId}") {
        fun createRoute(vendorId: String) = "vendor_store/$vendorId"
    }
    object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: String) = "product_detail/$productId"
    }
    object Cart : Screen("cart")
    object Checkout : Screen("checkout")

    object VendorHome : Screen("vendor_home")
    object VendorOnboarding : Screen("vendor_onboarding")
    object VendorProducts : Screen("vendor_products")
    object AddEditProduct : Screen("add_edit_product/{productId}") {
        fun createRoute(productId: String = "new") = "add_edit_product/$productId"
    }
    object AdminHome : Screen("admin_home")
}
