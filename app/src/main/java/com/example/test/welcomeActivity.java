/*package com.example.test;

import com.example.test.MyLocationListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.PermissionRequest;
import android.widget.Toast;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.directions.DirectionsFactory;
import com.yandex.mapkit.directions.driving.DrivingOptions;
import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.mapkit.directions.driving.DrivingSession;
import com.yandex.mapkit.directions.driving.VehicleOptions;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polyline;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import com.yandex.mapkit.location.LocationStatus;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.geometry.Circle;
import com.yandex.mapkit.map.CircleMapObject;
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.PolylineMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.map.MapObject;
import com.yandex.runtime.Error;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements DrivingSession.DrivingRouteListener {
    private final String API_KEY= "38bd5354-dafa-4a7e-80e0-fc098ccd3d8a";
    private final Point ROUTE_START_LOCATION = new Point(59.959194, 30.407094);
    private final Point ROUTE_END_LOCATION = new Point(59.93, 30.38);

    private Point centerPoint = new Point(59.93, 30.38);
    private MapView mapView;
    private MapObjectCollection mapObjects;
    private DrivingRouter drivingRouter;
    private DrivingSession drivingSession = null;
    private PlacemarkMapObject nowPoint;
    private PlacemarkMapObject userGeo;
    private DrivingRoute lastRoute;
    private PolylineMapObject lastRoutePolyline;
    private boolean setStartPostition = false;


    private InputListener inputListener = new InputListener() {
        @Override
        public void onMapTap(@NonNull Map map, @NonNull Point point) {
            if (setStartPostition) {
                centerPoint = point;
                userGeo.setGeometry(point);
                mapView.getMap().move(new CameraPosition(centerPoint, 14.0f, 0.0f, 0.0f));
                setStartPostition = false;
            }
            else {
                if (nowPoint == null) {
                    nowPoint = mapObjects.addPlacemark(point);
                } else {
                    nowPoint.setGeometry(point);
                }
                submitRequest(point);
            }
        }

        @Override
        public void onMapLongTap(@NonNull Map map, @NonNull Point point) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        MapKitFactory.setApiKey(API_KEY);
        MapKitFactory.initialize(this);
        DirectionsFactory.initialize(this);

        setGeo();

        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        mapView = (MapView)findViewById(R.id.mapview);
        mapView.getMap().move(
                new CameraPosition(centerPoint, 14.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);
        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter();
        mapObjects = mapView.getMap().getMapObjects().addCollection();
        userGeo = mapObjects.addPlacemark(centerPoint);
        mapView.getMap().addInputListener(inputListener);
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    public void setGeo() {
        MyLocationListener.SetUpLocationListener(this);
        centerPoint = new Point(MyLocationListener.imHere.getLatitude(),
                MyLocationListener.imHere.getLongitude());
    }

    public void onClickGeo(View view) {
        setGeo();
        mapView.getMap().move(new CameraPosition(centerPoint, 14.0f, 0.0f, 0.0f));
        userGeo.setGeometry(centerPoint);
    }

    public void onClickStartGeo (View view) {
        setStartPostition = true;
    }

    @Override
    public void onDrivingRoutes(List<DrivingRoute> routes) {
        if(lastRoutePolyline != null){
            mapObjects.remove(lastRoutePolyline);
        }
        lastRoutePolyline = mapObjects.addPolyline(routes.get(0).getGeometry());
        lastRoute = routes.get(0);
        Toast.makeText(getApplicationContext(), "Coast: " + lastRoute.getMetadata().getWeight().getDistance().getValue(),
                Toast.LENGTH_LONG).show();
        //for (DrivingRoute route : routes) {
        //    mapObjects.addPolyline(route.getGeometry());
        //}
    }

    @Override
    public void onDrivingRoutesError(Error error) {
        String errorMessage = getString(R.string.unknown_error_message);
        if (error instanceof RemoteError) {
            errorMessage = getString(R.string.remote_error_message);
        } else if (error instanceof NetworkError) {
            errorMessage = getString(R.string.network_error_message);
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void submitRequest(Point routeEndPoint) {
        DrivingOptions drivingOptions = new DrivingOptions();
        VehicleOptions vehicleOptions = new VehicleOptions();
        ArrayList<RequestPoint> requestPoints = new ArrayList<>();
        requestPoints.add(new RequestPoint(
                centerPoint,
                RequestPointType.WAYPOINT,
                null));
        requestPoints.add(new RequestPoint(
                routeEndPoint,
                RequestPointType.WAYPOINT,
                null));
        if (drivingSession != null) {
            drivingSession.cancel();
        }
        drivingSession = drivingRouter.requestRoutes(requestPoints, drivingOptions, vehicleOptions, this);
    }
}*/
package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.util.List;

public class welcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(3000);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    PermissionListener permissionlistener = new PermissionListener() {
                        @Override
                        public void onPermissionGranted() {
                            Toast.makeText(welcomeActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPermissionDenied(List<String> deniedPermissions) {
                            Toast.makeText(welcomeActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                        }


                    };
                        TedPermission.create()
                            .setPermissionListener(permissionlistener)
                            .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                            .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                            .check();
                    Intent welcomeIntent = new Intent(welcomeActivity.this, MainActivity.class);
                    startActivity(welcomeIntent);
                }
            }
        };
        thread.start();
    }
}