package com.example.dc2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

/**
 * 底部导航栏辅助工具类
 * 管理底部导航栏的设置、用户ID存储以及页面导航
 */
public class BottomNavHelper {
    private static final String TAG = "BottomNavHelper";

    // SharedPreferences 相关常量
    private static final String USER_PREFS = "UserPrefs";
    private static final String KEY_USER_ID = "user_id";

    // 导航项ID与目标Activity的映射关系
    private static final Map<Integer, Class<? extends Activity>> NAV_ITEM_ACTIVITY_MAP = new HashMap<>();

    static {
        // 初始化导航项与Activity的映射关系
        NAV_ITEM_ACTIVITY_MAP.put(R.id.nav_home, MainActivity.class);
        NAV_ITEM_ACTIVITY_MAP.put(R.id.nav_inventory, InventoryActivity.class);
        NAV_ITEM_ACTIVITY_MAP.put(R.id.nav_sell, OnSaleItemsActivity.class);
        NAV_ITEM_ACTIVITY_MAP.put(R.id.nav_buy, BuyActivity.class);
        NAV_ITEM_ACTIVITY_MAP.put(R.id.nav_profile, ProfileActivity.class);
    }

    /**
     * 设置底部导航栏
     * @param activity 当前Activity
     * @param defaultItemId 默认选中的导航项ID
     */
    public static void setupBottomNavigation(Activity activity, int defaultItemId) {
        BottomNavigationView bottomNav = activity.findViewById(R.id.bottom_navigation);
        if (bottomNav == null) {
            Log.e(TAG, "BottomNavigationView not found in activity");
            return;
        }

        // 设置默认选中项
        bottomNav.setSelectedItemId(defaultItemId);

        // 设置导航项选中监听
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Class<? extends Activity> targetActivity = NAV_ITEM_ACTIVITY_MAP.get(itemId);

            // 如果目标Activity不存在或已是当前Activity，则不跳转
            if (targetActivity == null || targetActivity.isInstance(activity)) {
                return true;
            }

            // 执行页面跳转
            navigateToActivity(activity, targetActivity);
            return true;
        });
    }

    /**
     * 保存用户ID到SharedPreferences
     * @param context 上下文
     * @param userId 用户ID
     */
    public static void saveUserId(Context context, int userId) {
        SharedPreferences prefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_USER_ID, userId).apply();
        Log.d(TAG, "Saved user ID: " + userId);
    }

    /**
     * 从SharedPreferences获取用户ID
     * @param context 上下文
     * @return 用户ID，未找到返回-1
     */
    public static int getUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        int userId = prefs.getInt(KEY_USER_ID, -1);
        Log.d(TAG, "Retrieved user ID: " + userId);
        return userId;
    }

    /**
     * 清除用户ID（用于退出登录）
     * @param context 上下文
     */
    public static void clearUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_USER_ID).apply();
        Log.d(TAG, "Cleared user ID");
    }

    /**
     * 跳转到目标Activity
     * @param context 上下文
     * @param targetActivity 目标Activity类
     */
    public static void navigateToActivity(Context context, Class<? extends Activity> targetActivity) {
        navigateToActivity(context, targetActivity, -1);
    }

    /**
     * 跳转到目标Activity并传递额外参数
     * @param context 上下文
     * @param targetActivity 目标Activity类
     * @param extraUserId 指定用户ID，-1表示使用默认存储的用户ID
     */
    public static void navigateToActivity(Context context, Class<? extends Activity> targetActivity, int extraUserId) {
        Intent intent = new Intent(context, targetActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // 确定要传递的用户ID
        int userId = extraUserId != -1 ? extraUserId : getUserId(context);
        if (userId != -1) {
            intent.putExtra("user_id", userId);
        }

        // 启动Activity
        if (context instanceof Activity) {
            ((Activity) context).startActivity(intent);
            ((Activity) context).overridePendingTransition(0, 0); // 无过渡动画
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    /**
     * 根据导航项ID获取目标Activity类
     * @param itemId 导航项ID
     * @return 目标Activity类，未找到返回null
     */
    @Nullable
    public static Class<? extends Activity> getTargetActivity(int itemId) {
        return NAV_ITEM_ACTIVITY_MAP.get(itemId);
    }

    /**
     * 检查用户是否已登录
     * @param context 上下文
     * @return true表示已登录，false表示未登录
     */
    public static boolean isUserLoggedIn(Context context) {
        return getUserId(context) != -1;
    }
}