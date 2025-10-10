package com.jholachhapdevs.pdfjuggler.repository

import com.jholachhapdevs.pdfjuggler.entity.FlashCardSet
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface FlashCardSetRepository: MongoRepository<FlashCardSet, String> {
}