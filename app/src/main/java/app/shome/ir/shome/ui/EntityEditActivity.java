package app.shome.ir.shome.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collection;
import java.util.HashMap;

import app.shome.ir.shome.R;
import app.shome.ir.shome.SHomeActivity;
import app.shome.ir.shome.db.MySqliteOpenHelper;
import app.shome.ir.shome.db.model.Device;
import app.shome.ir.shome.db.model.Zone;
import app.shome.ir.shome.design.CircleCheckBox;

/**
 * Created by Mahdi on 11/01/2016.
 */
public class EntityEditActivity extends SHomeActivity {


    String editType;
    CircleCheckBox circleCheckBox;

    Zone[] zones;
    View saveName;
    Device[] devices;
    ListView lv;

    Button add_entity, save_entity;

    Zone currentZone;
    Device currentDevice;
    TextView category;
    EditText name, entity_name;
    Spinner zoneSpinner;
    ImageView icon;
    BaseAdapter deviceAdapter, zoneAdapter;
    LinearLayout add_entity_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                editType = extras.getString("type");
            }
        }
        if (editType == null)
            finish();
        setContentView(R.layout.activity_entity_edit);

        lv = (ListView) findViewById(R.id.entity_list);

        icon = (ImageView) findViewById(R.id.zone_icon);
        add_entity = (Button) findViewById(R.id.add_entity);
        save_entity = (Button) findViewById(R.id.save_entity);
        entity_name = (EditText) findViewById(R.id.entity_name);
        saveName = findViewById(R.id.savename);
        saveName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editType.trim().equals("zone")) {
                    currentZone.name_fa = name.getText().toString();
                    MySqliteOpenHelper.getInstance().updateZone(currentZone);
                    zoneAdapter.notifyDataSetChanged();
                } else {
                    currentDevice.name_fa = name.getText().toString();
                    MySqliteOpenHelper.getInstance().updateDevice(currentDevice);
                    deviceAdapter.notifyDataSetChanged();

                }
                saveName.setVisibility(View.GONE);

            }
        });

        add_entity_layout = (LinearLayout) findViewById(R.id.add_entity_layer);
        circleCheckBox = (CircleCheckBox) findViewById(R.id.circle_check_box);

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(EntityEditActivity.this, IconSelectionActivity.class);
                a.putExtra("type", editType);
                startActivityForResult(a, 1000);
            }
        });
        category = (TextView) findViewById(R.id.category);
        zoneSpinner = (Spinner) findViewById(R.id.zone_spinner);
        name = (EditText) findViewById(R.id.zone_name);
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editType.trim().equals("zone")) {
                    if(s.toString().trim().equals(currentZone.name_fa))
                    {
                        saveName.setVisibility(View.GONE);

                    }else
                    {
                        saveName.setVisibility(View.VISIBLE);

                    }
                }else
                {
                    if(s.toString().trim().equals(currentDevice.name_fa))
                    {
                        saveName.setVisibility(View.GONE);

                    }else
                    {
                        saveName.setVisibility(View.VISIBLE);

                    }

                }

            }
        });
        saveName.setVisibility(View.GONE);
        if (editType.trim().equals("zone")) {
            initZoneEdit();
            circleCheckBox.setVisibility(View.GONE);
            add_entity_layout.setVisibility(View.VISIBLE);
        } else {
            add_entity_layout.setVisibility(View.GONE);
            initDeviceEdit();
            circleCheckBox.setListener(new CircleCheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(boolean isChecked) {
                    currentDevice.isdash = (isChecked) ? 1 : 0;
                    MySqliteOpenHelper.getInstance().updateDevice(currentDevice);

                }
            });
            deviceAdapter.notifyDataSetChanged();
        }


    }

    private void initZoneEdit() {
        zoneSpinner.setVisibility(View.GONE);
        category.setVisibility(View.GONE);
        HashMap<Long, Zone> allZones = MySqliteOpenHelper.getInstance().allZones;
        Collection<Zone> values = allZones.values();
        zones = new Zone[values.size()];

        values.toArray(zones);

        if (zones.length > 0 && zones[0] != null)
            setCurrentZone(zones[0]);

        zoneAdapter = new ZoneAddapter();
        lv.setAdapter(zoneAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setCurrentZone(zones[position]);
            }
        });
    }

    private void setCurrentDevice(Device device) {
        currentDevice = device;
        name.setText(currentDevice.name_fa);
        if ((circleCheckBox.isChecked() && currentDevice.isdash != 1) || (!circleCheckBox.isChecked() && currentDevice.isdash == 1))
            circleCheckBox.toggle();
        int index = java.util.Arrays.asList(zones).indexOf(currentDevice.zone);
        zoneSpinner.setSelection(index);
        if (device.category != null) {
            String name = (device.category.name_fa != null && device.category.name_fa.length() > 0) ? device.category.name_fa : device.category.name;
            category.setText(name);
        }
        if (currentDevice.iconRes != 0) {
            icon.setImageResource(currentDevice.iconRes);
        } else {
            icon.setImageResource(R.drawable.light2);
        }
    }

    private void setCurrentZone(Zone z) {
        currentZone = z;
        name.setText(currentZone.name_fa);
        if (currentZone.iconRes != 0) {
            icon.setImageResource(currentZone.iconRes);
        } else {
            icon.setImageResource(R.drawable.room2);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000 && resultCode == OK_STATUS) {
            int data1 = data.getExtras().getInt("data", -1);
            if (data1 != -1) {
                icon.setImageResource(data1);
                if (currentDevice != null) {
                    currentDevice.iconRes = data1;
                    MySqliteOpenHelper.getInstance().updateDevice(currentDevice);
                    deviceAdapter.notifyDataSetChanged();

                } else if (currentZone != null) {
                    currentZone.iconRes = data1;
                    MySqliteOpenHelper.getInstance().updateZone(currentZone);
                    zoneAdapter.notifyDataSetChanged();

                }

            }
        }
    }

    private void initDeviceEdit() {
        HashMap<Long, Zone> allZones = MySqliteOpenHelper.getInstance().allZones;
        Collection<Zone> zone_values = allZones.values();
        zones = new Zone[zone_values.size()];
        zone_values.toArray(zones);


        zoneSpinner.setVisibility(View.VISIBLE);
        category.setVisibility(View.VISIBLE);
        HashMap<String, Device> allDevice = MySqliteOpenHelper.getInstance().allDevice;
        Collection<Device> values = allDevice.values();
        devices = new Device[values.size()];

        values.toArray(devices);
        if (devices.length > 0 && devices[0] != null)
            setCurrentDevice(devices[0]);


        deviceAdapter = new DeviceAddapter();
        lv.setAdapter(deviceAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setCurrentDevice(devices[position]);
            }
        });


        zoneSpinner.setAdapter(new ArrayAdapter(this, android.R.layout.simple_spinner_item, zones));
        int index = java.util.Arrays.asList(zones).indexOf(currentDevice.zone);
        zoneSpinner.setSelection(index);
        zoneSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentDevice.zone = zones[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    class ZoneAddapter extends BaseAdapter {

        @Override
        public int getCount() {
            return zones == null ? 0 : zones.length;
        }

        @Override
        public Object getItem(int position) {
            return zones[position];
        }

        @Override
        public long getItemId(int position) {
            return zones[position].id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View inflate = inflater.inflate(R.layout.activity_zone_tab, null);
            TextView title = (TextView) inflate.findViewById(R.id.zonetitle);
            ImageView imageView = (ImageView) inflate.findViewById(R.id.zone_image);
            if (zones[position].iconRes != 0)
                imageView.setImageResource(zones[position].iconRes);
            title.setText(zones[position].name_fa);
            return inflate;
        }
    }


    class DeviceAddapter extends BaseAdapter {

        @Override
        public int getCount() {
            return devices == null ? 0 : devices.length;
        }

        @Override
        public Object getItem(int position) {
            return devices[position];
        }

        @Override
        public long getItemId(int position) {
            return devices[position].id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View inflate = inflater.inflate(R.layout.activity_device_detail, null);
            TextView title = (TextView) inflate.findViewById(R.id.devName);
            ImageView imageView = (ImageView) inflate.findViewById(R.id.imageView2);
            if (devices[position].iconRes != 0)
                imageView.setImageResource(devices[position].iconRes);
            title.setText(devices[position].name_fa);
            return inflate;
        }
    }
}
