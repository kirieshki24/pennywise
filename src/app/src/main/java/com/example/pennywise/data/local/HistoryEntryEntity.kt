package com.example.pennywise.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.pennywise.domain.model.HistoryEntry
import com.example.pennywise.domain.model.TransactionType

@Entity(
    tableName = "history_entries",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("profileId")]
)
data class HistoryEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val profileId: Long,
    val amount: Double,
    val note: String,
    val timestamp: Long,
    val type: String
)

fun HistoryEntryEntity.toDomain(): HistoryEntry {
    return HistoryEntry(
        id = id,
        profileId = profileId,
        amount = amount,
        note = note,
        timestamp = timestamp,
        type = TransactionType.fromStorage(type)
    )
}

fun HistoryEntry.toEntity(): HistoryEntryEntity {
    return HistoryEntryEntity(
        id = id,
        profileId = profileId,
        amount = amount,
        note = note,
        timestamp = timestamp,
        type = type.storageValue
    )
}
