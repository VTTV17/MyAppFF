package haidang.com.myappff;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LocationListener, OnMapReadyCallback, DirectionFinderListener {


    public static String idfb;
    public static String namefb,nameB;
    public String urlphoto;
    public String Lati;
    public String Longi;
    public String LatiF;
    public String LongiF;

    String urlUpdateLocation = "https://apptimnhau.000webhostapp.com/updateLocation1.php";
    String urlInsertLocation = "https://apptimnhau.000webhostapp.com/insertLocation.php";
    String urlTestPhone = "https://apptimnhau.000webhostapp.com/testPhone.php";
    String urlGetLocation = "https://apptimnhau.000webhostapp.com/getLocations.php";
    String urlSaveLocation = "https://apptimnhau.000webhostapp.com/insertSaveLocation1.php";
    String urlDeleteLocation="https://apptimnhau.000webhostapp.com/deleteLocation.php";
    // Of spinner
    String myurl = "https://apptimnhau.000webhostapp.com/getspinner.php";
    Spinner spinnerLocation;
    ArrayList<ShareLoaction> arrayUser;
    ArrayList<Locations> arrayLoctions;
    AdapterSpinner adapter;

    TextView txtUserName,textViewLoaction_Friend,textViewidfb,textViewnameB;
    CircleImageView imgUser;
    private GoogleMap myMap;
    private NavigationView navigationView;
    private static final String MYTAG = "MYTAG";
    // Mã yêu cầu uhỏi người dùng cho phép xem vị trí hiện tại của họ (***).
    // Giá trị mã 8bit (value < 256).
    public static final int REQUEST_ID_ACCESS_COURSE_FINE_LOCATION = 100;
    //Vẽ đường đi
    private ImageButton btnFindPath;
    private ImageButton imgvReload;
    private ImageButton imgSave;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private GoogleApiClient gac;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        //chỗ này phải từ bên spinner adapter chuyển qua hk....uk
//        Bundle bd2 = getIntent().getExtras();
//        if (bd2 != null) {
//            LatiF = bd2.getString("Lati");
//            LongiF = bd2.getString("Longi");
//            //để đó lấy qua luôn..tại thấy dòng duois cũng có lấy qua nè
//            idfb = bd2.getString("Userid");//3 cái này cần hk?
//            namefb = bd2.getString("Name");
//            nameB=bd2.getString("NameB");
//            //Toast.makeText(MainActivity.this, LatiF + " : " + LongiF, Toast.LENGTH_LONG).show();
//        }//
//        idfb=textViewidfb.getText().toString();
//        nameB=textViewnameB.getText().toString();
        Bundle bd1 = getIntent().getExtras();
        if (bd1 != null) {
            idfb = bd1.getString("Userid");
            namefb = bd1.getString("Name");
            nameB=bd1.getString("NameB");
        }
        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        AnhXa();

        txtUserName.setText(namefb);
        urlphoto = "https://graph.facebook.com/" + idfb + "/picture?type=large";
        Picasso.with(this)
                .load(urlphoto)
                .placeholder(R.drawable.loading)
                .into(imgUser);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentMap);

        // Sét đặt sự kiện thời điểm GoogleMap đã sẵn sàng.
        mapFragment.getMapAsync(this);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Spinner

        arrayUser = new ArrayList<>();
        adapter = new AdapterSpinner(MainActivity.this, R.layout.itemspinner, arrayUser, namefb);
        
        GetListLocationShare(myurl);
        TestPhone(urlTestPhone);
        spinnerLocation.setAdapter(adapter);
        spinnerLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String location=arrayUser.get(position).getLati().toString()+"," +arrayUser.get(position).getLongi().toString();
                textViewLoaction_Friend.setText(location.toString());
                textViewidfb.setText(arrayUser.get(position).getUserid2().toString());
                textViewnameB.setText(arrayUser.get(position).getNameu1().toString());
               }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //
        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest(textViewLoaction_Friend.getText().toString());
