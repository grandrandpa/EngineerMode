package com.cdtsp.hmilib.dms;

import android.content.Context;
import android.util.Log;

import com.sensetime.dms.DmsImage;
import com.sensetime.dms.DmsStatus;
import com.sensetime.dms.faceDetect.FaceDetect;
import com.sensetime.dms.faceDetect.common.DmsFaceDetectHandle;
import com.sensetime.dms.faceDetect.common.DmsFaceDetectResultDesc;
import com.sensetime.dms.faceDetect.common.DmsFacePosition;

/**
 * @author liangguanhua@sensetime.com
 * @version 1.0.0
 *          create on 2017/12/18
 */

public class FaceDTWork {

    private String TAG = "FaceDTwork";

    private FaceDetect faceDetect;
    private DmsFaceDetectHandle faceDetectHandle;
    private DmsFaceDetectResultDesc faceDetectResultDesc;

    private DmsFacePosition facePosition;
    private Float quality = 0.0f;
    private Boolean hasFace = false;

    public FaceDTWork() {
        faceDetect = new FaceDetect();
        faceDetectResultDesc = new DmsFaceDetectResultDesc();
        faceDetectHandle = new DmsFaceDetectHandle();

        facePosition = new DmsFacePosition();
    }

    public void initResult(DmsFaceDetectResultDesc resultDesc) {
        DmsStatus status = faceDetect.initResultDesc(resultDesc);
        Log.d(TAG, "init status:" + status.getStatus());
        if (status.getStatus() != DmsStatus.Status.ST_DMS_SUCCESS) {
            Log.e(TAG, "initResultDesc failed");
        }
    }

    public void init(Context context) {
        DmsStatus dmsStatus = faceDetect.initResultDesc(faceDetectResultDesc);
        Log.d(TAG, "init status:" + dmsStatus.getStatus());
        if (dmsStatus.getStatus() != DmsStatus.Status.ST_DMS_SUCCESS) {
            Log.e(TAG, "faceDT init result failed");
        }
        dmsStatus = faceDetect.initHandle(faceDetectHandle, context);
        if (dmsStatus.getStatus() != DmsStatus.Status.ST_DMS_SUCCESS) {
            Log.e(TAG, "faceDT init handle failed");
        }
    }

    public void processFrame(DmsImage image, boolean isTrack) {
        DmsStatus status = faceDetect.run(faceDetectHandle, image, isTrack, hasFace, faceDetectResultDesc);

        //Log.d(TAG, "processFrame status:" + status.getStatus());
        if (status.getStatus() == DmsStatus.Status.ST_DMS_SUCCESS) {
            if (hasFace) {
                status = faceDetect.getPostion(faceDetectResultDesc, facePosition);
                if (status.getStatus() != DmsStatus.Status.ST_DMS_SUCCESS) {
                    Log.e(TAG, "faceDT get position failed." + status.getStatus());
                    facePosition = new DmsFacePosition();
                }
                status = faceDetect.getQuality(faceDetectResultDesc, quality);
                if (status.getStatus() != DmsStatus.Status.ST_DMS_SUCCESS) {
                    Log.e(TAG, "faceDT get quality failed");
                    quality = 0.0f;
                }
            } else {
                facePosition = new DmsFacePosition();
                quality = 0.0f;
            }
        } else {
            Log.e(TAG, "faceDT run failed");
            facePosition = new DmsFacePosition();
            quality = 0.0f;
            hasFace = false;
        }
    }

    public void deepCopy(DmsFaceDetectResultDesc dstResultDesc) {
        DmsStatus status = faceDetect.deepCopyResultDesc(faceDetectResultDesc, dstResultDesc);
        if (status.getStatus() != DmsStatus.Status.ST_DMS_SUCCESS) {
            Log.e(TAG, "deepCopyResultDesc failed" + status.getStatus());
        }
    }

    public void destroyResultDesc(DmsFaceDetectResultDesc resultDesc) {
        DmsStatus status = faceDetect.destroyResultDesc(resultDesc);
        if (status.getStatus() != DmsStatus.Status.ST_DMS_SUCCESS) {
            Log.e(TAG, "destroyResultDesc failed" + status.getStatus());
        }
    }

    public void destroy() {
        DmsStatus dmsStatus = faceDetect.destroyResultDesc(faceDetectResultDesc);
        if (dmsStatus.getStatus() != DmsStatus.Status.ST_DMS_SUCCESS) {
            Log.e(TAG, "faceDT destroy result failed");
        }
        dmsStatus = faceDetect.destroyHandle(faceDetectHandle);
        if (dmsStatus.getStatus() != DmsStatus.Status.ST_DMS_SUCCESS) {
            Log.e(TAG, "faceDT destroy handle failed");
        }
    }

    public DmsFacePosition getFacePosition() {
        return facePosition;
    }

    public float getQuality() {
        return quality;
    }

    public Boolean getHasFace() {
        return hasFace;
    }

    public DmsFaceDetectResultDesc getFaceDetectResultDesc() {
        return faceDetectResultDesc;
    }
}
