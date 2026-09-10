package com.jay.sokoni.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object CustomerHome : Screen("customer_home")
    object VendorHome : Screen("vendor_home")
    object VendorOnboarding : Screen("vendor_onboarding")
    object VendorProducts : Screen("vendor_products")
    object AddEditProduct : Screen("add_edit_product/{productId}") {
        fun createRoute(productId: String = "new") = "add_edit_product/$productId"
    }
    object AdminHome : Screen("admin_home")
}
