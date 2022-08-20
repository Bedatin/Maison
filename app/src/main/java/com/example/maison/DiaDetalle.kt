package com.example.maison

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.maison.objeto.Comida
import com.example.maison.objeto.Dia
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_dia_detalle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DiaDetalle : AppCompatActivity() {

    private val comidaRef = Firebase.firestore.collection("Comida")
    var listaComidas = mutableListOf<Comida>()
    private val calendarioRef = Firebase.firestore.collection("Calendario")

    var calendarioComidaBajado = mutableListOf<Dia>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dia_detalle)
        bajaComida()

        val fecha = intent.extras!!.get("fecha") as String
        val dia = intent.extras!!.get("dia") as String
        val comida = intent.extras!!.get("comida") as String
        val cena = intent.extras!!.get("cena") as String

        val num1 = fecha.substring(8, 10)
        val num2 = fecha.substring(5, 7)
        val num3 = fecha.substring(0, 4)

        tvDia.text = "$dia $num1/$num2/$num3"
        if (comida != "null") {
            tvComida.text = comida
            etComida.hint = comida
        }else{
            tvComida.text = "-"
        }
        if (cena != "null") {
            tvCena.text = cena
            etCena.hint = cena
        }else{
            tvCena.text = "-"
        }

        btnSubeCambio.setOnClickListener {
            val nuevaComida = if (etComida.text.isNotEmpty()) {
                etComida.text
            } else {
                tvComida.text
            }
            val nuevaCena = if (etCena.text.isNotEmpty()) {
                etCena.text
            } else {
                tvCena.text
            }
            if (etComida.text.isNotEmpty() or etCena.text.isNotEmpty()) {
                val nuevo = Dia(fecha, dia, nuevaComida.toString(), nuevaCena.toString())
                calendarioRef.document(fecha).set(nuevo)
                for (i in calendarioComidaBajado.indices) {
                    if (calendarioComidaBajado[i].fecha == fecha) {
                        calendarioComidaBajado[i] = nuevo
                    }
                }
            }
            val intent = Intent(this, CalendarioComidasActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        val intent = Intent(this, CalendarioComidasActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun bajaComida() = CoroutineScope(Dispatchers.IO).launch {
        try {
            listaComidas.clear()
            val querySnapshot = comidaRef.get().await()
            for (document in querySnapshot.documents) {
                val comida = document.get("comida").toString()
                val tiempo = document.get("tiempo").toString().toInt()
                val tipo = document.get("tipo").toString()
                val duplicable = document.get("duplicable").toString().toBoolean()
                val comidaNueva = Comida(comida, tipo, tiempo, duplicable)
                listaComidas.add((comidaNueva))
            }


        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@DiaDetalle, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}
