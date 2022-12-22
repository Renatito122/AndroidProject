package com.example.fyreapp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fyreapp.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun OnCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonConcert1.setOnClickListener(this)
        buttonConcert2.setOnClickListener(this)
        buttonConcert3.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.buttonConcert1 -> Toast.makeText(this@MainActivity, "CONCERT ONE", Toast.LENGTH_LONG).show()
            R.id.buttonConcert2 -> Toast.makeText(this@MainActivity, "CONCERT TWO", Toast.LENGTH_LONG).show()
            R.id.buttonConcert3 -> Toast.makeText(this@MainActivity, "CONCERT THREE", Toast.LENGTH_LONG).show()
        }
    }
}
