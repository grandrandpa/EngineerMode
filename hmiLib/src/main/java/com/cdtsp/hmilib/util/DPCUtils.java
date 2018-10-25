package com.cdtsp.hmilib.util;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.widget.Toast;

import one.cluster.ClusterInteractive;
import android.qnxproxy.QnxProxyManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.os.SystemClock;

/**
 * Created by Administrator on 2018/2/27.
 */

public class DPCUtils {

    private static final String TAG = "DPCUtils";
    private static QnxProxyManager sQnxProxyManager = new QnxProxyManager();
    private static byte[] outData = new byte[512];
    private static HandlerThread sWorkThread = new HandlerThread("worker_Thread");
    static {
        sWorkThread.start();
    }
    private static Handler sWorkHandler = new Handler(sWorkThread.getLooper());

    /**
     * 创建图片信息的构造器
     * @param coverName
     * @param coverPath
     * @param coverWidth
     * @param coverHeight
     * @param coverFormart
     * @return
     */
    public static ClusterInteractive.CModuleCoverInfo.Builder createCoverInfo(String coverName, String coverPath, int coverWidth, int coverHeight, String coverFormart) {
        ClusterInteractive.CModuleCoverInfo.Builder coverInfoBuilder = ClusterInteractive.CModuleCoverInfo.newBuilder();
        coverInfoBuilder.setName(coverName);
        coverInfoBuilder.setPath(coverPath);
        coverInfoBuilder.setWidth(coverWidth);
        coverInfoBuilder.setHeight(coverHeight);
        coverInfoBuilder.setFormat(coverFormart);
        return coverInfoBuilder;
    }

    /**
     * 创建图片坐标信息的构造器
     * @param corX
     * @param corY
     * @return
     */
    public static ClusterInteractive.CModuleCoverPosition.Builder createCoverPosition(int corX, int corY) {
        ClusterInteractive.CModuleCoverPosition.Builder positionBuilder = ClusterInteractive.CModuleCoverPosition.newBuilder();
        positionBuilder.setX(corX);
        positionBuilder.setY(corY);
        return positionBuilder;
    }

    /**
     * 创建图片拖动交互信息的构造器
     * @param state 拖动状态
     * @param info 图片信息
     * @param coverposition 图片坐标
     * @return
     */
    public static ClusterInteractive.CModuleInterActive.Builder createModuleInteractive(ClusterInteractive.eModuleChangeState state, ClusterInteractive.CModuleCoverInfo info, ClusterInteractive.CModuleCoverPosition coverposition) {
        ClusterInteractive.CModuleInterActive.Builder interactiveBuilder = ClusterInteractive.CModuleInterActive.newBuilder();
        interactiveBuilder.setState(state);
        interactiveBuilder.setInfo(info);
        interactiveBuilder.setPosition(coverposition);
        return interactiveBuilder;
    }
    public static ClusterInteractive.CModuleInterActive.Builder createModuleInteractive(ClusterInteractive.eModuleChangeState state) {
        ClusterInteractive.CModuleInterActive.Builder interactiveBuilder = ClusterInteractive.CModuleInterActive.newBuilder();
        interactiveBuilder.setState(state);
        return interactiveBuilder;
    }

    /**
     * 创建仪表主题切换信息的构造器
     * @param theme
     * @return
     */
    public static ClusterInteractive.CTheme.Builder createClusterTheme(int theme) {
        ClusterInteractive.CTheme.Builder themBuilder = ClusterInteractive.CTheme.newBuilder();
        themBuilder.setTheme(theme);
        return themBuilder;
    }

    /**
     * 创建页面(tunner、media、btphone)切换信息的构造器
     * @param centerPage
     * @return
     */
    public static ClusterInteractive.CCenterPage.Builder createCenterPage(int centerPage) {
        ClusterInteractive.CCenterPage.Builder centerPageBuilder = ClusterInteractive.CCenterPage.newBuilder();
        centerPageBuilder.setCenterpage(centerPage);
        return centerPageBuilder;
    }

    /**
     * 创建音乐信息的构造器
     * @param index
     * @param title
     * @param album
     * @param artist
     * @param duration
     * @return
     */
    public static ClusterInteractive.CMediaMetaData.Builder createMediaMetaData(int index, String title, String album, String artist, int duration) {
        ClusterInteractive.CMediaMetaData.Builder mediaMetaDataBuilder = ClusterInteractive.CMediaMetaData.newBuilder();
        mediaMetaDataBuilder.setIndex(index);
        mediaMetaDataBuilder.setTitle(title);
        mediaMetaDataBuilder.setAlbum(album);
        mediaMetaDataBuilder.setArtist(artist);
        mediaMetaDataBuilder.setDuration(duration);
        return mediaMetaDataBuilder;
    }

