package example.naoki.ble_myo.Communication;

/**
 * Created by sec on 2016-11-13.
 */

public class Session {
    private static String id;
    private static String name;
    private static boolean logined;

    private static boolean idCheck;
    private static boolean signUp;
    private static boolean loginCheck;

    private Session(){
        id=null;
        name=null;
        logined=false;
        idCheck=false;
        signUp=false;
        loginCheck=false;
    }

    public static String getId(){
        return id;
    }
    public static void setId(String _id){
        id=_id;
    }
    public static String getName(){
        return name;
    }
    public static void setName(String _name){
        name=_name;
    }
    public static boolean getLogined(){
        return logined;
    }
    public static void setLogined(boolean _logined){
        logined=_logined;
    }
    public static boolean getIdCheck(){
        return idCheck;
    }
    public static void setIdCheck(boolean _idCheck){
        idCheck=_idCheck;
    }
    public static boolean getSignUp(){
        return signUp;
    }
    public static void setSignUp(boolean _signUp){
        signUp=_signUp;
    }
    public static boolean getLoginCheck(){
        return loginCheck;
    }
    public static void setLoginCheck(boolean _loginCheck){
        loginCheck=_loginCheck;
    }
}