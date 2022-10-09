package com.example.maison

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import com.example.maison.objeto.Dia
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_calendario_in_site.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDate

class CalendarioInSite : AppCompatActivity() {

    var calendarioComida = mutableListOf<Dia>()
    var semana0 = mutableListOf<Dia>()
    var semana1 = mutableListOf<Dia>()
    var semana2 = mutableListOf<Dia>()
    var semana3 = mutableListOf<Dia>()

    var calendarioComidaBajado = listOf<Dia>()
    var lunes = LocalDate.now()

    private val calendarioRef = Firebase.firestore.collection("Calendario")

    var semanaAMostrar = arrayListOf<String>()

    var semana = arrayListOf<String>(
        "L",
        "M",
        "X",
        "J",
        "V",
        "S",
        "D"
    )

    val semanaInglesa = arrayListOf<String>(
        "MONDAY",
        "TUESDAY",
        "WEDNESDAY",
        "THURSDAY",
        "FRIDAY",
        "SATURDAY",
        "SUNDAY"
    )
    val diaIngles = LocalDate.now().dayOfWeek.toString()

    val listatvDias = arrayListOf<TextView>()

    val listaetComidas = arrayListOf<TextView>()

    val listaetCenas = arrayListOf<TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendario_in_site)
        semanaAMostrar.clear()
        bajaCalendario()

        //habilitarBotones()
        llenarArrays()
        btnGuardar.setOnClickListener {
            for (i in 0..6) {
                calendarioRef.document(semanaAMostrar[i]).set(
                    Dia(
                        semanaAMostrar[i],
                        semana[i],
                        listaetComidas[i].text.toString(),
                        listaetCenas[i].text.toString()
                    )
                )
            }
            for (i in 7..13) {
                calendarioRef.document(semanaAMostrar[i]).set(
                    Dia(
                        semanaAMostrar[i],
                        semana[i - 7],
                        listaetComidas[i].text.toString(),
                        listaetCenas[i].text.toString()
                    )
                )
            }

        }
    }

    fun llenarArrays() {
        listatvDias.add(tvDia1)
        listatvDias.add(tvDia2)
        listatvDias.add(tvDia3)
        listatvDias.add(tvDia4)
        listatvDias.add(tvDia5)
        listatvDias.add(tvDia6)
        listatvDias.add(tvDia7)
        listatvDias.add(tvDia8)
        listatvDias.add(tvDia9)
        listatvDias.add(tvDia10)
        listatvDias.add(tvDia11)
        listatvDias.add(tvDia12)
        listatvDias.add(tvDia13)
        listatvDias.add(tvDia14)



        listaetComidas.add(etComida1)
        listaetComidas.add(etComida2)
        listaetComidas.add(etComida3)
        listaetComidas.add(etComida4)
        listaetComidas.add(etComida5)
        listaetComidas.add(etComida6)
        listaetComidas.add(etComida7)
        listaetComidas.add(etComida8)
        listaetComidas.add(etComida9)
        listaetComidas.add(etComida10)
        listaetComidas.add(etComida11)
        listaetComidas.add(etComida12)
        listaetComidas.add(etComida13)
        listaetComidas.add(etComida14)


        listaetCenas.add(etCena1)
        listaetCenas.add(etCena2)
        listaetCenas.add(etCena3)
        listaetCenas.add(etCena4)
        listaetCenas.add(etCena5)
        listaetCenas.add(etCena6)
        listaetCenas.add(etCena7)
        listaetCenas.add(etCena8)
        listaetCenas.add(etCena9)
        listaetCenas.add(etCena10)
        listaetCenas.add(etCena11)
        listaetCenas.add(etCena12)
        listaetCenas.add(etCena13)
        listaetCenas.add(etCena14)


    }

    fun habilitarBotones() {
        for (i in 0 until listaetComidas.size) {
            listaetComidas[i].isEnabled = !listaetComidas[i].isEnabled
            listaetCenas[i].isEnabled = !listaetCenas[i].isEnabled
        }
    }

    fun bajaCalendario() = CoroutineScope(Dispatchers.IO).launch {
        val diasBajados = mutableListOf<Dia>()
        for (i in 0..6) {
            if (diaIngles == semanaInglesa[i]) {
                lunes = LocalDate.now().minusDays(i.toLong())
            }
        }
        for (i in 0..13) {
            val doc = lunes.plusDays(i.toLong()).toString()
            semanaAMostrar.add(doc)
            lateinit var documento: DocumentSnapshot
            lateinit var comida: String
            lateinit var cena: String
            try {
                documento = calendarioRef.document(doc).get().await()
                comida = documento?.get("comida").toString()
                cena = documento?.get("cena").toString()
                val fecha = documento?.get("fecha").toString()
                val dia = documento?.get("dia").toString()
                when {
                    i < 7 -> {
                        //val dia = semana[i]
                        //val nuevoDia = Dia(doc, dia, comida, cena)
                        //diasBajados.add(nuevoDia)
                        //semana0.add(nuevoDia)
                        withContext(Dispatchers.Main) {
                            val num1 = doc.substring(8, 10)
                            val num2 = doc.substring(5, 7)
                            //tvDia.text = "${semana[i]} $num1/$num2"
                            listatvDias[i].text = "${semana[i]} $num1/$num2"
                            listaetComidas[i].text = comida
                            listaetCenas[i].text = cena
                        }
                    }
                    i in 7..13 -> {
                        withContext(Dispatchers.Main) {
                            val num1 = doc.substring(8, 10)
                            val num2 = doc.substring(5, 7)
                            //tvDia.text = "${semana[i-7]} $num1/$num2"
                            listatvDias[i].text = "${semana[i - 7]} $num1/$num2"
                            listaetComidas[i].text = comida
                            listaetCenas[i].text = cena
                        }
                        /*val dia = semana[i-7]
                        val nuevoDia = Dia(doc, dia, comida, cena)
                        diasBajados.add(nuevoDia)
                        semana1.add(nuevoDia)*/
                    }
                    else -> {
                        Toast.makeText(
                            this@CalendarioInSite,
                            "Fallo al separar semanas",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CalendarioInSite, e.message, Toast.LENGTH_LONG).show()
                }
            }
            /*withContext(Dispatchers.Main) {
                val num1 = doc.substring(8, 10)
                val num2 = doc.substring(5, 7)
                tvDia.text = "${listado.dia} $num1/$num2"
                listatvDias[i].text = doc
                listaetComidas[i].text = comida
                listaetCenas[i].text = cena
            }*/
        }
        habilitarBotones()
        calendarioComidaBajado = diasBajados.sortedBy { it.fecha }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_calendario_layout, menu)
        val item: MenuItem = menu!!.findItem(R.id.nav_actualizar)
        val navActualizar = menu.findItem(R.id.nav_actualizar)


        item.setOnMenuItemClickListener {
            habilitarBotones()
            if (!etCena1.isEnabled) {
                item.title = "Modificar"
                btnGuardar.visibility = View.INVISIBLE
                btnCancelar.visibility = View.INVISIBLE
            } else {
                item.title = "Actualizando"
                btnGuardar.visibility = View.VISIBLE
                btnCancelar.visibility = View.VISIBLE
            }
            true
        }
        return super.onCreateOptionsMenu(menu)
    }

}
