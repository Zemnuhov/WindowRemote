package com.novsu.windowremoote

import android.app.Application
import com.novsu.windowremoote.di.AppComponent

class Application: Application() {
    override fun onCreate() {
        super.onCreate()
        if(!AppComponent.componentIsInit()){
            AppComponent.init(this)
            AppComponent.provideDependencies()
        }
    }
}