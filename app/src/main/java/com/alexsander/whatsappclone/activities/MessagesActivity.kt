/*
 * Copyright (c) 2024. Created by Alexsander Fernandes at 2/28. All rights reserved.
 * GitHub: https://github.com/alexsanderfer/
 * Portfolio: https://alexsanderfer.netlify.app/
 */

package com.alexsander.whatsappclone.activities

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexsander.whatsappclone.R
import com.alexsander.whatsappclone.databinding.ActivityMessagesBinding
import com.alexsander.whatsappclone.model.Chats
import com.alexsander.whatsappclone.model.Message
import com.alexsander.whatsappclone.model.User
import com.alexsander.whatsappclone.utils.Constants
import com.alexsander.whatsappclone.utils.adapters.MessagesAdapter
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
    private lateinit var messagesAdapter: MessagesAdapter
    private var recipientData: User? = null
    private var originData: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        recoverUserData()
        initializeToolbar()
        initializeClickEvents()
        initializeRecyclerView()
        initializeListeners()

    }

    private fun initializeRecyclerView() {
        with(binding) {
            messagesAdapter = MessagesAdapter()
            recyclerViewMessages.adapter = messagesAdapter
            recyclerViewMessages.layoutManager = LinearLayoutManager(applicationContext)
        }
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
                        }
                    }

                    if (listMessage.isNotEmpty()) {
                        messagesAdapter.addList(listMessage)
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

                val chatSender = Chats(
                    idUserSender, idUserRecipient, recipientData!!.photoProfile, recipientData!!.name, textMessage
                )
                saveChatOnFirestore(chatSender)

                val chatRecipient = Chats(
                    idUserRecipient, idUserSender, originData!!.photoProfile, originData!!.name, textMessage
                )

                saveChatOnFirestore(chatRecipient)

                binding.messageText.setText("")
            }
        }
    }

    private fun saveChatOnFirestore(chat: Chats) {
        firestore.collection(Constants.DB_COLLECTION_CHATS)
            .document(chat.idUserSender)
            .collection(Constants.LAST_CHATS)
            .document(chat.idUserRecipient)
            .set(chat)
            .addOnFailureListener {
                displayToastMessage(getString(R.string.erro_saving_chat))
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

    private fun recoverUserData() {
        auth.currentUser?.uid?.let {
            firestore.collection(Constants.DB_COLLECTION_USERS)
                .document(it)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val user = documentSnapshot.toObject(User::class.java)
                    if (user != null) {
                        originData = user
                    }
                }
        }

        val extras = intent.extras ?: throw RuntimeException("Intent extras are null")
        //val origin = extras.getString("originData") ?: throw RuntimeException("originData not found in extras")

        if (extras != null) {
            recipientData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                extras.getParcelable("recipientData", User::class.java) ?: throw RuntimeException("Failed to retrieve recipient data as User")
            } else {
                extras.getParcelable("recipientData") ?: throw RuntimeException("Failed to retrieve recipient data")
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