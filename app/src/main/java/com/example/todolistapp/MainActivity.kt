package com.example.todolistapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolistapp.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


class MainActivity : AppCompatActivity() {
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        val currentUser = auth.currentUser
        todoAdapter = TodoAdapter(mutableListOf())
        binding.apply {
            rvTodoItems.adapter = todoAdapter
            rvTodoItems.layoutManager = LinearLayoutManager(this@MainActivity)

            btnAddTodo.setOnClickListener {
                val todoTitle = textView.text.toString()
                if(todoTitle.isNotEmpty()){
                    val todo = Todo(todoTitle)
                    todoAdapter.AddTodos(todo)
                    textView.text.clear()
                }
            }

            btnDeleteTodo.setOnClickListener {
                todoAdapter.DeleteTodo()
            }

            if(currentUser!=null){
                tvEmailAddress.text = currentUser.email
            }
            else{
                val intent = Intent(this@MainActivity, Login::class.java)
                startActivity(intent)
                finish()
            }

            btnLogout.setOnClickListener {
                Firebase.auth.signOut()
                val intent = Intent(this@MainActivity, Login::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}