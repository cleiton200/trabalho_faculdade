package ui

import adapter.ItemAdapter
import android.os.Bundle
import android.text.InputType
import android.util.Log
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

        carregarItensDaLista()

        findViewById<ImageView>(R.id.ic_mais).setOnClickListener {
            mostrarDialogoAdicionarItem()
        }

        findViewById<ImageView>(R.id.iv_icone_editar).setOnClickListener {
            editarItemSelecionado()
        }

        findViewById<ImageView>(R.id.iv_icone_exluir).setOnClickListener {
            excluirItensSelecionados()
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
                    val id = doc.id
                    val nome = doc.getString("nomeProduto") ?: ""
                    val qtd = doc.getLong("quantidade")?.toInt() ?: 1
                    listaDeItens.add(ItemLista(id = id, nomeProduto = nome, quantidade = qtd))
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

    private fun excluirItensSelecionados() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        val listaSelecionados = listaDeItens.filter { it.selecionado }

        if (listaSelecionados.isEmpty()) {
            Toast.makeText(this, "Selecione pelo menos um item para excluir", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Excluir")
            .setMessage("Deseja excluir os itens selecionados?")
            .setPositiveButton("Sim") { _, _ ->
                listaSelecionados.forEach { item ->
                    db.collection("usuarios")
                        .document(uid)
                        .collection("listas")
                        .document(listaId ?: return@forEach)
                        .collection("itens")
                        .document(item.id)
                        .delete()
                        .addOnSuccessListener {
                            Log.d("DELETE", "Item ${item.nomeProduto} deletado")
                        }
                        .addOnFailureListener {
                            Log.e("DELETE", "Erro ao deletar ${item.nomeProduto}: ${it.message}")
                        }
                }

                Toast.makeText(this, "Itens excluídos", Toast.LENGTH_SHORT).show()
                carregarItensDaLista()
                atualizarQtdItens(listaId!!)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun editarItemSelecionado() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        val itemSelecionado = listaDeItens.find { it.selecionado }

        if (itemSelecionado == null) {
            Toast.makeText(this, "Selecione um item para editar", Toast.LENGTH_SHORT).show()
            return
        }

        // Cria o layout do dialog
        val dialogView = layoutInflater.inflate(R.layout.dialog_adicionar_item, null)
        val editNome = dialogView.findViewById<EditText>(R.id.et_nome_produto)
        val editQtd = dialogView.findViewById<EditText>(R.id.et_quantidade)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.rg_unidade)

        // Preenche os dados do item atual
        val regex = Regex("""(.*)\s+\((.*)\)""")
        val match = regex.find(itemSelecionado.nomeProduto)
        val nomeBase = match?.groups?.get(1)?.value ?: itemSelecionado.nomeProduto
        val unidade = match?.groups?.get(2)?.value ?: "unidade"

        editNome.setText(nomeBase)
        editQtd.setText(itemSelecionado.quantidade.toString())

        when (unidade) {
            "unidade" -> radioGroup.check(R.id.rb_unidade)
            "kg" -> radioGroup.check(R.id.rb_kg)
            "g" -> radioGroup.check(R.id.rb_g)
        }

        AlertDialog.Builder(this)
            .setTitle("Editar Item")
            .setView(dialogView)
            .setPositiveButton("Salvar") { dialog, _ ->
                val novoNome = editNome.text.toString().trim()
                val novaQtd = editQtd.text.toString().trim().toIntOrNull() ?: 1
                val novaUnidade = when (radioGroup.checkedRadioButtonId) {
                    R.id.rb_unidade -> "unidade"
                    R.id.rb_kg -> "kg"
                    R.id.rb_g -> "g"
                    else -> "unidade"
                }

                val novoNomeCompleto = "$novoNome ($novaUnidade)"

                db.collection("usuarios")
                    .document(uid)
                    .collection("listas")
                    .document(listaId!!)
                    .collection("itens")
                    .document(itemSelecionado.id)
                    .update(
                        mapOf(
                            "nomeProduto" to novoNomeCompleto,
                            "quantidade" to novaQtd
                        )
                    )
                    .addOnSuccessListener {
                        Toast.makeText(this, "Item editado", Toast.LENGTH_SHORT).show()
                        carregarItensDaLista()
                    }
            }
            .setNegativeButton("Cancelar", null)
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
}

