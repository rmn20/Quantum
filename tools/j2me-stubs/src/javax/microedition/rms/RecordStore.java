package javax.microedition.rms;
public class RecordStore {
    public static final int AUTHMODE_PRIVATE = 0, AUTHMODE_ANY = 1;
    public static RecordStore openRecordStore(String name, boolean create) throws RecordStoreException { return null; }
    public static RecordStore openRecordStore(String name, boolean create, int mode) throws RecordStoreException { return null; }
    public static RecordStore openRecordStore(String name, String vendor, String suite) throws RecordStoreException { return null; }
    public static void deleteRecordStore(String name) throws RecordStoreException {}
    public static String[] listRecordStores() { return null; }
    public void closeRecordStore() throws RecordStoreException {}
    public int addRecord(byte[] data, int off, int len) throws RecordStoreException { return 0; }
    public byte[] getRecord(int id) throws RecordStoreException { return null; }
    public int getRecord(int id, byte[] buf, int off) throws RecordStoreException { return 0; }
    public void setRecord(int id, byte[] data, int off, int len) throws RecordStoreException {}
    public void deleteRecord(int id) throws RecordStoreException {}
    public int getNumRecords() throws RecordStoreException { return 0; }
    public int getNextRecordID() throws RecordStoreException { return 0; }
    public int getSize() throws RecordStoreException { return 0; }
    public int getSizeAvailable() throws RecordStoreException { return 0; }
    public String getName() throws RecordStoreException { return null; }
    public long getLastModified() throws RecordStoreException { return 0; }
}
