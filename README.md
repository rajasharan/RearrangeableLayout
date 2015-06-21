# Android Rearrangeable Layout
An android layout to re-arrange child views via dragging

## Screencast Demo
![](/screencast.gif)

### Layout Usage
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
    app:outlineWidth="5dp"
    app:outlineColor="@color/cyan"
    app:outlineAlpha="128"
    >

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Sample Demo"
        android:textSize="30sp"
        android:background="@android:color/darker_gray"
        android:layout_margin="15dp"
        />

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Sample Demo with very large text that will overflow in width"
        android:textSize="30sp"
        android:background="@android:color/holo_green_light"
        android:layout_margin="15dp"
        />

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Medium length text"
        android:textSize="30sp"
        android:background="@android:color/holo_blue_light"
        android:layout_margin="15dp"
        />

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Sample"
        android:textSize="15sp"
        android:background="@android:color/holo_orange_light"
        android:layout_margin="15dp"
        />
</com.rajasharan.layout.RearrangeableLayout>
```
