package com.cdtsp.hmilib.dms;

import android.content.Context;
import android.util.Log;

import com.sensetime.dms.DmsStatus;
import com.sensetime.dms.faceDetect.common.DmsFaceDetectResultDesc;
import com.sensetime.dms.faceRecog.FaceRecog;
import com.sensetime.dms.faceRecog.common.DmsFaceRecogFeatureItem;
import com.sensetime.dms.faceRecog.common.DmsFaceRecogHandle;
import com.sensetime.dms.faceRecog.common.DmsSearchResult;
import com.sensetime.dms.faceRecog.common.DmsVerificationResult;

/**
 * @author liangguanhua@sensetime.com
 * @version 1.0.0
 * create on 2017/12/18
 */

public class FaceRecogWork {

    private String TAG = "FaceRecogWork";
    private FaceRecog faceRecog;
    private DmsFaceRecogHandle face_recog_handle;

    private DmsFaceRecogFeatureItem featureItem;
    private DmsVerificationResult verificationResult;
    private DmsSearchResult searchResult;

    public FaceRecogWork(){
        faceRecog = new FaceRecog();
        face_recog_handle = new DmsFaceRecogHandle();
        featureItem = new DmsFaceRecogFeatureItem();
        searchResult = new DmsSearchResult();
        verificationResult = new DmsVerificationResult();
    }

    public void init(Context context){
        DmsStatus dmsStatus = faceRecog.initHandle(face_recog_handle, context);
        if(dmsStatus.getStatus() != DmsStatus.Status.ST_DMS_SUCCESS){
            Log.e(TAG, "faceRecog init handle failed");
        }
    }

    public void processFrame(DmsFaceDetectResultDesc faceResultDesc){
        DmsStatus dmsStatus = faceRecog.extractFeatures(face_recog_handle, faceResultDesc, featureItem);
        if(dmsStatus.getStatus() != DmsStatus.Status.ST_DMS_SUCCESS){
            Log.e(TAG, "faceRecog extract feature failed");
            featureItem = new DmsFaceRecogFeatureItem();
        }
    }

    public void verification(DmsFaceRecogFeatureItem featureItem1, DmsFaceRecogFeatureItem featureItem2){
        DmsStatus dmsStatus = faceRecog.verification(face_recog_handle, featureItem1, featureItem2, verificationResult);
        if(dmsStatus.getStatus() != DmsStatus.Status.ST_DMS_SUCCESS){
            Log.e(TAG, "faceRecog verification feature failed");
            verificationResult = new DmsVerificationResult();
        }
    }

    public void search(DmsFaceRecogFeatureItem featureItemToSearch, DmsFaceRecogFeatureItem[] featureItems){
        DmsStatus dmsStatus = faceRecog.search(face_recog_handle, featureItemToSearch, featureItems, searchResult);
        Log.d(TAG, "faceRecog search state:" + dmsStatus.getStatus());
        if(dmsStatus.getStatus() != DmsStatus.Status.ST_DMS_SUCCESS){
            Log.e(TAG, "faceRecog search feature failed");
            searchResult = new DmsSearchResult();
        }
    }

    public void destroy(){
        DmsStatus dmsStatus = faceRecog.destroyHandle(face_recog_handle);
        if(dmsStatus.getStatus() != DmsStatus.Status.ST_DMS_SUCCESS){
            Log.e(TAG, "faceRecog destroy handle failed");
        }
    }

    public DmsFaceRecogFeatureItem getFeatureItem() {
        return new DmsFaceRecogFeatureItem(featureItem);
    }

    public DmsVerificationResult getVerificationResult() {
        return verificationResult;
    }

    public DmsSearchResult getSearchResult() {
        return searchResult;
    }
}
