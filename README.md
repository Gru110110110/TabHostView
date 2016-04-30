# TabHostView
a simple TabHostView for android
#usage
##1、created by xml like:
```java
 <com.seek.tabhostview.TabHostView
        android:id="@+id/tab"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textSize="10sp"
        android:textColor="@color/tab_text_color"
        seek:dividerEnabled="true"
        seek:dividerWidth="1dp"
        seek:topLineHeigh="1dp">
    </com.seek.tabhostview.TabHostView>
```
##2、then:
```java
tabHostView.addFragments(R.id.container,fragments,this);
```
![](http://img.my.csdn.net/uploads/201604/30/1461997808_1287.png)
