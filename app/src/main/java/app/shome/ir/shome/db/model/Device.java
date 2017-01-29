package app.shome.ir.shome.db.model;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Iman on 10/19/2016.
 */
public class Device implements Comparable<Device> {
    public long id;
    public Integer index=-1;
    public Integer dashindex=-1;
    public int isdash=-1;
    public String generationId;
    public String status;
    public int type;
    public String name;
    public Zone defaulZone;
    public String name_fa;

    public View dashView;
    public int span;


    public Zone zone;
    public Category category;


    public int iconRes;


    @Override
    public int compareTo(Device another) {
        return index.compareTo(another.index);
    }
}


//    public long module_id;
//    public long zone_id;
//    public Module module;
