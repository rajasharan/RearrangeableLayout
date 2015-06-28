# Android Rearrangeable Layout
An android layout to re-arrange child views via dragging

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-RearrangeableLayout-green.svg?style=flat)](https://android-arsenal.com/details/1/2037)

## Screencast Demo
![](/screencast.gif)

## Layout Usage
All the child views are draggable once the layout is added to an activity
([activity_main.xml](/demo/src/main/res/layout/activity_main.xml))
```xml

<com.rajasharan.layout.RearrangeableLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/rearrangeable_layout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:clipToPadding="false"
    app:outlineWidth="2dp"
    app:outlineColor="@color/cyan"
    app:selectionAlpha="0.5"
    app:selectionZoom="1.2"
    >

    <!-- add child views with `android:id` attr to
         save position during orientation change -->

    <TextView
        android:id="@+id/textview_1"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Sample Demo with very large text that will overflow in width"
        android:textSize="30sp"
        android:background="@android:color/holo_green_light"
        android:layout_margin="15dp"
        />

    <!-- more child views -->

</com.rajasharan.layout.RearrangeableLayout>
```

## Child Position Listener
Add a `ChildPositionListener` to the root layout to receive updates whenever any child view is dragged
([MainActivity.java](/demo/src/main/java/com/rajasharan/demo/MainActivity.java))
```java

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    root = (RearrangeableLayout) findViewById(R.id.rearrangeable_layout);
    root.setChildPositionListener(new RearrangeableLayout.ChildPositionListener() {
        @Override
        public void onChildMoved(View childView, Rect oldPosition, Rect newPosition) {
            Log.d(TAG, childView.toString());
            Log.d(TAG, oldPosition.toString() + " -> " + newPosition.toString());
        }
    });
}
```

## [License](/LICENSE)
    The MIT License (MIT)
