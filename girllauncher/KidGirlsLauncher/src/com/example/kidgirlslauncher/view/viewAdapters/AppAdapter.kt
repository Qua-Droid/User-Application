package com.example.kidgirlslauncher.view.viewAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.kidgirlslauncher.R
import com.example.kidgirlslauncher.model.AppInfo

class AppAdapter(private val context: Context, private var apps: List<AppInfo>) : BaseAdapter() {

    fun updateApps(newApps: List<AppInfo>) {
        apps = newApps
        notifyDataSetChanged()
    }

    override fun getCount(): Int = apps.size

    override fun getItem(position: Int): Any = apps[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_app, parent, false)

        val appNameTextView: TextView = view.findViewById(R.id.app_name)
        val appIconImageView: ImageView = view.findViewById(R.id.app_icon)

        val appInfo = apps[position]
        appNameTextView.text = appInfo.name

        try {
            val icon = context.packageManager.getApplicationIcon(appInfo.packageName)
            appIconImageView.setImageDrawable(icon)
        } catch (e: Exception) {
            appIconImageView.setImageResource(R.drawable.ic_launcher_foreground) // fallback icon
        }

        return view
    }
}

