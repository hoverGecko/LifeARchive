package hku.cs.lifearchive

import android.app.Application
import com.google.android.material.color.DynamicColors

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        // Material You support
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}