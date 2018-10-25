package com.cdtsp.hmilib.dms;

import android.content.Context;
import android.util.Log;

import com.sensetime.dms.DmsStatus;
import com.sensetime.dms.faceDetect.common.DmsFaceDetectResultDesc;
import com.sensetime.dms.liveness.LivenessDetect;
import com.sensetime.dms.liveness.common.DmsLivenessHandle;
import com.sensetime.dms.liveness.common.DmsLivenessResult;

/**
 * @author liangguanhua@sensetime.com
 * @version 1.0.0
 * create on 2017/12/18
 */

public class LivenessWork {

    private String TAG = "LivenessWork";
    private LivenessDetect livenessDetect;
    private DmsLivenessHandle dmsLivenessHandle;

    private DmsLivenessResult dmsLivenessResult;

    public LivenessWork(){
        livenessDetect = new LivenessDetect();
        dmsLivenessHandle = new DmsLivenessHandle();
        dmsLivenessResult = new DmsLivenessResult();
    }

    public void init(Context context){
        DmsStatus dmsStatus = livenessDetect.initHandle(dmsLivenessHandle, context);
        if(dmsStatus.getStatus() != DmsStatus.Status.ST_DMS_SUCCESS){
            Log.e(TAG, "liveness init handle failed");
        }
    }

    public void processFrame(DmsFaceDetectResultDesc faceDetectResultDesc){
        DmsStatus dmsStatus = livenessDetect.run(dmsLivenessHandle, faceDetectResultDesc, dmsLivenessResult);
        if(dmsStatus.getStatus() != DmsStatus.Status.ST_DMS_SUCCESS){
            Log.e(TAG, "liveness run failed");
            dmsLivenessResult = new DmsLivenessResult();
        }
    }

    public void destroy(){
        DmsStatus dmsStatus = livenessDetect.destroyHandle(dmsLivenessHandle);
        if(dmsStatus.getStatus() != DmsStatus.Status.ST_DMS_SUCCESS){
            Log.e(TAG, "liveness destroy handle failed");
        }
    }

    public DmsLivenessResult getDmsLivenessResult() {
        return dmsLivenessResult;
    }
}
