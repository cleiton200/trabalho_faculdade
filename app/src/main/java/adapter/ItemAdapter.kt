package adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trabalho_facul.R
import modelodados.ItemLista

class ItemAdapter(
    private val itens: List<ItemLista>,
    private val onItemClick: (position: Int) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    val selecionados = mutableSetOf<Int>()

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val qtd: TextView = view.findViewById(R.id.tv_item_produto)
        val nome: TextView = view.findViewById(R.id.tv_quantidade_produto)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (selecionados.contains(position)) {
                    selecionados.remove(position)
                    view.setBackgroundColor(Color.TRANSPARENT) // desmarca
                } else {
                    selecionados.add(position)
                    view.setBackgroundColor(Color.parseColor("#DD52D3C2")) // marca com leve branco
                }
                onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_produto, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itens[position]
        holder.qtd.text = item.quantidade.toString()
        holder.nome.text = item.nomeProduto

        val isSelecionado = selecionados.contains(position)
        holder.itemView.setBackgroundColor(
            if (isSelecionado) Color.parseColor("#DD52D3C2") else Color.TRANSPARENT
        )
    }

    override fun getItemCount(): Int = itens.size
}
