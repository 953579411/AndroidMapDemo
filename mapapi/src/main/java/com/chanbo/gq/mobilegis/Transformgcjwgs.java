package com.chanbo.gq.mobilegis;

import android.content.Context;
import android.view.MotionEvent;

import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;

public class Transformgcjwgs {

	/*public static void main(String[] args) {
		LocatePoint point=new LocatePoint();
		gcj2wgs(2219109.879860811,5170520.71284654,point);
		System.out.println(point.Lon+","+point.Lat);
	}*/
	public static boolean outOfChina(double lng, double lat) {

		if (lng < 72.004 || lng > 137.8347) {
			return true;
		}
		if (lat < 0.8293 || lat > 55.8271) {
			return true;
		}
		return false;
	}

	public static void transform(double x, double y, LocatePoint p) {

		double xy = x * y;
		double absX = Math.sqrt(Math.abs(x));
		double d = (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0
				* x * Math.PI)) * 2.0 / 3.0;

		p.Lat = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * xy + 0.2
				* absX;
		p.Lon = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * xy + 0.1 * absX;

		p.Lat += d;
		p.Lon += d;

		p.Lat += (20.0 * Math.sin(y * Math.PI) + 40.0 * Math.sin(y / 3.0
				* Math.PI)) * 2.0 / 3.0;
		p.Lon += (20.0 * Math.sin(x * Math.PI) + 40.0 * Math.sin(x / 3.0
				* Math.PI)) * 2.0 / 3.0;

		p.Lat += (160.0 * Math.sin(y / 12.0 * Math.PI) + 320 * Math.sin(y
				/ 30.0 * Math.PI)) * 2.0 / 3.0;
		p.Lon += (150.0 * Math.sin(x / 12.0 * Math.PI) + 300.0 * Math.sin(x
				/ 30.0 * Math.PI)) * 2.0 / 3.0;
	}

	public static void delta(double lng, double lat, LocatePoint p) {

		double a = 6378245.0;
		double ee = 0.00669342162296594323;

		transform(lng - 105.0, lat - 35.0, p);
		double radLat = lat / 180.0 * Math.PI;
		double magic = Math.sin(radLat);
		magic = 1 - ee * magic * magic;
		double sqrtMagic = Math.sqrt(magic);
		p.Lat = (p.Lat * 180.0)
				/ ((a * (1 - ee)) / (magic * sqrtMagic) * Math.PI);
		p.Lon = (p.Lon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * Math.PI);
	}

	public static LocatePoint wgs2gcj(double wgsLng, double wgsLat, LocatePoint p) {

		boolean IsOutOfChina = outOfChina(wgsLng, wgsLat);
		if (IsOutOfChina) {
			p.Lat = wgsLat;
			p.Lon = wgsLng;
			return null;
		}

		LocatePoint dp = new LocatePoint();

		delta(wgsLng, wgsLat, dp);

		p.Lat = wgsLat + dp.Lat;
		p.Lon = wgsLng + dp.Lon;
		return p;
	}

	public static void gcj2wgs(double gcjLng, double gcjLat, LocatePoint p) {

		if (outOfChina(gcjLng, gcjLat)) {
			p.Lat = gcjLat;
			p.Lon = gcjLng;
			return;
		}

		LocatePoint dp = new LocatePoint();

		delta(gcjLng, gcjLat, dp);
		p.Lat = gcjLat - dp.Lat;
		p.Lon = gcjLng - dp.Lon;

	}

	public static void gcj2wgs_exact(double gcjLng, double gcjLat, LocatePoint p) {
		double initDelta = 0.01;
		double threshold = 0.000001;
		double dLat = initDelta, dLng = initDelta;
		double mLat = gcjLat - dLat, mLng = gcjLng - dLng;
		double pLat = gcjLat + dLat, pLng = gcjLng + dLng;
		int i;
		for (i = 0; i < 30; i++) {
			p.Lat = (mLat + pLat) / 2;
			p.Lon = (mLng + pLng) / 2;
			LocatePoint tmpp = new LocatePoint();
			wgs2gcj(p.Lat, p.Lon, tmpp);
			dLat = tmpp.Lat - gcjLat;
			dLng = tmpp.Lon - gcjLng;
			if ((Math.abs(dLat) < threshold) && (Math.abs(dLng) < threshold)) {
				return;
			}
			if (dLat > 0) {
				pLat = p.Lat;
			} else {
				mLat = p.Lat;
			}
			if (dLng > 0) {
				pLng = p.Lon;
			} else {
				mLng = p.Lon;
			}
		}
	}
}
