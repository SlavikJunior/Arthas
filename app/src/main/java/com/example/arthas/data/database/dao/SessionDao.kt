package com.example.arthas.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.arthas.data.database.entity.Session
import java.util.concurrent.TimeUnit


@Dao
interface SessionDao {

    @Insert(entity = Session::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: Session): Long

    @Query("select * from $TABLE_NAME where id = :id limit 1")
    suspend fun getSessionById(id: Int): Session?

    @Query("update $TABLE_NAME set removed_at = :timestamp where id = :id")
    suspend fun removeSessionByUser(id: Int, timestamp: Long = System.currentTimeMillis())

    @Query("update $TABLE_NAME set deleted_at = :timestamp where id = :id")
    suspend fun softDelete(id: Int, timestamp: Long = System.currentTimeMillis())

    @Query("""
    update $TABLE_NAME
    set
        removed_at = null,
        updated_at = :updatedAt,
        valid_to = :validTo
    where id = :id""")
    suspend fun recoverSession(
        id: Int,
        updatedAt: Long = System.currentTimeMillis(),
        validTo: Long = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7),
    )

    @Query("""
        update $TABLE_NAME 
        set deleted_at = :timestamp 
        where id = :id
    """)
    suspend fun markAsDeleted(id: Int, timestamp: Long = System.currentTimeMillis())

    @Query("""
        select *
        from $TABLE_NAME 
        where
            removed_at is not null 
            and deleted_at is null
    """)
    suspend fun getAllRemovedSessions(): List<Session>

    companion object {
        const val TABLE_NAME = "sessions"
    }
}