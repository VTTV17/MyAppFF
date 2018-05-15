package haidang.com.myappff;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FindFriendActivity extends AppCompatActivity {

    String myurl ="https://apptimnhau.000webhostapp.com/findfriend.php";
    String myurl1 ="https://apptimnhau.000webhostapp.com/yeuCauKetBan.php";
    ListView lvuser,listViewUser;
    ArrayList<User> arrayFindUser;
    ArrayList<User> arrayAddUser;
    AdapterFindFriend adapter;
    AdapterAddFriend adapterAddFriend;
    String userid;
    String nauser;
    ImageButton btnBack;
    EditText edtPhone;
    ImageButton btnSearch;
    ProgressBar progressBar;
    TextView tvDanhBa;
    public static final int REQUEST_ID_ACCESS_COURSE_FINE_LOCATION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);
        AnhXa();

        Bundle bdLF = getIntent().getExtras();
        if(bdLF!=null){
            userid = bdLF.getString("Userid");
            nauser = bdLF.getString("NaUser");
        }
        // an hien listview
        listViewUser.setVisibility(View.VISIBLE);
        lvuser.setVisibility(View.GONE);
        //get thong tin user khong phai ban be

        AddFriend(myurl1);

        adapterAddFriend = new AdapterAddFriend(FindFriendActivity.this, R.layout.itemlistfriend,arrayAddUser,userid,nauser);
        listViewUser.setAdapter(adapterAddFriend);
        // set addapter
        adapter = new AdapterFindFriend(FindFriendActivity.this, R.layout.itemlistfriend,arrayFindUser,userid,nauser);
        lvuser.setAdapter(adapter);

        //
        btnBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(FindFriendActivity.this,MainActivity.class));
                finish();

            }
        });
        tvDanhBa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int accessReadContactPermission
                        = ContextCompat.checkSelfPermission(FindFriendActivity.this, android.Manifest.permission.READ_CONTACTS);
                if (accessReadContactPermission != PackageManager.PERMISSION_GRANTED) {

                    // Các quyền cần người dùng cho phép.
                    String[] permissions = new String[]{android.Manifest.permission.READ_CONTACTS};
                    //  , Manifest.permission.READ_CONTACTS,Manifest.permission.SEND_SMS};

                    // Hiển thị một Dialog hỏi người dùng cho phép các quyền trên.
                    ActivityCompat.requestPermissions(FindFriendActivity.this, permissions,
                            REQUEST_ID_ACCESS_COURSE_FINE_LOCATION);
                }
                else {
                    Intent mhuser = new Intent(FindFriendActivity.this, ListContactDirectoryActivity.class);
                    mhuser.putExtra("Userid", userid);
                    startActivity(mhuser);
                    finish();
                }
            }
        });


        btnSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //getting the progressbar

                //making the progressbar visible
                lvuser.setVisibility(View.VISIBLE);
                listViewUser.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                FindFriend(myurl);
            }
        });

    }
    private void AnhXa()
    {
        progressBar = (ProgressBar) findViewById(R.id.progressBarFF);
        listViewUser=(ListView)findViewById(R.id.listAddFriend);
        lvuser = (ListView)findViewById(R.id.listFindFriend);
        edtPhone = (EditText) findViewById(R.id.et_search);
        btnSearch =(ImageButton) findViewById(R.id.btnFindFriend);
        btnBack =(ImageButton) findViewById(R.id.btnBackFF);
        tvDanhBa= (TextView) findViewById(R.id.tvCapnhatDB);
        arrayAddUser = new ArrayList<>();
        arrayFindUser = new ArrayList<>();
    }
    //
    private void AddFriend(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(FindFriendActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String  response) {
                progressBar.setVisibility(View.INVISIBLE);
                arrayAddUser.clear();
                for (int i = 0; i < response.length(); i++){
                    try {
                        JSONObject jsonobject = new JSONObject(response);
                        JSONArray jsonarray = jsonobject.getJSONArray("userresponse");
                        JSONObject object = jsonarray.getJSONObject(i);
                        arrayAddUser.add(new User(
                                object.getString("Id"),
                                object.getString("Name"),
                                object.getString("BirthDay"),
                                object.getString("Email"),
                                object.getString("Phone"),
                                object.getInt("Status"),
                                object.getString("Photo")

                        ));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapterAddFriend.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FindFriendActivity.this, "Không tìm thấy", Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                //m truyền cái id của cái user ma vào đay. để nguyên là "" id
                params.put("Phone",userid);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
//

    private void FindFriend(String url  ){
        RequestQueue requestQueue = Volley.newRequestQueue(FindFriendActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String  response) {
                progressBar.setVisibility(View.INVISIBLE);
                arrayFindUser.clear();
                for (int i = 0; i < response.length(); i++){
                    try {
                        JSONObject jsonobject = new JSONObject(response);
                        JSONArray jsonarray = jsonobject.getJSONArray("user");
                        JSONObject object = jsonarray.getJSONObject(i);
                        arrayFindUser.add(new User(
                                object.getString("Id"),
                                object.getString("Name"),
                                object.getString("BirthDay"),
                                object.getString("Email"),
                                object.getString("Phone"),
                                object.getInt("Status"),
                                object.getString("Photo")

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
                Toast.makeText(FindFriendActivity.this, "Không tìm thấy", Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Phone",edtPhone.getText().toString().trim());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}
