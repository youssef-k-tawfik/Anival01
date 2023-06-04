package com.example.anival01.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.anival01.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private lateinit var b: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        Timber.plant(Timber.DebugTree())
    }
}