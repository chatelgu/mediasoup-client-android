package org.mediasoup.droid;

import org.webrtc.CalledByNative;
import org.webrtc.MediaStreamTrack;
import org.webrtc.RTCUtils;
import org.webrtc.RtpParameters;

import java.util.List;

public class SendTransport extends Transport {

  /** This is an abstract class which must be implemented and used according to the API. */
  public interface Listener extends Transport.Listener {

    /**
     * Emitted when the transport needs to transmit information about a new producer to the
     * associated server side transport. This event occurs before the produce() method completes.
     *
     * @param transport SendTransport instance.
     * @param kind Producer's media kind ("audio" or "video").
     * @param rtpParameters Producer's RTP parameters.
     * @param appData Custom application data as given in the transport.produce() method.
     * @return std::future<std::string> ID of the producer created in server side mediasoup
     */
    @CalledByNative("Listener")
    String onProduce(Transport transport, String kind, String rtpParameters, String appData);

    /**
     * Emitted when the transport needs to transmit information about a new data producer to the
     * associated server side transport. This event occurs before the produceData() method
     * completes.
     *
     * @param transport SendTransport instance.
     * @param sctpStreamParameters sctpStreamParameters.
     * @param label A label which can be used to distinguish this DataChannel from others.
     * @param protocol Name of the sub-protocol used by this DataChannel.
     * @param appData Custom application data as given in the transport.produceData() method.
     * @return std::future<std::string> ID of the data producer created in server side mediasoup
     */
    @CalledByNative("Listener")
    String onProduceData(
        Transport transport,
        String sctpStreamParameters,
        String label,
        String protocol,
        String appData);
  }

  private long mNativeTransport;

  @CalledByNative
  public SendTransport(long nativeTransport) {
    mNativeTransport = nativeTransport;
  }

  /** dispose */
  public void dispose() {
    checkTransportExists();
    nativeFreeTransport(mNativeTransport);
    mNativeTransport = 0;
  }

  private void checkTransportExists() {
    if (mNativeTransport == 0) {
      throw new IllegalStateException("SendTransport has been disposed.");
    }
  }

  @Override
  public long getNativeTransport() {
    return nativeGetNativeTransport(mNativeTransport);
  }

  /**
   * Instructs the transport to send an audio or video track to the mediasoup router.
   *
   * @param listener Producer listener.
   * @param track An audio or video track.
   * @param encodings Encoding settings.
   * @param codecOptions Per codec specific options.
   * @param codec codec.
   * @return {@link Producer}
   * @throws MediasoupException
   */
  public Producer produce(
      Producer.Listener listener,
      MediaStreamTrack track,
      List<RtpParameters.Encoding> encodings,
      String codecOptions,
      String codec)
      throws MediasoupException {
    return produce(listener, track, encodings, codecOptions, codec, null);
  }

  /**
   * Instructs the transport to send an audio or video track to the mediasoup router.
   *
   * @param listener Producer listener.
   * @param track An audio or video track.
   * @param encodings Encoding settings.
   * @param codecOptions Per codec specific options.
   * @param codec codec.
   * @param appData Custom application data.
   * @return {@link Producer}
   * @throws MediasoupException
   */
  public Producer produce(
      Producer.Listener listener,
      MediaStreamTrack track,
      List<RtpParameters.Encoding> encodings,
      String codecOptions,
      String codec,
      String appData)
      throws MediasoupException {
    checkTransportExists();
    long nativeTrack = RTCUtils.getNativeMediaStreamTrack(track);
    RtpParameters.Encoding[] pEncodings = null;
    if (encodings != null && !encodings.isEmpty()) {
      pEncodings = new RtpParameters.Encoding[encodings.size()];
      encodings.toArray(pEncodings);
    }
    return nativeProduce(
        mNativeTransport, listener, nativeTrack, pEncodings, codecOptions, codec, appData);
  }

  private static native long nativeGetNativeTransport(long transport);

  // may throws MediasoupException
  private static native Producer nativeProduce(
      long transport,
      Producer.Listener listener,
      long track,
      RtpParameters.Encoding[] encodings,
      String codecOptions,
      String codec,
      String appData);

  private static native void nativeFreeTransport(long transport);
}
