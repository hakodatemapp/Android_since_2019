package ac.fun.hakodatemapplus;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.ClusterItem;

///
//
// 以下のコードは以下の公式ドキュメントに書いてあるコードをコピペしてちょっとだけ改変したものである
// https://developers.google.com/maps/documentation/android-sdk/utility/marker-clustering
//
///

public class SegmentClusterItem implements ClusterItem {
    private final LatLng mPosition;
    private final String mTitle;
    private final String mSnippet;

    public SegmentClusterItem(double lat, double lng, String title, String snippet) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }
}

///
// コピペおわり
///