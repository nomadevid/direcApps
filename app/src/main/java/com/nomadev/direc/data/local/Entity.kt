package com.nomadev.direc.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PasienEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val phone: String,
    val address: String
)
