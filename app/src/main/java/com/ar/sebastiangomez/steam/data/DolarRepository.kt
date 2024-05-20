package com.ar.sebastiangomez.steam.data

import com.ar.sebastiangomez.steam.model.Dolar
import com.ar.sebastiangomez.steam.model.Game
import com.ar.sebastiangomez.steam.model.GameDetail

class DolarRepository {
    suspend fun getDolarTarjeta() : Dolar? {
        return DolarDataSource.getDolarTarjeta()
    }

}