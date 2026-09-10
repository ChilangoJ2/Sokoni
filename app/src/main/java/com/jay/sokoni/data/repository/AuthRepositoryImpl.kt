package com.jay.sokoni.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jay.sokoni.domain.model.User
import com.jay.sokoni.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {
    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: Flow<User?> = _currentUser

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return Result.failure(Exception("Login failed"))
            val user = fetchUser(uid) ?: return Result.failure(Exception("User not found"))
            _currentUser.value = user
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(user: User, password: String): Result<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(user.email, password).await()
            val uid = result.user?.uid ?: return Result.failure(Exception("Registration failed"))
            val finalUser = user.copy(uid = uid)
            saveUser(finalUser)
            _currentUser.value = finalUser
            Result.success(finalUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
        _currentUser.value = null
    }

    private suspend fun fetchUser(uid: String): User? {
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            doc.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun saveUser(user: User) {
        firestore.collection("users").document(user.uid).set(user).await()
    }
}
