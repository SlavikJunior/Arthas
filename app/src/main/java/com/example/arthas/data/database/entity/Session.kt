package com.example.arthas.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.arthas.util.plusDays
import java.sql.Timestamp

@Entity(tableName = "sessions")
data class Session(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "created_at")
    val createdAt: Timestamp = Timestamp(System.currentTimeMillis()),
    @ColumnInfo(name = "valid_to")
    val validTo: Timestamp = createdAt.plusDays(7),
    @ColumnInfo(name = "updated_at")
    val updatedAt: Timestamp = Timestamp(System.currentTimeMillis()),
    @ColumnInfo(name = "removed_at")
    val removedAt: Timestamp? = null, // поле для удаления пользователем
    @ColumnInfo(name = "deleted_at")
    val deletedAt: Timestamp? = null // поле для soft-delete
)