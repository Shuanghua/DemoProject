package com.shuanghua.myapplication

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import android.widget.RemoteViews

class DesktopAppWidgetProvider : AppWidgetProvider() {

    /**
     * 每添加一个本应用的桌面 Widget 都回调一次
     * 通过 AlarmManager 去唤醒 我们的 Service,然后在我们的 Service 中对 Widget 进行数据更新
     * AlarmManager 最快只能 1 分钟唤醒一次
     */
    override fun onUpdate(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray
    ) {
        // TODO("从本地获取数据,然后调用 setTextViewText()")
        // 从而让每次添加新的 Widget 能立即显示数据
        // 而不是等 AlarmManager 时间到后通过 Service 来刷新
        Log.d(TAG, "onUpdate")
        updateAppWidget(context, "第一次添加")
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    /**
     * 创建第一个本应用的 Widget 时调用
     * 此函数由 ACTION_APPWIDGET_ENABLED 广播意图拦截触发
     * 可以重写 onReceive 函数来提前拦截广播意图调用此类函数
     */
    override fun onEnabled(context: Context) {
        Log.d(TAG, "onEnabled")
        startAlarm(context)
        super.onEnabled(context)
    }

    /**
     * 每次删除本应用的一个 Widget 时调用
     * 此函数由 ACTION_APPWIDGET_DELETED 广播意图拦截触发
     */
    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        Log.d(TAG, "onDeleted")
        super.onDeleted(context, appWidgetIds)
    }

    /**
     * 删除本应用最后一个 Widget 时调用
     * 此函数由 ACTION_APPWIDGET_DISABLED 广播意图拦截触发
     */
    override fun onDisabled(context: Context) {
        Log.d(TAG, "onDisabled")
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, DesktopWidgetService::class.java)
        val service = PendingIntent.getService(context, 1234, intent,
                PendingIntent.FLAG_CANCEL_CURRENT)
        alarmManager.cancel(service)
        val serviceIntent = Intent(context, DesktopWidgetService::class.java)
        context.stopService(serviceIntent)
    }

    /**
     * 利用系统警报器来广播驱动 Widget 更新，避免在系统休眠状态更新 Widget
     * 手机在休眠状态下停止更新，非休眠状态才开启更新（且每分钟更新一次）
     * 替换默认 Widget 的最短 30 分钟更新机制
     * AlarmManager 最短只能设置 1 分钟
     */
    private fun startAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, DesktopWidgetService::class.java)
        val service = PendingIntent.getService(context, 1234, intent,
                PendingIntent.FLAG_CANCEL_CURRENT)
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),
                60000, service)
    }

    companion object {
        private const val TAG = "AppWidgetProvider"

        fun updateAppWidget(context: Context, data: String) {
            Log.d(TAG, data)

            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, DesktopAppWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

            for (appWidgetId in appWidgetIds) {
                val clickIntent = Intent(context, MainActivity::class.java)
                clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetId)
                val pendingIntent = PendingIntent.getActivity(context, 0, clickIntent, 0)

                val views = RemoteViews(context.packageName, R.layout.appwidget_provider)
                views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent)
                views.setTextViewText(R.id.appwidget_text, data)

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
}

