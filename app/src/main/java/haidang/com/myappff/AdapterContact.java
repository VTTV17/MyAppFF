package haidang.com.myappff;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by MinhTuy on 1/20/2018.
 */

public class AdapterContact extends BaseAdapter {
    private List<Contact> ListContact;
    private LayoutInflater Inflater;
    private Context context;
    public static final int REQUEST_ID_ACCESS_COURSE_FINE_LOCATION = 100;

    public AdapterContact(Context context, List<Contact> listContact)
    {

        ListContact=listContact;
        Inflater= LayoutInflater.from(context);
        this.context=context;
    }

    @Override
    public int getCount(){return ListContact.size();}

    @Override
    public Contact getItem(int position){
        return ListContact.get(position);
    }
    @Override
    public long getItemId(int position){
        return position;
    }

    static class ViewHolder{
        TextView tvName;
        TextView tvPhone;
        Button buttonMoi;
        ImageView avatar;
    }

    @Override
    public View getView(final int positon, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder=new ViewHolder();

        if(convertView==null)
        {
            convertView=Inflater.inflate(R.layout.item_contact1,parent,false);

            viewHolder.tvName=(TextView) convertView.findViewById(R.id.txtName);
            //     viewHolder.tvPhone=(TextView) convertView.findViewById(R.id.tv_itemphone);
             viewHolder.avatar=(ImageView) convertView.findViewById(R.id.avatar);
            convertView.setTag(viewHolder);
        }else{
            viewHolder =(ViewHolder)convertView.getTag();
        }
        final  Contact contact =ListContact.get(positon);
        viewHolder.tvName.setText(contact.getName());
        //    viewHolder.tvPhone.setText(contact.getPhoneNumber());
        if(contact.getPhoto()!=null){
            viewHolder.avatar.setImageBitmap(contact.getPhoto());
        }
        else {
            viewHolder.avatar.setImageResource(R.drawable.acount);
        }

        viewHolder.buttonMoi=(Button)convertView.findViewById(R.id.btnMoi);

        final ViewHolder finalViewHolder = viewHolder;
        viewHolder.buttonMoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finalViewHolder.buttonMoi.setVisibility(view.INVISIBLE);


                int accessSMSPermission
                        = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS);
                if (accessSMSPermission != PackageManager.PERMISSION_GRANTED) {

                    // Các quyền cần người dùng cho phép.
                    String[] permissions = new String[]{Manifest.permission.SEND_SMS};
                    // Hiển thị một Dialog hỏi người dùng cho phép các quyền trên.
                    ActivityCompat.requestPermissions((Activity) context, permissions,
                            REQUEST_ID_ACCESS_COURSE_FINE_LOCATION);
                }
                else {
                    SmsManager sms = SmsManager.getDefault();
                    sms.sendTextMessage(contact.getPhoneNumber(), null, "gửi tn nè", null, null);
                }
            }
        });


        return convertView;
    }
}
