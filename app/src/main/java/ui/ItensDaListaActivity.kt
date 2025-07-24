package ui

import adapter.ItemAdapter
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trabalho_facul.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import modelodados.ItemLista

class ItensDaListaActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private val listaDeItens = mutableListOf<ItemLista>()

    private var listaId: String? = null
    private var nomeLista: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_itens_da_lista)

        nomeLista = intent.getStringExtra("nomeLista")
        listaId = intent.getStringExtra("listaId")

        if (listaId == null) return

        findViewById<TextView>(R.id.tv_nome_da_lista).text = "$nomeLista"

        recyclerView = findViewById(R.id.recyclerViewItens)
        recyclerView.layoutManager = LinearLayoutManager(this)


        adapter = ItemAdapter(listaDeItens) { position ->
            listaDeItens[position].selecionado = !listaDeItens[position].selecionado
            adapter.notifyItemChanged(position)
        }
        recyclerView.adapter = adapter
        recyclerView.adapter = adapter

        carregarItensDaLista()

        findViewById<ImageView>(R.id.ic_mais).setOnClickListener {
            mostrarDialogoAdicionarItem()
        }

        findViewById<ImageView>(R.id.iv_icone_editar).setOnClickListener {
            Toast.makeText(this, "Funcionalidade de edição em construção", Toast.LENGTH_SHORT)
                .show()
        }

        findViewById<ImageView>(R.id.iv_icone_exluir).setOnClickListener {
            mostrarConfirmacaoExcluirTodos()
        }
    }

    private fun carregarItensDaLista() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("usuarios")
            .document(uid)
            .collection("listas")
            .document(listaId ?: return)
            .collection("itens")
            .get()
            .addOnSuccessListener { result ->
                listaDeItens.clear()
                for (doc in result) {
                    val nome = doc.getString("nomeProduto") ?: ""
                    val qtd = doc.getLong("quantidade")?.toInt() ?: 1
                    listaDeItens.add(ItemLista(nomeProduto = nome, quantidade = qtd))
                }
                adapter.notifyDataSetChanged()
            }
    }

    private fun mostrarDialogoAdicionarItem() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_adicionar_item, null)
        val editNome = dialogView.findViewById<EditText>(R.id.et_nome_produto)
        val editQtd = dialogView.findViewById<EditText>(R.id.et_quantidade)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.rg_unidade)

        AlertDialog.Builder(this)
            .setTitle("Adicionar Item")
            .setView(dialogView)
            .setPositiveButton("Adicionar") { dialog, _ ->
                val nome = editNome.text.toString().trim()
                val qtdTexto = editQtd.text.toString().trim()
                val checkedId = radioGroup.checkedRadioButtonId
                val unidade = when (checkedId) {
                    R.id.rb_unidade -> "unidade"
                    R.id.rb_kg -> "kg"
                    R.id.rb_g -> "g"
                    else -> "unidade"
                }

                if (nome.isNotEmpty() && qtdTexto.isNotEmpty()) {
                    val qtd = qtdTexto.toIntOrNull() ?: 1
                    salvarItemNoFirebase("$nome ($unidade)", qtd)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun salvarItemNoFirebase(nome: String, qtd: Int) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        val idLista = listaId ?: return


        val novoItem = hashMapOf(
            "nomeProduto" to nome,
            "quantidade" to qtd
        )

        db.collection("usuarios")
            .document(uid)
            .collection("listas")
            .document(listaId ?: return)
            .collection("itens")
            .add(novoItem)
            .addOnSuccessListener {
                Toast.makeText(this, "Item adicionado", Toast.LENGTH_SHORT).show()
                atualizarQtdItens(idLista)
                carregarItensDaLista()
            }
    }

    private fun mostrarConfirmacaoExcluirTodos() {
        AlertDialog.Builder(this)
            .setTitle("Excluir Todos")
            .setMessage("Tem certeza que deseja excluir todos os itens da lista \"$nomeLista\"?")
            .setPositiveButton("Sim") { dialog, _ ->
                excluirItens()
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    fun atualizarQtdItens(listaId: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        val itensRef = db.collection("usuarios").document(uid)
            .collection("listas").document(listaId)
            .collection("itens")

        itensRef.get().addOnSuccessListener { result ->
            val qtd = result.size() // número de documentos = número de itens

            // Atualiza a quantidade de itens no documento da lista
            db.collection("usuarios").document(uid)
                .collection("listas").document(listaId)
                .update("qtdItens", qtd)
        }
    }

    private fun excluirItens() {
        findViewById<ImageView>(R.id.iv_icone_exluir).setOnClickListener {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
            val db = FirebaseFirestore.getInstance()

            val listaSelecionados = adapter.selecionados.map { listaDeItens[it] }

            listaSelecionados.forEach { item ->
                db.collection("usuarios")
                    .document(uid)
                    .collection("listas")
                    .document(listaId ?: return@forEach)
                    .collection("itens")
                    .whereEqualTo("nomeProduto", item.nomeProduto)
                    .whereEqualTo("quantidade", item.quantidade)
                    .get()
                    .addOnSuccessListener { result ->
                        for (doc in result) {
                            db.collection("usuarios")
                                .document(uid)
                                .collection("listas")
                                .document(listaId!!)
                                .collection("itens")
                                .document(doc.id)
                                .delete()
                        }
                        Toast.makeText(this, "Itens selecionados excluídos", Toast.LENGTH_SHORT)
                            .show()
                        carregarItensDaLista()
                        atualizarQtdItens(listaId!!)
                    }
            }

        }

    }
}

