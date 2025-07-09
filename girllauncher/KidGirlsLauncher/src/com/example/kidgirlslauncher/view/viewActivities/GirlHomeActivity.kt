package com.example.kidgirlslauncher.view.viewActivities 

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.kidgirlslauncher.R

class GirlHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val childUserId = intent.getIntExtra("android.intent.extra.user_handle", -1)

        findViewById<ImageView>(R.id.ic_watchremovebg).setOnClickListener {
            openAppCategory("watch", childUserId)
        }
        findViewById<ImageView>(R.id.ic_playremovebg).setOnClickListener {
            openAppCategory("play", childUserId)
        }
        findViewById<ImageView>(R.id.ic_drawremovebg).setOnClickListener {
            openAppCategory("draw", childUserId)
        }
        findViewById<ImageView>(R.id.ic_readremovebg).setOnClickListener {
            openAppCategory("read", childUserId)
        }
    }

    private fun openAppCategory(category: String, childUserId: Int) {
        val intent = Intent(this, CategoryActivity::class.java).apply {
            putExtra("category", category)
            putExtra("child_user_id", childUserId)
        }
        startActivity(intent)
    }
}

