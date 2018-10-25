package com.cdtsp.hmilib.dms;

import android.content.Context;
import android.util.Log;

import com.sensetime.dms.DmsImage;
import com.sensetime.dms.DmsStatus;
import com.sensetime.dms.hand.Hand;
import com.sensetime.dms.hand.common.DmsHandActionType;
import com.sensetime.dms.hand.common.DmsHandHandle;
import com.sensetime.dms.hand.common.DmsHandPosition;
import com.sensetime.dms.hand.common.DmsHandResultDesc;
import com.sensetime.dms.hand.common.DmsHandType;

/**
 * @author liangguanhua@sensetime.com
 * @version 1.0.0
 * create on 2017/12/18
 */

public class HandWork {

    private String TAG = "HandWork";
    private boolean DEBUG = true;
    private Hand hand;
    private DmsHandHandle dmsHandHandle;
    private DmsHandResultDesc dmsHandResultDesc;

    private Boolean hasHand = false;
    private DmsHandType dmsHandType;
    private DmsHandPosition dmsHandPosition;
    private DmsHandActionType dmsHandActionType;
    private Boolean hasHandAction = new Boolean(false);

    public HandWork(){
        hand = new Hand();
        dmsHandHandle = new DmsHandHandle();
        dmsHandResultDesc = new DmsHandResultDesc();
        dmsHandType = new DmsHandType();
        dmsHandPosition = new DmsHandPosition();
        dmsHandActionType = new DmsHandActionType();
    }

    public boolean init(Context context){
        if (DEBUG) Log.d(TAG, "init() called with: context = [" + context + "]");
        DmsStatus dmsStatus = hand.initHandle(dmsHandHandle, context);
        if(dmsStatus.getStatus() != DmsStatus.Status.ST_DMS_SUCCESS){
            Log.e(TAG, "hand init handle failed");
            return false;
        }
        dmsStatus = hand.initResultDesc(dmsHandResultDesc);
        if(dmsStatus.getStatus() != DmsStatus.Status.ST_DMS_SUCCESS){
            Log.e(TAG, "hand init result failed");
            return false;
        }
        return true;
    }

    public void processFrame(DmsImage dmsImage){
        if (DEBUG) Log.d(TAG, "processFrame() called with: dmsImage = [" + dmsImage.format + "]");
        DmsStatus dmsStatus = hand.run(dmsHandHandle, dmsImage, hasHand, dmsHandResultDesc);
        if (DEBUG) Log.d(TAG, "processFrame: dmsStatus.getStatus(): " + dmsStatus.getStatus() + ", hasHand=" + hasHand);
        if(dmsStatus.getStatus() != DmsStatus.Status.ST_DMS_SUCCESS){
            Log.e(TAG, "hand run failed");
            hasHand = false;
            dmsHandType = new DmsHandType();
            dmsHandPosition = new DmsHandPosition();
        }else {
            if(hasHand){
                dmsStatus = hand.getHandType(dmsHandResultDesc, dmsHandType);
                if(dmsStatus.getStatus() != DmsStatus.Status.ST_DMS_SUCCESS){
                    Log.e(TAG, "hand get type failed");
                    dmsHandType = new DmsHandType();
                }
                dmsStatus = hand.getPosition(dmsHandResultDesc, dmsHandPosition);
                if(dmsStatus.getStatus() != DmsStatus.Status.ST_DMS_SUCCESS){
                    Log.e(TAG, "hand get position failed");
                    dmsHandPosition = new DmsHandPosition();
                }
            }else {
                dmsHandType = new DmsHandType();
                dmsHandPosition = new DmsHandPosition();
            }
            dmsStatus = hand.getHandActionType(dmsHandResultDesc, hasHandAction, dmsHandActionType);
            if(dmsStatus.getStatus() != DmsStatus.Status.ST_DMS_SUCCESS){
                Log.e(TAG, "hand get action failed");
                dmsHandActionType = new DmsHandActionType();
            }
        }
    }

    public void destroy(){
        DmsStatus dmsStatus = hand.destroyHandle(dmsHandHandle);
        if(dmsStatus.getStatus() != DmsStatus.Status.ST_DMS_SUCCESS){
            Log.e(TAG, "hand destroy handle failed");
        }
        dmsStatus = hand.destroyResultDesc(dmsHandResultDesc);
        if(dmsStatus.getStatus() != DmsStatus.Status.ST_DMS_SUCCESS){
            Log.e(TAG, "hand destroy result failed");
        }
    }

    public Boolean getHasHand() {
        return hasHand;
    }

    public DmsHandType getDmsHandType() {
        return dmsHandType;
    }

    public DmsHandPosition getDmsHandPosition() {
        return dmsHandPosition;
    }

    public Boolean getHasHandAction() {
        return hasHandAction;
    }

    public DmsHandActionType getDmsHandActionType() {
        return dmsHandActionType;
    }
}
