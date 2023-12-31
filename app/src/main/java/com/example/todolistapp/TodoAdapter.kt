    package com.example.todolistapp

    import android.annotation.SuppressLint
    import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.ViewGroup
    import android.widget.CheckBox
    import android.widget.TextView
    import androidx.recyclerview.widget.RecyclerView
    import com.example.todolistapp.databinding.ActivityTodoBinding
    import com.google.firebase.Firebase
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.auth.auth
    import com.google.firebase.database.DataSnapshot
    import com.google.firebase.database.DatabaseError
    import com.google.firebase.database.DatabaseReference
    import com.google.firebase.database.ValueEventListener
    import com.google.firebase.database.database

    class TodoAdapter(private val todos: MutableList<Todo>) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {
        private lateinit var auth: FirebaseAuth
        private lateinit var database: DatabaseReference

        class TodoViewHolder(val binding: ActivityTodoBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
            val binding = ActivityTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            val viewHolder = TodoViewHolder(binding)

            viewHolder.binding.cbIsChecked.setOnCheckedChangeListener { _, isChecked ->
                val curTodo = todos[viewHolder.adapterPosition]
                strikeThrough(viewHolder.binding.tvTodoTitle, isChecked)
                curTodo.is_Checked = isChecked
                updateCheckedDB(curTodo)
            }

            return viewHolder
        }

        override fun getItemCount(): Int {
            return todos.size
        }

        @SuppressLint("NotifyDataSetChanged")
        fun DeleteTodo() {
            auth = Firebase.auth
            val currentUser = auth.currentUser
            val encodedEmail = encodeEmail(currentUser?.email)
            if (encodedEmail != null) {
                val todosRef = database.child("todos").child(encodedEmail)

                todosRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val toDelete = mutableListOf<String>()

                        // Identify the nodes to delete
                        for (todoSnapshot in snapshot.children) {
                            val isChecked = todoSnapshot.child("_Checked").getValue(Boolean::class.java)
                            if (isChecked == true) {
                                toDelete.add(todoSnapshot.key!!)
                            }
                        }

                        // Delete the identified nodes
                        for (deleteKey in toDelete) {
                            todosRef.child(deleteKey).removeValue()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle errors if needed
                    }
                })
            }
            notifyDataSetChanged()
        }

        fun AddTodos(todo: Todo){
            todos.add(todo)
            notifyItemInserted(todos.size - 1)
        }

        @SuppressLint("NotifyDataSetChanged")
        fun setTodos(newTodos: List<Todo>) {
            todos.clear()
            todos.addAll(newTodos)
            notifyDataSetChanged()
        }

        private fun strikeThrough(tvTodoTitle: TextView, isChecked: Boolean){
            if(isChecked){
                tvTodoTitle.paintFlags = tvTodoTitle.paintFlags or STRIKE_THRU_TEXT_FLAG
            }
            else{
                tvTodoTitle.paintFlags = tvTodoTitle.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
            }
        }

        override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
            val curTodo = todos[position]
            holder.binding.apply {
                tvTodoTitle.text = curTodo.title
                cbIsChecked.isChecked = curTodo.is_Checked

                // Update the strikethrough based on the isChecked value
                strikeThrough(tvTodoTitle, curTodo.is_Checked)
            }
        }

        fun encodeEmail(email: String?): String? {
            return email?.replace(".", ",")
        }

        private fun updateCheckedDB(curTodo: Todo) {
            auth = Firebase.auth
            val currentUser = auth.currentUser
            database = Firebase.database.reference
            val encodedEmail = encodeEmail(currentUser?.email)
            if (encodedEmail != null) {
                val todosRef = database.child("todos").child(encodedEmail)
                val todoId = curTodo.id

                // Update the isChecked value in the database
                todosRef.child(todoId).child("_Checked").setValue(curTodo.is_Checked)
            }
        }
    }
