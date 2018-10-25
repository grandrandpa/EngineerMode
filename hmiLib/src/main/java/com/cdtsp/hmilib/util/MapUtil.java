package com.cdtsp.hmilib.util;

/**
 * Created by Administrator on 2018/4/19.
 */

public class MapUtil {

    /**
     * 地图app包名
     */
    public static final String PKG_NAME_MAP = "com.autonavi.amapauto";
    /**
     * 地图app启动类名
     */
    public static final String CLS_NAME_MAP = "com.autonavi.auto.remote.fill.UsbFillActivity";

    /**
     * 地图导航引导信息的ACTION
     */
    public static final String ACTION_GUIDE_INFO = "AUTONAVI_STANDARD_BROADCAST_SEND";

    /**
     * 地图导航引导信息
     */
    public static class GuideInfoExtraKey {

        //导航类型，对应的值为int类型
        //：GPS导航
        //1：模拟导航
        public static final String TYPE = "TYPE";

        //当前道路名称，对应的值为String类型
        public static final String CUR_ROAD_NAME = "CUR_ROAD_NAME";

        //下一道路名，对应的值为String类型
        public static final String NEXT_ROAD_NAME = "NEXT_ROAD_NAME";


        //距离最近服务区的距离，对应的值为int类型，单位：米
        public static final String SAPA_DIST = "SAPA_DIST";

        //服务区类型，对应的值为int类型
        //0：高速服务区
        //1：其他服务器
        public static final String SAPA_TYPE = "SAPA_TYPE";

        //距离最近的电子眼距离，对应的值为int类型，单位：米
        public static final String CAMERA_DIST = "CAMERA_DIST";

        //电子眼类型，对应的值为int类型
        //0 测速摄像头
        //1为监控摄像头
        //2为闯红灯拍照
        //3为违章拍照
        //4为公交专用道摄像头
        //5为应急车道摄像头
        public static final String CAMERA_TYPE = "CAMERA_TYPE";

        //电子眼限速度，对应的值为int类型，无限速则为0，单位：公里/小时
        public static final String CAMERA_SPEED = "CAMERA_SPEED";

        //下一个将要路过的电子眼编号，若为-1则对应的道路上没有电子眼，对应的值为int类型
        public static final String CAMERA_INDEX = "CAMERA_INDEX";

        //导航转向图标，对应的值为int类型
        public static final String ICON = "ICON";

        //路径剩余距离，对应的值为int类型，单位：米
        public static final String ROUTE_REMAIN_DIS = "ROUTE_REMAIN_DIS";

        //路径剩余时间，对应的值为int类型，单位：秒
        public static final String ROUTE_REMAIN_TIME = "ROUTE_REMAIN_TIME";

        //当前导航段剩余距离，对应的值为int类型，单位：米
        public static final String SEG_REMAIN_DIS = "SEG_REMAIN_DIS";

        //当前导航段剩余时间，对应的值为int类型，单位：秒
        public static final String SEG_REMAIN_TIME = "SEG_REMAIN_TIME";

        //自车方向，对应的值为int类型，单位：度，以正北为基准，顺时针增加
        public static final String CAR_DIRECTION = "CAR_DIRECTION";

        //当前道路速度限制，对应的值为int类型，单位：公里/小时
        public static final String LIMITED_SPEED = "LIMITED_SPEED";

        //当前自车所在Link，对应的值为int类型，从0开始
        public static final String CUR_SEG_NUM = "CUR_SEG_NUM";

        //当前位置的前一个形状点号，对应的值为int类型，从0开始
        public static final String CUR_POINT_NUM = "CUR_POINT_NUM";

        //环岛出口序号，对应的值为int类型，从0开始.
        //只有在icon为11、12、17、18时有效，其余为无效值0
        public static final String ROUND_ABOUT_NUM = "ROUNG_ABOUT_NUM";

        //路径总距离，对应的值为int类型，单位：米
        public static final String ROUTE_ALL_DIS = "ROUTE_ALL_DIS";

        //路径总时间，对应的值为int类型，单位：秒
        public static final String ROUTE_ALL_TIME = "ROUTE_ALL_TIME";

        //当前车速，对应的值为int类型，单位：公里/小时
        public static final String CUR_SPEED = "CUR_SPEED";

        //红绿灯个数，对应的值为int类型
        public static final String TRAFFIC_LIGHT_NUM = "TRAFFIC_LIGHT_NUM";

        //服务区个数，对应的值为int类型
        public static final String SAPA_NUM = "SAPA_NUM";

        //下一个服务区名称，对应的值为String类型
        public static final String SAPA_NAME = "SAPA_NAME";

        //当前道路类型，对应的值为int类型
        //0：高速公路
        //1：国道
        //2：省道
        //3：县道
        //4：乡公路
        //5：县乡村内部道路
        //6：主要大街、城市快速道
        //7：主要道路
        //8：次要道路
        //9：普通道路
        //10：非导航道路
        public static final String ROAD_TYPE = "ROAD_TYPE";

        //是否到达目的地,对应的值为boolean类型
        public static final String ARRIVE_STATUS = "ARRIVE_STATUS";

    }
}
