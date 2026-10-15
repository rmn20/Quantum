package javax.microedition.media;
public interface PlayerListener {
    String STARTED = "started";
    String STOPPED = "stopped";
    String END_OF_MEDIA = "endOfMedia";
    String CLOSED = "closed";
    String VOLUME_CHANGED = "volumeChanged";
    String DEVICE_AVAILABLE = "deviceAvailable";
    String DEVICE_UNAVAILABLE = "deviceUnavailable";
    String RECORD_STARTED = "recordStarted";
    String RECORD_STOPPED = "recordStopped";
    String RECORD_ERROR = "recordError";
    String BUFFERING_STARTED = "bufferingStarted";
    String BUFFERING_STOPPED = "bufferingStopped";
    String ERROR = "error";
    void playerUpdate(Player player, String event, Object eventData);
}
