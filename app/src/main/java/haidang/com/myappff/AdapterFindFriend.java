package haidang.com.myappff;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by HaiDang_PC on 10/27/2017.
 */

public class AdapterFindFriend extends BaseAdapter {
    private Context context;
    private  int layout;
    Bitmap bitmap_pic;
    String userid1 ;
    String userid2;
    String nameu1;
    String nameu2;
    String test = "1";
    String urlFriendRq = "https://apptimnhau.000webhostapp.com/insertFriendRq.php";
    String urlTestSendedRQ = "https://apptimnhau.000webhostapp.com/testSendRQ.php";


    public AdapterFindFriend(Context context, int layout, List<User> userlist, String userid1, String nameu1) {
        this.context = context;
        this.layout = layout;
        this.userlist = userlist;
        this.userid1 = userid1;
        this.nameu1 = nameu1;
    }

    private List<User> userlist;
    @Override
    public int getCount() {
        return userlist.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
    private class ViewHolder{
        TextView txtName;
        Button addFriendRq;
        CircleImageView imgPic;

    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final AdapterFindFriend.ViewHolder holder;
        if (view == null) {
            holder = new AdapterFindFriend.ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout, null);
            holder.txtName = (TextView) view.findViewById(R.id.txtNameLF);
            holder.imgPic = (CircleImageView) view.findViewById(R.id.imgPicLF);
            holder.addFriendRq = (Button) view.findViewById(R.id.btnKetBan);
            view.setTag(holder);
        } else {
            holder = (AdapterFindFriend.ViewHolder) view.getTag();
        }
        final User user = userlist.get(i);
        userid2 = user.getId();
        holder.txtName.setText(user.getName());
        String urlpic = "https://graph.facebook.com/" + user.getId() + "/picture?type=large";
        Picasso.with(context)
                .load(urlpic)
                .placeholder(R.drawable.loading)
                .into(holder.imgPic);


        //Check đã gửi lời mời chưa
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlTestSendedRQ, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.trim().equals("no")) {
                    holder.addFriendRq.setEnabled(false);
                    holder.addFriendRq.setText("Đã gửi");
                } else {
                    if (response.trim().equals("friend")){
                        holder.addFriendRq.setEnabled(false);
                        holder.addFriendRq.setText("Bạn bè");
                    }
                    else {
                        if (response.trim().equals("yours")){
                            holder.addFriendRq.setEnabled(false);
                            holder.addFriendRq.setText("....");
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("AAA2", "Error:\n" + error.toString());

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Userid1", userid1);
                params.put("Userid2", userid2);
                return params;
            }
        };
        requestQueue.add(stringRequest);

        holder.addFriendRq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userid2 = user.getId();
                nameu2 = user.getName();
                AddFriendRq(urlFriendRq);
                holder.addFriendRq.setEnabled(false);
                holder.addFriendRq.setText("Đã gửi");
                sendNotification(userid2);
            }
        });
        return view;
    }



// Hàm thêm yêu cầu kết bạn vào cơ sở dữ liệu
    private  void AddFriendRq(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.trim().equals("success")){
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
                        Log.d("AAA1","Error:\n" +error.toString());
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Userid1",userid1);
                params.put("Userid2",userid2);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
    private void sendNotification(final String userIdFriend)
    {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);


                    //This is a Simple Logic to Send Notification different Device Programmatically....


                    try {
                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic NTZlOTJjZDAtMzAxOS00NTI2LTg4MzctZDBlMWUzMjY5MTEx");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                + "\"app_id\": \"74e1a00d-7240-4836-98f9-990b8f5d30fa\","

                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_ID\", \"relation\": \"=\", \"value\": \"" + userIdFriend + "\"}],"

                                + "\"data\": {\"foo\": \"bar\"},"
                                + "\"contents\": {\"en\": \"Bạn có yêu cầu kết bạn.\"}"
                                + "}";


                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);
                        // Toast.makeText(InfFriendActivity.this, "Đã gửi yêu cầu", Toast.LENGTH_LONG).show();
                        if (httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        } else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }

            }

        });
    }
}
