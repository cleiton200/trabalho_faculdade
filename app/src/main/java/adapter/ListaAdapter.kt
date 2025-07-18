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


class ListaAdapter(private val lista: List<ListaCompra.ListaCompra>) :
    RecyclerView.Adapter<ListaAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtId: TextView = itemView.findViewById(R.id.tv_id)
        val txtNome: TextView = itemView.findViewById(R.id.tv_nome)
        val txtQtd: TextView = itemView.findViewById(R.id.tv_Qtd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lista, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.txtId.text = item.id
        holder.txtNome.text = item.nome
        holder.txtQtd.text = item.numeroProdutos

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ItensDaListaActivity::class.java)
            intent.putExtra("listaId", item.id) // ou lista.id
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = lista.size
}