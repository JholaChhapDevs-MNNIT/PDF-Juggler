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
    var question: String,

    @field:NotBlank(message = "Answer cannot be blank")
    var answer: String
)