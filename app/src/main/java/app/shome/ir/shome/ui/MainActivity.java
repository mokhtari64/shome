package app.shome.ir.shome.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import app.shome.ir.shome.Const;
import app.shome.ir.shome.R;
import app.shome.ir.shome.SHomeActivity;
import app.shome.ir.shome.SHomeApplication;
import app.shome.ir.shome.SettingActivity;
import app.shome.ir.shome.Utils;
import app.shome.ir.shome.db.MySqliteOpenHelper;
import app.shome.ir.shome.db.model.Device;
import app.shome.ir.shome.db.model.Scenario;
import app.shome.ir.shome.db.model.ScenarioDeviceStatus;
import app.shome.ir.shome.db.model.Zone;
import app.shome.ir.shome.grid.twowayview.TwoWayLayoutManager;
import app.shome.ir.shome.grid.twowayview.widget.SpannableGridLayoutManager;
import app.shome.ir.shome.grid.twowayview.widget.TwoWayView;
import app.shome.ir.shome.service.ServiceDelegate;
import app.shome.ir.shome.service.Services;
import app.shome.ir.shome.utils.YearMonthDate;

public class
MainActivity extends SHomeActivity implements ServiceDelegate, Const, OnClickListener {
    ImageView add;
    float orgPos1X;
    LinearLayout progress;
    LinearLayout zoneTabLayout;

    LinearLayout dashboard_layer;

    Zone currentZone = null;


    ImageButton dashboardTab;
    ToggleButton settingbtn;
    LayoutInflater inflater;
    LinearLayout edit_device;
    LinearLayout edit_zone;
    LinearLayout edit_senario;
    LinearLayout setting_layer;
    LinearLayout menu_space;
    LinearLayout edit_user;
    LinearLayout rules;
    LinearLayout about_me;
    LinearLayout exit;
    Vector<Device> data;
    TwoWayView recyclerView;
    LayoutAdapter adapter;
    ClockHolder clockHolder;

    Comparator<Device> dashboardComprator = new Comparator<Device>() {
        @Override
        public int compare(Device lhs, Device rhs) {

            return (lhs == null || rhs == null) ? 1 : lhs.dashindex.compareTo(rhs.dashindex);
        }
    };
    Comparator<Device> zoneComprator = new Comparator<Device>() {
        @Override
        public int compare(Device lhs, Device rhs) {
            return lhs.index.compareTo(rhs.index);
        }
    };

    SwipeRefreshLayout mSwipeRefreshLayout;

    Animation alpha;
    Animation alpha_out;
    Animation rotation;
    Animation rotation_out;
    int screenWidth;
    int sw;
    int dtime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        add = (ImageView) findViewById(R.id.add);
//        add.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Scenario a = new Scenario();
//                a.name = "iman";
//                Collection<Device> values = MySqliteOpenHelper.getInstance().allDevice.values();
//                Device[] devices = new Device[values.size()];
//                values.toArray(devices);
//                for (int i = 0; i < 2; i++) {
//                    ScenarioDeviceStatus b = new ScenarioDeviceStatus();
//                    b.device = devices[i];
//                    b.status = "HIGH";
//                    a.devicesStatus.add(b);
//                }
//
//                new Services.AddEditScenario(ADD_EDIT_SCENARIO_REQUEST, MainActivity.this, a, SHomeApplication.LOCAL_IP, SHomeApplication.LOCAL_PORT).execute();
//
//
//            }
//        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();

            }
        });
        settingbtn = (ToggleButton) findViewById(R.id.setting);
        alpha = AnimationUtils.loadAnimation(this, R.anim.alpha);
        alpha_out = AnimationUtils.loadAnimation(this, R.anim.alpha_out);
        rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_rotation);
        rotation_out = AnimationUtils.loadAnimation(this, R.anim.unclockwise_rotation);

        setting_layer = (LinearLayout) findViewById(R.id.setting_layer);
        menu_space = (LinearLayout) findViewById(R.id.menu_space);
        dashboard_layer = (LinearLayout) findViewById(R.id.dashboardLayer);
        edit_device = (LinearLayout) findViewById(R.id.edit_device);
        edit_zone = (LinearLayout) findViewById(R.id.edit_zone);
        edit_senario = (LinearLayout) findViewById(R.id.edit_senario);
        edit_user = (LinearLayout) findViewById(R.id.edit_user);
        rules = (LinearLayout) findViewById(R.id.rules);
        about_me = (LinearLayout) findViewById(R.id.about_me);
        exit = (LinearLayout) findViewById(R.id.exit);
        menu_space.setOnClickListener(this);
        orgPos1X = setting_layer.getX();


        progress = (LinearLayout) findViewById(R.id.progressLayout);

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        OnItemClickListener itemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(DeviecHolder item) {
                item.progressBar.setVisibility(View.VISIBLE);
                new Services.ChangeDeviceState(CHANGE_DEVICE_STATUS_REQUEST, item, item.device, SHomeApplication.LOCAL_IP, SHomeApplication.LOCAL_PORT).execute();


            }
        };
        zoneTabLayout = (LinearLayout) findViewById(R.id.zoneLayout);

        recyclerView = (TwoWayView) findViewById(R.id.recycler_view);
        adapter = new LayoutAdapter(this, recyclerView, itemClickListener);
        recyclerView.setAdapter(adapter);

