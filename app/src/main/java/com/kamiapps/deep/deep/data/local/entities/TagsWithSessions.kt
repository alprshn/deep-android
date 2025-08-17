package com.kamiapps.deep.deep.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation

data class TagsWithSessions(
    @Embedded val tag: Tags,
    @Relation(
        parentColumn = "tag_id",
        entityColumn = "tag_id"
    )
    val sessions: List<Sessions>
)