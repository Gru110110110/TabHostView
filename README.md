# TabHostView
a simple TabHostView for android
![](https://github.com/pruas/TabHostView/blob/master/app/src/main/java/example.png)
#usage
##1、created by xml like:
```xml
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
### activity_main.xml :
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    xmlns:seek="http://schemas.android.com/apk/res-auto"
    android:orientation="vertical">
    <FrameLayout
        android:id="@+id/container"
        android:layout_weight="1"
        android:layout_width="match_parent"
        android:layout_height="0dp"></FrameLayout>
    <com.seek.tabhostview.TabHostView
        android:id="@+id/tab"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textSize="10sp"
        android:textColor="@color/tab_text_color"
       >
    </com.seek.tabhostview.TabHostView>

</LinearLayout>
```

##2、then:
```java
tabHostView.addFragments(R.id.container,fragments,this);
```
### MainActivity.java :
```java
public class MainActivity extends AppCompatActivity implements TabHostView.TabDataProvider {

    private TabHostView tabHostView;

    private int[] resId = new int[]{R.drawable.tab_main,R.drawable.tab_dis,R.drawable.tab_me};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabHostView = (TabHostView) findViewById(R.id.tab);
        List<Fragment> fragments = new ArrayList<>(3);
        for (int i=0;i<3;i++){
            fragments.add(TestFragment.newInstance("hi,i am fragment "+i));
        }
        tabHostView.addFragments(R.id.container,fragments,this);
        tabHostView.setTabTextSize(14);
    }

    @Override
    public int getTabIconDrawable(int position) {
        return resId[position];
    }

    @Override
    public String getTabText(int position) {
        return getResources().getStringArray(R.array.tabStrs)[position];
    }
}
```

## if you have any quetions,contact me by 951882080@qq.com
