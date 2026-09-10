package com.jay.sokoni.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.jay.sokoni.data.repository.CartRepositoryImpl
import com.jay.sokoni.data.repository.MarketplaceRepositoryImpl
import com.jay.sokoni.data.repository.OrderRepositoryImpl
import com.jay.sokoni.data.repository.PaymentRepositoryImpl
import com.jay.sokoni.domain.repository.CartRepository
import com.jay.sokoni.domain.repository.MarketplaceRepository
import com.jay.sokoni.domain.repository.OrderRepository
import com.jay.sokoni.domain.repository.PaymentRepository
import com.jay.sokoni.data.repository.AuthRepositoryImpl
import com.jay.sokoni.data.repository.ProductRepositoryImpl
import com.jay.sokoni.data.repository.VendorRepositoryImpl
import com.jay.sokoni.domain.repository.AuthRepository
import com.jay.sokoni.domain.repository.ProductRepository
import com.jay.sokoni.domain.repository.VendorRepository
import com.jay.sokoni.util.DefaultLocationTracker
import com.jay.sokoni.util.LocationTracker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFunctions(): FirebaseFunctions = FirebaseFunctions.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

    @Provides
    @Singleton
    fun provideVendorRepository(impl: VendorRepositoryImpl): VendorRepository = impl

    @Provides
    @Singleton
    fun provideProductRepository(impl: ProductRepositoryImpl): ProductRepository = impl

    @Provides
    @Singleton
    fun provideMarketplaceRepository(impl: MarketplaceRepositoryImpl): MarketplaceRepository = impl

    @Provides
    @Singleton
    fun provideCartRepository(impl: CartRepositoryImpl): CartRepository = impl

    @Provides
    @Singleton
    fun provideOrderRepository(impl: OrderRepositoryImpl): OrderRepository = impl

    @Provides
    @Singleton
    fun providePaymentRepository(impl: PaymentRepositoryImpl): PaymentRepository = impl

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    @Provides
    @Singleton
    fun provideLocationTracker(
        fusedLocationProviderClient: FusedLocationProviderClient,
        @ApplicationContext context: Context
    ): LocationTracker = DefaultLocationTracker(fusedLocationProviderClient, context)
}
