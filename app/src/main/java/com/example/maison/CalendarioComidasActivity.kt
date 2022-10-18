package com.example.maison

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.maison.objeto.Dia
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_calencario_comidas.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDate

class CalendarioComidasActivity : AppCompatActivity() {

    var semana0 = mutableListOf<Dia>()
    var semana1 = mutableListOf<Dia>()
    var semana2 = mutableListOf<Dia>()
    var semana3 = mutableListOf<Dia>()

    var calendarioComidaBajado = listOf<Dia>()
    var lunes = LocalDate.now()

    private val calendarioRef = Firebase.firestore.collection("Calendario")

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

    lateinit var mRecyclerView: RecyclerView
    lateinit var mAdapter: CalendarioComidasActivity.CalendarioAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calencario_comidas)
        bajaCalendario()
    }

    fun setUpRecyclerView(listado: List<Dia>, RV: RecyclerView) {
        mRecyclerView = RV
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        mAdapter = CalendarioAdapter {
            val intent = Intent(this, DiaDetalle::class.java)
            intent.putExtra("fecha", it.fecha)
            intent.putExtra("dia", it.dia)
            intent.putExtra("comida", it.comida)
            intent.putExtra("cena", it.cena)
            startActivity(intent)
            finish()
        }
        mAdapter.RecyclerAdapter(listado, this)
        mRecyclerView.adapter = mAdapter
    }

    fun recicla() {
        setUpRecyclerView(semana0, rvSem1)
        setUpRecyclerView(semana1, rvSem2)
    }

    fun bajaCalendario() = CoroutineScope(Dispatchers.IO).launch {
        val diasBajados = mutableListOf<Dia>()
        for (i in 0..6) {
            if (diaIngles == semanaInglesa[i]) {
                lunes = LocalDate.now().minusDays(i.toLong())
            }
        }
        for (i in 0..13) {
            try {
                val doc = lunes.plusDays(i.toLong()).toString()
                val documento = calendarioRef.document(doc).get().await()
                val comida = documento?.get("comida").toString()
                val cena = documento?.get("cena").toString()

                when {
                    i < 7 -> {
                        val dia = semana[i]
                        val nuevoDia = Dia(doc, dia, comida, cena)
                        diasBajados.add(nuevoDia)
                        semana0.add(nuevoDia)
                        withContext(Dispatchers.Main) {
                            recicla()
                        }
                    }
                    i in 7..13 -> {
                        val dia = semana[i-7]
                        val nuevoDia = Dia(doc, dia, comida, cena)
                        diasBajados.add(nuevoDia)
                        semana1.add(nuevoDia)
                        withContext(Dispatchers.Main) {
                            recicla()
                        }
                    }
                    /*i in 14..20 -> {
                        val dia = semana[i-14]
                        val nuevoDia = Dia(doc, dia, comida, cena)
                        diasBajados.add(nuevoDia)
                        semana2.add(nuevoDia)
                        withContext(Dispatchers.Main) {
                            recicla()
                        }
                    }
                    i in 21..27 -> {
                        val dia = semana[i-21]
                        val nuevoDia = Dia(doc, dia, comida, cena)
                        diasBajados.add(nuevoDia)
                        semana3.add(nuevoDia)
                        withContext(Dispatchers.Main) {
                            recicla()
                        }
                    }*/
                    else -> {
                        Toast.makeText(
                            this@CalendarioComidasActivity,
                            "Fallo al separar semanas",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CalendarioComidasActivity, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        calendarioComidaBajado = diasBajados.sortedBy { it.fecha }
        withContext(Dispatchers.Main)
        {
            recicla()
        }
    }

    inner class CalendarioAdapter(var onClick: (dia: Dia) -> Unit) :
        RecyclerView.Adapter<CalendarioAdapter.ViewHolder>() {

        var lista: List<Dia> = ArrayList()
        lateinit var context: Context

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvDia = view.findViewById(R.id.tvDia) as TextView
            val tvComida = view.findViewById(R.id.tvComida) as TextView
            val tvCena = view.findViewById(R.id.tvCena) as TextView
            val cuadro = view.findViewById(R.id.item_dia) as LinearLayout


            fun bind(listado: Dia, item: Dia) {
                val num = listado.fecha
                val num1 = num.substring(8, 10)
                val num2 = num.substring(5, 7)
                tvDia.text = "${listado.dia} $num1/$num2"
                tvComida.text = listado.comida
                tvCena.text = listado.cena
                tvComida.text = listado.comida
                if(tvComida.text == "null"){
                    tvComida.text = "-"
                }
                if(tvCena.text == "null"){
                    tvCena.text = "-"
                }
                //cuadro.layoutParams.width = windowManager.defaultDisplay.width / 4

                itemView.setOnClickListener {
                    onClick(item)
                    //it.setBackgroundResource(R.color.amarillo)
                }
            }
        }

        fun RecyclerAdapter(listado: List<Dia>, context: Context) {
            lista = listado
            this.context = context
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = lista[position]
            holder.bind(item, item)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            return ViewHolder(layoutInflater.inflate(R.layout.item_dia, parent, false))
        }

        override fun getItemCount(): Int {
            return lista.size
        }
    }

}
