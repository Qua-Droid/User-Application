package com.example.boyprofile.adapter

import android.content.Context
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.boyprofile.R
import com.example.boyprofile.model.AppInfo

class AppAdapter(private val context: Context, private var apps: List<AppInfo>) : BaseAdapter() {

    override fun getCount(): Int = apps.size

    override fun getItem(position: Int): Any = apps[position]

    override fun getItemId(position: Int): Long = position.toLong()

    fun updateData(newApps: List<AppInfo>) {
        apps = newApps
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.grid_item_app, parent, false)
        val app = apps[position]

        val iconView = view.findViewById<ImageView>(R.id.ivIcon)
        val nameView = view.findViewById<TextView>(R.id.tvLabel)

        try {
            val icon = context.packageManager.getApplicationIcon(app.packageName)
            iconView.setImageDrawable(icon)
        } catch (e: PackageManager.NameNotFoundException) {
            iconView.setImageResource(R.drawable.ic_launcher_foreground)
        }

        nameView.text = app.name
        return view
    }
}

