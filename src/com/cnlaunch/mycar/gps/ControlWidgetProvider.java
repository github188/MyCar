package com.cnlaunch.mycar.gps;

import com.cnlaunch.mycar.R;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;

public class ControlWidgetProvider extends AppWidgetProvider {

	private int mLoggerState;

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		super.onDisabled(context);
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		context.startService(new Intent(GpsConstants.GPS_LOGGER_SERVICE_NAME));
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		final int len = appWidgetIds.length;
		for (int i = 0; i < len; i++) {
			final int appWidgetId = appWidgetIds[i];
			RemoteViews views = builderRemoteView(context, appWidgetId);
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}

	}

	private RemoteViews builderRemoteView(Context context, int appWidgetId) {
		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.gps_control_appwidget);
		views.setOnClickPendingIntent(
				R.id.gps_button_locate,
				getLaunchPendingIntent(context, appWidgetId,
						R.id.gps_button_locate));
		views.setOnClickPendingIntent(
				R.id.gps_button_menu,
				getLaunchPendingIntent(context, appWidgetId,
						R.id.gps_button_menu));
		views.setOnClickPendingIntent(
				R.id.gps_button_note,
				getLaunchPendingIntent(context, appWidgetId,
						R.id.gps_button_note));
		views.setOnClickPendingIntent(
				R.id.gps_button_pause,
				getLaunchPendingIntent(context, appWidgetId,
						R.id.gps_button_pause));
		views.setOnClickPendingIntent(
				R.id.gps_button_start,
				getLaunchPendingIntent(context, appWidgetId,
						R.id.gps_button_start));
		views.setOnClickPendingIntent(
				R.id.gps_button_stop,
				getLaunchPendingIntent(context, appWidgetId,
						R.id.gps_button_stop));

		updateButtons(views, context);
		return views;
	}

	private static PendingIntent getLaunchPendingIntent(Context context,
			int appWidgetId, int buttonId) {
		Intent launchIntent = new Intent();
		launchIntent.setClass(context, ControlWidgetProvider.class);
		launchIntent.addCategory(Intent.CATEGORY_ALTERNATIVE);
		launchIntent.setData(Uri.parse("custom:" + buttonId));
		PendingIntent pi = PendingIntent.getBroadcast(context, 0 /*
																 * no
																 * requestCode
																 */,
				launchIntent, 0 /*
								 * no flags
								 */);
		return pi;
	}

	private void updateButtons(RemoteViews views, Context context) {
		switch (mLoggerState) {
			case GpsConstants.GPS_LOGGER_LOGGING :
				views.setViewVisibility(R.id.gps_widget_menu_before_start,
						View.GONE);
				views.setViewVisibility(R.id.gps_widget_menu_after_start,
						View.VISIBLE);
				views.setViewVisibility(R.id.gps_widget_button_resume,
						View.GONE);
				views.setViewVisibility(R.id.gps_widget_button_pause,
						View.VISIBLE);
				break;
			case GpsConstants.GPS_LOGGER_PAUSED :
				views.setViewVisibility(R.id.gps_widget_menu_before_start,
						View.GONE);
				views.setViewVisibility(R.id.gps_widget_menu_after_start,
						View.VISIBLE);
				views.setViewVisibility(R.id.gps_widget_button_resume,
						View.VISIBLE);
				views.setViewVisibility(R.id.gps_widget_button_pause, View.GONE);
				break;
			case GpsConstants.GPS_LOGGER_STOPPED :
			case GpsConstants.GPS_LOGGER_UNKNOWN :
				views.setViewVisibility(R.id.gps_widget_menu_before_start,
						View.VISIBLE);
				views.setViewVisibility(R.id.gps_widget_menu_after_start,
						View.GONE);
				break;
			default :
				break;
		}
	}

}
