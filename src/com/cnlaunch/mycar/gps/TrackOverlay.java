package com.cnlaunch.mycar.gps;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

class TrackOverlay extends Overlay {
	private ArrayList<ArrayList<GeoPoint>> mTrack;

	public TrackOverlay(ArrayList<ArrayList<GeoPoint>> track) {
		this.mTrack = track;
	}

	public ArrayList<ArrayList<GeoPoint>> getmTrack() {
		return mTrack;
	}

	public void setmTrack(ArrayList<ArrayList<GeoPoint>> mTrack) {
		this.mTrack = mTrack;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		drawTrack(canvas, mapView);
	}

	/**
	 * ªÊ÷∆πÏº£
	 * @param canvas
	 * @param mapView
	 */
	private void drawTrack(Canvas canvas, MapView mapView) {
		Paint paint = new Paint();
		paint.setColor(Color.RED);
		paint.setDither(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(2);
		Projection projection = mapView.getProjection();

		if (mTrack != null) {
			for (ArrayList<GeoPoint> waypoints : mTrack) {
				Path path = new Path();
				Point p = null;
				for (GeoPoint geoPoint : waypoints) {
					if (p == null) {
						p = new Point();
						projection.toPixels(geoPoint, p);
						path.moveTo(p.x, p.y);
					} else {
						p = new Point();
						projection.toPixels(geoPoint, p);
						path.lineTo(p.x, p.y);
					}
				}
				canvas.drawPath(path, paint);
			}
		}

	}
}
