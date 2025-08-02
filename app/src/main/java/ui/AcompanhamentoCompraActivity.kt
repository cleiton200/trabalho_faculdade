package ui

import adapter.AcompanhamentoAdapter
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trabalho_facul.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import modelodados.ItemEmCompra

class AcompanhamentoCompraActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AcompanhamentoAdapter
    private lateinit var textTitulo: TextView
    private lateinit var textTotal: TextView

    private val itensEmCompra = mutableListOf<ItemEmCompra>()
    private var listaId: String? = null
    private var nomeLista: String? = null
    private var totalGasto = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acompanhar_compra)

        recyclerView = findViewById(R.id.recyclerViewAcompanhamento)
        textTitulo = findViewById(R.id.textTituloLista)
        textTotal = findViewById(R.id.textTotal)

        listaId = intent.getStringExtra("listaId")
        nomeLista = intent.getStringExtra("nomeLista")

        textTitulo.text = nomeLista ?: "Lista"

        adapter = AcompanhamentoAdapter(itensEmCompra) { atualizarTotal() }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        buscarItensDaLista()

        findViewById<Button>(R.id.btn_terminar).setOnClickListener {
            Toast.makeText(this, "Compra finalizada!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun buscarItensDaLista() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val idLista = listaId ?: return

        val db = FirebaseFirestore.getInstance()
        db.collection("usuarios").document(uid)
            .collection("listas").document(idLista)
            .collection("itens")
            .get()
            .addOnSuccessListener { result ->
                itensEmCompra.clear()
                for (doc in result) {
                    val nome = doc.getString("nomeProduto") ?: ""
                    val quantidade = doc.getLong("quantidade")?.toInt() ?: 0
                    val valor = doc.getDouble("valor") ?: 0.0
                    val comprado = doc.getBoolean("comprado") ?: false

                    itensEmCompra.add(
                        ItemEmCompra(nome, quantidade, valor, comprado)
                    )
                }
                adapter.notifyDataSetChanged()
                atualizarTotal()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao buscar itens", Toast.LENGTH_SHORT).show()
            }
    }

    private fun atualizarTotal() {
        totalGasto = itensEmCompra.filter { it.comprado }.sumOf { it.valorUnitario * it.quantidade }
        textTotal.text = "Total: R$ %.2f".format(totalGasto)
    }


}