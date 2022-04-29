package com.example.passportroom.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Passport(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,
    @ColumnInfo(name="name")
    var name:String,
    @ColumnInfo(name="surname")
    var surname:String,
    @ColumnInfo(name="fio")
    var fio:String,
    @ColumnInfo(name="region")
    var region:String,
    @ColumnInfo(name="city")
    var city:String,
    @ColumnInfo(name="address")
    var address:String,
    @ColumnInfo(name="date")
    var date:String,
    @ColumnInfo(name="deadline")
    var deadline:String,
    @ColumnInfo(name="gender")
    var gender:String,
    @ColumnInfo(name="seriesNumber")
    var seriesNumber:String,
    @ColumnInfo(name="imagePath")
    var imagePath:String,
):Serializable