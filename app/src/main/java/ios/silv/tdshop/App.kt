package ios.silv.tdshop

import android.app.Application
import ios.silv.tdshop.di.AppComponent
import ios.silv.tdshop.di.create
import logcat.AndroidLogcatLogger
import logcat.LogPriority

class App: Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        AndroidLogcatLogger.installOnDebuggableApp(this, minPriority = LogPriority.DEBUG)

        appComponent = AppComponent::class.create(this@App)
    }
}