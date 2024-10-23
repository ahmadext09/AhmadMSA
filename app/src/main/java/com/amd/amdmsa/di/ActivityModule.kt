package com.amd.amdmsa.di

import com.amd.amdmsa.activity.HomeActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent


@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {

    @Provides
    fun provideHomeActivity(): HomeActivity {
        return HomeActivity()
    }
}
