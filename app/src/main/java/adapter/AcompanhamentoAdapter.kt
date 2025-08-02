package adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trabalho_facul.R
import modelodados.ItemEmCompra

class AcompanhamentoAdapter(
    private val itens2: List<ItemEmCompra>,
    private val onTotalUpdated: () -> Unit
) : RecyclerView.Adapter<AcompanhamentoAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome = view.findViewById<TextView>(R.id.textNome)
        val quantidade = view.findViewById<TextView>(R.id.textQuantidade)
        val valor = view.findViewById<EditText>(R.id.editValor)
        val check = view.findViewById<CheckBox>(R.id.checkComprado)

        init {
            check.setOnCheckedChangeListener { _, isChecked ->
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    val item = itens2[pos]
                    item.comprado = isChecked
                    onTotalUpdated()
                }
            }

            valor.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val pos = adapterPosition
                    if (pos != RecyclerView.NO_POSITION) {
                        val item = itens2[pos]
                        val valorDigitado = s.toString().replace(",", ".").toDoubleOrNull()
                        item.valorUnitario = valorDigitado ?: 0.0
                        onTotalUpdated()
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_acompanhamento, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itens2[position]
        holder.nome.text = item.nomeProduto
        holder.quantidade.text = "x${item.quantidade}"

        val valorTexto = if (item.valorUnitario > 0) "%.2f".format(item.valorUnitario) else ""
        if (holder.valor.text.toString() != valorTexto) {
            holder.valor.setText(valorTexto)
        }

        holder.check.isChecked = item.comprado
    }

    override fun getItemCount() = itens2.size
}


