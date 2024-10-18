package org.mediasoup.droid;

import android.content.Context;

import org.webrtc.Loggable;
import org.webrtc.Logging;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.PeerConnectionFactory.InitializationOptions;

public class MediasoupClient {

  static {
    System.loadLibrary("mediasoupclient_so");
  }

  /**
   * libmediasoupclient initialization. Initializes libwebrtc.
   *
   * @param appContext app context
   */
  public static void initialize(Context appContext) {
    initialize(appContext, null, false);
  }

  /**
   * libmediasoupclient initialization. Initializes libwebrtc.
   *
   * @param appContext app context
   * @param fieldTrials fieldTrials desc
   */
  public static void initialize(Context appContext, String fieldTrials, Boolean logWebRtc) {
    Loggable loggable = (s, severity, s1) -> Logger.d("PeerConnectionFactory", severity+s+s1);

    InitializationOptions options =
        InitializationOptions.builder(appContext)
            .setFieldTrials(fieldTrials)
            .setEnableInternalTracer(true)
            .setNativeLibraryName("mediasoupclient_so")
            .setInjectableLogger(loggable, logWebRtc ? Logging.Severity.LS_VERBOSE : Logging.Severity.LS_WARNING)
            .createInitializationOptions();
    PeerConnectionFactory.initialize(options);
  }

  /**
   * @return The libmediasoupclient version.
   */
  public static String version() {
    return nativeVersion();
  }

  private static native String nativeVersion();
}
