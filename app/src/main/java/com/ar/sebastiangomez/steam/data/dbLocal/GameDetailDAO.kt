package com.ar.sebastiangomez.steam.data.dbLocal

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GameDetailDAO {
    @Query("SELECT * FROM gameDetail WHERE id = :id")
    suspend fun getByPK(id: String): GameDetailLocal?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg gameDetail: GameDetailLocal)

    @Delete
    suspend fun delete(vararg gameDetail: GameDetailLocal)

    @Query("DELETE FROM gameDetail")
    suspend fun deleteAll()
}