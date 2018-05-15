package haidang.com.myappff;

import com.google.android.gms.maps.GoogleMap;

import java.util.List;

/**
 * Created by Mai Thanh Hiep on 4/3/2016.
 */
public interface DirectionFinderListener {
    void onMyMapReady(GoogleMap googleMap);

    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
