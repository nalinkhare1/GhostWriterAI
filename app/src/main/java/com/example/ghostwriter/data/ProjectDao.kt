package com.example.ghostwriter.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY timestamp DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProject(id: Long)

    @Query("DELETE FROM projects")
    suspend fun clearHistory()

    @Query("UPDATE projects SET name = :newName WHERE id = :id")
    suspend fun updateProjectName(id: Long, newName: String)

    @Query("UPDATE projects SET html = :html WHERE id = :id")
    suspend fun updateProjectHtml(id: Long, html: String)
}
