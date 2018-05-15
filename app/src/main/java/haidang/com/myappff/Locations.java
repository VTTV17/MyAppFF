package haidang.com.myappff;

/**
 * Created by Vo on 5/2/2018.
 */

public class Locations {
    String userid;
    String latLng1;
    String latLng2;
    String latLng3;
    String latLng4;
    String latLng5;
    public Locations(String id, String LatLng1, String LatLng2, String LatLng3, String LatLng4, String LatLng5){
        userid= id;
        latLng1= LatLng1;
        latLng2=LatLng2;
        latLng3=LatLng3;
        latLng4=LatLng4;
        latLng5=LatLng5;
    }
    public String getUserid(){return userid;}
    public void setUserid(String id){this.userid=id;}
    public String getLatLng1(){return latLng1;}
    public String getLatLng2(){return latLng2;}
    public String getLatLng3(){return latLng3;}
    public String getLatLng4(){return latLng4;}
    public String getLatLng5(){return latLng5;}
    public void setLatLng1(String latLng1){this.latLng1=latLng1;}
    public void setLatLng2(String latLng2){this.latLng1=latLng2;}
    public void setLatLng3(String latLng3){this.latLng1=latLng3;}
    public void setLatLng4(String latLng4){this.latLng1=latLng4;}
    public void setLatLng5(String latLng5){this.latLng1=latLng5;}




}
