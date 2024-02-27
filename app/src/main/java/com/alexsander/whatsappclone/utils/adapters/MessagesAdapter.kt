/*
 * Copyright (c) 2024. Created by Alexsander Fernandes at 2/27. All rights reserved.
 * GitHub: https://github.com/alexsanderfer/
 * Portfolio: https://alexsanderfer.netlify.app/
 */

package com.alexsander.whatsappclone.utils.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.alexsander.whatsappclone.databinding.ItemMessageRecipientBinding
import com.alexsander.whatsappclone.databinding.ItemMessageSenderBinding
import com.alexsander.whatsappclone.model.Message
import com.alexsander.whatsappclone.utils.Constants
import com.google.firebase.auth.FirebaseAuth

class MessagesAdapter : Adapter<ViewHolder>() {
    private var listMessages = emptyList<Message>()
    fun addList(list: List<Message>) {
        listMessages = list
        notifyDataSetChanged()
    }

    class MessagesSenderViewHolder(
        private val binding: ItemMessageSenderBinding
    ) : ViewHolder(binding.root) {
        fun bind(message: Message) {

        }
        companion object {
            fun layoutInflater(parent: ViewGroup): MessagesSenderViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val itemMessageSender = ItemMessageSenderBinding.inflate(inflater, parent, false)
                return MessagesSenderViewHolder(itemMessageSender)
            }
        }
    }

    class MessageRecipientViewHolder(
        private val binding: ItemMessageRecipientBinding
    ) : ViewHolder(binding.root) {
        fun bin(message: Message){

        }
        companion object {
            fun layoutInflater(parent: ViewGroup): MessageRecipientViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val itemMessageRecipient = ItemMessageRecipientBinding.inflate(inflater, parent, false)
                return MessageRecipientViewHolder(itemMessageRecipient)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = listMessages[position]
        val idUserLogged = FirebaseAuth.getInstance().currentUser?.uid.toString()
        return if (idUserLogged == message.idUser) {
            Constants.SENDER_TYPE
        } else {
            Constants.RECIPIENT_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == Constants.SENDER_TYPE) {
            return MessagesSenderViewHolder.layoutInflater(parent)
        }
        return MessageRecipientViewHolder.layoutInflater(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = listMessages[position]
        when (holder) {
            is MessagesSenderViewHolder -> holder.bind(message)
            is MessageRecipientViewHolder -> holder.bin(message)
        }
    }

    override fun getItemCount(): Int {
        return listMessages.size
    }
}










