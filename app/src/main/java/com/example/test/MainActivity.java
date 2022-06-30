package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.mapkit.directions.driving.DrivingSession;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.PolylineMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private MapView mapview;
    private MapObjectCollection mapObjects;
    private DrivingRouter drivingRouter;
    private DrivingSession drivingSession = null;
    private PlacemarkMapObject NowPoint;
    private DrivingRoute lastRoute;
    private PolylineMapObject lastRoutePolyline;
    private PlacemarkMapObject userGeo;
    private Point mainPoint = new Point(54.7348, 55.957);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        MapKitFactory.setApiKey("c873c3d7-4da8-433b-94ee-664d84fa8aed");
        MapKitFactory.initialize(this);

        setCurrLocation();
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        mapview = (MapView) findViewById(R.id.mapview);
        mapview.getMap().move(
                new CameraPosition(new Point(mainPoint.getLatitude(), mainPoint.getLongitude()), 14.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);
        mapview.getMap().getMapObjects().addPlacemark(new Point(mainPoint.getLatitude(), mainPoint.getLongitude()), ImageProvider.fromResource(this, R.drawable.arrow));


    }

    @Override
    protected void onStop() {
        mapview.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapview.onStart();
    }

    public void setCurrLocation() {
        MyLocationListener.SetUpLocationListener(this);
        mainPoint = new Point(MyLocationListener.userLoc.getLatitude(),
                MyLocationListener.userLoc.getLongitude());

    }

    public void onClickGeo(View view) {
        setCurrLocation();
        mapview.getMap().move(new CameraPosition(mainPoint, 14.0f, 0.0f, 0.0f));
        userGeo.setGeometry(mainPoint);
    }

}