//
        ItemTouchHelper.Callback _ithCallback = new ItemTouchHelper.Callback() {
            //and in your imlpementaion of
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                Collections.swap(data, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                MySqliteOpenHelper.getInstance().updateIndex(data);


                adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());

                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //TODO
            }

            //defines the enabled move directions in each state (idle, swiping, dragging).
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                        ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
            }
        };
        ItemTouchHelper ith = new ItemTouchHelper(_ithCallback);
        ith.attachToRecyclerView(recyclerView);
        startTimer(true);


        screenWidth = Utils.getScreenWidth(MainActivity.this);
        sw = screenWidth;
        dtime = 1500;
        setting_layer.setX(orgPos1X - screenWidth);
        assert settingbtn != null;
        settingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (settingbtn.isChecked()) {
                    settingbtn.setClickable(false);
//                    setting_layer.setX(orgPos1X - screenWidth );

                    rotation.setRepeatCount(Animation.INFINITE);
                    rotation.setRepeatCount(0);
                    settingbtn.startAnimation(rotation_out);
//                    settingbtn.setHighlightColor(0xff33b5e5);
                    setting_layer.animate().translationX(setting_layer.getX() + sw).setDuration(dtime);
                    dashboard_layer.animate().alpha((float) 0.3).setDuration(dtime);

//                    recyclerView.animate().translationX(screenWidth).setDuration(dtime);
//                    setting_layer.setVisibility(View.VISIBLE);
//                    recyclerView.setVisibility(View.GONE);
                    final Handler handler = new Handler();
                    setting_layer.setEnabled(false);
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            settingbtn.setClickable(true);
                        }
                    }, dtime);


                } else {
                    closeMenu();

                }


            }
        });

        edit_device.setOnClickListener(this);
        edit_zone.setOnClickListener(this);
        edit_senario.setOnClickListener(this);
        edit_user.setOnClickListener(this);
        about_me.setOnClickListener(this);
        rules.setOnClickListener(this);
        dashboardTab = (ImageButton) findViewById(R.id.dashboardtab);
        dashboardTab.setOnClickListener(this);
        setCurrentZone(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SHomeApplication.isInitialization) {
            progress.setVisibility(View.GONE);
            init();
        }
    }

    Timer getStatusTime;

    public void startTimer(boolean forceRestart) {
        if (forceRestart) {
            if (getStatusTime != null)
                getStatusTime.cancel();
            getStatusTime = null;

        }
        if (getStatusTime == null) {
            SharedPreferences config = getSharedPreferences("config", MODE_PRIVATE);
            int get_status_time = config.getInt("get_status_time", 5 * 60 * 1000);
            getStatusTime = new Timer("get_Status");
            getStatusTime.schedule(new TimerTask() {
                @Override
                public void run() {
                    new Services.GetStatusService(GET_DEVICE_STATUS_REQUEST, MainActivity.this, SHomeApplication.LOCAL_IP, SHomeApplication.LOCAL_PORT).execute();
                }
            }, 0, get_status_time);
        }


    }

    void closeMenu() {
        if (settingbtn.isChecked()) {
            settingbtn.setChecked(false);
            settingbtn.setClickable(false);
            settingbtn.startAnimation(rotation);
            setting_layer.animate().translationX(orgPos1X - screenWidth).setDuration(dtime);
//                    recyclerView.animate().translationX(orgPos1X).setDuration(dtime);
            dashboard_layer.animate().alpha(1).setDuration(dtime);
//                    setting_layer.setVisibility(View.GONE);
//                    recyclerView.setVisibility(View.VISIBLE);
            final Handler handler = new Handler();
            setting_layer.setEnabled(false);
            handler.postDelayed(new Runnable() {
                public void run() {
                    settingbtn.setClickable(true);
                }
            }, dtime);
        }

    }

    private void refreshContent() {
        new Services.GetStatusService(GET_DEVICE_STATUS_REQUEST, this, SHomeApplication.LOCAL_IP, SHomeApplication.LOCAL_PORT).execute();

    }

    @Override
    public void onBackPressed() {
        if (settingbtn.isChecked()) {
            closeMenu();
        } else
            super.onBackPressed();
    }

    private void initZoneTab() {
        zoneTabLayout.removeAllViews();

        Collection<Zone> values = MySqliteOpenHelper.getInstance().allZones.values();
        for (Zone z : values) {
            View inflate = inflater.inflate(R.layout.activity_zone_tab, null);
            z.textView = (TextView)inflate.findViewById(R.id.zonetitle);
//            TextView zonetitle = (TextView) inflate.findViewById(R.id.zonetitle);
//            zonetitle.setText(z.name_fa);
            z.textView.setText(z.name_fa);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 86, getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 62, getResources().getDisplayMetrics()));
            params.leftMargin = 2;
            z.imageView = (ImageView) inflate.findViewById(R.id.zone_image);

            if (z.iconRes != 0) {
                z.imageView.setImageResource(z.iconRes);
            }
            params.rightMargin = 2;
            params.topMargin = 4;
            params.bottomMargin = 4;

            inflate.setLayoutParams(params);
            inflate.setTag(z);
            z.textView.setGravity(Gravity.CENTER_HORIZONTAL);
            zoneTabLayout.addView(inflate);
            inflate.setOnClickListener(this);

        }
    }


    private void init() {
        progress.setVisibility(View.GONE);
        MySqliteOpenHelper.getInstance().loadData(true);
        initZoneTab();
        setCurrentZone(currentZone);


    }

    private void setCurrentZone(Zone z) {
        if (currentZone != null) {
            currentZone.imageView.setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP);
            currentZone.imageView.animate().scaleX(1).setDuration(AnimDuration);
            currentZone.imageView.animate().scaleY(1).setDuration(AnimDuration);
//            currentZone.textView.setGravity(Gravity.CENTER );
//            currentZone.textView.animate().rotation(0).setDuration(AnimDuration);
            currentZone.textView.setTextColor(Color.GREEN);
//            currentZone.textView.animate().x(0).setDuration(AnimDuration);
            currentZone.textView.animate().y(0).setDuration(AnimDuration);
            currentZone.textView.animate().scaleX(1).setDuration(AnimDuration);
            currentZone.textView.animate().scaleY(1).setDuration(AnimDuration);
            app.shome.ir.shome.utils.Utils.change_color(currentZone.textView, getResources().getColor(R.color.my_orange), getResources().getColor(R.color.transparent));
        }


        if (z == null) {
            currentZone = z;
            data = MySqliteOpenHelper.getInstance().dashboarDevice;
            Collections.sort(data, dashboardComprator);
            adapter.notifyDataSetChanged();
        } else {
            currentZone = MySqliteOpenHelper.getInstance().allZones.get(z.id);
            data = currentZone.devices;
            Collections.sort(data, zoneComprator);
            adapter.notifyDataSetChanged();
        }

        if (currentZone != null) {
            currentZone.imageView.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
            currentZone.imageView.animate().scaleX(0.85f).setDuration(AnimDuration);
            currentZone.imageView.animate().scaleY(0.85f).setDuration(AnimDuration);
//            currentZone.textView.setGravity(Gravity.TOP |Gravity.RIGHT);
//            currentZone.textView.setGravity(Gravity.CENTER);
//            currentZone.textView.animate().rotation(45).setDuration(AnimDuration);
            currentZone.textView.setTextColor(Color.WHITE);
//            currentZone.textView.animate().x(15).setDuration(AnimDuration);
            currentZone.textView.animate().y(30).setDuration(AnimDuration);
            currentZone.textView.animate().scaleX(0.8f).setDuration(AnimDuration);
            currentZone.textView.animate().scaleY(0.8f).setDuration(AnimDuration);
//            currentZone.textView.setBackgroundColor(Color.TRANSPARENT);
//            currentZone.textView.setTextColor(Color.RED);
            app.shome.ir.shome.utils.Utils.change_color(currentZone.textView, getResources().getColor(R.color.transparent), getResources().getColor(R.color.my_orange));
        }
    }


    private int blendColors(int from, int to, float ratio) {
        final float inverseRatio = 1f - ratio;

        final float r = Color.red(to) * ratio + Color.red(from) * inverseRatio;
        final float g = Color.green(to) * ratio + Color.green(from) * inverseRatio;
        final float b = Color.blue(to) * ratio + Color.blue(from) * inverseRatio;

        return Color.rgb((int) r, (int) g, (int) b);
    }

    @Override
    public void onPostResult(int requestCOde, int statuscode, String data) {

        if (requestCOde == GET_DEVICE_STATUS_REQUEST) {

            boolean ok = false;
            Device[] devices;
            int zone_id = 1;
            HashMap<String, Zone> zone = new HashMap<>();
            if (data != null) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    String cmdT = jsonObject.getString("CmdT");
                    if (cmdT.equals("3")) {
                        JSONArray zoneD = jsonObject.getJSONArray("ZoneD");
                        devices = new Device[zoneD.length()];
                        for (int i = 0; i < zoneD.length(); i++) {
                            devices[i] = new Device();
                            JSONObject jsonObject1 = zoneD.getJSONObject(i);
                            devices[i].generationId = jsonObject1.getString("DGID");
                            devices[i].status = jsonObject1.getString("ST");
                            Device device = MySqliteOpenHelper.getInstance().allDevice.get(devices[i].generationId);
                            if (device != null) {
                                device.status = devices[i].status;
                                MySqliteOpenHelper.getInstance().allDevice.put(device.generationId, device);
                                MySqliteOpenHelper.getInstance().updateDevice(device);

//                            MySqliteOpenHelper.getInstance().allDevice.put(devices[i].generationId,)
                                continue;
                            }
                            String zone1 = jsonObject1.getString("Zone");
                            if (zone.containsKey(zone1)) {
                                devices[i].defaulZone = zone.get(zone1);
                                devices[i].index = i;
                                devices[i].dashindex = i;
                            } else {
                                Zone a = new Zone();
                                a.name_fa = zone1;
                                a.name = zone1;
                                a = MySqliteOpenHelper.getInstance().addZone(zone1);

                                devices[i].defaulZone = a;
                                zone.put(a.name_fa, a);
                            }
                            devices[i].category = MySqliteOpenHelper.getInstance().allCategories.get(jsonObject1.getLong("DTYPE"));

                            if (devices[i].status.toUpperCase().equals("HIGH") || devices[i].status.toUpperCase().equals("LOW"))
                                devices[i].type = ON_OFF_DEVICE_TYPE;
                            else
                                devices[i].type = VOLUME_DEVICE_TYPE;
                            devices[i].isdash = 1;
                            MySqliteOpenHelper.getInstance().addDevice(devices[i]);
                        }
                    }
                    ok = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (!ok) {
//            progressDialog.dismiss();
//            tryAgainDialog.show();
            } else {
                SHomeApplication.isInitialization = true;
                SHomeApplication.save();

                init();
//            progressDialog.dismiss();
            }
            mSwipeRefreshLayout.setRefreshing(false);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        closeMenu();
        if (v == edit_device) {

            Intent a = new Intent(MainActivity.this, EntityEditActivity.class);
            a.putExtra("type", "device");
            startActivity(a);
        } else if (v == edit_zone) {

            Intent a = new Intent(MainActivity.this, EntityEditActivity.class);
            a.putExtra("type", "zone");
            startActivity(a);
        } else if (menu_space == v) {

        } else if (edit_senario == v) {
            Intent a = new Intent(MainActivity.this, SenarioActivity.class);
            startActivity(a);

        } else if (edit_user == v) {
            Intent a = new Intent(MainActivity.this, UserActivity.class);
            startActivity(a);
        } else if (about_me == v) {
            setContentView(R.layout.activity_about_me);
            Intent a = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(a);
        } else if (rules == v) {
            setContentView(R.layout.activity_rules);
        } else if (dashboardTab == v) {
            setCurrentZone(null);
//            currentZone = null;
//            data = MySqliteOpenHelper.getInstance().dashboarDevice;
//            Collections.sort(data, dashboardComprator);
//            adapter.notifyDataSetChanged();
        }
        Object o = v.getTag();
        if (o instanceof Zone) {
            Zone z = (Zone) o;
            setCurrentZone(z);
//            currentZone = z;
//            data = z.devices;
//            Collections.sort(data, zoneComprator);
//            adapter.notifyDataSetChanged();
        }


    }

    public interface OnItemClickListener {
        void onItemClick(DeviecHolder item);
    }

    class LayoutAdapter extends RecyclerView.Adapter {
        OnItemClickListener itemClickListener;
        private static final int COUNT = 100;

        private final Context mContext;
        private final TwoWayView mRecyclerView;
//        private final List<Integer> mItems;

        private int mCurrentItemId = 0;


        public LayoutAdapter(Context context, TwoWayView recyclerView, OnItemClickListener a) {
            mContext = context;
            itemClickListener = a;
//            mItems = new ArrayList<Integer>(COUNT);
//            for (int i = 0; i < COUNT; i++) {
//                addItem(i);
//            }

            mRecyclerView = recyclerView;

        }

//        public void addItem(int position) {
//            final int id = mCurrentItemId++;
////            mItems.add(position, id);
//            notifyItemInserted(position);
//        }

//        public void removeItem(int position) {
//            mItems.remove(position);
//            notifyItemRemoved(position);
//        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 3) {
                if (clockHolder == null) {
                    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_analog_clock, parent, false);
                    clockHolder = new ClockHolder(itemView);
                }
                return clockHolder;
            } else {

                View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_light_device, parent, false);
                return new DeviecHolder(view);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (data.get(position) == null) {
                return 3;
            } else

                return (data.get(position).span);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int colSpan;//= (isVertical ? span2 : span1);
            int rowSpan;//= (isVertical ? span1 : span2);
            if (holder instanceof DeviecHolder) {
                ((DeviecHolder) holder).setDevice(data.get(position), itemClickListener);
                colSpan = 1;

            } else {
//                ((DeviecHolder) holder).setDevice(data.get(position), itemClickListener);
                colSpan = 2;
            }
            rowSpan = colSpan;

            boolean isVertical = (mRecyclerView.getOrientation() == TwoWayLayoutManager.Orientation.VERTICAL);
            final View itemView = holder.itemView;

