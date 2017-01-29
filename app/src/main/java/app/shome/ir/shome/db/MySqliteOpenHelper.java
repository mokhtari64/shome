package app.shome.ir.shome.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.Vector;

import app.shome.ir.shome.R;
import app.shome.ir.shome.SHomeApplication;
import app.shome.ir.shome.Const;
import app.shome.ir.shome.db.model.Category;
import app.shome.ir.shome.db.model.Device;


import app.shome.ir.shome.db.model.Scenario;
import app.shome.ir.shome.db.model.ScenarioDeviceStatus;
import app.shome.ir.shome.db.model.Zone;

/**
 * Created by Iman on 10/19/2016.
 */
public class MySqliteOpenHelper extends SQLiteOpenHelper implements Const {

    public HashMap<Long, Category> allCategories;
    public HashMap<String, Device> allDevice;
    public HashMap<Long, Scenario> allsScenarios;
    public HashMap<Long, Zone> allZones;
    public Vector<Device> dashboarDevice = new Vector<>();


    private static MySqliteOpenHelper instance;

    public static MySqliteOpenHelper getInstance() {
        if (instance == null) {
            instance = new MySqliteOpenHelper();
            instance.loadData(true);
        }
        return instance;

    }



    private String zoneTableName = "zone";
    private String deviceTableName = "device";
    private String scenarioTableName = "scenario";
    private String scenarioDeviceTableName = "scenario_device";






    private String deviceTableQuery = "create table " + deviceTableName + "(id integer primary key AUTOINCREMENT," +
            "name text," +
            "type number," +
            "generationid text," +
            "zoneid number," +
            "icon number," +
            "defaultzoneid number," +
            "categoryid number," +
            "name_fa text," +
            "isdash number default 1," +
            "status text," +
            "indx number default -1," +
            "dashindx number default -1)";

    private String zoneTableQuery = "create table " + zoneTableName + "(id integer primary key AUTOINCREMENT default 50," +
            "name text," +
            "icon number," +
            "name_fa text,deleted number)";
    private String scenarioTableQuery = "create table " + scenarioTableName + "(id integer primary key ," +
            "name text)";
    private String scenarioDeviceQuery = "create table " + scenarioDeviceTableName + "(scenario_id integer,device_id text,status text)" ;


