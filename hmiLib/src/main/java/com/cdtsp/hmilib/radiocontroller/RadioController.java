/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cdtsp.hmilib.radiocontroller;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.radio.RadioManager;
import android.hardware.radio.RadioTuner;
import android.media.AudioManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.cdtsp.hmilib.radiocontroller.service.IRadioCallback;
import com.cdtsp.hmilib.radiocontroller.service.IRadioManager;
import com.cdtsp.hmilib.radiocontroller.service.RadioRds;
import com.cdtsp.hmilib.radiocontroller.service.RadioStation;

import java.util.ArrayList;
import java.util.List;

/**
 * A controller that handles the display of metadata on the current radio station.
 */
public class RadioController {
    private static final String TAG = "Em.RadioController";
    public static final int INVALID_RADIO_CHANNEL = -1;
    public static final int INVALID_RADIO_BAND = -1;

    private static HandlerThread mReconnThread = new HandlerThread("RS_Reconn_Thread");

    static {
        mReconnThread.start();
    }

    private static Handler mReconnHandler = new Handler(mReconnThread.getLooper());

    private final String PKG_NAME_RADIO_SERVICE = "com.chinatsp.hmi.tuner";
    private final String CLS_NAME_RADIO_SERVICE = "com.chinatsp.hmi.tuner.RadioService";

    private int mCurrentChannelNumber = INVALID_RADIO_CHANNEL;

    private final Context mContext;
    private IRadioManager mRadioManager;


    private boolean mHasDualTuners;
    private RadioRds mCurrentRds;
    /**
     * Keeps track of if the user has manually muted the radio. This value is used to determine
     * whether or not to un-mute the radio after an {@link AudioManager#AUDIOFOCUS_LOSS_TRANSIENT}
     * event has been received.
     */
    private boolean mUserHasMuted;

    /**
     * The current radio band. This value is one of the BAND_* values from {@link RadioManager}.
     * For example, {@link RadioManager#BAND_FM}.
     */
    private int mCurrentRadioBand = INVALID_RADIO_BAND;

    public enum PlayState {
        play,
        pause
    }

    public enum RadioBand {
        FM,
        AM
    }

