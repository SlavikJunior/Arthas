package com.example.arthas.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["email"], name = "email_index", unique = true),
        Index(value = ["email", "password_hash"], name = "email_password_hash_index")
    ],
    foreignKeys = [ForeignKey(
        entity = Session::class,
        parentColumns = ["id"],
        childColumns = ["session_id"],
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.SET_NULL
    )]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "session_id")
    val sessionId: Int? = null,
    val email: String = "",
    @ColumnInfo(name = "password_hash")
    val passwordHash: String = ""
)