    private MySqliteOpenHelper() {
        super(SHomeApplication.context, "localdb", null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(zoneTableQuery);
        db.execSQL(deviceTableQuery);
        db.execSQL(scenarioTableQuery);
        db.execSQL(scenarioDeviceQuery);
    }


    public void loadData(boolean reload) {
        if (allDevice != null && !reload) {
            return;
        }
        dashboarDevice.removeAllElements();

        allCategories = new HashMap<>();
        allDevice = new HashMap<>();
        allsScenarios = new HashMap<>();
        allZones = new HashMap<>();
//        String[] zones = SHomeApplication.context.getResources().getStringArray(R.array.zones);
//        String[] zones_fa = SHomeApplication.context.getResources().getStringArray(R.array.zones_fa);
        String[] category = SHomeApplication.context.getResources().getStringArray(R.array.catrgories);
        String[] category_fa = SHomeApplication.context.getResources().getStringArray(R.array.catrgories_fa);

//        for (int i = 0; i < zones.length; i++) {
//            Zone a = new Zone();
//            a.name_fa = zones_fa[i];
//            a.name = zones[i];
//            a.id = i + 1;
//            allZones.put((long) i + 1, a);
//        }
        for (int i = 0; i < category_fa.length; i++) {
            Category a = new Category();
            a.name_fa = category_fa[i];
            a.name = category[i];
            a.id = i + 1;
            allCategories.put( a.id, a);
        }




        String query = "select * from " + zoneTableName;// + " union all select * from " + zoneSystemTableName;
        Cursor cursor = getReadableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            Zone a = new Zone();
            a.name_fa = cursor.getString(cursor.getColumnIndex("name_fa"));
            a.id = cursor.getLong(cursor.getColumnIndex("id"));
            a.iconRes = cursor.getInt(cursor.getColumnIndex("icon"));
            allZones.put(a.id, a);
            cursor.moveToNext();
        }
        query = "select * from " + deviceTableName;
        cursor = getReadableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            Device a = new Device();
            a.name_fa = cursor.getString(cursor.getColumnIndex("name_fa"));
            a.id = cursor.getLong(cursor.getColumnIndex("id"));
            a.index = cursor.getInt(cursor.getColumnIndex("indx"));
            a.isdash = cursor.getInt(cursor.getColumnIndex("isdash"));
            a.dashindex = cursor.getInt(cursor.getColumnIndex("dashindx"));
            a.iconRes = cursor.getInt(cursor.getColumnIndex("icon"));

            if (a.dashindex == -1)
                a.dashindex = i;
            if (a.index == -1)
                a.index = i;

            a.type = cursor.getInt(cursor.getColumnIndex("type"));
            a.span = (a.type == VOLUME_DEVICE_TYPE) ? 2 : 1;
            a.generationId = cursor.getString(cursor.getColumnIndex("generationid"));
            long zoneid = cursor.getLong(cursor.getColumnIndex("zoneid"));
            Zone zone = allZones.get(zoneid);
            if (zone != null) {
                a.zone = zone;
                zone.devices.add(a);
            }else {
                long defaultzoneid = cursor.getLong(cursor.getColumnIndex("defaultzoneid"));
                Zone zone1 = allZones.get(defaultzoneid);
                if (zone1 != null) {
                    a.defaulZone = zone1;
                    zone1.devices.add(a);
                }
            }
            long catid = cursor.getLong(cursor.getColumnIndex("categoryid"));
            Category category1 = allCategories.get(catid);
            if (category1 != null) {
                a.category = category1;
                category1.devices.add(a);
            }

            a.status = cursor.getString(cursor.getColumnIndex("status"));
            if (a.isdash == 1)
                dashboarDevice.add(a);
            allDevice.put(a.generationId, a);

            cursor.moveToNext();
        }
        query = "select * from " + scenarioTableName;
        cursor = getReadableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            Scenario scenario=new Scenario();
            scenario.name = cursor.getString(cursor.getColumnIndex("name"));
            scenario.id = cursor.getLong(cursor.getColumnIndex("id"));
            allsScenarios.put(scenario.id,scenario);
            cursor.moveToNext();
        }
        query = "select * from " + scenarioDeviceTableName;
        cursor = getReadableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            ScenarioDeviceStatus scenario=new ScenarioDeviceStatus();
            scenario.device = allDevice.get(cursor.getString(cursor.getColumnIndex("device_id")));
            scenario.status = cursor.getString(cursor.getColumnIndex("status"));
            allsScenarios.get(cursor.getLong(cursor.getColumnIndex("scenario_id"))).devicesStatus.add(scenario);
            cursor.moveToNext();
        }

        cursor.close();
        dashboarDevice.add(0, null);


    }

    public Zone addZone(String name) {
        Zone z = new Zone();
        z.id = addNew(name, zoneTableName);
        z.name_fa = name;
        return z;
    }



    public Device addDevice(Device a) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("name_fa", a.name_fa);
        contentValues.put("name", a.name);
        contentValues.put("generationid", a.generationId);
        if (a.zone != null)
            contentValues.put("zoneid", a.zone.id);
        if (a.defaulZone != null)
            contentValues.put("defaultzoneid", a.defaulZone.id);
        if (a.category != null)
            contentValues.put("categoryid", a.category.id);

        contentValues.put("indx", a.generationId);
        contentValues.put("isdash", a.isdash);
        contentValues.put("type", a.type);
        contentValues.put("status", a.status);
        SQLiteDatabase writableDatabase = getWritableDatabase();
        a.id = writableDatabase.insert(deviceTableName, null, contentValues);

        return a;
    }

    public Scenario addScenario(Scenario z) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", z.name);
        contentValues.put("id", z.id);
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.insert(scenarioTableName, null, contentValues);
        for (int i = 0; i < z.devicesStatus.size(); i++) {
            contentValues = new ContentValues();
            ScenarioDeviceStatus scenarioDeviceStatus = z.devicesStatus.get(i);
            contentValues.put("scenario_id", z.id);
            contentValues.put("device_id", scenarioDeviceStatus.device.generationId);
            contentValues.put("status", scenarioDeviceStatus.status);
            writableDatabase.insert(scenarioDeviceTableName, null, contentValues);
        }
        allsScenarios.put(z.id,z);
        writableDatabase.close();
        return z;
    }


    private long addNew(String name, String tableName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name_fa", name);
        SQLiteDatabase writableDatabase = getWritableDatabase();
        long id = writableDatabase.insert(tableName, null, contentValues);
        writableDatabase.close();
        return id;

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void updateIndex(Vector<Device> data) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        String query;
        if (data == dashboarDevice) {
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i) != null) {
                    query = "update " + deviceTableName + "  set dashindx=" + i + " where id=" + data.get(i).id;
                    writableDatabase.execSQL(query);
                }
            }

        } else {
            for (int i = 0; i < data.size(); i++) {
                query = "update " + deviceTableName + "  set indx=" + i + " where id=" + data.get(i).id;
                writableDatabase.execSQL(query);
            }

        }
        writableDatabase.close();

    }

    public void updateDevice(Device device) {
        Zone z=device.zone;
        if(device.name_fa!=null) {
            String query = "update " + deviceTableName + "  set  isdash="+device.isdash+",icon="+device.iconRes+",status='" + device.status + "'" +
                    ",name_fa='" + device.name_fa + "',zoneid="+((z==null)?0:device.zone.id)+" where id=" + device.id;
            getWritableDatabase().execSQL(query);
        }else
        {
            String query = "update " + deviceTableName + "  set isdash="+device.isdash+",icon="+device.iconRes+",status='" + device.status + "',zoneid="+((z==null)?0:device.zone.id)+" where id=" + device.id;
            getWritableDatabase().execSQL(query);
        }

    }

    public void updateZone(Zone zone) {
        String query = "update " + zoneTableName + "  set icon="+zone.iconRes+",name_fa='" + zone.name_fa + "' where id=" + zone.id;
        getWritableDatabase().execSQL(query);

    }
}
