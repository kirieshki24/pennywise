package com.example.pennywise

import android.app.Application
import com.example.pennywise.ui.AppContainer

class PennyWiseApplication : Application() {
    val container: AppContainer by lazy { AppContainer(this) }
}
