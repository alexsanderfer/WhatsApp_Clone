/*
 * Copyright (c) 2024. Created by Alexsander Fernandes at 2/25. All rights reserved.
 * GitHub: https://github.com/alexsanderfer/
 * Portfolio: https://alexsanderfer.netlify.app/
 */

package com.alexsander.whatsappclone.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Message(
    val idUser: String = "",
    val message: String = "",
    @ServerTimestamp
    val date: Date? = null
)
