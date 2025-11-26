package com.example.apimovies

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp // <--- ¡ESTA ES LA LLAVE DE ENCENDIDO!
class MovieApplication : Application()