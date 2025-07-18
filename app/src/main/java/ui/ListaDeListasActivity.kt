package ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.trabalho_facul.R
import com.google.firebase.auth.FirebaseAuth

class cadastro2 : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setContentView(R.layout.activity_cadastro) // seu layout de cadastro

        auth = FirebaseAuth.getInstance()

        val emailField = findViewById<EditText>(R.id.edt_email)
        val senhaField = findViewById<EditText>(R.id.edt_senha)
        val confirmarSenhaField = findViewById<EditText>(R.id.edt_confirmarsenha)
        val btnCadastro = findViewById<Button>(R.id.btn_login)


        btnCadastro.setOnClickListener {
            val email = emailField.text.toString().trim()
            val senha = senhaField.text.toString().trim()
            val confirmarSenha = confirmarSenhaField.text.toString().trim()

            if (email.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (senha != confirmarSenha) {
                Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, senha)
                .addOnSuccessListener {
                    Toast.makeText(this, "Usuário cadastrado com sucesso", Toast.LENGTH_SHORT).show()
                    // Aqui você pode redirecionar para a tela principal
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao cadastrar: ${it.message}", Toast.LENGTH_LONG).show()
                }
        }

    }
}