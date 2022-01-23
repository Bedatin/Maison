package com.example.maison

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnLista.setOnClickListener {
            val intent = Intent(this, ListaCompraActivity::class.java)
            startActivity(intent)
        }

        btnCalendario.setOnClickListener {
            Toast.makeText(
                this,
                "Sin autorización",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnComida.setOnClickListener {
            Toast.makeText(
                this,
                "Sin autorización",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}