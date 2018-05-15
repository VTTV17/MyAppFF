package haidang.com.myappff;


import android.graphics.Bitmap;

/**
 * Created by MinhTuy on 1/20/2018.
 */

public class Contact {
    private String Name;
    private String PhoneNumber;
    private Bitmap Avartar;

    public Contact(String name, String phoneNumber, Bitmap avartar)
    {
        Name=name;
        PhoneNumber=phoneNumber;
        Avartar=avartar;
    }
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