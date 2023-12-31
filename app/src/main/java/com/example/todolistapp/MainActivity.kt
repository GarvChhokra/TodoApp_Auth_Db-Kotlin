package com.example.todolistapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolistapp.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database


class MainActivity : AppCompatActivity() {
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        val currentUser = auth.currentUser
        database = Firebase.database.reference
        todoAdapter = TodoAdapter(mutableListOf())
        binding.apply {
            rvTodoItems.adapter = todoAdapter
            rvTodoItems.layoutManager = LinearLayoutManager(this@MainActivity)

            btnAddTodo.setOnClickListener {
                val todoTitle = textView.text.toString()
                if(todoTitle.isNotEmpty()){
                    val encodedEmail = encodeEmail(currentUser?.email)
                    val todosRef = database.child("todos").child(encodedEmail?: "default")
                    val todoKey = todosRef.push().key
                    val todo = Todo(id = todoKey.toString(), title = todoTitle, is_Checked = false)
                    if (todoKey != null) {
                        todosRef.child(todoKey).setValue(todo)
                    }
                    todoAdapter.AddTodos(todo)
                    textView.text.clear()
                }
            }

            btnDeleteTodo.setOnClickListener {
                todoAdapter.DeleteTodo()
            }

            if(currentUser!=null){
                tvEmailAddress.text = currentUser.email
                loadTodosFromDatabase(currentUser.email ?: "default")
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
    fun encodeEmail(email: String?): String? {
        return email?.replace(".", ",")
    }
    private fun loadTodosFromDatabase(userEmail: String) {
        val encodedEmail = encodeEmail(userEmail)
        val todosRef = database.child("todos").child(encodedEmail ?: "default")

        todosRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val loadedTodos = mutableListOf<Todo>()

                // Iterate through the todos in the database and add them to the list
                for (todoSnapshot in snapshot.children) {
                    val todoId = todoSnapshot.key
                    val title = todoSnapshot.child("title").getValue(String::class.java)
                    val isChecked = todoSnapshot.child("_Checked").getValue(Boolean::class.java)

                    if (todoId != null && title != null && isChecked != null) {
                        val todo = Todo(id = todoId, title = title, is_Checked = isChecked)
                        loadedTodos.add(todo)
                    }
                }


                // Update the adapter with the loaded todos
                todoAdapter.setTodos(loadedTodos)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors if needed
            }
        })
    }

}