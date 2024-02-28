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
import com.alexsander.whatsappclone.activities.MessagesActivity
import com.alexsander.whatsappclone.databinding.FragmentContactsBinding
import com.alexsander.whatsappclone.model.User
import com.alexsander.whatsappclone.utils.Constants
import com.alexsander.whatsappclone.utils.adapters.ContactsAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class ContactsFragment : Fragment() {
    private lateinit var binding: FragmentContactsBinding
    private lateinit var eventSnapshot: ListenerRegistration
    private lateinit var contactsAdapter: ContactsAdapter
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentContactsBinding.inflate(
            inflater, container, false
        )
        contactsAdapter = ContactsAdapter {
            val intent = Intent(context, MessagesActivity::class.java)
            intent.putExtra("recipientData", it) // It's who receive the messages datas
            intent.putExtra("originData", Constants.ORIGIN_CONTACT) // Origin of the messages datas
            startActivity(intent)
        }
        binding.recyclerViewContacts.adapter = contactsAdapter
        binding.recyclerViewContacts.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewContacts.addItemDecoration(
            DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        )

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        addContactsListener()
    }

    private fun addContactsListener() {

        eventSnapshot = firestore
            .collection("users")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { value, _ ->
                val userList = mutableListOf<User>()
                val document = value?.documents
                document?.forEach {
                    val idLoggedUser = auth.currentUser?.uid
                    val user = it.toObject(User::class.java)
                    if (user != null && idLoggedUser != null) {
                        if (idLoggedUser != user.id) {
                            userList.add(user)
                        }
                    }
                }
                if (userList.isNotEmpty()) {
                    contactsAdapter.addList(userList)
                }
            }

    }


    override fun onDestroy() {
        super.onDestroy()
        eventSnapshot.remove()
    }

}