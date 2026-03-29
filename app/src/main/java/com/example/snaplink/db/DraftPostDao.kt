package com.example.snaplink.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DraftPostDao {
    @Query("SELECT * FROM draft_posts ORDER BY timestamp DESC")
    fun getAllDrafts(): Flow<List<DraftPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDraft(draft: DraftPost)

    @Delete
    suspend fun deleteDraft(draft: DraftPost)

    @Query("DELETE FROM draft_posts WHERE id = :draftId")
    suspend fun deleteById(draftId: Int)

    @Query("DELETE FROM draft_posts")
    suspend fun deleteAll()
}
