package com.jholachhapdevs.pdfjuggler.repository

import com.jholachhapdevs.pdfjuggler.entity.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository : MongoRepository<User, ObjectId>{
    fun findByUname(username: String): User?
    fun existsByUname(username: String?): Boolean

    fun findByPhone(phone: String): User?
    fun existsByPhone(phone: String): Boolean
}