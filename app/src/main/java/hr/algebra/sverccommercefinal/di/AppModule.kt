package hr.algebra.sverccommercefinal.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hr.algebra.sverccommercefinal.firebase.FirebaseCommon
import javax.inject.Singleton

/**
 * Dagger Hilt module providing dependencies for the application.
 *
 * This module is installed in the SingletonComponent, ensuring that the provided dependencies are singletons.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides a FirebaseAuth instance.
     *
     * @return A FirebaseAuth instance that can be used for user authentication.
     */
    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    /**
     * Provides a FirebaseFirestore instance (Firebase Firestore database).
     *
     * @return A FirebaseFirestore instance that can be used for Firestore database operations.
     */
    @Provides
    @Singleton
    fun provideFirebaseFirestoreDatabase() = Firebase.firestore

    /**
     * Provides a FirebaseCommon instance.
     *
     * @param firebaseAuth: The FirebaseAuth instance provided by Dagger Hilt.
     * @param firestore: The FirebaseFirestore instance provided by Dagger Hilt.
     * @return A FirebaseCommon instance that can be used for common Firebase operations.
     */
    @Provides
    @Singleton
    fun provideFirebaseCommon(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ) = FirebaseCommon(firestore, firebaseAuth)
}

