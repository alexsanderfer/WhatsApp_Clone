/*
 * Copyright (c) 2024. Created by Alexsander Fernandes at 2/28. All rights reserved.
 * GitHub: https://github.com/alexsanderfer/
 * Portfolio: https://alexsanderfer.netlify.app/
 */

package com.alexsander.whatsappclone.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Chats(
    val idUserSender: String = "",
    val idUserRecipient: String = "",
    val photo: String = "",
    val name: String = "",
    val lastMessage: String = "",
    @ServerTimestamp
    val date: Date? = null
)
