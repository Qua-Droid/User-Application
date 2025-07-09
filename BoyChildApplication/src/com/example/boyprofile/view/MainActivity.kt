package com.example.boyprofile.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.boyprofile.R

class MainActivity : AppCompatActivity() {

    private var childUserId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Retrieve child user ID from Intent
        childUserId = intent.getIntExtra("child_user_id", -1)

        // Set up category buttons
        val btnWatch = findViewById<Button>(R.id.btnWatch)
        val btnPlay = findViewById<Button>(R.id.btnPlay)
        val btnDraw = findViewById<Button>(R.id.btnDraw)
        val btnRead = findViewById<Button>(R.id.btnRead)

        btnWatch.setOnClickListener { launchCategory("watch") }
        btnPlay.setOnClickListener { launchCategory("play") }
        btnDraw.setOnClickListener { launchCategory("draw") }
        btnRead.setOnClickListener { launchCategory("read") }

        // Optional: Exit button
        val btnExit = findViewById<ImageButton>(R.id.exit_button)
        btnExit?.setOnClickListener {
            finish()
        }
    }

    private fun launchCategory(category: String) {
        val intent = Intent(this, AppGridActivity::class.java).apply {
            putExtra("category", category)
            putExtra("child_user_id", childUserId)
        }
        startActivity(intent)
    }
}

