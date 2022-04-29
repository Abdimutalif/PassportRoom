package com.example.passportroom.dao

import androidx.room.*
import com.example.passportroom.entity.Passport

@Dao
interface PassportDao {
    @Insert
    fun addPassport(passport: Passport)

    @Delete
    fun deletePassport(passport: Passport)

    @Update
    fun editPassport(passport: Passport)

    @Query("select* from passport")
    fun getPassportList(): List<Passport>

    @Query("select * from passport where name like :searchQuery or surname like :searchQuery")
    fun searchDatabase(searchQuery:String):List<Passport>
}