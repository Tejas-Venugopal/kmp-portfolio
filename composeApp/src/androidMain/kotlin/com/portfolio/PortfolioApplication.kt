package com.portfolio

import android.app.Application
import com.portfolio.platform.AppContextHolder

class PortfolioApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContextHolder.context = applicationContext
    }
}

