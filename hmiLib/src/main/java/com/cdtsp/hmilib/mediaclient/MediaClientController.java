package com.cdtsp.hmilib.mediaclient;

import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.util.Slog;

import com.cdtsp.hmilib.mediaclient.os.IMediaClientController;
import com.cdtsp.hmilib.mediaclient.os.MediaInfo;

import java.lang.ref.WeakReference;

public class MediaClientController {

    private static final String TAG = MediaClientController.class.getSimpleName();
    private static MediaClientController sInstance;
    private IMediaClientController mControllerImpl;
    public static String MEDIA_CLIENT_SERVICE = "media_client_service";

    private MediaClientController() {}

    public static MediaClientController getInstance() {
        if (sInstance == null) {
            synchronized (MediaClientController.class) {
                if (sInstance == null) {
                    sInstance = new MediaClientController();
                }
            }
        }
        return sInstance;
    }

    private IMediaClientController getControllerImpl() {
        if (mControllerImpl == null) {
            IBinder controllerBinder = ServiceManager.getService(MEDIA_CLIENT_SERVICE);
            if (controllerBinder != null) {
                mControllerImpl = IMediaClientController.Stub.asInterface(controllerBinder);
                try {
                    controllerBinder.linkToDeath(new BinderDeathRecipient(this), 0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        if (mControllerImpl == null) {
            Slog.w(TAG, "warning: no MEDIA_CLIENT_SERVICE", new Throwable());
        }
        return mControllerImpl;
    }

    /**
     * 调用MediaClient播放视频
     * @param info
     */
    public void playVideo(MediaInfo info) {
        Log.d(TAG, "playVideo() called with: info = [" + info + "]");
        playVideoAsCurrent(info);
    }

    /**
     * 在当期正在播放视频的界面上播放视频
     * 若当前没有正在播放视频的界面，则会启动默认的视频界面进行播放
     * @param info
     */
    private void playVideoAsCurrent(MediaInfo info) {
        IMediaClientController controller = getControllerImpl();
        if (controller != null) {
            try {
                if (controller.asBinder().pingBinder()) {
                    controller.playVideoAsCurrent(info);
                } else {
                    Log.d(TAG, "playVideo: failed, because ControllerImpl binder is dead ! please try again !");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 调用MediaClient播放视频
     * @param info
     */
    public void playVideo(MediaInfo info, int displayId, boolean asCopyMode) {
        Log.d(TAG, "playVideo() called with: info = [" + info + "]");
        IMediaClientController controller = getControllerImpl();
        if (controller != null) {
            try {
                if (controller.asBinder().pingBinder()) {
                    controller.playVideo(info, displayId, asCopyMode);
                } else {
                    Log.d(TAG, "playVideo: failed, because ControllerImpl binder is dead ! please try again !");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private static class BinderDeathRecipient implements IBinder.DeathRecipient {

        private WeakReference<MediaClientController> controller;

        public BinderDeathRecipient(MediaClientController controller) {
            this.controller = new WeakReference<>(controller);
        }

        @Override
        public void binderDied() {
            Log.d(TAG, "binderDied() called, " + MEDIA_CLIENT_SERVICE + " is dead ! now reconnect !");
            controller.get().mControllerImpl.asBinder().unlinkToDeath(this, 0);
            controller.get().mControllerImpl = null;
            controller.get().getControllerImpl();
        }
    }
}
