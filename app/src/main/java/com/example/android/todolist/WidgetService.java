package com.example.android.todolist;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by lenovo on 3/14/2017.
 */

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        // return remote view factory here
        return new WidgetDataProvider(this,intent);
    }
}
