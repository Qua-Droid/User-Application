package com.example.boyprofile.view

import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.boyprofile.viewmodel.AppGridViewModel
import com.example.boyprofile.adapter.AppAdapter
import com.example.boyprofile.R

class AppGridActivity : AppCompatActivity() {

    private val viewModel: AppGridViewModel by viewModels()
    private val categoryColors = mapOf(
        "watch" to "#024C73".toColorInt(),
        "play" to "#E68620".toColorInt(),
        "draw" to "#E68620".toColorInt(),
        "read" to "#024C73".toColorInt()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grid)

        val category = intent.getStringExtra("category")?.lowercase()
        val childUserId = intent.getIntExtra("child_user_id", -1)

        findViewById<TextView>(R.id.tvCategoryTitle).apply {
            text = category?.capitalize()
            setTextColor(categoryColors[category] ?: 0x000000)
        }

        val gridView = findViewById<GridView>(R.id.appGrid)
        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener { finish() }

        val adapter = AppAdapter(this, emptyList())
        gridView.adapter = adapter

        viewModel.apps.observe(this, Observer {
            adapter.updateData(it)
        })

        if (childUserId != -1) {
            viewModel.loadApps(category, childUserId)
        }
    }
}

