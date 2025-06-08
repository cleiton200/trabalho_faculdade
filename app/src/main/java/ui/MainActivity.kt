package ui

import adapter.ListaAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trabalho_facul.R
import modelodados.ListaCompra

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        class TelasListasActivity : AppCompatActivity() {

            private lateinit var recyclerView: RecyclerView
            private lateinit var adapter: ListaAdapter

            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.tela_listas)

                recyclerView = findViewById(R.id.recyclerView)

                val listas = listOf(
                    ListaCompra.ListaCompra("1", "Mercado", "10"),
                    ListaCompra.ListaCompra("2", "Farmácia", "3"),
                    ListaCompra.ListaCompra("3", "Padaria", "5"),
                    ListaCompra.ListaCompra("4", "Limpeza", "6"),
                    ListaCompra.ListaCompra("5", "Frutas", "7")
                )

                adapter = ListaAdapter(listas)
                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = adapter
            }



        }


        btn_login.setOnClickListener {
            val usuario = findViewById<EditText>(R.id.tv_usuario).text.toString()
            val senha = findViewById<EditText>(R.id.tv_senha).text.toString()

            if (usuario == "admin" && senha == "1234") {
                startActivity(Intent(this, TelasListasActivity::class.java))
            } else {
                Toast.makeText(this, "Usuário ou senha incorretos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}