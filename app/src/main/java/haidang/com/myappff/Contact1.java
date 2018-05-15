package haidang.com.myappff;

import android.graphics.Bitmap;

/**
 * Created by Vo on 4/16/2018.
 */

public class Contact1 {
    private String userid;
    private String Name;
    private String PhoneNumber;
    private Bitmap Avartar;

    public Contact1(String id,String name, String phoneNumber, Bitmap avartar)
    {
        userid=id;
        Name=name;
        PhoneNumber=phoneNumber;
        Avartar=avartar;
    }
    public String getUserid(){return  userid;}
    public String getName(){
        return Name;
    }
    public String getPhoneNumber(){
        return PhoneNumber;
    }
    public Bitmap getPhoto(){
        return Avartar;
    }


//    public  Bitmap  getAvatar(){
//        return Avartar;
//    }
}