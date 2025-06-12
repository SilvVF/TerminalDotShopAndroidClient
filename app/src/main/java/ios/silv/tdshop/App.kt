package ios.silv.tdshop

import android.app.Application
import ios.silv.tdshop.di.AppComponent
import ios.silv.tdshop.di.create

class App: Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = AppComponent::class.create(this@App)
    }
}