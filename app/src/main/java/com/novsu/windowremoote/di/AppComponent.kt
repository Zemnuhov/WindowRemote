package com.novsu.windowremoote.di

import android.app.Application
import android.util.Log

import com.neurotech.core_bluetooth_comunication_impl.di.BleCommunicationDependencies
import com.neurotech.core_bluetooth_comunication_impl.di.BleCommunicationDependenciesStore
import com.novsu.windowremoote.MainActivity
import com.novsu.windowremoote.ui.main_screen.MainScreen
import com.novsu.windowremoote.ui.scan_screen.ScanFragment
import dagger.BindsInstance
import dagger.Component
import dagger.Component.Builder
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent:
    BleCommunicationDependencies
{

    fun inject(mainActivity: MainActivity)
    fun inject(scanFragment: ScanFragment)
    fun inject(mainScreen: MainScreen)

    @Builder
    interface AppBuilder{
        @BindsInstance
        fun application(application: Application): AppBuilder
        fun build(): AppComponent
    }

    companion object{
        private var component: AppComponent? = null

        fun componentIsInit() = component != null

        fun init(application: Application){
            component = DaggerAppComponent.builder().application(application).build()
        }

        fun get(): AppComponent = checkNotNull(component) {
            Log.e(
                "AppComponent",
                "Component is null"
            )
        }

        fun provideDependencies(){
            BleCommunicationDependenciesStore.dependencies = get()
        }


    }


}


