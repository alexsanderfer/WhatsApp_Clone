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
import com.alexsander.whatsappclone.databinding.ActivityRegisterBinding
import com.alexsander.whatsappclone.model.User
import com.alexsander.whatsappclone.utils.displayToastMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private val binding by lazy { ActivityRegisterBinding.inflate(layoutInflater) }
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeToolbar()
        initializeClickEvents()
    }

    private fun initializeClickEvents() {
        binding.buttonRegister.setOnClickListener {
            if (validateFields()) {
                registerUser(name, email, password)
            }
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        // Create a user in Firebase
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    // Save datas into Firestore Database
                    val idUser = it.result.user?.uid
                    if (!idUser.isNullOrEmpty()) {
                        val user = User(idUser.toString(), name, email)
                        saveUserToFirestore(user)
                    }
                }
            }.addOnFailureListener { error ->
                try {
                    throw error
                } catch (errorWeakPassword: FirebaseAuthWeakPasswordException) {
                    displayToastMessage(getString(R.string.weak_password))
                    errorWeakPassword.printStackTrace()
                } catch (errorCredentials: FirebaseAuthInvalidCredentialsException) {
                    error.printStackTrace()
                    displayToastMessage(getString(R.string.invalid_email))
                } catch (errorUserAlreadyExist: FirebaseAuthUserCollisionException) {
                    errorUserAlreadyExist.printStackTrace()
                    displayToastMessage(getString(R.string.email_already_in_use))
                }
            }
    }

    private fun saveUserToFirestore(user: User) {
        db.collection("users")
            .document(user.id)
            .set(user)
            .addOnSuccessListener {
                displayToastMessage(getString(R.string.user_successfully_registered))
                startActivity(Intent(applicationContext, MainActivity::class.java))
            }
            .addOnFailureListener {
                displayToastMessage(getString(R.string.error_register))
            }
    }

    private fun validateFields(): Boolean {
        name = binding.editPersonName.text.toString().trim()
        email = binding.editEmail.text.toString().trim()
        password = binding.editPassword.text.toString().trim()

        when {
            name.isNotEmpty() -> {
                binding.textInputPersonName.error = null
                if (email.isNotEmpty()) {
                    binding.textInputLayoutEmail.error = null
                    if (password.isNotEmpty()) {
                        binding.textInputLayoutPassword.error = null
                    } else {
                        binding.textInputLayoutPassword.error = getString(R.string.fill_password_field)
                    }
                } else {
                    binding.textInputLayoutEmail.error = getString(R.string.fill_email_field)
                }
            }

            else -> {
                binding.textInputPersonName.error = getString(R.string.fill_name_field)
                return false
            }
        }
        return true
    }

    private fun initializeToolbar() {
        setSupportActionBar(binding.includeToolbar.mMainToolbar)
        supportActionBar?.apply {
            title = "Faça o seu cadastro"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}