package com.ifthen.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ifthen.app.domain.model.Delegate
import com.ifthen.app.domain.model.DelegateChannel

@Entity(tableName = "delegates")
data class DelegateEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val channel: DelegateChannel,
    val contact: String,
    val areas: List<String>,
    val createdAt: Long
)

fun DelegateEntity.toDomain(): Delegate = Delegate(
    id = id,
    name = name,
    channel = channel,
    contact = contact,
    areas = areas,
    createdAt = createdAt
)

fun Delegate.toEntity(): DelegateEntity = DelegateEntity(
    id = id,
    name = name,
    channel = channel,
    contact = contact,
    areas = areas,
    createdAt = createdAt
)
