package app.shome.ir.shome.db.model;

import java.util.Vector;

/**
 * Created by Iman on 10/19/2016.
 */
public class Scenario {
    public String name;
    public long id;
    public Vector<ScenarioDeviceStatus> devicesStatus = new Vector<>();
}