//            final int itemId = mItems.get(position);


            final SpannableGridLayoutManager.LayoutParams lp =
                    (SpannableGridLayoutManager.LayoutParams) itemView.getLayoutParams();

//            final int span1 = (itemId == 0 || itemId == 3 ? 2 : 1);
//            final int span2 = (itemId == 0 ? 2 : (itemId == 3 ? 3 : 1));


            if (lp.rowSpan != rowSpan || lp.colSpan != colSpan) {
                lp.rowSpan = rowSpan;
                lp.colSpan = colSpan;
                itemView.setLayoutParams(lp);
            }

        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }


    public class ClockHolder extends RecyclerView.ViewHolder {
        public View mView;

        TextView year, days, month;

        public ClockHolder(View itemView) {
            super(itemView);
            mView = itemView;
            year = (TextView) itemView.findViewById(R.id.year);
            days = (TextView) itemView.findViewById(R.id.days);
            month = (TextView) itemView.findViewById(R.id.month);

            year.setTypeface(SHomeApplication.BYEKAN_NORMAL);
            days.setTypeface(SHomeApplication.BYEKAN_NORMAL);
            month.setTypeface(SHomeApplication.BYEKAN_NORMAL);
            Date d = new Date();
            Calendar instance = Calendar.getInstance();
            instance.setTime(d);
            YearMonthDate a = new YearMonthDate(instance.get(Calendar.YEAR), instance.get(Calendar.MONTH), instance.get(Calendar.DAY_OF_MONTH));
            YearMonthDate yearMonthDate = YearMonthDate.gregorianToJalali(a);
            year.setText("" + yearMonthDate.getYear());
            days.setText("" + yearMonthDate.getDate());
            month.setText(yearMonthDate.getMonthText());

        }
    }

    public class DeviecHolder extends RecyclerView.ViewHolder implements ServiceDelegate {
        Device device;
        ProgressBar progressBar;
        ImageView devIcon;
        TextView zoneTextView, titleTextView;
        public View mview;
        public View image;

        public void setDevice(final Device device, final OnItemClickListener itemClickListener) {

            this.device = device;
            image.setTag(device);
            if (device.name_fa != null)
                titleTextView.setText(device.name_fa);
            else if (device.name != null)
                titleTextView.setText(device.name);
            else if (device.category != null) {
                if (device.category.name_fa != null)
                    titleTextView.setText(device.category.name_fa);
                else if (device.category.name != null)
                    titleTextView.setText(device.category.name);

            }
            if (currentZone == null || (!currentZone.equals(device.zone) && !currentZone.equals(device.defaulZone))) {
                zoneTextView.setVisibility(View.VISIBLE);
                if (device.zone != null) {
                    if (device.zone.name_fa != null)
                        zoneTextView.setText(device.zone.name_fa);
                    else if (device.zone.name != null)
                        zoneTextView.setText(device.zone.name);
                } else if (device.defaulZone != null) {
                    if (device.defaulZone.name_fa != null)
                        zoneTextView.setText(device.defaulZone.name_fa);
                    else if (device.defaulZone.name != null)
                        zoneTextView.setText(device.defaulZone.name);
                }
            } else {
                zoneTextView.setVisibility(View.GONE);
            }
            if (device.iconRes != 0) {
                devIcon.setImageResource(device.iconRes);


            }
            image.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(DeviecHolder.this);
                }
            });
            switchDevice();
