/*
 * Copyright (c) 2024. Created by Alexsander Fernandes at 2/24. All rights reserved.
 * GitHub: https://github.com/alexsanderfer/
 * Portfolio: https://alexsanderfer.netlify.app/
 */

package com.alexsander.whatsappclone.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alexsander.whatsappclone.databinding.ActivityMessagesBinding

class MessagesActivity : AppCompatActivity() {
    private val binding by lazy {ActivityMessagesBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        recoverRecipientUserData()
    }

    private fun recoverRecipientUserData() {
        TODO("Not yet implemented")
    }
}