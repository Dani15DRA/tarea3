package com.example.informe3

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class UserManagementActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var userDAO: UserDAO
    private var userList: List<User> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_management)

        recyclerView = findViewById(R.id.recyclerViewUsers)
        recyclerView.layoutManager = LinearLayoutManager(this)

        userDAO = UserDAO(this)

        loadUsers()

        val buttonAddUser = findViewById<Button>(R.id.buttonAddUser)
        buttonAddUser.setOnClickListener {
            showAddUserDialog()
        }

        val fabAddUser = findViewById<FloatingActionButton>(R.id.fabAddUser)
        fabAddUser.setOnClickListener {
            showAddUserDialog()
        }

        val buttonLogout = findViewById<Button>(R.id.buttonLogout)
        buttonLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadUsers()
    }

    private fun loadUsers() {
        userList = userDAO.allUsers
        userAdapter = UserAdapter(userList)
        recyclerView.adapter = userAdapter
    }

    private fun showAddUserDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_edit_user, null)

        val editTextUsername = dialogView.findViewById<EditText>(R.id.editTextUsername)
        val editTextEmail = dialogView.findViewById<EditText>(R.id.editTextEmail)
        val editTextFullName = dialogView.findViewById<EditText>(R.id.editTextFullName)
        val editTextPassword = dialogView.findViewById<EditText>(R.id.editTextPassword)

        builder.setView(dialogView)
            .setTitle("Añadir Usuario")
            .setPositiveButton("Guardar") { _, _ ->
                val newUser = User()
                newUser.username = editTextUsername.text.toString()
                newUser.email = editTextEmail.text.toString()
                newUser.fullName = editTextFullName.text.toString()
                newUser.password = editTextPassword.text.toString()

                if (newUser.username.isBlank() || newUser.password.isBlank()) {
                    Toast.makeText(this, "Usuario y contraseña son obligatorios", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val result = userDAO.insertUser(newUser)
                if (result > 0) {
                    Toast.makeText(this, "Usuario creado correctamente", Toast.LENGTH_SHORT).show()
                    loadUsers()
                } else {
                    Toast.makeText(this, "Error al crear usuario", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.cancel()
            }

        builder.create().show()
    }

    inner class UserAdapter(private val users: List<User>) :
        RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

        inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textViewUsername: TextView = view.findViewById(R.id.textViewUsername)
            val textViewEmail: TextView = view.findViewById(R.id.textViewEmail)
            val textViewFullName: TextView = view.findViewById(R.id.textViewFullName)
            val buttonEdit: Button = view.findViewById(R.id.buttonEdit)
            val buttonDelete: Button = view.findViewById(R.id.buttonDelete)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_user, parent, false)
            return UserViewHolder(view)
        }

        override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
            val user = users[position]
            holder.textViewUsername.text = "Usuario: ${user.username}"
            holder.textViewEmail.text = "Email: ${user.email}"
            holder.textViewFullName.text = "Nombre: ${user.fullName}"

            holder.buttonEdit.setOnClickListener {
                showEditDialog(user)
            }

            holder.buttonDelete.setOnClickListener {
                showDeleteConfirmation(user)
            }
        }

        override fun getItemCount() = users.size
    }

    private fun showEditDialog(user: User) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_edit_user, null)

        val editTextUsername = dialogView.findViewById<EditText>(R.id.editTextUsername)
        val editTextEmail = dialogView.findViewById<EditText>(R.id.editTextEmail)
        val editTextFullName = dialogView.findViewById<EditText>(R.id.editTextFullName)
        val editTextPassword = dialogView.findViewById<EditText>(R.id.editTextPassword)

        editTextUsername.setText(user.username)
        editTextEmail.setText(user.email)
        editTextFullName.setText(user.fullName)
        editTextPassword.setText(user.password)

        builder.setView(dialogView)
            .setTitle("Editar Usuario")
            .setPositiveButton("Guardar") { _, _ ->
                user.username = editTextUsername.text.toString()
                user.email = editTextEmail.text.toString()
                user.fullName = editTextFullName.text.toString()
                user.password = editTextPassword.text.toString()

                val result = userDAO.updateUser(user)
                if (result > 0) {
                    Toast.makeText(this, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                    loadUsers()
                } else {
                    Toast.makeText(this, "Error al actualizar usuario", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.cancel()
            }

        builder.create().show()
    }

    private fun showDeleteConfirmation(user: User) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Está seguro que desea eliminar el usuario ${user.username}?")
            .setPositiveButton("Eliminar") { _, _ ->
                val result = userDAO.deleteUser(user.id)
                if (result > 0) {
                    Toast.makeText(this, "Usuario eliminado", Toast.LENGTH_SHORT).show()
                    loadUsers()
                } else {
                    Toast.makeText(this, "Error al eliminar usuario", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}