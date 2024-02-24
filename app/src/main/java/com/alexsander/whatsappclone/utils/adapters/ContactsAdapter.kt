/*
 * Copyright (c) 2024. Created by Alexsander Fernandes at 2/23. All rights reserved.
 * GitHub: https://github.com/alexsanderfer/
 * Portfolio: https://alexsanderfer.netlify.app/
 */

package com.alexsander.whatsappclone.utils.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.URLUtil.isValidUrl
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.alexsander.whatsappclone.R
import com.alexsander.whatsappclone.databinding.ItemContactsBinding
import com.alexsander.whatsappclone.model.User
import com.squareup.picasso.Picasso

class ContactsAdapter(
    private val onClick: (User) -> Unit
) : Adapter<ContactsAdapter.ContactsViewHolder>() {
    private var listContacts = emptyList<User>()

    fun addList(list: List<User>) {
        listContacts = list
        notifyDataSetChanged()
    }

    inner class ContactsViewHolder(
        private val binding: ItemContactsBinding
    ) : ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.constraintLayoutItemContact.setOnClickListener {
                onClick(user)
            }

            binding.textContactName.text = user.name
            if (user.photoProfile.isEmpty() || !isValidUrl(user.photoProfile)) {
                binding.imageContactProfile.setImageResource(R.drawable.profile_placeholder)
            } else {
                Picasso.get()
                    .load(user.photoProfile)
                    .into(binding.imageContactProfile);
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = ItemContactsBinding.inflate(inflater, parent, false)
        return ContactsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        val user = listContacts[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int {
        return listContacts.size
    }
}