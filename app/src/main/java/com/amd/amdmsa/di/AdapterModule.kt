package com.amd.amdmsa.di

import com.amd.amdmsa.activity.HomeActivity
import com.amd.amdmsa.adapter.PlaceAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent


@Module
@InstallIn(ActivityComponent::class)
object AdapterModule {

    @Provides
    fun providePlaceItemListener(activity: HomeActivity): PlaceAdapter.PlaceItemListener {
        return activity
    }

    @Provides
    fun providePlaceAdapter(placeItemListener: PlaceAdapter.PlaceItemListener): PlaceAdapter {
        return PlaceAdapter(placeItemListener)
    }
}