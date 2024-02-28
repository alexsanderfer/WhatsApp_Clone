/*
 * Copyright (c) 2024. Created by Alexsander Fernandes at 2/28. All rights reserved.
 * GitHub: https://github.com/alexsanderfer/
 * Portfolio: https://alexsanderfer.netlify.app/
 */

package com.alexsander.whatsappclone.utils.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.alexsander.whatsappclone.databinding.ItemChatBinding
import com.alexsander.whatsappclone.model.Chats
import com.squareup.picasso.Picasso

class ChatsAdapter(
    private val onClick: (Chats) -> Unit
) : Adapter<ChatsAdapter.ChatViewHolder>() {
    private var listChats = emptyList<Chats>()
    fun addList(list: List<Chats>){
        listChats = list
        notifyDataSetChanged()
    }

    inner class ChatViewHolder(
        private val binding: ItemChatBinding
    ) : ViewHolder(binding.root) {

        fun bind(chats: Chats) {
            binding.textChatName.text = chats.name
            Picasso.get()
                .load(chats.photo)
                .into(binding.imageChatProfile)

            binding.textLastMessage.text = chats.lastMessage
            binding.constraintLayoutItemChat.setOnClickListener {
                onClick(chats)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = ItemChatBinding.inflate(inflater, parent, false)
        return ChatViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chats = listChats[position]
        holder.bind(chats)
    }


    override fun getItemCount(): Int {
        return listChats.size
    }
}