package com.cdtsp.hmilib.dms;

import android.content.Context;
import android.util.Log;

import com.sensetime.dms.DmsStatus;
import com.sensetime.dms.distraction.DistractionDetect;
import com.sensetime.dms.distraction.common.DmsDistractionAngle;
import com.sensetime.dms.distraction.common.DmsDistractionHandle;
import com.sensetime.dms.distraction.common.DmsDistractionResultDesc;
import com.sensetime.dms.distraction.common.DmsDistractionStatus;
import com.sensetime.dms.faceDetect.common.DmsFaceDetectResultDesc;

/**
 * @author liangguanhua@sensetime.com
 * @version 1.0.0
 * create on 2017/12/18
 */

public class DistractionWork {

    private String TAG = "DistractionWork";
    private DistractionDetect distractionDetect;
    private DmsDistractionResultDesc distraction_result_desc;
    private DmsDistractionHandle distractionHandle;

    private DmsDistractionStatus dmsDistractionStatus;
    private DmsDistractionAngle dmsDistractionAngle;

    public DistractionWork(){
        distractionDetect = new DistractionDetect();
        distraction_result_desc = new DmsDistractionResultDesc();
        distractionHandle = new DmsDistractionHandle();
        dmsDistractionAngle = new DmsDistractionAngle();
        dmsDistractionStatus = new DmsDistractionStatus();
    }

    public void init(Context context){
        DmsStatus dmsStatus = distractionDetect.initHandle(distractionHandle, context);
        if(dmsStatus.getStatus() != DmsStatus.Status.ST_DMS_SUCCESS){
            Log.e(TAG, "distraction init handle failed");
        }
        dmsStatus = distractionDetect.initResultDesc(distraction_result_desc);
        if(dmsStatus.getStatus() != DmsStatus.Status.ST_DMS_SUCCESS){
            Log.e(TAG, "distraction init result failed");
        }
    }

    public void processFrame(DmsFaceDetectResultDesc faceDetectResultDesc){
        DmsStatus dmsStatus = distractionDetect.run(distractionHandle, faceDetectResultDesc, distraction_result_desc);
        if(dmsStatus.getStatus() == DmsStatus.Status.ST_DMS_SUCCESS){
            dmsStatus = distractionDetect.getDistractionStatus(distraction_result_desc, dmsDistractionStatus);
            if(dmsStatus.getStatus() != DmsStatus.Status.ST_DMS_SUCCESS){
                Log.e(TAG, "distraction get status failed");
                dmsDistractionStatus = new DmsDistractionStatus();
            }
            dmsStatus = distractionDetect.getGaze(distraction_result_desc, dmsDistractionAngle);
            if(dmsStatus.getStatus() != DmsStatus.Status.ST_DMS_SUCCESS){
                Log.e(TAG, "distraction get gaze failed");
                dmsDistractionAngle = new DmsDistractionAngle();
            }
        }else {
            Log.e(TAG, "distraction run failed");
            dmsDistractionAngle = new DmsDistractionAngle();
            dmsDistractionStatus = new DmsDistractionStatus();
        }
    }

    public void destroy(){
        DmsStatus dmsStatus = distractionDetect.destroyHandle(distractionHandle);
        if(dmsStatus.getStatus() != DmsStatus.Status.ST_DMS_SUCCESS){
            Log.e(TAG, "distraction destroy handle failed");
        }
        dmsStatus = distractionDetect.destroyResultDesc(distraction_result_desc);
        if(dmsStatus.getStatus() != DmsStatus.Status.ST_DMS_SUCCESS){
            Log.e(TAG, "distraction destroy result failed");
        }
    }

    public DmsDistractionStatus getDmsDistractionStatus() {
        return dmsDistractionStatus;
    }

    public DmsDistractionAngle getDmsDistractionAngle() {
        return dmsDistractionAngle;
    }
}
