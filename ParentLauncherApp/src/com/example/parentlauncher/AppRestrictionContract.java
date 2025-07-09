package com.example.parentlauncher;

import android.provider.BaseColumns;

public final class AppRestrictionContract {
    private AppRestrictionContract() {}

    public static class AppRestrictionEntry implements BaseColumns {
        public static final String TABLE_NAME = "app_restrictions";
        public static final String COLUMN_CHILD_USER_ID = "child_user_id";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_PACKAGE_NAME = "package_name";
        public static final String COLUMN_IS_ALLOWED = "is_allowed";
        public static final String COLUMN_IS_APP_SPECIFIC = "is_app_specific";
    }
}