    /**
     * 创建歌曲当前播放进度信息的构造器
     * @param position
     * @return
     */
    public static ClusterInteractive.CMediaPosition.Builder createMediaPosition(int position) {
        ClusterInteractive.CMediaPosition.Builder mediaPositionBuilder = ClusterInteractive.CMediaPosition.newBuilder();
        mediaPositionBuilder.setPosition(position);
        return mediaPositionBuilder;
    }

    /**
     * 创建当前播放状态信息的构造器
     * @param position
     * @return
     */
    public static ClusterInteractive.CMediaPlayState.Builder createMediaPlayState(ClusterInteractive.eMediaPlayState state) {
        ClusterInteractive.CMediaPlayState.Builder mediaPlayStateBuilder = ClusterInteractive.CMediaPlayState.newBuilder();
        mediaPlayStateBuilder.setState(state);
        return mediaPlayStateBuilder;
    }

    /**
     * 创建音乐封面信息的构造器
     * @param coverartId
     * @param coverart
     * @return
     */
    public static ClusterInteractive.CMediaCoverart.Builder createMediaCoverart(int coverartId, String coverart) {
        ClusterInteractive.CMediaCoverart.Builder mediaCoverartBuilder = ClusterInteractive.CMediaCoverart.newBuilder();
        mediaCoverartBuilder.setCoverartId(coverartId);
        mediaCoverartBuilder.setCoverart(coverart);
        return mediaCoverartBuilder;
    }
    public static ClusterInteractive.CMediaCoverart.Builder createMediaCoverart(int coverartId) {
        ClusterInteractive.CMediaCoverart.Builder mediaCoverartBuilder = ClusterInteractive.CMediaCoverart.newBuilder();
        mediaCoverartBuilder.setCoverartId(coverartId);
        return mediaCoverartBuilder;
    }

    /**
     * 创建音乐封面图片列表信息的构造器
     * @return
     */
    public static ClusterInteractive.CMediaCoverartList.Builder createMediaCoverartList() {
        ClusterInteractive.CMediaCoverartList.Builder mediaCoverartListBuilder = ClusterInteractive.CMediaCoverartList.newBuilder();
        return mediaCoverartListBuilder;
    }

    /**
     * 创建tunner band信息的构造器
     * @param tunerBand
     * @return
     */
    public static ClusterInteractive.CTunerBand.Builder createTunerBand(ClusterInteractive.eTunerBand tunerBand) {
        ClusterInteractive.CTunerBand.Builder tunerBandBuilder = ClusterInteractive.CTunerBand.newBuilder();
        tunerBandBuilder.setBand(tunerBand);
        return tunerBandBuilder;
    }

    /**
     * 创建收音机状态信息的构造器
     * @param state true表示在播放，false表示没播放
     * @return
     */
    public static ClusterInteractive.CTunerState.Builder createTunerState(boolean state) {
        ClusterInteractive.CTunerState.Builder tunerStateBuilder = ClusterInteractive.CTunerState.newBuilder();
        tunerStateBuilder.setState(state);
        return tunerStateBuilder;
    }

    /**
     * 创建收音机频道信息的构造器
     * @param state
     * @return
     */
    public static ClusterInteractive.CTunerStation.Builder createTunerStation(ClusterInteractive.eTunerBand band, int index, String freq, boolean playing) {
        ClusterInteractive.CTunerStation.Builder tunerStationBuilder = ClusterInteractive.CTunerStation.newBuilder();
        tunerStationBuilder.setBand(band);
        tunerStationBuilder.setIndex(index);
        tunerStationBuilder.setFreq(freq);
        tunerStationBuilder.setPlaying(playing);
        return tunerStationBuilder;
    }
    public static ClusterInteractive.CTunerStation.Builder createTunerStation(ClusterInteractive.eTunerBand band, int index, String freq) {
        ClusterInteractive.CTunerStation.Builder tunerStationBuilder = ClusterInteractive.CTunerStation.newBuilder();
        tunerStationBuilder.setBand(band);
        tunerStationBuilder.setIndex(index);
        tunerStationBuilder.setFreq(freq);
        return tunerStationBuilder;
    }

    /**
     * 创建收音机频道列表信息的构造器
     * @return
     */
    public static ClusterInteractive.CTunerStationList.Builder createTunerStationList() {
        ClusterInteractive.CTunerStationList.Builder tunerStationListBuilder = ClusterInteractive.CTunerStationList.newBuilder();
        return tunerStationListBuilder;
    }

    /**
     * 创建手机联系人信息的构造器
     * @return
     */
    public static ClusterInteractive.CPhoneContact.Builder createPhoneContact(int index, String name, String number, String photo) {
        ClusterInteractive.CPhoneContact.Builder phoneContactBuilder = ClusterInteractive.CPhoneContact.newBuilder();
        phoneContactBuilder.setIndex(index);
        phoneContactBuilder.setName(name);
        phoneContactBuilder.setNumber(number);
        phoneContactBuilder.setPhoto(photo);
        return phoneContactBuilder;
    }
    public static ClusterInteractive.CPhoneContact.Builder createPhoneContact(int index, String name, String number) {
        ClusterInteractive.CPhoneContact.Builder phoneContactBuilder = ClusterInteractive.CPhoneContact.newBuilder();
        phoneContactBuilder.setIndex(index);
        phoneContactBuilder.setName(name);
        phoneContactBuilder.setNumber(number);
        return phoneContactBuilder;
    }

