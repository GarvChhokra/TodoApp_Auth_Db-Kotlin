package com.example.todolistapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.todolistapp.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import java.util.zip.Inflater

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this@Login, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        binding.apply {
            btnLogin.setOnClickListener{
                if(editTextEmail.text.toString().isEmpty()){
                    Toast.makeText(this@Login, "Enter your Email", Toast.LENGTH_SHORT).show()
                }
                if(editTextPassword.text.toString().isEmpty()){
                    Toast.makeText(this@Login, "Enter your Password", Toast.LENGTH_SHORT).show()
                }
                else {
                    auth.signInWithEmailAndPassword(editTextEmail.text.toString(), editTextPassword.text.toString())
                        .addOnCompleteListener(this@Login) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                val user = auth.currentUser
                                Toast.makeText(
                                    baseContext,
                                    "Authentication Success.",
                                    Toast.LENGTH_SHORT,
                                ).show()
                                val intent = Intent(this@Login, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(
                                    baseContext,
                                    "Authentication failed.",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        }
                }
            }

            // Get the SHID by going into the gradle from right hand side and by searching 'signingReport' and paste it into the Firebase
//            btnGoogleLogin.setOnClickListener {
//
//            }

            btnRegister.setOnClickListener {
                val intent = Intent(this@Login, Register::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}