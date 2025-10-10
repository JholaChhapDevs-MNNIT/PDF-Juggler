package com.jholachhapdevs.pdfjuggler.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Document(collection = "flashcards")
data class FlashCard(

    @Id
    var id: String? = null,

    @field:NotBlank(message = "Question cannot be blank")
    @field:Size(min = 3, message = "Question must be at least 3 characters long")
    var question: String,

    @field:NotBlank(message = "Answer cannot be blank")
    @field:Size(min = 1, message = "Answer must not be empty")
    var answer: String,

    @field:NotBlank(message = "Set ID cannot be blank")
    var setId: String   // Reference to FlashCardSet.id
)
