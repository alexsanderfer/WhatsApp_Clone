/*
 * Copyright (c) 2024. Created by Alexsander Fernandes at 2/24. All rights reserved.
 * GitHub: https://github.com/alexsanderfer/
 * Portfolio: https://alexsanderfer.netlify.app/
 */

package com.alexsander.whatsappclone.activities

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.alexsander.whatsappclone.R
import com.alexsander.whatsappclone.databinding.ActivityMessagesBinding
import com.alexsander.whatsappclone.model.Message
import com.alexsander.whatsappclone.model.User
import com.alexsander.whatsappclone.utils.Constants
import com.alexsander.whatsappclone.utils.displayToastMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso

class MessagesActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMessagesBinding.inflate(layoutInflater) }
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }
    private lateinit var listenerRegistration: ListenerRegistration
    private var recipientData: User? = null
    private var originData: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        recoverRecipientUserData()
        initializeToolbar()
        initializeClickEvents()
        initializeListeners()

    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration.remove()
    }

    private fun initializeListeners() {
        val idUserSender = auth.currentUser?.uid // ID of the user who sends the message
        val idUserRecipient = recipientData?.id  // ID of the user receiving the message

        if (idUserSender != null && idUserRecipient != null) {
            listenerRegistration = firestore
                .collection(Constants.DB_COLLECTION_MESSAGES)
                .document(idUserSender)
                .collection(idUserRecipient)
                .orderBy("date", Query.Direction.ASCENDING)
                .addSnapshotListener { querySnapshot, error ->
                    if (error != null) {
                        displayToastMessage(getString(R.string.error_get_messages))
                    }
                    val listMessage = mutableListOf<Message>()
                    val documents = querySnapshot?.documents
                    documents?.forEach {
                        val message = it.toObject(Message::class.java)
                        if (message != null) {
                            listMessage.add(message)
                            Log.i("listener", "initializeListeners: ${message.message}")
                        }

                    }

                    if (listMessage.isNotEmpty()) {
                        // TODO adapter
                    }
                }

        }
    }

    private fun initializeClickEvents() {
        binding.fabSendMessage.setOnClickListener {
            val message = binding.messageText.text.toString()
            saveMessage(message)
            binding.messageText.setHint(R.string.input_message)
        }
    }

    private fun saveMessage(textMessage: String) {
        if (textMessage.isNotEmpty()) {
            val idUserSender = auth.currentUser?.uid // ID of the user who sends the message
            val idUserRecipient = recipientData?.id  // ID of the user receiving the message

            if (idUserSender != null && idUserRecipient != null) {
                val userMessage = Message(idUserSender, textMessage)
                saveMessageOnFirestore(idUserSender, idUserRecipient, userMessage) // This method saves the message for the sender.
                saveMessageOnFirestore(idUserRecipient, idUserSender, userMessage) // This method saves the message for the recipient.
                binding.messageText.setText("")
            }
        }
    }

    private fun saveMessageOnFirestore(idUserSender: String, idUserRecipient: String, userMessage: Message) {
        firestore.collection(Constants.DB_COLLECTION_MESSAGES)
            .document(idUserSender)
            .collection(idUserRecipient)
            .add(userMessage)
            .addOnFailureListener {
                displayToastMessage(getString(R.string.error_sending_message))
            }
    }

    private fun recoverRecipientUserData() {
        val extras = intent.extras ?: throw RuntimeException("Intent extras are null")
        val origin = extras.getString("originData") ?: throw RuntimeException("originData not found in extras")

        when (origin) {
            Constants.ORIGIN_CONTACT -> {
                recipientData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    extras.getParcelable("recipientData", User::class.java) ?: throw RuntimeException("Failed to retrieve recipient data as User")
                } else {
                    extras.getParcelable("recipientData") ?: throw RuntimeException("Failed to retrieve recipient data")
                }
            }

            Constants.ORIGIN_CHAT -> {
                // Implement data recovery logic for chats here
                // Store the retrieved data in originData
                originData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    extras.getParcelable("originData", User::class.java) ?: throw RuntimeException("Failed to retrieve origin data as User")
                } else {
                    extras.getParcelable("originData") ?: throw RuntimeException("Failed to retrieve origin data")
                }
            }

            else -> {
                throw RuntimeException("Invalid origin data: $origin")
            }
        }
    }

    private fun initializeToolbar() {
        setSupportActionBar(binding.materialToolbar)
        supportActionBar?.apply {
            title = ""
            if (recipientData != null) {
                binding.textName.text = recipientData!!.name
                Picasso.get().load(recipientData!!.photoProfile)
                    .into(binding.imageProfilePhoto)
            }
            setDisplayHomeAsUpEnabled(true)
        }
    }

}