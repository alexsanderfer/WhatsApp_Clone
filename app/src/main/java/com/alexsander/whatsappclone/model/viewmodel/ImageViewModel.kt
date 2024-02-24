/*
 * Copyright (c) 2024. Created by Alexsander Fernandes at 2/24. All rights reserved.
 * GitHub: https://github.com/alexsanderfer/
 * Portfolio: https://alexsanderfer.netlify.app/
 */

package com.alexsander.whatsappclone.model.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ImageViewModel : ViewModel() {

    private val _selectedImage = MutableLiveData<Bitmap>()
    val selectedImage: LiveData<Bitmap> get() = _selectedImage

    fun onImageSelected(bitmap: Bitmap) {
        _selectedImage.value = bitmap
    }


}