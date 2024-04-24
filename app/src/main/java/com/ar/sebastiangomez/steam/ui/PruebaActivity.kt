package com.ar.sebastiangomez.steam.ui

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.ar.sebastiangomez.steam.R

class PruebaActivity : AppCompatActivity() {

    private lateinit var viewModel : PruebaViewModel

    lateinit var ImporteEdt : EditText
    lateinit var PersonasEdt : EditText
    lateinit var ResultadoTxt : TextView
    lateinit var CalcularBtn : Button
    lateinit var btnRes : Button
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

        bindViewModel()
    }

    fun bindViewModel() {
        viewModel = ViewModelProvider(this)[PruebaViewModel::class.java]

        viewModel.TextoTotalPersonas

        ImporteEdt = findViewById(R.id.edtImporte)
        PersonasEdt = findViewById(R.id.edtCantidad)
        ResultadoTxt = findViewById(R.id.txtResultado)
        CalcularBtn = findViewById(R.id.btnCalcular)
        btnRes = findViewById(R.id.btnRes)

        btnRes.isEnabled = false
        CalcularBtn.isEnabled = true

        CalcularBtn.setOnClickListener{
            calcular()
        }

        btnRes.setOnClickListener {
            reset()
        }
    }

    fun calcular() {
        Log.d(tag, "Calcular Ejecutado")

        CalcularBtn.isEnabled = false
        ImporteEdt.isEnabled = false
        PersonasEdt.isEnabled = false

        val totalFactura : Float = ImporteEdt.text.toString().toFloat()
        val cantPersonas : Int = PersonasEdt.text.toString().toInt()
        var resultado : Float = totalFactura / cantPersonas

        Log.d(tag, "Resultado: " + resultado)

        ResultadoTxt.text = "Total por persona: $ " + resultado
        btnRes.isEnabled= true
    }

    fun reset() {
        Log.d(tag, "reset")

        ImporteEdt.setText("")
        PersonasEdt.setText("")
        ResultadoTxt.setText("Ingrese importe y cantidad de personas")
        ImporteEdt.isEnabled = true
        PersonasEdt.isEnabled = true
        btnRes.isEnabled = false
        CalcularBtn.isEnabled = true

    }
}