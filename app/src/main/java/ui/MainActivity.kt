package ui

import adapter.ListaAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
                
            }
        }

        val btnLogin = findViewById<Button>(R.id.btn_login)

        btnLogin.setOnClickListener {
            val intent = Intent(this, Tela_inicial::class.java)
            startActivity(intent)
        }

        val tvCriarConta = findViewById<TextView>(R.id.tv_criar_conta)

        tvCriarConta.setOnClickListener {
val intent = Intent(this, cadastro2::class.java)
startActivity(intent)
}
}
}