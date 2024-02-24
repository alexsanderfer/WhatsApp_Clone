/*
 * Copyright (c) 2024. Created by Alexsander Fernandes at 2/22. All rights reserved.
 * GitHub: https://github.com/alexsanderfer/
 * Portfolio: https://alexsanderfer.netlify.app/
 */

package com.alexsander.whatsappclone.utils

import android.app.Activity
import android.widget.Toast
import androidx.fragment.app.DialogFragment

fun Activity.displayToastMessage(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun DialogFragment.displayToastMessage(text: String) {
    requireActivity().displayToastMessage(text)
}
