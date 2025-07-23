package ui

import adapter.ItemAdapter
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trabalho_facul.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import modelodados.ItemLista

class ItensDaListaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_itens_da_lista)

        val nomeLista = intent.getStringExtra("nomeLista") ?: ""
        val listaId = intent.getStringExtra("listaId") ?: return
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        findViewById<TextView>(R.id.tv_nome_da_lista).text = "Itens da lista: $nomeLista"

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewItens)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val db = FirebaseFirestore.getInstance()

        db.collection("usuarios")
            .document(usuarioId)
            .collection("listas")
            .document(listaId)
            .collection("itens")
            .get()
            .addOnSuccessListener { result ->
                val itens = result.map { doc ->
                    ItemLista(
                        nomeProduto = doc.getString("nomeProduto") ?: "",
                        quantidade = doc.getLong("quantidade")?.toInt() ?: 0
                    )
                }
                recyclerView.adapter = ItemAdapter(itens)
            }


    }
}