    private List<RadioStationChangeListener> mStationChangeListener = new ArrayList<>();
    public List<UIUpdateListener> mUIUpdateListener = new ArrayList<>();

    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {

        @Override
        public void binderDied() {
            Log.d(TAG, "binderDied");
            // TODO Auto-generated method stub
            if (mRadioManager == null)
                return;
            mRadioManager.asBinder().unlinkToDeath(mDeathRecipient, 0);
            mRadioManager = null;
            // 重新绑定远程服务
            bindRadioService(1000);

        }

    };

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            mRadioManager = IRadioManager.Stub.asInterface(binder);
            Log.d(TAG, "onServiceConnected");
            try {
                if (mRadioManager == null || !mRadioManager.isInitialized()) {
                    Log.e(TAG, "onServiceConnected error");
                    return;
                }
                binder.linkToDeath(mDeathRecipient, 0);

                mHasDualTuners = mRadioManager.hasDualTuners();

//                if (mHasDualTuners) {
//                    initializeDualTunerController();
//                } else {
//                    mRadioDisplayController.setSingleChannelDisplay(mRadioBackground);
//                }

                mRadioManager.addRadioTunerCallback(mCallback);


                // Upon successful connection, open the radio.
//                openRadioBand(radioBand);
//                maybeTuneToStoredRadioChannel();

                Log.d(TAG, "onServiceConnected mStationChangeListener size = " + mStationChangeListener.size());
                for (RadioStationChangeListener listener : mStationChangeListener) {
                    listener.onRadioStationChanged(
                            mRadioManager.getCurrentRadioStation());
                }
                Log.d(TAG, "onServiceConnected mUIUpdateListener size = " + mUIUpdateListener.size());

                setPlayPauseButtonState(mRadioManager.isMuted());
                mRadioManager.getPresetsList();
            } catch (RemoteException e) {
                Log.e(TAG, "onServiceConnected(); remote exception: " + e.getMessage());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            if (mRadioManager != null) {
                try {
                    mRadioManager.removeRadioTunerCallback(mCallback);
                } catch (RemoteException e) {
                    Log.e(TAG, "onServiceDisconnected remote exception: " + e.getMessage());
                }
            }
            Log.e(TAG, "onServiceDisconnected");
        }
    };

    /**
     * Interface for a class that will be notified when the current radio station has been changed.
     */
    public interface RadioStationChangeListener {
        /**
         * Called when the current radio station has changed in the radio.
         *
         * @param station The current radio station.
         */
        default void onRadioStationChanged(RadioStation station){};
        default void onRadioPreScannedChanged(List<RadioStation> preScanned, int band){};
        default void onRadioPresetsChanged(List<RadioStation> presets){};
    }


    /**
     * Listener that will be called .
     */
    public interface UIUpdateListener {

        default void onUpdatePosition(int position){};
        default void onUpdatePlayState(PlayState playState){};
        default void onUpdateRadioBand(int band){};
        default void onRadioChannelString(String channel){};
    }

    public RadioController(Context context) {
        mContext = context;
    }

    /**
     * Sets the listener that will be notified whenever the radio station changes.
     */
    public void setRadioStationChangeListener(RadioStationChangeListener listener) {
        Log.d(TAG, "setRadioStationChangeListener " + listener);
        mStationChangeListener.remove(listener);
        mStationChangeListener.add(listener);
    }

    public void setUIUpdateListener(UIUpdateListener listener) {
        Log.d(TAG, "setUIUpdateListener " + listener);
        mUIUpdateListener.remove(listener);
        mUIUpdateListener.add(listener);
    }

    /**
     * Starts the controller to handle radio tuning. This method should be called to begin
     * radio playback.
     */
    public void start() {
        if (!Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "starting radio");
        }
        bindRadioService(0);

        Log.d(TAG, "start end");
    }


    private void bindRadioService(int delayed) {
        mReconnHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "bindRadioService Process： " + android.os.Process.myPid() + " Thread: " + android.os.Process.myTid() + " name " + Thread.currentThread().getName());
                Intent serviceIntent = new Intent();
                serviceIntent.setComponent(new ComponentName(PKG_NAME_RADIO_SERVICE, CLS_NAME_RADIO_SERVICE));
                Log.d(TAG, "bindRadioService: ");
                if (mContext != null) {
                    if (!mContext.bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE)) {
                        Log.e(TAG, "bindRadioService Failed to connect to RadioService.");
                    }
                }
            }
        }, delayed);

    }

    /**
     * Retrieves information about the current radio station from {@link #mRadioManager} and updates
     * the display of that information accordingly.
     */
    private void updateRadioDisplay() {
        if (mRadioManager == null) {
            return;
        }

        try {
            RadioStation station = mRadioManager.getCurrentRadioStation();

            if (!Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "updateRadioDisplay(); current station: " + station);
            }

            mHasDualTuners = mRadioManager.hasDualTuners();

            if (mHasDualTuners) {
                initializeDualTunerController();
            } else {
//                mRadioDisplayController.setSingleChannelDisplay(mRadioBackground);
            }

            // Update the AM/FM band display.
            mCurrentRadioBand = station.getRadioBand();
            updateAmFmDisplayState();

            // Update the channel number.
            setRadioChannel(station.getChannelNumber());

            // Ensure the play button properly reflects the current mute state.
//            mRadioDisplayController.setPlayPauseButtonState(mRadioManager.isMuted());
            setPlayPauseButtonState(mRadioManager.isMuted());
        } catch (RemoteException e) {
            Log.e(TAG, "updateRadioDisplay(); remote exception: " + e.getMessage());
        }
    }

    /**
     * Tunes the radio to the given channel if it is valid and a {@link RadioTuner} has been opened.
     */
    public void tuneToRadioChannel(RadioStation radioStation) {
        if (mRadioManager == null) {
            return;
        }

        try {
            mRadioManager.tune(radioStation);
        } catch (RemoteException e) {
            Log.e(TAG, "tuneToRadioChannel(); remote exception: " + e.getMessage());
        }
    }

    /**
     * Returns the band this radio is currently tuned to.
     */
    public int getCurrentRadioBand() {
        if (mRadioManager == null) {
            return mCurrentRadioBand;
        }

        try {
            mCurrentRadioBand = mRadioManager.getCurrentRadioBand();
        } catch (RemoteException e) {
            Log.e(TAG, "getCurrentRadioStation(); error retrieving current station: "
                    + e.getMessage());
        }

        return mCurrentRadioBand;
    }

    /**
     * Returns the radio station that is currently playing on the radio. If this controller is
     * not connected to the {@link RadioService} or a radio station cannot be retrieved, then
     * {@code null} is returned.
     */
    @Nullable
    public RadioStation getCurrentRadioStation() {
        if (mRadioManager == null) {
            return null;
        }

        try {
            return mRadioManager.getCurrentRadioStation();
        } catch (RemoteException e) {
            Log.e(TAG, "getCurrentRadioStation(); error retrieving current station: "
                    + e.getMessage());
        }

        return null;
    }

    /**
     * Opens the given current radio band. Currently, this only supports FM and AM bands.
     *
     * @param radioBand One of {@link RadioManager#BAND_FM}, {@link RadioManager#BAND_AM},
     *                  {@link RadioManager#BAND_FM_HD} or {@link RadioManager#BAND_AM_HD}.
     */
    public void openRadioBand(int radioBand) {
        if (mRadioManager == null || radioBand == mCurrentRadioBand) {
            return;
        }

        // Reset the channel number so that we do not animate number changes between band changes.
        mCurrentChannelNumber = INVALID_RADIO_CHANNEL;

        setCurrentRadioBand(radioBand);

        try {
            mRadioManager.openRadioBand(radioBand);

            updateAmFmDisplayState();

            // Sets the initial mute state. This will resolve the mute state should be if an
            // {@link AudioManager#AUDIOFOCUS_LOSS_TRANSIENT} event is received followed by an
            // {@link AudioManager#AUDIOFOCUS_GAIN} event. In this case, the radio will un-mute itself
            // if the user has not muted beforehand.
            if (mUserHasMuted) {
                mRadioManager.mute();
            }

            // Ensure the play button properly reflects the current mute state.
//            mRadioDisplayController.setPlayPauseButtonState(mRadioManager.isMuted());
            setPlayPauseButtonState(mRadioManager.isMuted());

//            maybeTuneToStoredRadioChannel();
        } catch (RemoteException e) {
            Log.e(TAG, "openRadioBand(); remote exception: " + e.getMessage());
        }
    }

    /**
     * Attempts to tune to the last played radio channel for a particular band. For example, if
     * the user switches to the AM band from FM, this method will attempt to tune to the last
     * AM band that the user was on.
     *
     * <p>If a stored radio station cannot be found, then this method will initiate a seek so that
     * the radio is always on a valid radio station.
     */
    private void maybeTuneToStoredRadioChannel() {

        if (!Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, String.format("maybeTuneToStoredRadioChannel(); band: %s, channel %s",
                    mCurrentRadioBand, mCurrentChannelNumber));
        }

        // Tune to a stored radio channel if it exists.
        if (mCurrentChannelNumber != INVALID_RADIO_CHANNEL) {
            RadioStation station = new RadioStation(mCurrentChannelNumber, 0 /* subchannel */,
                    mCurrentRadioBand, mCurrentRds);
            tuneToRadioChannel(station);
        } else {
            // Otherwise, ensure that the radio is on a valid radio station (i.e. it will not
            // start playing static) by initiating a seek.
            try {
                mRadioManager.seekForward();
            } catch (RemoteException e) {
                Log.e(TAG, "maybeTuneToStoredRadioChannel(); remote exception: " + e.getMessage());
            }
        }
    }

    /**
     * Delegates
     * up to {@link #mCurrentRadioBand}.
     */
    private void updateAmFmDisplayState() {

        for (UIUpdateListener listener : mUIUpdateListener) {
            listener.onUpdateRadioBand(mCurrentRadioBand);
        }
//        switch (mCurrentRadioBand) {
//            case RadioManager.BAND_FM:
//                mRadioDisplayController.setChannelBand(mFmBandString);
//                break;
//
//            case RadioManager.BAND_AM:
//                mRadioDisplayController.setChannelBand(mAmBandString);
//                break;
//
//            // TODO: Support BAND_FM_HD and BAND_AM_HD.
//
//            default:
//                mRadioDisplayController.setChannelBand(null);
//        }
    }

    /**
     * Sets the radio channel to display.
     *
     * @param channel The radio channel frequency in Hz.
     */
    private void setRadioChannel(int channel) {
        if (!Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Setting radio channel: " + channel);
        }

        if (channel <= 0) {
            mCurrentChannelNumber = channel;
//            mRadioDisplayController.setChannelNumber("");
            return;
        }

//        if (mHasDualTuners) {
//            int position = mAdapter.getIndexOrInsertForStation(channel, mCurrentRadioBand);
//            mRadioDisplayController.setCurrentStationInList(position);
//        }

        switch (mCurrentRadioBand) {
            case RadioManager.BAND_FM:
//                setRadioChannelForFm(channel);
                break;

            case RadioManager.BAND_AM:
//                setRadioChannelForAm(channel);
                break;

            // TODO: Support BAND_FM_HD and BAND_AM_HD.

            default:
                // Do nothing and don't check presets, so return here.
                return;
        }

        mCurrentChannelNumber = channel;

    }


    /**
     * Sets the internal {@link #mCurrentRadioBand} to be the given radio band. Will also take care
     * of restarting a load of the pre-scanned radio stations for the given band if there are dual
     * tuners on the device.
     */
    private void setCurrentRadioBand(int radioBand) {
        if (mCurrentRadioBand == radioBand) {
            return;
        }

        mCurrentRadioBand = radioBand;

//        if (mChannelLoader != null) {
//            mAdapter.setStations(new ArrayList<>());
//            mChannelLoader.setCurrentRadioBand(radioBand);
//            mChannelLoader.forceLoad();
//        }
    }


    /**
     * Closes all active connections in the {@link RadioController}.
     */
    public void shutdown() {
        if (!Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "shutdown()");
        }

        mContext.unbindService(mServiceConnection);
        mStationChangeListener.clear();
        mUIUpdateListener.clear();

        if (mRadioManager != null) {
            try {
                mRadioManager.removeRadioTunerCallback(mCallback);
            } catch (RemoteException e) {
                Log.e(TAG, "shutdown(); remote exception: " + e.getMessage());
            }
        }
        mRadioManager = null;
    }

    /**
     * Stop to scan or seek any channel {@link RadioController}.
     */
    public void stopScan() {
        Log.d(TAG, "stopScan()");
        if (mRadioManager != null) {
            try {
                mRadioManager.stop();
            } catch (RemoteException e) {
                Log.e(TAG, "stopScan(); remote exception: " + e.getMessage());
            }
        }
    }

    /**
     * Initializes all the extra components that are needed if this radio has dual tuners.
     */
    private void initializeDualTunerController() {
        if (!Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "initializeDualTunerController()");
        }
    }

    private final IRadioCallback mCallback = new IRadioCallback.Stub() {
        @Override
        public void onRadioPreScannedChanged(List<RadioStation> preScanned, int band) {
//            Log.d(TAG, "onRadioPreScannedChanged: " + preScanned);
            for (RadioStationChangeListener listener : mStationChangeListener) {
                listener.onRadioPreScannedChanged(preScanned, band);
            }
        }

        @Override
        public void onRadioPresetsChanged(List<RadioStation> presets) {
//            Log.d(TAG, "onRadioPresetsChanged: " + presets);
            for (RadioStationChangeListener listener : mStationChangeListener) {
                listener.onRadioPresetsChanged(presets);
            }
        }

        @Override
        public void onRadioStationPosition(int position) {
//            Log.d(TAG, "onRadioPresetsChanged: " + presets);
            for (UIUpdateListener listener : mUIUpdateListener) {
                listener.onUpdatePosition(position);
            }
        }

        @Override
        public void onRadioChannelString(String channel) {
            Log.d(TAG, "onRadioChannelString: " + channel + " size = " + mUIUpdateListener.size());
            for (UIUpdateListener listener : mUIUpdateListener) {
                listener.onRadioChannelString(channel);
            }
        }

        @Override
        public void onRadioStationChanged(RadioStation station) {
            if (!Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "onRadioStationChanged: " + station);
            }
            Log.d(TAG, "onRadioStationChanged: " + station);
//            try {
//                mRadioManager.unMute();
//            } catch (RemoteException e) {
//                Log.e(TAG, "onRadioStationChanged(); remote exception: " + e.getMessage());
//            }
            if (station == null) {
                return;
            }

            if (mCurrentChannelNumber != station.getChannelNumber()) {
                setRadioChannel(station.getChannelNumber());
            }

//            onRadioMetadataChanged(station.getRds());

            // Notify that the current radio station has changed.
            for (RadioStationChangeListener listener : mStationChangeListener) {
                try {
                    listener.onRadioStationChanged(
                            mRadioManager.getCurrentRadioStation());
                } catch (RemoteException e) {
                    Log.e(TAG, "onRadioStationChanged(); remote exception: " + e.getMessage());
                }
            }
        }

        /**
         * Updates radio information based on the given {@link RadioRds}.
         */
        @Override
        public void onRadioMetadataChanged(RadioRds radioRds) {
            if (!Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "onMetadataChanged(); metadata: " + radioRds);
            }
            mCurrentRds = radioRds;
        }

        @Override
        public void onRadioBandChanged(int radioBand) {
            if (!Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "onRadioBandChanged: " + radioBand);
            }

            setCurrentRadioBand(radioBand);
            updateAmFmDisplayState();

            // Check that the radio channel is being correctly formatted.
            setRadioChannel(mCurrentChannelNumber);
        }

        @Override
        public void onRadioMuteChanged(boolean isMuted) {
            Log.d(TAG, "onRadioMuteChanged isMuted: " + isMuted);
            setPlayPauseButtonState(isMuted);
        }

        @Override
        public void onError(int status) {
            Log.e(TAG, "Radio callback error with status: " + status);
        }
    };

    /**
     * Click listener for the play/pause button. Currently, all this does is mute/unmute the radio
     * because the {@link RadioManager} does not support the ability to pause/start again.
     */
    public void switchPlayPause() {
        if (mRadioManager == null) {
            return;
        }

        try {
            if (!Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Play button clicked. Currently muted: " + mRadioManager.isMuted());
            }

            if (mRadioManager.stop()) {
                Log.d(TAG, "switchPlayPause:  stopped");
                return;
            }

            if (mRadioManager.isMuted()) {
                mRadioManager.unMute();
            } else {
                mRadioManager.mute();
            }

            boolean isMuted = mRadioManager.isMuted();

            mUserHasMuted = isMuted;
//            if(mUserHasMuted) {
//                mUserHasMuted = false;
//            }
//            else {
//                mUserHasMuted = true;
//            }
            setPlayPauseButtonState(mUserHasMuted);
        } catch (RemoteException e) {
            Log.e(TAG, "switchPlayPause(); remote exception: " + e.getMessage());
        }
    }

    public void unMute() {

        if (mRadioManager == null) {
            return;
        }

        try {
            if (!Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Play button clicked. Currently muted: " + mRadioManager.isMuted());
            }

            if (mRadioManager.isMuted()) {
                mRadioManager.unMute();
            }

            boolean isMuted = mRadioManager.isMuted();
            mUserHasMuted = isMuted;
//            mUserHasMuted = false;
            setPlayPauseButtonState(mUserHasMuted);
        } catch (RemoteException e) {
            Log.e(TAG, "unMute(); remote exception: " + e.getMessage());
        }
    }

    public void mute() {

        if (mRadioManager == null) {
            return;
        }

        try {
            if (!Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Play button clicked. Currently muted: " + mRadioManager.isMuted());
            }

            if (!mRadioManager.isMuted()) {
                mRadioManager.mute();
            }

            boolean isMuted = mRadioManager.isMuted();
            mUserHasMuted = isMuted;
//            mUserHasMuted = false;
            setPlayPauseButtonState(mUserHasMuted);
        } catch (RemoteException e) {
            Log.e(TAG, "mute(); remote exception: " + e.getMessage());
        }
    }

    public void play(int band) {
        Log.d(TAG, "play() band = " + band);
        if (mRadioManager == null) {
            Log.e(TAG, "play() error");
            return;
        }

        try {
            mRadioManager.play(band);
        } catch (RemoteException e) {
            Log.e(TAG, "play(); remote exception: " + e.getMessage());
        }

    }

    public void directPlayNextStation() {
        Log.d(TAG, "directPlayNextStation()");
        if (mRadioManager == null) {
            Log.e(TAG, "directPlayNextStation() error");
            return;
        }

        try {
            mRadioManager.playNext();
        } catch (RemoteException e) {
            Log.e(TAG, "directPlayNextStation(); remote exception: " + e.getMessage());
        }
    }

    public void directPlayPrevStation() {
        Log.d(TAG, "directPlayPrevStation()");
        if (mRadioManager == null) {
            Log.e(TAG, "directPlayPrevStation() error");
            return;
        }

        try {
            mRadioManager.playPrev();
        } catch (RemoteException e) {
            Log.e(TAG, "directPlayPrevStation(); remote exception: " + e.getMessage());
        }

    }

    public void playNextStation() {
        Log.d(TAG, "playNextStation()");
        if (mRadioManager == null) {
            Log.e(TAG, "playNextStation() error");
            return;
        }

        try {
//            unMute();
            mRadioManager.seekForward();
//            mRadioManager.playNext();
        } catch (RemoteException e) {
            Log.e(TAG, "playNextStation(); remote exception: " + e.getMessage());
        }

    }

    public void playPrevStation() {
        Log.d(TAG, "playPrevStation()");
        if (mRadioManager == null) {
            Log.e(TAG, "playPrevStation() error");
            return;
        }

        try {
//            unMute();
            mRadioManager.seekBackward();
//            mRadioManager.playPrev();
        } catch (RemoteException e) {
            Log.e(TAG, "playPrevStation(); remote exception: " + e.getMessage());
        }

    }

    public void togglePreset() {
        Log.d(TAG, "togglePreset()");
        if (mRadioManager == null) {
            Log.e(TAG, "togglePreset() error");
            return;
        }

        try {
            mRadioManager.togglePreset();
        } catch (RemoteException e) {
            Log.e(TAG, "togglePreset(); remote exception: " + e.getMessage());
        }

    }

    public void searchStation() {
        Log.d(TAG, "searchStation()");
        if (mRadioManager == null) {
            Log.e(TAG, "searchStation() error");
            return;
        }

        try {
//            unMute();
            mRadioManager.search();
        } catch (RemoteException e) {
            Log.e(TAG, "searchStation(); remote exception: " + e.getMessage());
        }

    }

    public void switchBand() {
        Log.d(TAG, "switchBand()");
        if (mRadioManager == null)

        {
            Log.e(TAG, "switchBand() error");
            return;
        }

        try

        {
            mRadioManager.switchBand();
        } catch (
                RemoteException e)

        {
            Log.e(TAG, "switchBand(); remote exception: " + e.getMessage());
        }
    }


    public void tune(RadioStation station) {

        Log.d(TAG, "tune()");
        if (mRadioManager == null) {
            Log.e(TAG, "tune() error");
            return;
        }

        try {
            mRadioManager.tune(station);
        } catch (RemoteException e) {
            Log.e(TAG, "tune(); remote exception: " + e.getMessage());
        }
    }

    public void playIndexInList(int index) {

        Log.d(TAG, "playIndexInList()");
        if (mRadioManager == null) {
            Log.e(TAG, "playIndexInList() error");
            return;
        }

        try {
            mRadioManager.playIndexInList(index);
        } catch (RemoteException e) {
            Log.e(TAG, "playIndexInList(); remote exception: " + e.getMessage());
        }
    }

    public void playIndexInPresetList(int index) {

        Log.d(TAG, "getPresetOfIndex()");
        if (mRadioManager == null) {
            Log.e(TAG, "getPresetOfIndex() error");
            return;
        }

        try {
            mRadioManager.playIndexInPresetList(index);
        } catch (RemoteException e) {
            Log.e(TAG, "getPresetOfIndex(); remote exception: " + e.getMessage());
        }
    }

    public void deletePresetOfIndex(int index) {

        Log.d(TAG, "deletePresetOfIndex()");
        if (mRadioManager == null) {
            Log.e(TAG, "deletePresetOfIndex() error");
            return;
        }

        try {
            mRadioManager.deletePresetOfIndex(index);
        } catch (RemoteException e) {
            Log.e(TAG, "deletePresetOfIndex(); remote exception: " + e.getMessage());
        }
    }

    public void store() {

        Log.d(TAG, "store()");
        if (mRadioManager == null) {
            Log.e(TAG, "store() error");
            return;
        }

        try {
            mRadioManager.store();
        } catch (RemoteException e) {
            Log.e(TAG, "store(); remote exception: " + e.getMessage());
        }
    }

    public void setPresetChannel() {
        if (mCurrentChannelNumber == INVALID_RADIO_CHANNEL) {
            if (!Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Attempting to store invalid radio station as a preset. Ignoring");
            }

            return;
        }

        RadioStation station = new RadioStation(mCurrentChannelNumber, 0 /* subchannel */,
                mCurrentRadioBand, mCurrentRds);
        boolean isPreset = false;

        if (!Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Toggling preset for " + station
                    + "\n\tIs currently a preset: " + isPreset);
        }
    };

    private void setPlayPauseButtonState(boolean isMuteFlag) {
        Log.d(TAG, "switchPlayPause: ");
        mPlayState = PlayState.play;
        if (isMuteFlag) {
            mPlayState = PlayState.pause;
        }
        for (UIUpdateListener listener : mUIUpdateListener) {
            listener.onUpdatePlayState(mPlayState);
        }
    }

    private PlayState mPlayState;

    public PlayState getCurrentPlayState() {
        return mPlayState;
    }

    public List<RadioStation> getPresetsList() {
        try {
            return mRadioManager.getPresetsList();
        } catch (RemoteException e) {
            Log.e(TAG, "getPresetsList(); remote exception: " + e.getMessage());
        }
        return null;
    }

    public List<RadioStation> getPreScanned() {
        try {
            return mRadioManager.getPreScanned();
        } catch (RemoteException e) {
            Log.e(TAG, "getPreScanned(); remote exception: " + e.getMessage());
        }
        return null;
    }

    public int getCurrentRadioPosition() {
        try {
            return mRadioManager.getCurrentRadioPosition();
        } catch (RemoteException e) {
            Log.e(TAG, "getCurrentRadioPosition(); remote exception: " + e.getMessage());
        }
        return -1;
    }

    public boolean isInitialized() {
        return (mRadioManager != null);
    }
}
