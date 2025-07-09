package com.example.parentlauncher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AppRecyclerAdapter extends RecyclerView.Adapter<AppRecyclerAdapter.AppViewHolder> implements Filterable {
    private Context context;
    private List<AppInfo> originalAppList;
    private List<AppInfo> filteredAppList;
    private LayoutInflater inflater;
    private AppFilter appFilter;

    public AppRecyclerAdapter(Context context, List<AppInfo> appList) {
        this.context = context;
        this.originalAppList = appList;
        this.filteredAppList = new ArrayList<>(appList);
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.app_item, parent, false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        AppInfo appInfo = filteredAppList.get(position);
        holder.appIcon.setImageDrawable(appInfo.getIcon());
        holder.appName.setText(appInfo.getName());
        holder.appPackage.setText(appInfo.getPackageName());
        
        // Clear any existing listener to prevent unwanted triggers
        holder.appSwitch.setOnCheckedChangeListener(null);
        
        // Set the current state
        holder.appSwitch.setChecked(appInfo.isAllowed());

        // Set listener for switch changes
        holder.appSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Update the app info in the filtered list
            appInfo.setAllowed(isChecked);
            
            // Also update the original list to persist the state
            for (AppInfo originalApp : originalAppList) {
                if (originalApp.getPackageName().equals(appInfo.getPackageName())) {
                    originalApp.setAllowed(isChecked);
                    break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredAppList.size();
    }

    @Override
    public Filter getFilter() {
        if (appFilter == null) {
            appFilter = new AppFilter();
        }
        return appFilter;
    }

    /**
     * Get the current state of all apps (useful for saving restrictions)
     */
    public List<AppInfo> getAllApps() {
        return originalAppList;
    }

    /**
     * Update an app's allowed state by package name
     */
    public void updateAppState(String packageName, boolean isAllowed) {
        // Update in original list
        for (AppInfo app : originalAppList) {
            if (app.getPackageName().equals(packageName)) {
                app.setAllowed(isAllowed);
                break;
            }
        }
        
        // Update in filtered list
        for (AppInfo app : filteredAppList) {
            if (app.getPackageName().equals(packageName)) {
                app.setAllowed(isAllowed);
                break;
            }
        }
        
        // Notify adapter of data change
        notifyDataSetChanged();
    }

    public static class AppViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        TextView appPackage;
        Switch appSwitch;

        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.app_icon);
            appName = itemView.findViewById(R.id.app_name);
            appPackage = itemView.findViewById(R.id.app_package);
            appSwitch = itemView.findViewById(R.id.app_switch);
        }
    }

    private class AppFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            
            if (constraint == null || constraint.length() == 0) {
                // Create a new list with the same apps but preserve their current states
                List<AppInfo> resetList = new ArrayList<>();
                for (AppInfo originalApp : originalAppList) {
                    resetList.add(originalApp);
                }
                results.values = resetList;
                results.count = resetList.size();
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

