package com.jholachhapdevs.pdfjuggler.repository

import FlashCard
import org.springframework.data.mongodb.repository.MongoRepository

interface FlashCardRepository: MongoRepository<FlashCard, String> {
}