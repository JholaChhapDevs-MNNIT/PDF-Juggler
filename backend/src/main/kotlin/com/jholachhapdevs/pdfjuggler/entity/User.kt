package com.jholachhapdevs.pdfjuggler.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Document("users")
data class User(
    @Id val id: ObjectId = ObjectId(),

    // different property names -> no generated getUsername()/getPassword()
    val uname: String,

    @get:JsonIgnore
    val pwd: String,
    @Indexed(unique = true)
    val phone: String
) : UserDetails {

    // Implement the UserDetails methods manually, delegating to uname/pwd
    override fun getUsername(): String = uname
    override fun getPassword(): String = pwd

    override fun getAuthorities(): Collection<GrantedAuthority> = listOf()
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true
}
