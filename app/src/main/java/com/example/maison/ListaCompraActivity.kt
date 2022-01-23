package com.example.maison

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.maison.Objetos.Compra
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_lista_compra.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class ListaCompraActivity : AppCompatActivity() {

    var listaAll = mutableListOf<Compra>()

    lateinit var mRecyclerView2: RecyclerView
    val mAdapter2: ComprasAdapder = ComprasAdapder() {
        val intent = Intent(this, CompraDetalle::class.java)
        intent.putExtra("compra", it.compra)
        intent.putExtra("cantidad", it.cantidad)
        intent.putExtra("check", it.check)
        startActivity(intent)
        this.finish()
    }

    private val comprasRef = Firebase.firestore.collection("Compras")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_compra)
        var cantNuevoObjeto = 1

        setUpRecyclerView(listaAll)
        bajaCompra()

        /*val adapder = CompraAdapter(listaAll)
        rvContainer.adapter = adapder
        rvContainer.layoutManager = LinearLayoutManager(this)*/

        btnAdd.setOnClickListener {
            if (etNew.text.toString() == "") {
                Toast.makeText(
                    this,
                    "Introduzca texto",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                var apuntado = false
                for (i in 0 until listaAll.size) {
                    if (listaAll[i].compra == etNew.text.toString()) {
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
                    val incluyo = Compra(nuevo, cantNuevoObjeto, false)
                    listaAll.add(incluyo)
                    mAdapter2.notifyItemInserted(listaAll.size - 1)
                    etNew.text.clear()
                    subeCompra(incluyo)
                    setUpRecyclerView(listaAll)
                }
            }
        }

        btnMas.setOnClickListener {
            cantNuevoObjeto++
            tvCant.text = "x$cantNuevoObjeto"
        }

        btnMenos.setOnClickListener {
            if (cantNuevoObjeto > 1) {
                cantNuevoObjeto--
                tvCant.text = "x$cantNuevoObjeto"
            }
        }

    }

    fun setUpRecyclerView(listado: List<Compra>) {
        mRecyclerView2 = findViewById<RecyclerView>(R.id.rvContainer)
        mRecyclerView2.setHasFixedSize(true)
        mRecyclerView2.layoutManager = LinearLayoutManager(this)
        mAdapter2.RecyclerAdapter(listado, this)
        mRecyclerView2.adapter = mAdapter2
    }

    fun subeCompra(compra: Compra) = CoroutineScope(Dispatchers.IO).launch {
        try {
            //ComprasRef.add(compra).await()
            comprasRef.document(compra.compra).set(compra)

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@ListaCompraActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun bajaCompra() = CoroutineScope(Dispatchers.IO).launch {
        try {
            listaAll.clear()
            val querySnapshot = comprasRef.get().await()
            for (document in querySnapshot.documents) {
                val comprita = document.get("compra").toString()
                val canditad = document.get("cantidad").toString().toInt()
                val checkeo = document.get("check").toString().toBoolean()
                val compraNueva = Compra(comprita, canditad, checkeo)
                listaAll.add((compraNueva))
            }
            withContext(Dispatchers.Main) {
                setUpRecyclerView(listaAll)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@ListaCompraActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    inner class ComprasAdapder(var onClick: (compra: Compra) -> Unit) :
        RecyclerView.Adapter<ComprasAdapder.ViewHolder>() {

        var lista: List<Compra> = ArrayList()
        lateinit var context: Context
        private val ComprasRef = Firebase.firestore.collection("Compras")
        var pos = 0

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvCompra = view.findViewById(R.id.tvCompra) as TextView
            val tvCantidad = view.findViewById(R.id.tvCantidad) as TextView
            val cbCheck = view.findViewById(R.id.cbCheck) as CheckBox
            val btnDelete = view.findViewById(R.id.btnDelete) as ImageButton

            fun bind(listado: Compra, item: Compra) {
                val cant = "x${listado.cantidad}"
                tvCompra.text = listado.compra
                tvCantidad.text = cant
                cbCheck.isChecked = item.check
                cbCheck.setOnClickListener {
                    item.check = !item.check
                    ComprasRef.document(item.compra).update("check", item.check)
                }
                btnDelete.setOnClickListener {
                    listaAll.removeAt(pos)
                    ComprasRef.document(item.compra).delete()
                    //setUpRecyclerView(listaAll)
                    //notifyItemRemoved(lista.indexOf(item))
                    //notifyItemRangeChanged(lista.indexOf(item),lista.size)
                    bajaCompra()

                    Snackbar.make(
                        mRecyclerView2,
                        "${item.compra} eliminado",
                        Snackbar.LENGTH_LONG
                    ).setAction("Deshacer", View.OnClickListener {
                        listaAll.add(pos, item)
                        subeCompra(item)
                        //setUpRecyclerView(listaAll)
                        bajaCompra()
                    }).show()

                }
                itemView.setOnClickListener {
                    onClick(item)
                    //it.setBackgroundResource(R.color.amarillo)
                }
            }
        }

        fun RecyclerAdapter(listado: List<Compra>, context: Context) {
            lista = listado
            this.context = context
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = lista[position]
            pos = position
            holder.bind(item, item)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            return ViewHolder(layoutInflater.inflate(R.layout.item_compra, parent, false))
        }

        override fun getItemCount(): Int {
            return lista.size
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_compra_layout, menu)
        val item: MenuItem = menu!!.findItem(R.id.nav_deleteAll)
        val navDeleteAll = menu.findItem(R.id.nav_deleteAll)

        navDeleteAll.setOnMenuItemClickListener {
            val listaBorrados = mutableListOf<Compra>()
            listaBorrados.addAll(listaAll)
            listaAll.clear()
            for (i in 0 until listaBorrados.size){
                comprasRef.document(listaBorrados[i].compra).delete()
            }
            bajaCompra()
            Snackbar.make(
                mRecyclerView2,
                "Lista eliminada",
                Snackbar.LENGTH_LONG
            ).setAction("Deshacer", View.OnClickListener {
                listaAll.addAll(listaBorrados)
                for (i in 0 until listaAll.size){
                    subeCompra(listaAll[i])
                }
                bajaCompra()
            }).show()
            true
        }
        return super.onCreateOptionsMenu(menu)
    }

}
