package ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.trabalho_facul.R
class Tela_inicial : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_listas)

        val intent = Intent(this, ListaDeListasActivity::class.java)
        startActivity(intent)
    }

}