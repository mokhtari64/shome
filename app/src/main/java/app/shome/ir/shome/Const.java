package app.shome.ir.shome;

/**
 * Created by Iman on 10/19/2016.
 */
public interface Const {

    String DDNS_IP = "10.10.10.10";
    int DDNS_PORT = 6666;


    String LOCAL_IP = "172.16.1.193";
    int LOCAL_PORT = 8080;


    int OK_STATUS=1;
    int ERROR_STATUS=2;


    int SERVER_DETECT_REQUEST = 100;
    int GET_DEVICE_STATUS_REQUEST = 200;
    int CHANGE_DEVICE_STATUS_REQUEST = 300;
    int DETECT_SERVER_REQUEST = 400;
    int CHECK_CONNECTION_REQUEST = 500;
    int ADD_EDIT_SCENARIO_REQUEST = 600;


    int DB_VERSION = 1;
    int DEVICE_TYPE = 1;
    int CATEGORY_TYPE = 2;
    int MODULE_TYPE = 3;
    int ZONE_TYPE = 4;
    int SCENARIO_TYPE = 5;
    int COMMAND_TYPE = 6;
    /// Device Type


    int VOLUME_DEVICE_TYPE = 2;
    int ON_OFF_DEVICE_TYPE = 1;

    int AnimDuration = 200;


}
