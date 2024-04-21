package com.ar.sebastiangomez.steam

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PruebaActivity : AppCompatActivity() {

    lateinit var ImporteEdt : EditText
    lateinit var PersonasEdt : EditText
    lateinit var ResultadoTxt : TextView
    lateinit var CalcularBtn : Button
    val tag = "LOG-CALCULO"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_prueba)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bindViewObject()
    }

    fun bindViewObject() {
        ImporteEdt = findViewById(R.id.edtImporte)
        PersonasEdt = findViewById(R.id.edtCantidad)
        ResultadoTxt = findViewById(R.id.txtResultado)
        CalcularBtn = findViewById(R.id.btnCalcular)

        CalcularBtn.setOnClickListener{
            calcular()
        }
    }

    fun calcular() {
        Log.d(tag, "Calcular Ejecutado")

        val totalFactura : Float = ImporteEdt.text.toString().toFloat()
        val cantPersonas : Int = PersonasEdt.text.toString().toInt()
        var resultado : Float = totalFactura / cantPersonas

        ResultadoTxt.text = "Total: $" + resultado
    }
}