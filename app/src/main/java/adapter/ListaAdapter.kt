package adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trabalho_facul.R
import modelodados.ListaCompra
import ui.ItensDaListaActivity


class ListaAdapter(
    private val listas: List<ListaCompra>,
    private val onClick: (ListaCompra) -> Unit
) : RecyclerView.Adapter<ListaAdapter.ListaViewHolder>() {

    inner class ListaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idItem = itemView.findViewById<TextView>(R.id.idItem)
        val nomeItem = itemView.findViewById<TextView>(R.id.nomeItem)
        val qtdeItem = itemView.findViewById<TextView>(R.id.qtdeItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lista_salva, parent, false)
        return ListaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListaViewHolder, position: Int) {
        val lista = listas[position]
        holder.idItem.text = "$position"
        holder.nomeItem.text = lista.nome
        holder.qtdeItem.text = lista.numeroProdutos.toString()

        holder.itemView.setOnClickListener { onClick(lista) }
    }

    override fun getItemCount() = listas.size
}