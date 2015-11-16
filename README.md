# android-triangleloadingview
A loadingview that imitates the loadingview on the alipay app, with six different color triangle moving or rotating along a shanpe of circle, also you can move the triangle by applying distance <br><br>
![](https://github.com/yxping/android-triangleloadingview/raw/master/show1.gif)
![](https://github.com/yxping/android-triangleloadingview/raw/master/show2.gif) <br>
# Usage
1.include the procject 
``` gradle
compile project(':triangleloadingview')
```
2.define the view in layout.xml
``` xml
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:triangle="http://schemas.android.com/apk/com.yxp.triangleloadingview"
    android:orientation="vertical" android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.yxp.triangleloadingview.TriangleLoadingView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_centerInParent="true"
        <!-- if state = normal, the loadingview is not allow to move-->
        triangle:state="normal"/>
</RelativeLayout>
```
3.You can handler the changes of the view by interface, by this you can add other animation
``` java
public interface OnStateListener {
        void onRefreshing();

        void onReady();

        void onUp();

        void onFall(int deltaY);
    }
```

