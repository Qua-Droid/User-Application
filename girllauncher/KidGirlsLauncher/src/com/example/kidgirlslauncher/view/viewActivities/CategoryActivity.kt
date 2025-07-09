package com.example.kidgirlslauncher.view.viewActivities 

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.lifecycle.Observer
import com.example.kidgirlslauncher.R
import com.example.kidgirlslauncher.viewmodel.CategoryViewModel
import com.example.kidgirlslauncher.viewmodel.CategoryViewModelFactory
import com.example.kidgirlslauncher.repository.AppRepository
import com.example.kidgirlslauncher.view.viewAdapters

class CategoryActivity : AppCompatActivity() {

    private val TAG = "CategoryActivity"

    private val categoryColors = mapOf(
        "Watch" to "#a1176b".toColorInt(),
        "Play"  to "#dbaa4b".toColorInt(),
        "Draw"  to "#dbaa4b".toColorInt(),
        "Read"  to "#a1176b".toColorInt()
    )

    private lateinit var adapter: AppAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        val category = intent.getStringExtra("category")?.lowercase()
        val childUserId = intent.getIntExtra("child_user_id", -1)

        if (childUserId == -1) {
            Log.e(TAG, "Missing child user ID")
            finish()
            return
        }

        val repository = AppRepository(applicationContext)
        val viewModel: CategoryViewModel by viewModels { CategoryViewModelFactory(repository) }

        val titleTv = findViewById<TextView>(R.id.tvCategoryTitle)
        titleTv.text = category?.replaceFirstChar { it.uppercase() }
        titleTv.setTextColor(categoryColors[category?.replaceFirstChar { it.uppercase() }] ?: Color.BLACK)

        val gridView = findViewById<GridView>(R.id.appGrid)
        adapter = AppAdapter(this, emptyList())
        gridView.adapter = adapter

        viewModel.apps.observe(this, Observer { appList ->
            adapter.updateApps(appList)
        })

        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            finish()
        }

        gridView.setOnItemClickListener { _, _, position, _ ->
            val app = adapter.getItem(position) as com.example.kidgirlslauncher.model.AppInfo
            val launchIntent = packageManager.getLaunchIntentForPackage(app.packageName)
            if (launchIntent != null) startActivity(launchIntent)
        }

        viewModel.loadApps(category, childUserId)
    }
}

