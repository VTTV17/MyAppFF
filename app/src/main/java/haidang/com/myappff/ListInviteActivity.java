package haidang.com.myappff;


import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListInviteActivity extends AppCompatActivity {


    String myurl ="https://apptimnhau.000webhostapp.com/listSDT.php";
    private ImageButton imageButtonBack;
    private NumberPhone numberPhone;
    private List<Contact> listContact;
    private ListView mListView;
    String id;
    Cursor cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_contact);
        Bundle bdLF = getIntent().getExtras();
        if(bdLF!=null){
            id = bdLF.getString("Userid");
            //Toast.makeText(ListFriendActivity.this,id,Toast.LENGTH_LONG).show();
        }
        AnhXa();

        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ListInviteActivity.this,MainActivity.class));
                finish();
            }
        });
        GetListPhone(myurl);

    }
    public void AnhXa()
    {
        mListView = (ListView) findViewById(R.id.list);
        imageButtonBack =(ImageButton)findViewById(R.id.btnBackCT);
    }

    public String GetPhoneNumber(String contact_id,ContentResolver contentResolver)
    {
        String phoneNumber = "";

        int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( ContactsContract.Contacts.HAS_PHONE_NUMBER )));

        if (hasPhoneNumber > 0) {
            //Doc so dien thoai liet ke cung so lien lac
            Cursor phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    new String[] { contact_id }, null);

            while (phoneCursor.moveToNext()) {
                phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            }
            phoneCursor.close();
        }

        return phoneNumber.replaceAll(" ","").replaceAll("-","");
    }
    private Bitmap GetPhotoFromURL(String photourl){
        if(photourl!=null){
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(photourl));
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    //ham lay contact
    public List<Contact> getContacts(List<NumberPhone> listNumberPhone) {

        listContact = new ArrayList<Contact>();
        String photoUrl="";

        ContentResolver contentResolver = getContentResolver();

        cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null,null, null, null);

        // lap lai moi lien lac trong dien thoai
        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                String contact_id = cursor.getString(cursor.getColumnIndex( ContactsContract.Contacts._ID ));
                String name=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                photoUrl = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                Bitmap photo= GetPhotoFromURL(photoUrl);
                String phoneNumber=GetPhoneNumber(contact_id,contentResolver);
                Log.d(name," "+phoneNumber);
                boolean check=true;
                for (int i=0;i<listNumberPhone.size();i++)
                {
                    if (phoneNumber.equals(listNumberPhone.get(i).getPhoneNumber().toString())||phoneNumber.toString().trim()=="") {
                        check=false;
                        break;
                    }
                }
                if(check)
                {
                    listContact.add(new Contact(name,phoneNumber,photo));
                }

            }

        }
        Collections.sort(listContact, new Comparator<Contact>() {
            @Override
            public int compare(Contact lhs, Contact rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });
        return  listContact;

    }

    //lấy số điện thoại
    private void GetListPhone(String url){
        final List<NumberPhone> ListNumberPhone=new ArrayList<NumberPhone>();
        RequestQueue requestQueue = Volley.newRequestQueue(ListInviteActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String  response) {

                for (int i = 0; i < response.length(); i++){
                    try {
                        JSONObject jsonobject = new JSONObject(response);
                        JSONArray jsonarray = jsonobject.getJSONArray("friend");
                        JSONObject object = jsonarray.getJSONObject(i);
                        numberPhone=new NumberPhone(object.get("Phone").toString());
                        ListNumberPhone.add(numberPhone);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                //
                final List<Contact>listContacts=  getContacts(ListNumberPhone);
                mListView.setAdapter(new AdapterContact(ListInviteActivity.this,listContacts));


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ListInviteActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        });
        requestQueue.add(stringRequest);
    }



}
