package haidang.com.myappff;

/**
 * Created by Vo on 4/16/2018.
 */

public class Phone{
    private  String userId;
    private String PhoneNumber;

    public Phone(String id, String phoneNumber)
    {
        userId  = id;
        PhoneNumber = phoneNumber;
    }


    public String getPhoneNumber() {
        return PhoneNumber;
    }
    public String getUserId(){
        return userId;
    }
}