package com.example.arthas.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.arthas.data.database.entity.User

@Dao
interface UserDao {

    @Insert(entity = User::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Delete(entity = User::class)
    suspend fun delete(user: User)

    @Query("select * from $TABLE_NAME where email like :email")
    suspend fun getUserByEmail(email: String): User?
    @Query("select * from $TABLE_NAME where email like :email and password_hash like :passwordHash")
    suspend fun getUserByEmailAndPasswordHash(email: String, passwordHash: String): User?

    @Query("""
        select u.* from $TABLE_NAME u
        join sessions s on u.session_id = s.id
        where u.id = :userId
        and s.removed_at is null
        and s.deleted_at is null
        and s.valid_to > :currentTime
        limit 1
    """)
    suspend fun getUserWithValidSession(userId: Int, currentTime: Long = System.currentTimeMillis()): User?

    companion object {
        const val TABLE_NAME = "users"
    }
}