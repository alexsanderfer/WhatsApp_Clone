package com.alexsander.whatsappclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alexsander.whatsappclone.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private val binding by lazy { ActivityRegisterBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeToolbar()
    }

    private fun initializeToolbar() {
        val toolbar = binding.includeToolbar.mMainToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Faça o seu cadastro"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}