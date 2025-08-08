package ui

import adapter.ListaAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.trabalho_facul.R
import com.google.firebase.auth.FirebaseAuth
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

        val btnLogin = findViewById<Button>(R.id.btn_login)


        var auth = FirebaseAuth.getInstance()
        btnLogin.setOnClickListener {

            val edt_email = findViewById<EditText>(R.id.edt_email)
            val edt_senha = findViewById<EditText>(R.id.edt_senha)

            val email = edt_email.text.toString().trim()
            val senha = edt_senha.text.toString().trim()

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha email e senha", Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(email, senha)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(this, ListaDeListasActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Usuário não encontrado. Cadastre-se.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
        //Criar a conta
        val tvCriarConta = findViewById<TextView>(R.id.tv_criar_conta)
        tvCriarConta.setOnClickListener {
            val intent = Intent(this, cadastro2::class.java)
            startActivity(intent)
        }
        val tvRecuperar = findViewById<TextView>(R.id.tv_recuperar_senha)
        tvRecuperar.setOnClickListener { mostrarDialogReset() }
    }

    private fun mostrarDialogReset() {
        val input = EditText(this).apply {
            hint = "Seu e-mail"
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            setPadding(24, 24, 24, 24)
        }

        AlertDialog.Builder(this)
            .setTitle("Recuperar senha")
            .setMessage("Digite o e-mail cadastrado para receber o link.")
            .setView(input)
            .setPositiveButton("Enviar") { d, _ ->
                val email = input.text.toString().trim()
                enviarEmailReset(email)
                d.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun enviarEmailReset(email: String) {
        if (email.isEmpty()) {
            Toast.makeText(this, "Informe seu e-mail.", Toast.LENGTH_SHORT).show()
            return
        }
        val auth = FirebaseAuth.getInstance()
        auth.setLanguageCode("pt")

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                val msg = if (task.isSuccessful)
                    "Se o e-mail existir, enviaremos um link de redefinição."
                else
                    task.exception?.localizedMessage ?: "Erro ao enviar. Tente novamente."
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            }
    }



}