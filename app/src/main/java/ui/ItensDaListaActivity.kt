package ui

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.trabalho_facul.R

class ItensDaListaActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_itens_da_lista)

            val nomeLista = intent.getStringExtra("nomeLista")

            // Exibir na tela ou usar para buscar os itens
            findViewById<TextView>(R.id.tv_nome_da_lista).text = "Itens da lista: $nomeLista"

            // Aqui você pode buscar os itens no banco/local
            // e carregar em uma RecyclerView
        }
}