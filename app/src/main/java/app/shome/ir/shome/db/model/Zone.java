package app.shome.ir.shome.db.model;

import android.widget.ImageView;

import java.util.Vector;

/**
 * Created by Iman on 10/19/2016.
 */
public class Zone {
    public String name;
    public String name_fa;
    public long id;
    public int iconRes=0;
    public Vector<Device> devices = new Vector<>();
    public ImageView imageView;

    @Override
    public boolean equals(Object o) {
        if(o==null)
            return false;
        return id == ((Zone) o).id;
    }

    @Override
    public String toString() {
        return  (name_fa != null && name_fa.length() > 0)? name_fa: name;
    }
}
