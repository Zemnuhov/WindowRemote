package com.novsu.windowremoote.di

import android.app.Application
import android.content.Context
import com.neurotech.core_ble_device_scan.impl.BluetoothScan
import com.neurotech.core_ble_device_scan_api.BluetoothScanAPI
import com.neurotech.core_bluetooth_comunication_api.BluetoothConnectionApi
import com.neurotech.core_bluetooth_comunication_api.BluetoothDataApi
import com.neurotech.core_bluetooth_comunication_api.BluetoothWriterApi
import com.neurotech.core_bluetooth_comunication_impl.implementation.BluetoothConnection
import com.neurotech.core_bluetooth_comunication_impl.implementation.BluetoothData
import com.neurotech.core_bluetooth_comunication_impl.implementation.BluetoothWriter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule{

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideScanApi(): BluetoothScanAPI {
        return BluetoothScan()
    }

    @Provides
    @Singleton
    fun provideConnectionApi(): BluetoothConnectionApi {
        return BluetoothConnection()
    }

    @Provides
    @Singleton
    fun provideBluetoothDataApi(): BluetoothDataApi {
        return BluetoothData()
    }

    @Provides
    @Singleton
    fun provideBluetoothWriterApi(): BluetoothWriterApi {
        return BluetoothWriter()
    }


}