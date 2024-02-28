/*
 * Copyright (c) 2024. Created by Alexsander Fernandes at 2/28. All rights reserved.
 * GitHub: https://github.com/alexsanderfer/
 * Portfolio: https://alexsanderfer.netlify.app/
 */

package com.alexsander.whatsappclone.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexsander.whatsappclone.R
import com.alexsander.whatsappclone.activities.MessagesActivity
import com.alexsander.whatsappclone.databinding.FragmentChatsBinding
import com.alexsander.whatsappclone.model.Chats
import com.alexsander.whatsappclone.model.User
import com.alexsander.whatsappclone.utils.Constants
import com.alexsander.whatsappclone.utils.adapters.ChatsAdapter
import com.alexsander.whatsappclone.utils.displayToastMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ChatsFragment : Fragment() {
    private lateinit var binding: FragmentChatsBinding
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private lateinit var chatsAdapter: ChatsAdapter
    private lateinit var listenerSnapshot: ListenerRegistration

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentChatsBinding.inflate(
            inflater, container, false
        )

        chatsAdapter = ChatsAdapter{chat ->
            val intent = Intent(context, MessagesActivity::class.java)
            val user = User(
                id = chat.idUserRecipient,
                name = chat.name,
                photoProfile = chat.photo
            )
            intent.putExtra("recipientData", user) // It's who receive the messages datas
            intent.putExtra("originData", Constants.ORIGIN_CHAT) // Origin of the messages datas
            startActivity(intent)
        }
        binding.recyclerViewChats.adapter = chatsAdapter
        binding.recyclerViewChats.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewChats.addItemDecoration(
            DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        )

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        addChatsListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerSnapshot.remove()
    }

    private fun addChatsListener() {
        listenerSnapshot = firestore.collection(Constants.DB_COLLECTION_CHATS)
            .document(auth.currentUser?.uid!!)
            .collection(Constants.LAST_CHATS)
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    activity?.displayToastMessage(getString(R.string.error_retrieving_conversations))
                }
                val listChats = mutableListOf<Chats>()
                val documents = querySnapshot?.documents

                documents?.forEach {
                    val chat = it.toObject(Chats::class.java)
                    if (chat != null) {
                        listChats.add(chat)

                    }
                }
                // adapter
                if (listChats.isNotEmpty()) {
                    chatsAdapter.addList(listChats)
                }
            }
    }

}