//            image.setTag(image.getId(),progressBar);
//            mview.setOnClickListener(MainActivity.this);
        }


        public DeviecHolder(View view) {
            super(view);
//            view.setOnClickListener(this);
            mview = view;
            image = view.findViewById(R.id.toggleButton2);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            devIcon = (ImageView) view.findViewById(R.id.devIcon);
            zoneTextView = (TextView) view.findViewById(R.id.zoneName);
            titleTextView = (TextView) view.findViewById(R.id.devName);
            devIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            progressBar.setVisibility(View.GONE);


        }

        void switchDevice() {
            if (device.status.toUpperCase().equals("LOW")) {
                image.setSelected(false);
                image.setBackgroundResource(R.drawable.buttom_main_normal);
                devIcon.setColorFilter(Color.WHITE
                        , PorterDuff.Mode.SRC_ATOP);
                zoneTextView.setTextColor(Color.WHITE);
                titleTextView.setTextColor(Color.WHITE);
            } else {
                image.setSelected(true);
                image.setBackgroundResource(R.drawable.buttom_main_passed);
                devIcon.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
                zoneTextView.setTextColor(Color.GREEN);
                titleTextView.setTextColor(Color.GREEN);

            }

        }


        @Override
        public void onPostResult(int requestCode, int statusCode, String toke) {
            progressBar.setVisibility(View.INVISIBLE);
            if (toke != null && toke.trim().length() > 0) {
                try {
                    JSONObject jsonObject = new JSONObject(toke);
                    String cmdT = jsonObject.getString("CmdT");
                    if (cmdT.equals("1")) {
                        String ackType = jsonObject.getString("AckType");
                        if (ackType.toUpperCase().trim().equals("JOB-FAIL")) {
                            Toast.makeText(MainActivity.this, "JOB-FAIL", Toast.LENGTH_SHORT).show();
                        } else if (ackType.toUpperCase().trim().equals("REPEAT")) {
                            Toast.makeText(MainActivity.this, "REPEAT", Toast.LENGTH_SHORT).show();

                        } else if (ackType.toUpperCase().trim().equals("OK")) {
                            device.status = (device.status.toUpperCase().equals("HIGH") ? "LOW" : "HIGH");
                            switchDevice();
                        } else if (ackType.toUpperCase().trim().equals("NOtRECONIZE ")) {
                            Toast.makeText(MainActivity.this, "NOtRECONIZE", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(MainActivity.this, "server error", Toast.LENGTH_SHORT).show();
            }


        }
    }

    DisplayMetrics dm;

    public int containerHeight(MainActivity ba) {
        if (dm == null) {
            dm = new DisplayMetrics();
            ba.getWindowManager().getDefaultDisplay().getMetrics(dm);
        }

        return (int) (dm.heightPixels / 3);
    }

}
