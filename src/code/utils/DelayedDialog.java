package code.utils;

import code.Gameplay.GameScreen;
import code.HUD.Base.TextView;

/**
 *
 * @author Roman Lahin
 */
public class DelayedDialog {

    public long messageTimeOut = -1L;
    public int messageType = 0;
    public String message = null;
    public long timeToShow = 0L;
    public long lastCheck = -1L;
    public long time = 0L;

    public DelayedDialog(long mto, int mt, String msg, long tts) {
        this.messageTimeOut = mto;
        this.messageType = mt;
        this.message = msg;
        this.timeToShow = tts;
    }

    public boolean update(GameScreen gs) {
        if(lastCheck == -1L) lastCheck = System.currentTimeMillis();
        time += System.currentTimeMillis() - lastCheck;

        if(time >= timeToShow) {
            activateDialog(gs);
            return true;
        }

        lastCheck = System.currentTimeMillis();
        return false;
    }

    private void activateDialog(GameScreen gs) {
        if(messageType > 0) {
            gs.customMessage = message;
            gs.customMessagePause = false;
            gs.customMessageEndTime = System.currentTimeMillis() + messageTimeOut;
            if(messageType == 2) {
                gs.customMessagePause = true;
                gs.customMessageEndTime = messageTimeOut;
            }
            GameScreen.lines.removeAllElements();
            TextView.createLines(message, GameScreen.lines, gs.font, GameScreen.width);
        }
        if(messageType == 0) {
            gs.showDialog(message);
        }
    }

}
