package com.alexsander.whatsappclone.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.alexsander.whatsappclone.R
import com.alexsander.whatsappclone.databinding.FragmentBottomProfileDialogBinding
import com.alexsander.whatsappclone.utils.displayToastMessage
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomProfileDialogFragment : BottomSheetDialogFragment()  {
    private lateinit var binding: FragmentBottomProfileDialogBinding

    private val galleryManager = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val selectedImage = getBitmapFromUri(uri)
            val activity = requireActivity() as ImageSelectedListener
            activity.onImageSelected(selectedImage)
            dismiss() // Close the dialog after successful image selection and upload initiation
            displayToastMessage("Imagem selecionada com sucesso.")
        } else {
            // User canceled or no image selected
            displayToastMessage(getString(R.string.no_image_selected))
        }
    }

    private val cameraManager = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val bitmap = result.data?.extras?.get("data") as Bitmap?
            if (bitmap != null) {
                val activity = requireActivity() as ImageSelectedListener
                activity.onImageSelected(bitmap)
                dismiss() // Close the dialog after successful image capture and upload initiation
                displayToastMessage("Imagem capturada com sucesso.")
            } else {
                displayToastMessage("Sem imagem capturada")
            }
        } else {
            // Handle user cancellation or failure to capture
            displayToastMessage(getString(R.string.no_image_selected))
        }
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap {
        return MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentBottomProfileDialogBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        handleButtonsOnClick()
    }

    private fun handleButtonsOnClick() {

        binding.fabCameraDrawer.setOnClickListener {
            cameraManager.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        }

        binding.fabGallery.setOnClickListener {
            galleryManager.launch("image/*")
        }

    }
}


interface ImageSelectedListener {
    fun onImageSelected(bitmap: Bitmap)
}