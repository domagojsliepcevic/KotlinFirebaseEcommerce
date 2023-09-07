package hr.algebra.sverccommercefinal.di

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module // Indicates that this is a Dagger Hilt module
@InstallIn(SingletonComponent::class) // Specifies that this module is installed in the SingletonComponent
object AppModule {

    // Provides a FirebaseAuth instance
    @Provides // Indicates that this function provides a dependency
    @Singleton // Specifies that the provided instance should be a Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()
}