//                getLocation();// gắn đt vào chạy. bấm nút chỉ đường á
//                Toast.makeText(MainActivity.this,Lati+"......"+Longi,Toast.LENGTH_LONG).show();

            }
        });
        //Load lại spinner
        imgvReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetListLocationShare(myurl);

            }
        });


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_infor) {
            Intent mhuser = new Intent(MainActivity.this, InfUserActivity.class);
            mhuser.putExtra("Userid", idfb);
            startActivity(mhuser);
        } else if (id == R.id.nav_friend) {
            Intent mh = new Intent(MainActivity.this, ListFriendActivity.class);
            mh.putExtra("Userid", idfb);
            startActivity(mh);
        } else if (id == R.id.nav_friendrq) {
            Intent mhm = new Intent(MainActivity.this, ListFriendRqActivity.class);
            mhm.putExtra("Userid", idfb);
            startActivity(mhm);

        } else if (id == R.id.nav_find_friend) {
            Intent mhm1 = new Intent(MainActivity.this, FindFriendActivity.class);
            mhm1.putExtra("Userid", idfb);
            mhm1.putExtra("NaUser", namefb);
            startActivity(mhm1);

        } else if (id == R.id.nav_locationrq) {
            Intent mhm2 = new Intent(MainActivity.this, ListLocationRqActivity.class);
            mhm2.putExtra("Userid", idfb);
            startActivity(mhm2);

        } else if(id==R.id.nav_logout){
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            finish();
        }
        else if(id==R.id.nav_savelocaton){
            SaveLocation(urlSaveLocation);
            Toast.makeText(MainActivity.this,"Lưu vị trí thành công.",Toast.LENGTH_LONG).show();
            startActivity(new Intent(MainActivity.this,MainActivity.class));
            finish();
        }
         else if(id==R.id.nav_invite) {
            //hỏi quyền truy cập

            int accessReadContactPermission
                    = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
            if (accessReadContactPermission != PackageManager.PERMISSION_GRANTED) {

                // Các quyền cần người dùng cho phép.
                String[] permissions = new String[]{Manifest.permission.READ_CONTACTS};
                //  , Manifest.permission.READ_CONTACTS,Manifest.permission.SEND_SMS};

                // Hiển thị một Dialog hỏi người dùng cho phép các quyền trên.
                ActivityCompat.requestPermissions(this, permissions,
                        REQUEST_ID_ACCESS_COURSE_FINE_LOCATION);
            }
            else
                {
                Intent mhm2 = new Intent(MainActivity.this, ListInviteActivity.class);

                mhm2.putExtra("Userid", idfb);
                startActivity(mhm2);
            }

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
//    private void getLocation() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
//        } else {
//            location = LocationServices.FusedLocationApi.getLastLocation(gac);
//
//            if (location != null) {
//                double latitude = location.getLatitude();
//                double longitude = location.getLongitude();
//                // Hiển thị
//                Toast.makeText(this,latitude + ", " + longitude,Toast.LENGTH_LONG).show();
//                Log.d("lati" ,latitude+"" );
//                Log.d("longt" ,longitude+"" );
//            } else {
//                Toast.makeText(this,"(Không thể hiển thị vị trí. " +
//                        "Bạn đã kích hoạt location trên thiết bị chưa?)",Toast.LENGTH_LONG).show();
//            }
//        }
//    }
    public void AnhXa() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        txtUserName = (TextView) header.findViewById(R.id.tv_name);
        imgUser = (CircleImageView) header.findViewById(R.id.imgUser);

        textViewidfb = (TextView) findViewById(R.id.tv_idfb);
        textViewnameB = (TextView) findViewById(R.id.tv_namefb);
        textViewLoaction_Friend = (TextView)findViewById(R.id.tv_locationfriend);
        spinnerLocation = (Spinner) findViewById(R.id.SpinnerShareLocation);
        btnFindPath = (ImageButton) findViewById(R.id.btnFindPath);
        imgvReload=(ImageButton)findViewById(R.id.imgvReload);

    }



    public class PicassoMarker implements Target {
        Marker mMarker;

        PicassoMarker(Marker marker) {
            mMarker = marker;
        }

        @Override
        public int hashCode() {
            return mMarker.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof PicassoMarker) {
                Marker marker = ((PicassoMarker) o).mMarker;
                return mMarker.equals(marker);
            } else {
                return false;
            }
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            mMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {


    }
    @Override
    public void onMyMapReady(GoogleMap googleMap) {

        // Lấy đối tượng Google Map ra:
        myMap = googleMap;

        // Thiết lập sự kiện đã tải Map thành công
        myMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {

            @Override
            public void onMapLoaded() {

                // Đã tải thành công thì tắt Dialog Progress đi
                // myProgress.dismiss();

                // Hiển thị vị trí người dùng.
                askPermissionsAndShowMyLocation();
            }
        });
        myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        myMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        myMap.setMyLocationEnabled(true);
    }

    private void askPermissionsAndShowMyLocation() {


        // Với API >= 23, bạn phải hỏi người dùng cho phép xem vị trí của họ.
        if (Build.VERSION.SDK_INT >= 23) {
            int accessCoarsePermission
                    = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            int accessFinePermission
                    = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            int accessSMSPermission
                    = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
            int accessReadContactPermission
                    = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);


            if (accessCoarsePermission != PackageManager.PERMISSION_GRANTED
                    || accessFinePermission != PackageManager.PERMISSION_GRANTED
                    || accessSMSPermission != PackageManager.PERMISSION_GRANTED
                    || accessReadContactPermission != PackageManager.PERMISSION_GRANTED)
            {

                // Các quyền cần người dùng cho phép.
                String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                        , Manifest.permission.READ_CONTACTS,Manifest.permission.SEND_SMS};

                // Hiển thị một Dialog hỏi người dùng cho phép các quyền trên.
                ActivityCompat.requestPermissions(this, permissions,
                        REQUEST_ID_ACCESS_COURSE_FINE_LOCATION);

                return;
            }
        }

        // Hiển thị vị trí hiện thời trên bản đồ.
        this.showMyLocation();
    }

    // Khi người dùng trả lời yêu cầu cấp quyền (cho phép hoặc từ chối).
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        switch (requestCode) {
            case REQUEST_ID_ACCESS_COURSE_FINE_LOCATION: {


                // Chú ý: Nếu yêu cầu bị bỏ qua, mảng kết quả là rỗng.
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED
                        ) {

                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_LONG).show();

                    // Hiển thị vị trí hiện thời trên bản đồ.
                    this.showMyLocation();
                }
                // Hủy bỏ hoặc từ chối.
                else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    // Tìm một nhà cung cấp vị trị hiện thời đang được mở.
    private String getEnabledLocationProvider() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        // Tiêu chí để tìm một nhà cung cấp vị trí.
        Criteria criteria = new Criteria();

        // Tìm một nhà cung vị trí hiện thời tốt nhất theo tiêu chí trên.
        // ==> "gps", "network",...
        String bestProvider = locationManager.getBestProvider(criteria, true);

        boolean enabled = locationManager.isProviderEnabled(bestProvider);

        if (!enabled) {
            Toast.makeText(this, "No location provider enabled!", Toast.LENGTH_LONG).show();
            Log.i(MYTAG, "No location provider enabled!");
            return null;
        }
        return bestProvider;
    }

    // Chỉ gọi phương thức này khi đã có quyền xem vị trí người dùng.
    private void showMyLocation() {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        String locationProvider = this.getEnabledLocationProvider();

        if (locationProvider == null) {
            return;
        }

        // Millisecond
        final long MIN_TIME_BW_UPDATES = 1000;
        // Met
        final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;

        Location myLocation = null;

        try {


            // Đoạn code nay cần người dùng cho phép (Hỏi ở trên ***).
            locationManager.requestLocationUpdates(
                    locationProvider,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);

            // Lấy ra vị trí.
            myLocation = locationManager
                    .getLastKnownLocation(locationProvider);
        }
        // Với Android API >= 23 phải catch SecurityException.
        catch (SecurityException e) {
            Toast.makeText(this, "Show My Location Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(MYTAG, "Show My Location Error:" + e.getMessage());
            e.printStackTrace();
            return;
        }

        if (myLocation != null) {
            Lati = String.valueOf(myLocation.getLatitude());
            Longi = String.valueOf(myLocation.getLongitude());
            AddLocation(urlInsertLocation);
            UpdateLocation(urlUpdateLocation);

            LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)             // Sets the center of the map to location user
                    .zoom(15)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            myMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            MarkerOptions marker = new MarkerOptions();
            marker.title(namefb);
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
            marker.position(latLng);
            Marker currentMarker = myMap.addMarker(marker);
            currentMarker.showInfoWindow();
            GetLocation(urlGetLocation);
          //  Toast.makeText(this,Lati+"......"+Longi,Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Location not found!", Toast.LENGTH_LONG).show();
            Log.i(MYTAG, "Location not found");
        }

    }
    private void GetLocation(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String  response) {
                for (int i = 0; i < response.length(); i++){
                    try {
                        JSONObject jsonobject = new JSONObject(response);
                        JSONArray jsonarray = jsonobject.getJSONArray("userresponse");
                        JSONObject object = jsonarray.getJSONObject(i);
//                        arrayLoctions.add(new Locations(
//                             object.getString("Id");
                        Double a=   Double.parseDouble(object.getString("Lati")) ;
                            Double b=  Double.parseDouble(object.getString("Longi"));

//                        ));
                        MarkerOptions marker = new MarkerOptions();
//                        marker.title(namefb);
                        LatLng latLng= new  LatLng(a,b);
                        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
                        marker.position(latLng);
                        Marker currentMarker = myMap.addMarker(marker);
                        currentMarker.showInfoWindow();
                        myMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                marker.setVisible(false);
                                LatLng latLng= marker.getPosition();
                                Double lati=latLng.latitude;
                                Double longi=latLng.longitude;
                                final String location= lati.toString()+","+ longi.toString();
                               AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("Xác nhận");
                                builder.setMessage("Bạn muốn xóa vị trí hay vẽ đường đến vị trí?");
                                builder.setCancelable(false);
                                builder.setNeutralButton("Xóa vị trí", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        ShowDialogDeleteLocation();
                                    }
                                });
                                builder.setNegativeButton("Vẽ đường", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        sendRequest(location);

                                    }
                                });
                                builder.setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();

                                return false;
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("userid",idfb);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
    //
    private void ShowDialogDeleteLocation(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Bạn muốn xóa vị trí?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DeleteLocation(urlDeleteLocation);
                        Toast.makeText(MainActivity.this,"Đã xóa!",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this,MainActivity.class));
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
    /// Thêm tọa độ người dùng lúc đăng nhập vào bảng Locaiton
    private void AddLocation(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equals("success")) {
                            //Toast.makeText(MainActivity.this,"success",Toast.LENGTH_LONG).show();
                        } else {
                            //Toast.makeText(MainActivity.this,"Error!",Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(MainActivity.this,"Error!!!",Toast.LENGTH_LONG).show();
                        Log.d("AAA", "Error:\n" + error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Userid", idfb);
                params.put("Lati", Lati);
                params.put("Longi", Longi);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
    // Save vị trí
    private void SaveLocation(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equals("success")) {
                          //  Toast.makeText(MainActivity.this,"Lưu vị trí thành công.",Toast.LENGTH_LONG).show();
                        } else {
                          //  Toast.makeText(MainActivity.this,"Error!",Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(MainActivity.this,"Error!!!",Toast.LENGTH_LONG).show();
                        Log.d("AAA", "Error:\n" + error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Userid", idfb);
                params.put("Lati", Lati);
                params.put("Longi", Longi);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
    // Delete vị trí đã lưu
    private  void DeleteLocation(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.trim().equals("succ " +
                                "ess")){
                            //Toast.makeText(MainActivity.this,"success",Toast.LENGTH_LONG).show();
                        }else
                        {
                            //Toast.makeText(MainActivity.this,"Error!",Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(MainActivity.this,"Error!!!",Toast.LENGTH_LONG).show();
                        Log.d("AAA","Error:\n" +error.toString());
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Userid", idfb);
                params.put("Lati", Lati);
                params.put("Longi", Longi);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
    // Hàm cập nhật tọa độ
    private void UpdateLocation(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equals("success")) {
                            //Toast.makeText(MainActivity.this, "Cập nhật tọa độ...", Toast.LENGTH_SHORT).show();
                        } else {
                            //Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(MainActivity.this, "Error!!!", Toast.LENGTH_LONG).show();
                        Log.d("AAA", "Error:\n" + error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Userid", idfb);
                params.put("Lati", Lati);
                params.put("Longi", Longi);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }


//Test phone null
    private void TestPhone(String url) {// cái này t mới thêm vô...để xét sdt có bị null thì kêu nó cập nhật.chay duoc chua..đc
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equals("yes")) {
                            AlertDialog.Builder myAlert= new AlertDialog.Builder(MainActivity.this);
                            myAlert.setMessage("Cập nhật số điện thoại để mọi người có thể tìm thấy bạn.").create();
                            myAlert.show();

                        } else {
                            //Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(MainActivity.this, "Error!!!", Toast.LENGTH_LONG).show();
                        Log.d("AAA", "Error:\n" + error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Userid", idfb);

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
    // ham get item of spinner
    private void GetListLocationShare(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                arrayUser.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonobject = new JSONObject(response);
                        JSONArray jsonarray = jsonobject.getJSONArray("friend");
                        JSONObject object = jsonarray.getJSONObject(i);
                        arrayUser.add(new ShareLoaction(
                                object.getString("Id"),
                                object.getString("Name"),
                                object.getString("Userid1"),
                                object.getString("Lati"),
                                object.getString("Longi")

                        ));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", idfb);
                params.put("Status", "2");
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void sendRequest(String destination) {
        String origin = Lati + "," + Longi;// cái này là cái location của mình hã..uk
   //     String destination = textViewLoaction_Friend.getText().toString();//cái này của đứa kia

        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Lấy đối tượng Google Map ra:
        myMap = googleMap;

        // Thiết lập sự kiện đã tải Map thành công
        myMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {

            @Override
            public void onMapLoaded() {

                // Đã tải thành công thì tắt Dialog Progress đi
                // myProgress.dismiss();

                // Hiển thị vị trí người dùng.
                askPermissionsAndShowMyLocation();
            }
        });
        myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        myMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        myMap.setMyLocationEnabled(true);
    }

    /////


    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();


        for (Route route : routes) {
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            originMarkers.add(myMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(namefb+": "+ route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(myMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(textViewnameB.getText().toString() +  ": "+ route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(15);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(myMap.addPolyline(polylineOptions));
        }
    }

}
