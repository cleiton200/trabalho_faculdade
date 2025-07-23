package adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trabalho_facul.R
import modelodados.ItemLista

class ItemAdapter(private val itens: List<ItemLista>) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val qtd: TextView = view.findViewById(R.id.tv_item_produto)
        val nome: TextView = view.findViewById(R.id.tv_quantidade_produto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_produto, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itens[position]
        holder.qtd.text = item.quantidade.toString()
        holder.nome.text = item.nomeProduto
    }

    override fun getItemCount(): Int = itens.size
}