package com.jholachhapdevs.pdfjuggler.entity


import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "flashcard_sets")
data class FlashCardSet(
    @Id
    var id: String? = null,         // MongoDB _id field
    var setname: String
)