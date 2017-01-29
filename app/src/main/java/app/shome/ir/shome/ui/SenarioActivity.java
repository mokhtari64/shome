package app.shome.ir.shome.ui;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collection;
import java.util.Vector;

import app.shome.ir.shome.R;
import app.shome.ir.shome.SHomeActivity;
import app.shome.ir.shome.Const;
import app.shome.ir.shome.Utils;
import app.shome.ir.shome.db.MySqliteOpenHelper;
import app.shome.ir.shome.db.model.Device;
import app.shome.ir.shome.db.model.Scenario;
import app.shome.ir.shome.db.model.ScenarioDeviceStatus;

/**
 * Created by Mahdi on 20/01/2017.
 */
public class SenarioActivity extends SHomeActivity {
    Vector<Device> selectedDevice = new Vector<>();
    Scenario currentScenario;
    Device allDevice[];
    Scenario allScenarios[];
    ScenarioAdapter scenarioAdapter;
    ScenarioDeviceAdapter scenariodeviceAdapter;
    static int senario_id = 0;
    Boolean kind = false;
    String Senario_name;
    Button add, save;
    EditText name;
    LinearLayout add_senario_layer, add_senario_layer2, senario_list_layer;

    public void reload() {
        Collection<Scenario> values = MySqliteOpenHelper.getInstance().allsScenarios.values();
        allScenarios = new Scenario[values.size()];

        values.toArray(allScenarios);
        for (int i = 0; i < values.size(); i++) {
            allScenarios[i].devicesStatus = MySqliteOpenHelper.getInstance().allsScenarios.get(allScenarios[i].id).devicesStatus;
        }
        if (currentScenario == null && allScenarios.length > 0) {
            currentScenario = allScenarios[0];
        }

    }


