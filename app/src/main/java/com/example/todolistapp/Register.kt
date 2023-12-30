package com.example.todolistapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.todolistapp.databinding.ActivityRegisterBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth

class Register : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this@Register, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        binding.apply {
            btnRegister.setOnClickListener {
                if(editTextEmail.text.toString().isEmpty()){
                    Toast.makeText(this@Register, "Enter your Email", Toast.LENGTH_SHORT).show()
                }
                if(editTextPassword.text.toString().isEmpty()){
                    Toast.makeText(this@Register, "Enter your Password", Toast.LENGTH_SHORT).show()
                }
                else{
                auth.createUserWithEmailAndPassword(editTextEmail.text.toString(), editTextPassword.text.toString())
                    .addOnCompleteListener(this@Register) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this@Register, "Account Created", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@Register, Login::class.java)
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

            btnLogin.setOnClickListener {
                val intent = Intent(this@Register, Login::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}