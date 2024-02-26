/*
 * Copyright (c) 2024. Created by Alexsander Fernandes at 2/23. All rights reserved.
 * GitHub: https://github.com/alexsanderfer/
 * Portfolio: https://alexsanderfer.netlify.app/
 */

package com.alexsander.whatsappclone.ui.login.profile

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.alexsander.whatsappclone.R
import com.alexsander.whatsappclone.databinding.ActivityProfileBinding
import com.alexsander.whatsappclone.fragments.BottomProfileDialogFragment
import com.alexsander.whatsappclone.fragments.ImageSelectedListener
import com.alexsander.whatsappclone.utils.displayToastMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class ProfileActivity() : AppCompatActivity(), ImageSelectedListener {
    private val binding by lazy { ActivityProfileBinding.inflate(layoutInflater) }
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val storage by lazy { FirebaseStorage.getInstance() }
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private var hasCameraPermission = false
    private var hasGalleryPermission = false
    private var bitmapImageProfile: Bitmap? = null

    override fun onImageSelected(bitmap: Bitmap) {
        binding.imageProfile.setImageBitmap(bitmap)
        uploadImageProfileToDatabase(bitmap)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeToolbar()
        initializeClickEvents()
        // getProfileImagemFromDatabase()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestUserPermission()
        }
    }

    override fun onStart() {
        super.onStart()
        getDataFromDatabaseOneTime()
    }

    private fun getDataFromDatabaseOneTime() {
        val idLoggedUser = auth.currentUser?.uid
        if (idLoggedUser != null) {
            firestore.collection("users")
                .document(idLoggedUser)
                .get()
                .addOnSuccessListener {
                    val dataUser = it.data
                    if (dataUser != null) {
                        val name = dataUser["name"] as String
                        val photo = dataUser["photoProfile"] as String

                        binding.textInputEditName.setText(name)
                        if (photo.isNotEmpty()) {
                            Picasso.get().load(photo).into(binding.imageProfile)
                        }
                    }
                }
        }
    }

    private fun uploadImageProfileToDatabase(bitmap: Bitmap) {
        val idLoggedUser = auth.currentUser?.uid
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

        if (idLoggedUser != null) {
            storage.getReference("photos")
                .child("users")
                .child(idLoggedUser)
                .child("profile")
                .child("profile.jpeg")
                .putBytes(outputStream.toByteArray())
                .addOnFailureListener { error ->
                    displayToastMessage(getString(R.string.error_upload_image, error.message))
                }
                .addOnSuccessListener { task ->
                    task.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                        val data = mapOf("photoProfile" to it.toString())
                        updateDataProfile(idLoggedUser, data)
                    }
                    displayToastMessage(getString(R.string.success_upload_image_profile))
                }
        }
    }

    private fun updateDataProfile(idLoggedUser: String, data: Map<String, Any>) {
        firestore.collection("users")
            .document(idLoggedUser)
            .update(data)
            .addOnSuccessListener {
                displayToastMessage(getString(R.string.success_updating_profile))
            }
            .addOnFailureListener {
                displayToastMessage(getString(R.string.error_updating_profile))
            }
    }

    constructor(parcel: Parcel) : this() {
        hasCameraPermission = parcel.readByte() != 0.toByte()
        hasGalleryPermission = parcel.readByte() != 0.toByte()
        bitmapImageProfile = parcel.readParcelable(Bitmap::class.java.classLoader)
    }

    private fun initializeClickEvents() {
        binding.fabCameraProfile.setOnClickListener {
            val dialogFragment = BottomProfileDialogFragment()
            dialogFragment.show(supportFragmentManager, "profile_bottom_sheet")
        }

        binding.buttonUpdate.setOnClickListener {
            val userName = binding.textInputEditName.text.toString()
            if (userName.isNotEmpty()) {
                val idLoggedUser = auth.currentUser?.uid
                if (idLoggedUser != null) {
                    val data = mapOf(
                        "name" to userName
                    )
                    updateDataProfile(idLoggedUser, data)
                }
            } else {
                displayToastMessage("Preencha o nome para atualizar.")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestUserPermission() {
        hasCameraPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        hasGalleryPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val listDeniedPermissions = mutableListOf<String>()
        if (!hasCameraPermission) listDeniedPermissions.add(Manifest.permission.CAMERA)
        if (!hasGalleryPermission) if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listDeniedPermissions.add(Manifest.permission.READ_MEDIA_IMAGES)
        }
        if (listDeniedPermissions.isNotEmpty()) {
            val managerPermissions = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) {
                hasCameraPermission = it[Manifest.permission.CAMERA] ?: hasCameraPermission
                hasGalleryPermission = it[Manifest.permission.READ_MEDIA_IMAGES] ?: hasGalleryPermission
            }
            managerPermissions.launch(listDeniedPermissions.toTypedArray())
        }
    }

    private fun initializeToolbar() {
        setSupportActionBar(binding.toolbarProfile.mMainToolbar)
        supportActionBar?.apply {
            title = getString(R.string.profile)
            setDisplayHomeAsUpEnabled(true)
        }
    }


}
