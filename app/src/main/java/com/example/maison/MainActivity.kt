package com.example.maison

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
            val intent = Intent(this, CalendarioComidasActivity::class.java)
            startActivity(intent)
        }
        btnCalendario2.setOnClickListener {
            val intent = Intent(this, CalendarioInSite::class.java)
            startActivity(intent)
        }

    }
}