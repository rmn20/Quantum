package code.HUD;

import code.utils.Main;
import code.utils.canvas.MyCanvas;
import javax.microedition.lcdui.*;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;

public class VideoPlayer implements PlayerListener {
  
  private String file;
  private Main main;
  private Player player;
  private Form videoScreen;
  private MyCanvas original;
  
  public VideoPlayer (Main main,String file,MyCanvas original) {
      this.file=file;
      this.main=main;
      videoScreen=new Form("Quantum engine");
      main.setCanvas(videoScreen);
      this.original=original;
      playMedia(file);
  }

  private void playMedia(String file) {
    try{

    String type="video/mp4";
    String file2 = file.toLowerCase();
    if(file2.endsWith(".3gp")) type="video/3gpp";
    player = Manager.createPlayer(getClass().getResourceAsStream(file), type);

    player.addPlayerListener(this);

    player.setLoopCount(1);
    player.prefetch();
    player.realize();

    player.start();
    } catch(Exception e) {}
  }

  public void playerUpdate(Player player, String event, Object eventData) {
try{
    if (event.equals(PlayerListener.STARTED) && new Long(0L).equals((Long)eventData))
        {
        //VideoControl vc = null;
     /*   if((vc = (VideoControl)player.getControl("VideoControl")) != null)
                {
          Item videoDisp =
          (Item)vc.initDisplayMode(VideoControl.USE_DIRECT_VIDEO, null);
          videoScreen.append(videoDisp);
          vc.setDisplayFullScreen(true);
          vc.setVisible(true);
        }*/
    } 
        else if(event.equals(PlayerListener.CLOSED))
        {
      videoScreen.deleteAll(); 
      player.close();
      main.resetCanvas();
      Main.setCurrent(original);
    }
}catch(Exception e){}
  }

}