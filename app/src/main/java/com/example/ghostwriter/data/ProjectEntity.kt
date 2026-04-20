package com.example.ghostwriter.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String? = null,
    val html: String,
    val timestamp: Long = System.currentTimeMillis(),
    val previewImage: String? = null
)
