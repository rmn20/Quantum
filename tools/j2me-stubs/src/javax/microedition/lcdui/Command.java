package javax.microedition.lcdui;
public class Command {
    public static final int SCREEN = 1, BACK = 2, CANCEL = 3, OK = 4, HELP = 5, STOP = 6, EXIT = 7, ITEM = 8;
    public Command(String label, int type, int prio) {}
    public Command(String shortLabel, String longLabel, int type, int prio) {}
    public String getLabel() { return null; }
    public String getLongLabel() { return null; }
    public int getCommandType() { return 0; }
    public int getPriority() { return 0; }
}