    protected void onCreate(Bundle savedInstanceState) {

        ListView lv, lv2;
        reload();
        allDevice = new Device[MySqliteOpenHelper.getInstance().allDevice.size()];
        MySqliteOpenHelper.getInstance().allDevice.values().toArray(allDevice);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senario);
        lv = (ListView) findViewById(R.id.senarioList);
        lv2 = (ListView) findViewById(R.id.senarioListDetail);
        add_senario_layer = (LinearLayout) findViewById(R.id.senario_add_layer);
        add_senario_layer2 = (LinearLayout) findViewById(R.id.senario_add_layer2);
        senario_list_layer = (LinearLayout) findViewById(R.id.senaro_list_layer);
        scenarioAdapter = new ScenarioAdapter();
        lv.setAdapter(scenarioAdapter);
        scenariodeviceAdapter = new ScenarioDeviceAdapter();
        lv2.setAdapter(scenariodeviceAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentScenario = allScenarios[position];
                scenariodeviceAdapter.notifyDataSetChanged();
                scenarioAdapter.notifyDataSetChanged();

            }
        });
        add = (Button) findViewById(R.id.add_senario);
        save = (Button) findViewById(R.id.save_senario);
        name = (EditText) findViewById(R.id.senario_name);


        add_senario_layer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!kind) {
                    app.shome.ir.shome.utils.Utils.expand(name);
                    app.shome.ir.shome.utils.Utils.expand(save);
                    kind = true;
                    app.shome.ir.shome.utils.Utils.zoom_in(add);
                    add.setBackground(getResources().getDrawable(R.drawable.back));
                    /*TransitionDrawable transition = (TransitionDrawable) add_senario_layer.getBackground();
                    transition.startTransition(AnimDuration);*/
                    app.shome.ir.shome.utils.Utils.change_color(add_senario_layer, getResources().getColor(R.color.transparent), getResources().getColor(R.color.my_transparent_light));
                    add_senario_layer2.setVisibility(View.VISIBLE);
                    senario_list_layer.animate().alpha((float) 0.1).setDuration(AnimDuration);
//                    overridePendingTransition(R.anim.animation_enter,
//                            R.anim.animation_leave);
                } else {

                    close();
                }

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = name.getText().toString();
                if (s.trim().length() == 0) {
                    Toast.makeText(SenarioActivity.this, "Name empty", Toast.LENGTH_SHORT).show();
                } else {

                    addScenario(s);
                    name.setText("");
                    close();
                }

            }
        });



       /* ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, str);
        lv.setAdapter(adapter);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog a = new Dialog(SenarioActivity.this);
                a.setContentView(R.layout.activity_senario_add_dialog);
                a.show();
                a.findViewById(R.id.senario_add_dialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        a.dismiss();
                        EditText name = (EditText) a.findViewById(R.id.senario_name_dialog);
                        Senario_name = name.getText().toString();


                    }
                });
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                Toast.makeText(getApplicationContext(), str[position] + " entekhabe shoma bud!", Toast.LENGTH_SHORT).show();

            }

        });*/

    }

    public void addScenario(String name) {
        Scenario a = new Scenario();
        a.name = name;
        a.id = senario_id++;
        showDeviceDialog(a);
        currentScenario = a;

    }

    public void showDeviceDialog(final Scenario senario) {
        final Dialog a = new Dialog(this);
        a.requestWindowFeature(Window.FEATURE_NO_TITLE);
        a.setContentView(R.layout.activity_senario_add_device_dialog_list);
        a.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < selectedDevice.size(); i++) {
                    ScenarioDeviceStatus b = new ScenarioDeviceStatus();
                    b.device = selectedDevice.get(i);
                    senario.devicesStatus.add(b);
                }

                MySqliteOpenHelper.getInstance().addScenario(senario);
                reload();
                currentScenario = senario;
                scenariodeviceAdapter.notifyDataSetChanged();
                scenarioAdapter.notifyDataSetChanged();
                selectedDevice.clear();
                a.dismiss();

            }
        });
        GridView gridView = (GridView) a.findViewById(R.id.gridView2);
        AllDeviceAdapter adapter = new AllDeviceAdapter();
        gridView.setAdapter(adapter);
        a.show();

    }

    public void close() {
        app.shome.ir.shome.utils.Utils.collapse(name,SenarioActivity.this);
        app.shome.ir.shome.utils.Utils.collapse(save,SenarioActivity.this);
        kind = false;
        app.shome.ir.shome.utils.Utils.zoom_out(add);
        add.setBackground(getResources().getDrawable(R.drawable.add));
        senario_list_layer.animate().alpha((float) 1.0).setDuration(AnimDuration);
        add_senario_layer2.setVisibility(View.INVISIBLE);
        app.shome.ir.shome.utils.Utils.change_color(add_senario_layer, getResources().getColor(R.color.white), getResources().getColor(R.color.transparent));
    }



    class ScenarioAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return allScenarios.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView a = new TextView(SenarioActivity.this);
            if (currentScenario != null && allScenarios[position].id == currentScenario.id) {
                a.setBackgroundColor(Color.RED);
            } else {
                a.setBackgroundColor(Color.TRANSPARENT);
            }
            a.setText(allScenarios[position].name);
            return a;
        }
    }

    class ScenarioDeviceAdapter extends BaseAdapter {

        @Override
        public int getCount() {

            if (currentScenario == null)
                return 0;
            else
                return currentScenario.devicesStatus.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ScenarioDeviceStatus scenarioDeviceStatus = currentScenario.devicesStatus.get(position);
            View view = inflater.inflate(R.layout.activity_senario_detail, null);

            TextView zonetitle = (TextView) view.findViewById(R.id.zonetitle);
            if (scenarioDeviceStatus.device != null && scenarioDeviceStatus.device.zone != null)
                zonetitle.setText(scenarioDeviceStatus.device.zone.name_fa);
            else if (scenarioDeviceStatus.device != null && scenarioDeviceStatus.device.defaulZone != null)
                zonetitle.setText(scenarioDeviceStatus.device.defaulZone.name_fa);
            ImageView zoneIcon = (ImageView) view.findViewById(R.id.zone_image);
            if (scenarioDeviceStatus.device != null && scenarioDeviceStatus.device.zone != null && scenarioDeviceStatus.device.zone.iconRes != 0)
                zoneIcon.setImageResource(scenarioDeviceStatus.device.zone.iconRes);
            else {
                zoneIcon.setImageResource(R.drawable.zone1);
            }

            ImageView deviceICon = (ImageView) view.findViewById(R.id.devIcon);
            if (scenarioDeviceStatus.device.iconRes != 0) {
                deviceICon.setImageResource(scenarioDeviceStatus.device.iconRes);
            } else {
                deviceICon.setImageResource(R.drawable.l1);
            }
            TextView deviceTitle = (TextView) view.findViewById(R.id.devName);
            deviceTitle.setText(scenarioDeviceStatus.device.name_fa);
            TextView deviceZOneTitle = (TextView) view.findViewById(R.id.zoneName);
            deviceZOneTitle.setVisibility(View.GONE);

            Switch status = (Switch) view.findViewById(R.id.switch1);
            if (scenarioDeviceStatus.status == null || scenarioDeviceStatus.status.length() == 0 || scenarioDeviceStatus.status.equals("LOW")) {
                status.setChecked(false);

            } else {
                status.setChecked(true);
            }

            return view;
        }
    }

    class AllDeviceAdapter extends BaseAdapter {

        @Override
        public int getCount() {

            return allDevice.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.activity_senario_add_device_dialog, null);

            ImageView imageView = (ImageView) view.findViewById(R.id.imageView2);
            TextView textView = (TextView) view.findViewById(R.id.devName);
            final CheckBox checkBox = (CheckBox) view.findViewById(R.id.selected_device);
            if (allDevice[position].iconRes == 0) {
                imageView.setImageResource(R.drawable.l2);
            } else {
                imageView.setImageResource(allDevice[position].iconRes);
            }

            textView.setText(allDevice[position].name_fa);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        selectedDevice.add(allDevice[position]);
                    } else {
                        selectedDevice.remove(allDevice[position]);
                    }
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBox.setChecked(!checkBox.isChecked());
                }
            });


            return view;
        }
    }


}

