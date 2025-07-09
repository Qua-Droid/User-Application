package com.example.parentlauncher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AppListAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private List<AppInfo> originalAppList;
    private List<AppInfo> filteredAppList;
    private LayoutInflater inflater;
    private AppFilter appFilter;

    public AppListAdapter(Context context, List<AppInfo> appList) {
        this.context = context;
        this.originalAppList = appList;
        this.filteredAppList = new ArrayList<>(appList);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return filteredAppList.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredAppList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.app_item, parent, false);
            holder = new ViewHolder();
            holder.appIcon = convertView.findViewById(R.id.app_icon);
            holder.appName = convertView.findViewById(R.id.app_name);
            holder.appPackage = convertView.findViewById(R.id.app_package);
            holder.appSwitch = convertView.findViewById(R.id.app_switch);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AppInfo appInfo = filteredAppList.get(position);
        holder.appIcon.setImageDrawable(appInfo.getIcon());
        holder.appName.setText(appInfo.getName());
        holder.appPackage.setText(appInfo.getPackageName());
        holder.appSwitch.setChecked(appInfo.isAllowed());

        // Set listener for switch changes
        holder.appSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            appInfo.setAllowed(isChecked);
        });

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (appFilter == null) {
            appFilter = new AppFilter();
        }
        return appFilter;
    }

    private static class ViewHolder {
        ImageView appIcon;
        TextView appName;
        TextView appPackage;
        Switch appSwitch;
    }

    private class AppFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            
            if (constraint == null || constraint.length() == 0) {
                results.values = originalAppList;
                results.count = originalAppList.size();
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                List<AppInfo> filteredList = new ArrayList<>();
                
                for (AppInfo app : originalAppList) {
                    if (app.getName().toLowerCase().contains(filterPattern) ||
                        app.getPackageName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(app);
                    }
                }
                
                results.values = filteredList;
                results.count = filteredList.size();
            }
            
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredAppList = (List<AppInfo>) results.values;
            notifyDataSetChanged();
        }
    }
}

