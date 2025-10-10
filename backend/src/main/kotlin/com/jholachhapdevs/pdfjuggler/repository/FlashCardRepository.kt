package com.jholachhapdevs.pdfjuggler.repository

import com.jholachhapdevs.pdfjuggler.entity.FlashCard
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface FlashCardRepository: MongoRepository<FlashCard, String> {
}