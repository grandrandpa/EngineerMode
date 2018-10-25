package com.cdtsp.hmilib.dms;

import android.content.Context;

/**
 * Contain all DMS work
 * @author liangguanhua@sensetime.com
 * @version 1.0.0
 * create on 2017/12/22
 */

public class Work {

//    public static FaceDTWork faceDTWork;
//    public static LivenessWork livenessWork;
//    public static FaceRecogWork faceRecogWork;
//    public static DistractionWork distractionWork;
    public static HandWork handWork;

    public static boolean init(Context context){
//        faceDTWork = new FaceDTWork();
//        faceDTWork.init(context);
//        livenessWork = new LivenessWork();
//        livenessWork.init(context);
//        faceRecogWork = new FaceRecogWork();
//        faceRecogWork.init(context);
//        distractionWork = new DistractionWork();
//        distractionWork.init(context);
        handWork = new HandWork();
        return handWork.init(context);
    }

    public static void destroy(){
//        if(null != faceDTWork) faceDTWork.destroy();
//        if(null != livenessWork) livenessWork.destroy();
//        if(null != faceRecogWork) faceRecogWork.destroy();
//        if(null != distractionWork) distractionWork.destroy();
        if(null != handWork) handWork.destroy();
    }
}
