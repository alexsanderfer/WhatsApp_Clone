/*
 * Copyright (c) 2024. Created by Alexsander Fernandes at 2/22. All rights reserved.
 * GitHub: https://github.com/alexsanderfer/
 * Portfolio: https://alexsanderfer.netlify.app/
 */

package com.alexsander.whatsappclone.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alexsander.whatsappclone.activities.MainActivity
import com.alexsander.whatsappclone.R
import com.alexsander.whatsappclone.databinding.ActivityLoginBinding
import com.alexsander.whatsappclone.utils.displayToastMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val auth by lazy { FirebaseAuth.getInstance() }
    private lateinit var email: String
    private lateinit var password: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeClickEvents()
    }

    override fun onStart() {
        super.onStart()
        checkUserLogged()
    }

    private fun checkUserLogged() {
        val loggedUser = auth.currentUser
        if (loggedUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun initializeClickEvents() {
        binding.textRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.buttonLogin.setOnClickListener {
            if (validateFields()) {
                loginUser(email, password)
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                startActivity(Intent(this, MainActivity::class.java))
                displayToastMessage(getString(R.string.login_successfully_with_user_email, it.user?.email))
            }.addOnFailureListener {
                try {
                    throw it
                } catch (errorEmail: FirebaseAuthInvalidUserException) {
                    displayToastMessage("E-mail não cadastrado!")
                    errorEmail.printStackTrace()
                } catch (errorCredentials: FirebaseAuthInvalidCredentialsException) {
                    displayToastMessage("E-mail ou senha incorreto, por favor tente novamente.")
                    errorCredentials.printStackTrace()
                }
            }
    }

    private fun validateFields(): Boolean {
        email = binding.editTextEmail.text.toString().trim()
        password = binding.editTextPassword.text.toString().trim()

        if (email.isNotEmpty()) {
            binding.textInputLayoutLoginEmail.error = null
            if (password.isNotEmpty()) {
                binding.textInputLayoutLoginPassword.error = null
            } else {
                binding.textInputLayoutLoginPassword.error = getString(R.string.password_field_empty)
                return false
            }
        } else {
            binding.textInputLayoutLoginEmail.error = getString(R.string.email_field_empty)
            return false
        }
        return true
    }
}