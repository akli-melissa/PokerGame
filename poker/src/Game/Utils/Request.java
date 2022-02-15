package Game.Utils;

public class Request {
    public static String JOIN="100 HELLO PLAYER .+";
    public static String CREATE_ROOM="110 CREATE -?\\d+ PLAYER -?\\d+ MIN -?\\d+ STACK -?\\d+";
    public static String GET_ROOMS="120 GETLIST";
    public static String JOIN_ROOM="130 JOIN -?\\d+";
    public static String ACK_Player="141 .+ ACK";
    public static String START_ROUND="150 REQUEST START";
    public static String START_RESPONSE="152 START (YES|NO)";

    public static String ACTION_RECIEVED="500 RECIEVED";
    public static String CARD_RECIEVED="600 RECIEVED";
    public static String CHANGE="710 CHANGE \\d+(\\s+[DCST](\\d|1[0123]))+";
    public static String CHANGE_RECIEVED="701 RECIEVED";
    public static String QUIT="210 QUIT";
    public static String QUIT_RECIEVED="201 RECIEVED";

    public static String ECHO="000 echo .+";
}
