package haidang.com.myappff;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Vo on 4/16/2018.
 */

public class AdapterContactAdd extends BaseAdapter {
    private List<Contact1> ListContact;
    private LayoutInflater Inflater;
    private Context context;
    private String userid1;
    private String userid2;
    String urlFriendRq = "https://apptimnhau.000webhostapp.com/insertFriendRq.php";


    public AdapterContactAdd(Context context, List<Contact1> listContact,String id)
    {
        userid1=id;
        ListContact=listContact;
        Inflater= LayoutInflater.from(context);
        this.context=context;
    }

    @Override
    public int getCount(){return ListContact.size();}

    @Override
    public Contact1 getItem(int position){
        return ListContact.get(position);
    }
    @Override
    public long getItemId(int position){
        return position;
    }

    private class ViewHolder{
        TextView tvName;
        TextView tvPhone;
        Button buttonMoi;
        ImageView avatar;
    }

    @Override
    public View getView(final int positon, View convertView, ViewGroup parent)
    {
      final ViewHolder viewHolder;

        if(convertView==null)

        {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView=inflate(R.layout.item_contact,parent,false);
            convertView = inflater.inflate(R.layout.item_contact,null);
            viewHolder.tvName=(TextView) convertView.findViewById(R.id.txtName);
            //     viewHolder.tvPhone=(TextView) convertView.findViewById(R.id.tv_itemphone);
            viewHolder.avatar=(ImageView) convertView.findViewById(R.id.avatar);
            convertView.setTag(viewHolder);
        }else{
            viewHolder =(ViewHolder)convertView.getTag();
        }
        final  Contact1 contact =ListContact.get(positon);
        userid2= contact.getUserid();
        viewHolder.tvName.setText(contact.getName());
        //    viewHolder.tvPhone.setText(contact.getPhoneNumber());
        if(contact.getPhoto()!=null){
            viewHolder.avatar.setImageBitmap(contact.getPhoto());
        }
        else {
            viewHolder.avatar.setImageResource(R.drawable.acount);
        }

        viewHolder.buttonMoi=(Button)convertView.findViewById(R.id.btnAdd);

        final ViewHolder finalViewHolder = viewHolder;
        viewHolder.buttonMoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddFriendRq(urlFriendRq);
               viewHolder.buttonMoi.setEnabled(false);
                viewHolder.buttonMoi.setText("Đã gửi");
            }
        });


        return convertView;
    }
    // Hàm thêm yêu cầu kết bạn vào cơ sở dữ liệu
    private  void AddFriendRq(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
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
                params.put("Userid1",userid1);
                params.put("Userid2",userid2);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}

