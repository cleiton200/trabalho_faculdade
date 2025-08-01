package ui

import adapter.ListaAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trabalho_facul.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import modelodados.ListaCompra
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ListaDeListasActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ListaAdapter
    private val listaDeListas = mutableListOf<ListaCompra>()
    private var listaSelecionada: ListaCompra? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_listas)
        // Inicializa o RecyclerView global
        recyclerView = findViewById(R.id.recyclerViewListas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Usa a lista global e o adapter global
        adapter = ListaAdapter(listaDeListas) { lista ->
            val intent = Intent(this, ItensDaListaActivity::class.java)
            intent.putExtra("listaId", lista.id)
            intent.putExtra("nomeLista", lista.nome)
            startActivity(intent)
            listaSelecionada = lista
        }

        val btnExcluir = findViewById<Button>(R.id.btn_excluir)
        btnExcluir.setOnClickListener {
            if (listaSelecionada == null) {
                Toast.makeText(this, "Selecione uma lista para excluir", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setTitle("Confirmar exclusão")
                .setMessage("Tem certeza que deseja excluir a lista \"${listaSelecionada?.nome}\"?")
                .setPositiveButton("Sim") { dialog, _ ->
                    excluirListaSelecionada()
                    dialog.dismiss()
                }
                .setNegativeButton("Não") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }


        recyclerView.adapter = adapter

        // Configura botão criar - aqui está o ponto principal
        val btnCriar = findViewById<Button>(R.id.btn_criar)
        btnCriar.setOnClickListener {
            mostrarDialogoCriarLista()
        }

        // Busca listas do Firestore
        buscarListasDoFirestore()

    }

     override fun onResume() {
        super.onResume()
        buscarListasDoFirestore() // recarrega quando voltar da tela de itens
    }

    private fun buscarListasDoFirestore() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("usuarios").document(uid)
            .collection("listas")
            .get()
            .addOnSuccessListener { result ->
                listaDeListas.clear()
                for (doc in result) {
                    val id = doc.id // ID real do documento
                    val nome = doc.getString("nome") ?: ""
                    val qtd = doc.getLong("qtdItens")?.toInt() ?: 0

                    listaDeListas.add(
                        ListaCompra(
                            id = id, // agora usa o ID real
                            nome = nome,
                            numeroProdutos = qtd.toString()
                        )
                    )
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar listas", Toast.LENGTH_SHORT).show()
            }
    }

    private fun mostrarDialogoCriarLista() {
        val editText = EditText(this)
        editText.hint = "Nome da nova lista"

        AlertDialog.Builder(this)
            .setTitle("Criar Lista")
            .setView(editText)
            .setPositiveButton("Criar") { dialog, _ ->
                val nomeDigitado = editText.text.toString().trim()
                if (nomeDigitado.isNotEmpty()) {
                    salvarListaNoFirebase(nomeDigitado)
                } else {
                    Toast.makeText(this, "Digite um nome válido", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun salvarListaNoFirebase(nome: String) {
        val db = FirebaseFirestore.getInstance()
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val novaLista = hashMapOf(
            "nome" to nome,
            "dataCriacao" to SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
            "qtdItens" to 0
        )

        db.collection("usuarios")
            .document(usuarioId)
            .collection("listas")
            .add(novaLista)
            .addOnSuccessListener {
                Toast.makeText(this, "Lista criada com sucesso!", Toast.LENGTH_SHORT).show()
                buscarListasDoFirestore() // atualiza o RecyclerView
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun excluirListaSelecionada() {
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        val nomeLista = listaSelecionada?.nome ?: return

        db.collection("usuarios")
            .document(usuarioId)
            .collection("listas")
            .whereEqualTo("nome", nomeLista) // busca pelo nome
            .get()
            .addOnSuccessListener { result ->
                for (doc in result) {
                    db.collection("usuarios")
                        .document(usuarioId)
                        .collection("listas")
                        .document(doc.id)
                        .delete()
                }
                Toast.makeText(this, "Lista excluída com sucesso!", Toast.LENGTH_SHORT).show()
                listaSelecionada = null
                buscarListasDoFirestore()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao excluir: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
