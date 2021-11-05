package com.nomadev.direc.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PasienDao {
    @Insert
    fun addPatient(pasienEntity: PasienEntity)

    @Query("SELECT * FROM PasienEntity WHERE name LIKE :searchQuery OR phone LIKE :searchQuery OR address LIKE :searchQuery ORDER BY name ASC")
    fun searchPatient(searchQuery: String): LiveData<List<PasienEntity>>

    @Query("DELETE FROM PasienEntity")
    fun deleteAllPatients()
}