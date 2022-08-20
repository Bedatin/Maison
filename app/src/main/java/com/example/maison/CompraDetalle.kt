package com.example.maison

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.maison.objeto.Compra
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_compra_detalle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CompraDetalle : AppCompatActivity() {

    private val ComprasRef = Firebase.firestore.collection("Compras")
    var listaAll = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compra_detalle)
        bajaCompra()
        val comprita = intent.extras!!.get("compra") as String
        var cant = intent.extras!!.get("cantidad") as Int
        var che = intent.extras!!.get("check") as Boolean

        etNew.setText("$comprita")
        if (che) {
            cbCheck.isChecked = true
        }
        tvCant.text = "x$cant"

        cbCheck.setOnClickListener {
            che = !che
        }

        btnMas.setOnClickListener {
            cant++
            tvCant.text = "x$cant"
        }

        btnMenos.setOnClickListener {
            if (cant > 1) {
                cant--
                tvCant.text = "x$cant"
            }
        }

        btnModificar.setOnClickListener {
            if (etNew.text.toString() == "") {
                Toast.makeText(
                    this,
                    "Introduzca texto",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                var apuntado = false
                for (i in 0 until listaAll.size) {
                    if (listaAll[i] == etNew.text.toString() && etNew.text.toString()!=comprita) {
                        apuntado = true
                    }
                }
                if (apuntado) {
                    Toast.makeText(
                        this,
                        "Objeto repetido",
                        Toast.LENGTH_SHORT
                    ).show()
                    etNew.text.clear()
                } else {
                    val nuevo = etNew.text.toString()
                    val incluyo = Compra(nuevo, cant, che)
                    ComprasRef.document(comprita).delete()
                    ComprasRef.document(nuevo).set(incluyo)
                    startActivity(
                        Intent(this, ListaCompraActivity::class.java)
                    )
                    this.finish()
                }
            }
        }

        btnCancelar.setOnClickListener{
            startActivity(
                Intent(this, ListaCompraActivity::class.java)
            )
            this.finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(
            Intent(this, ListaCompraActivity::class.java)
        )
        this.finish()
    }

    fun bajaCompra() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val querySnapshot = ComprasRef.get().await()
            for (document in querySnapshot.documents) {
                val comprita = document.get("compra").toString()
                listaAll.add((comprita))
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@CompraDetalle, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

}
