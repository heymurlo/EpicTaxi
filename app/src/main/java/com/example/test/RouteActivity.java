package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;

import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.transport.masstransit.MasstransitRouter;
import com.yandex.mapkit.transport.masstransit.Session;

public class RouteActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        {
            final String MAPKIT_API_KEY = "c873c3d7-4da8-433b-94ee-664d84fa8aed";
            final Point TARGET_LOCATION = new Point(55.752078, 37.592664);

            final Point ROUTE_START_LOCATION = new Point(55.699671, 37.567286);
            final Point ROUTE_END_LOCATION = new Point(55.790621, 37.558571);

            MapView mapView;
            MapObjectCollection mapObjects;
            MasstransitRouter mtRouter;
        }
    }
}