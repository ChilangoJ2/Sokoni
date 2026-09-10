package com.jay.sokoni.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.jay.sokoni.ui.admin.home.AdminHomeScreen
import com.jay.sokoni.ui.auth.login.LoginScreen
import com.jay.sokoni.ui.auth.register.RegisterScreen
import com.jay.sokoni.ui.customer.cart.CartScreen
import com.jay.sokoni.ui.customer.checkout.CheckoutScreen
import com.jay.sokoni.ui.customer.home.CustomerHomeScreen
import com.jay.sokoni.ui.customer.product.ProductDetailScreen
import com.jay.sokoni.ui.customer.search.SearchScreen
import com.jay.sokoni.ui.customer.vendor.VendorStoreScreen
import com.jay.sokoni.ui.vendor.home.VendorHomeScreen
import com.jay.sokoni.ui.vendor.onboarding.VendorOnboardingScreen
import com.jay.sokoni.ui.vendor.products.AddEditProductScreen
import com.jay.sokoni.ui.vendor.products.ProductListScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.CustomerHome.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.CustomerHome.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }
        composable(Screen.CustomerHome.route) {
            CustomerHomeScreen(
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToVendor = { navController.navigate(Screen.VendorStore.createRoute(it)) },
                onNavigateToProduct = { navController.navigate(Screen.ProductDetail.createRoute(it)) }
            )
        }
        composable(Screen.Search.route) {
            SearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onProductClick = { navController.navigate(Screen.ProductDetail.createRoute(it)) }
            )
        }
        composable(
            route = Screen.VendorStore.route,
            arguments = listOf(navArgument("vendorId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vendorId = backStackEntry.arguments?.getString("vendorId") ?: ""
            VendorStoreScreen(
                vendorId = vendorId,
                onNavigateBack = { navController.popBackStack() },
                onProductClick = { navController.navigate(Screen.ProductDetail.createRoute(it)) }
            )
        }
        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailScreen(
                productId = productId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Cart.route) {
            CartScreen(
                onNavigateBack = { navController.popBackStack() },
                onCheckout = { navController.navigate(Screen.Checkout.route) }
            )
        }
        composable(Screen.Checkout.route) {
            CheckoutScreen(
                onNavigateBack = { navController.popBackStack() },
                onOrderSuccess = {
                    navController.navigate(Screen.CustomerHome.route) {
                        popUpTo(Screen.CustomerHome.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.VendorHome.route) {
            VendorHomeScreen()
        }
        composable(Screen.VendorOnboarding.route) {
            VendorOnboardingScreen(onComplete = {
                navController.navigate(Screen.VendorHome.route) {
                    popUpTo(Screen.VendorOnboarding.route) { inclusive = true }
                }
            })
        }
        composable(Screen.VendorProducts.route) {
            ProductListScreen(
                vendorId = "current_vendor_id",
                onAddProduct = { navController.navigate(Screen.AddEditProduct.createRoute()) },
                onEditProduct = { navController.navigate(Screen.AddEditProduct.createRoute(it.productId)) }
            )
        }
        composable(
            route = Screen.AddEditProduct.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            AddEditProductScreen(
                vendorId = "current_vendor_id",
                productId = if (productId == "new") null else productId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AdminHome.route) {
            AdminHomeScreen()
        }
    }
}
