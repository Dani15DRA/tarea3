package com.example.informe3

import android.content.ContentValues
import android.content.Context
import at.favre.lib.crypto.bcrypt.BCrypt

class UserDAO(context: Context?) {
    private val dbHelper = DatabaseHelper(context)

    fun insertUser(user: User): Long {
        val db = dbHelper.writableDatabase

        val hashedPassword = hashPassword(user.password)

        val values = ContentValues()
        values.put(DatabaseHelper.COLUMN_USERNAME, user.username)
        values.put(DatabaseHelper.COLUMN_PASSWORD, hashedPassword) // Store hashed password
        values.put(DatabaseHelper.COLUMN_EMAIL, user.email)
        values.put(DatabaseHelper.COLUMN_FULL_NAME, user.fullName)

        val id = db.insert(DatabaseHelper.TABLE_USERS, null, values)
        db.close()

        return id
    }

    fun getUserById(id: Long): User? {
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            DatabaseHelper.TABLE_USERS,
            arrayOf(
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_USERNAME,
                DatabaseHelper.COLUMN_PASSWORD,
                DatabaseHelper.COLUMN_EMAIL,
                DatabaseHelper.COLUMN_FULL_NAME
            ),
            "${DatabaseHelper.COLUMN_ID}=?",
            arrayOf(id.toString()),
            null, null, null, null
        )

        var user: User? = null
        if (cursor != null && cursor.moveToFirst()) {
            user = User(
                cursor.getString(1),  // username
                cursor.getString(2),  // password - still stored but not used for verification
                cursor.getString(3),  // email
                cursor.getString(4)   // fullName
            )
            user.id = cursor.getLong(0)
            cursor.close()
        }

        db.close()
        return user
    }

    fun checkLogin(username: String, password: String): User? {
        val user = getUserByUsername(username)

        if (user != null && verifyPassword(password, user.password)) {
            return user
        }

        return null
    }

    val allUsers: List<User>
        get() {
            val userList: MutableList<User> = ArrayList()
            val selectQuery = "SELECT * FROM ${DatabaseHelper.TABLE_USERS}"
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()) {
                do {
                    val user = User(
                        cursor.getString(1),  // username
                        cursor.getString(2),  // password
                        cursor.getString(3),  // email
                        cursor.getString(4)   // fullName
                    )
                    user.id = cursor.getLong(0)
                    userList.add(user)
                } while (cursor.moveToNext())
            }

            cursor.close()
            db.close()
            return userList
        }

    fun updateUser(user: User): Int {
        val db = dbHelper.writableDatabase

        val currentUser = getUserById(user.id)
        val passwordToSave = if (currentUser?.password != user.password) {
            hashPassword(user.password)
        } else {
            user.password
        }

        val values = ContentValues()
        values.put(DatabaseHelper.COLUMN_USERNAME, user.username)
        values.put(DatabaseHelper.COLUMN_PASSWORD, passwordToSave)
        values.put(DatabaseHelper.COLUMN_EMAIL, user.email)
        values.put(DatabaseHelper.COLUMN_FULL_NAME, user.fullName)

        val rowsUpdated = db.update(
            DatabaseHelper.TABLE_USERS,
            values,
            "${DatabaseHelper.COLUMN_ID}=?",
            arrayOf(user.id.toString())
        )

        db.close()
        return rowsUpdated
    }

    fun deleteUser(userId: Long): Int {
        val db = dbHelper.writableDatabase
        val rowsDeleted = db.delete(
            DatabaseHelper.TABLE_USERS,
            "${DatabaseHelper.COLUMN_ID}=?",
            arrayOf(userId.toString())
        )

        db.close()
        return rowsDeleted
    }

    fun getUserByUsername(username: String): User? {
        val db = dbHelper.readableDatabase
        var user: User? = null

        val cursor = db.query(
            DatabaseHelper.TABLE_USERS,
            arrayOf(
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_USERNAME,
                DatabaseHelper.COLUMN_PASSWORD,
                DatabaseHelper.COLUMN_EMAIL,
                DatabaseHelper.COLUMN_FULL_NAME
            ),
            "${DatabaseHelper.COLUMN_USERNAME}=?",
            arrayOf(username),
            null, null, null
        )

        if (cursor != null && cursor.moveToFirst()) {
            user = User(
                cursor.getString(1),  // username
                cursor.getString(2),  // password
                cursor.getString(3),  // email
                cursor.getString(4)   // fullName
            )
            user.id = cursor.getLong(0)
            cursor.close()
        }

        db.close()
        return user
    }

    private fun hashPassword(plainPassword: String): String {
        return BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray())
    }

    private fun verifyPassword(plainPassword: String, hashedPassword: String): Boolean {
        return BCrypt.verifyer().verify(plainPassword.toCharArray(), hashedPassword).verified
    }
}