package com.escala.ministerial

import android.app.Application
import com.escala.ministerial.core.data.seed.LocalSeedDataSeeder
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class EscalaApp : Application() {

    @Inject lateinit var seeder: LocalSeedDataSeeder

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        appScope.launch { seeder.seedIfEmpty() }
    }
}