    /**
     * 创建手机联系人列表信息的构造器
     * @return
     */
    public static ClusterInteractive.CPhoneContactList.Builder createPhoneListContact() {
        ClusterInteractive.CPhoneContactList.Builder phoneContactListBuilder = ClusterInteractive.CPhoneContactList.newBuilder();
        return phoneContactListBuilder;
    }

    /**
     * 创建电话信息的构造器
     * @param state
     * @param name
     * @param number
     * @param duration
     * @return
     */
    public static ClusterInteractive.CPhoneCall.Builder createPhoneCall(ClusterInteractive.ePhoneCallState state, String name, String number, int duration) {
        ClusterInteractive.CPhoneCall.Builder phoneCallBuilder = ClusterInteractive.CPhoneCall.newBuilder();
        phoneCallBuilder.setState(state);
        phoneCallBuilder.setName(name);
        phoneCallBuilder.setNumber(number);
        phoneCallBuilder.setDuration(duration);
        return phoneCallBuilder;
    }

    /**
     * 创建车灯信息构造器
     * @param state
     * @return
     */
    public static ClusterInteractive.CCarLight.Builder createCarLight(ClusterInteractive.eCarLight state) {
        ClusterInteractive.CCarLight.Builder carLightBuilder = ClusterInteractive.CCarLight.newBuilder();
        carLightBuilder.setState(state);
        return carLightBuilder;
    }

    /**
     * 创建导航引导信息构造器
     * @param naviState
     * @param direction
     * @param nextRoad
     * @param nextTurnDistance
     * @param remainDistance
     * @param remainTime
     * @return
     */
    public static ClusterInteractive.CNaviInfo.Builder createNaviInfo(ClusterInteractive.eNaviState naviState,
                ClusterInteractive.eNaviDirection direction,
                String nextRoad, String nextTurnDistance, String remainDistance, String remainTime) {
        ClusterInteractive.CNaviInfo.Builder naviInfoBuilder = ClusterInteractive.CNaviInfo.newBuilder();
        naviInfoBuilder.setState(naviState);
        naviInfoBuilder.setNextTurn(direction);
        naviInfoBuilder.setNextRoad(nextRoad);
        naviInfoBuilder.setNextTurnDistance(nextTurnDistance);
        naviInfoBuilder.setRemainDistance(remainDistance);
        naviInfoBuilder.setRemainTime(remainTime);
        return naviInfoBuilder;
    }

    public static ClusterInteractive.CTTInfo.Builder createTTInfo(ClusterInteractive.eTTType ttType, Boolean state) {
        ClusterInteractive.CTTInfo.Builder ttInfoBuilder = ClusterInteractive.CTTInfo.newBuilder();
        ttInfoBuilder.setType(ttType);
        ttInfoBuilder.setActive(state);
        return ttInfoBuilder;
    }

    public static ClusterInteractive.CGearControl.Builder createGearControl(ClusterInteractive.eGearType gear) {
        ClusterInteractive.CGearControl.Builder gearInfoBuilder = ClusterInteractive.CGearControl.newBuilder();
        gearInfoBuilder.setGear(gear);
        return gearInfoBuilder;
    }

    public static ClusterInteractive.CCarModelAction.Builder createCarModelAction(ClusterInteractive.eCarModelAction action) {
        ClusterInteractive.CCarModelAction.Builder carModelAction = ClusterInteractive.CCarModelAction.newBuilder();
        carModelAction.setAction(action);
        return carModelAction;
    }

    /**
     * 通过dpc发送消息
     * @param messageId
     * @param inData
     */
    public static void sendRequest(int messageId, byte[] inData) {
        sWorkHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "sendRequest: send startTime=" + SystemClock.uptimeMillis() + ", on thread : " + Thread.currentThread().getName() + ", messageId=" + messageId);
                sQnxProxyManager.sendASyncRequest(
                        messageId,
                        inData,
                        inData.length
                );
                Log.d(TAG, "sendRequest: send completeTime=" + SystemClock.uptimeMillis() + ", on thread : " + Thread.currentThread().getName() + ", messageId=" + messageId);
            }
        });
    }


    /**
     * 创建蓝牙连接状态的构造器
     * @return
     */
    public static ClusterInteractive.CBTState.Builder createBTStatus(one.cluster.ClusterInteractive.eBTState status) {
        ClusterInteractive.CBTState.Builder mBTStatusBuilder = ClusterInteractive.CBTState.newBuilder();
        mBTStatusBuilder.setState(status);
        return mBTStatusBuilder;
    }

}
