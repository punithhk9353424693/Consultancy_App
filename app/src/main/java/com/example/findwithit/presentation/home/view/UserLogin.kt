package com.example.findwithit.presentation.home.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.findwithit.R
import com.google.android.material.textfield.TextInputLayout

class UserLogin : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loginpage)

        val useremail = findViewById<EditText>(R.id.useremail)
        val passwordEditText = findViewById<EditText>(R.id.userPassword)
        val loginbtn = findViewById<Button>(R.id.loginbtn)
        val passwordLayout = findViewById<TextInputLayout>(R.id.textInputLayout)


        loginbtn.setOnClickListener {
            val email = useremail.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill the Proper Inputs", Toast.LENGTH_SHORT).show()
            } else if (email == "mahesh" && password == "mahesh@123") {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
