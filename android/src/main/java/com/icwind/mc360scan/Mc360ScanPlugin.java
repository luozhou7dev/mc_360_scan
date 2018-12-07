package com.icwind.mc360scan;

import com.symbol.scanning.BarcodeManager;
import com.symbol.scanning.ScanDataCollection;
import com.symbol.scanning.Scanner;
import com.symbol.scanning.ScannerException;
import com.symbol.scanning.ScannerInfo;

import java.util.ArrayList;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * Mc360ScanPlugin
 */
public class Mc360ScanPlugin implements MethodChannel.MethodCallHandler, EventChannel.StreamHandler {
    private Scanner mScanner;

    private EventChannel.EventSink mEventSink;

    private Scanner.DataListener mDataListener = new Scanner.DataListener() {
        @Override
        public void onData(ScanDataCollection scanDataCollection) {
            String data = "";
            ArrayList<ScanDataCollection.ScanData> scanDataList = scanDataCollection.getScanData();
            for (ScanDataCollection.ScanData scanData : scanDataList) {
                data = scanData.getData();
            }
            mEventSink.success(data);
            stopScanning();
        }
    };

    private Mc360ScanPlugin() {
        try {
            BarcodeManager mBarcodeManager = new BarcodeManager();
            ScannerInfo mInfo = new ScannerInfo("se4710_cam_builtin", "DECODER_2D");
            mScanner = mBarcodeManager.getDevice(mInfo);
            mScanner.enable();
            mScanner.addDataListener(mDataListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        final MethodChannel methodChannel = new MethodChannel(registrar.messenger(), "mc_360_scan/method_channel");
        final EventChannel eventChannel = new EventChannel(registrar.messenger(), "mc_360_scan/event_channel");

        final Mc360ScanPlugin instance = new Mc360ScanPlugin();
        methodChannel.setMethodCallHandler(instance);
        eventChannel.setStreamHandler(instance);
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        switch (call.method) {
            case "getPlatformVersion":
                result.success("Android " + android.os.Build.VERSION.RELEASE);
                break;
            case "startScanning":
                startScanning();
                break;
            case "stopScanning":
                stopScanning();
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    private void startScanning() {
        try {
            mScanner.addDataListener(mDataListener);
            if (!mScanner.isEnable()) {
                mScanner.enable();
            }
            mScanner.read();
        } catch (ScannerException se) {
            se.printStackTrace();
        }
    }

    private void stopScanning() {
        try {
            mScanner.removeDataListener(mDataListener);
            mScanner.cancelRead();
            if (mScanner.isEnable()) {
                mScanner.disable();
            }
        } catch (ScannerException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void onListen(Object o, EventChannel.EventSink eventSink) {
        this.mEventSink = eventSink;
    }


    @Override
    public void onCancel(Object o) {
        mScanner.removeDataListener(mDataListener);
    }
}
