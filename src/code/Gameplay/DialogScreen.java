package code.Gameplay;

import code.HUD.Base.Font;
import code.HUD.Base.ItemList;
import code.HUD.Base.GameKeyboard;
import code.HUD.Base.TextView;
import code.Rendering.DirectX7;
import code.utils.QFPS;
import code.utils.GameIni;
import code.utils.ImageResize;
import code.utils.Main;
import code.utils.StringTools;
import code.utils.canvas.MyCanvas;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 *
 * @author DDDENISSS
 */
public class DialogScreen extends MyCanvas {

    private DirectX7 g3d;
    private GameKeyboard keyboard;
    private Main main;
    private GameScreen gs;
    
    private TextView textView;
    
    private ItemList itemList;
    private boolean itemListHasCaption;
    private int[] answersGoIndex;
    
    private Image[] bckList = new Image[10];
    private Image avatar;

    private int index = 0;
    private String[] dialog;
    private boolean scrollDown = false;
    private boolean scrollUp = false;

    public DialogScreen(Font font, DirectX7 g3d, Main main, GameScreen gs) {
        keyboard = new GameKeyboard();

        this.g3d = g3d;
        this.main = main;
        this.gs = gs;
    }

    public DialogScreen(String text, Font font, DirectX7 g3d, Main main, GameScreen gs) {
        keyboard = new GameKeyboard();
        set(text,font,g3d,main,gs);
    }

    public void set(String text, Font font, DirectX7 g3d, Main main, GameScreen gs) {
        keyboard.reset();

        this.g3d = g3d;
        this.main = main;
        this.gs = gs;
        itemList = null;
        index = -1;
        
        String newText = null;
        
        if(text.charAt(0)=='/' && text.toLowerCase().endsWith(".txt")) {
            newText = StringTools.getStringFromResource(text); //Load text from file
        }
        
        if(newText==null) dialog = StringTools.cutOnStrings(text, '@'); 
        else dialog = StringTools.cutOnStrings(newText, '\n'); 
        
        
        for (int i = 0; i < dialog.length; i++) {
            String s = dialog[i];
            if (s.charAt(0) == '\n') s = s.substring(1, s.length());
            if (s.charAt(s.length() - 1) == '\n') s = s.substring(0, s.length() - 1);
            
            dialog[i] = s;
        }

        int h = (int) (getHeight() / 3.5f);
        if (h < font.height() * 3) h = font.height() * 3;
        
        textView = new TextView(null, g3d.getWidth() - 20, h, font);
        
        nextText();
    }

    protected final void keyReleased(int keyCode) {
        if (keyCode == keyboard.SOFT_LEFT) {
            keyboard.keyReleased(keyboard.FIRE);
            return;
        }
        keyboard.keyReleased(keyCode);
    }

    protected final void keyPressed(int keyCode) {
        if (keyCode == keyboard.SOFT_LEFT) {
            keyboard.keyPressed(keyboard.FIRE);
            return;
        }
        keyboard.keyPressed(keyCode);
        
        if(keyCode == keyboard.DOWN || keyCode == Canvas.KEY_NUM8) {
            scrollDown = true;
        } else if(keyCode == keyboard.UP || keyCode == Canvas.KEY_NUM2) {
            scrollUp = true;
        }
    }

    protected final void pointerPressed(int x, int y) {
        keyboard.keyPressed(keyboard.FIRE);
    }

    protected final void pointerReleased(int x, int y) {
        keyboard.keyReleased(keyboard.FIRE);
    }

    protected final void mouseScrollUp() {
        scrollUp = true;
    }

    protected final void mouseScrollDown() {
        scrollDown = true;
    }

    private boolean nextText() {
        
        if(itemList != null) {
            if(answersGoIndex==null ) index += itemList.getItems().length+itemList.getIndex()-(itemListHasCaption?1:0); // Additional move
            else index = answersGoIndex[itemList.getIndex()-(itemListHasCaption?1:0)]-1;
            
            itemList = null; answersGoIndex = null;
        }
        
        
        if (index + 1 < dialog.length) {
            index++;
            
            if(dialog[index].charAt(0)!='$') {
                textView.setString(dialog[index]);
                textView.setY(0);
                return true;
            } else {
                String text = dialog[index];
                
                int spacePlace = text.indexOf(' ');
                
                String script;
                if(spacePlace!=-1) script = text.substring(1, spacePlace).toLowerCase();
                else script = text.substring(1);
                
                String option = text;
                if(spacePlace!=-1) option = text.substring(spacePlace+1);
                
                
                
                if(script.equals("exec")) {
                    //execute script
                    
                    gs.runScriptFromFile(option);
                    return nextText();
                } else if(script.equals("if")) {
                    //condition check
                    
                    if(!gs.readBooleanFromScript(option)) index++;
                    
                    return nextText();
                } else if(script.equals("cmd")) {
                    //run one script line
                    
                    gs.runScript(new String[]{option});
                    
                    return nextText();
                } else if(script.startsWith("bck")) {
                    //set background
                    final String[] scalingList = new String[]{ //Scaling types
                        "fith", "fitw", "full", "fit3dh", "full3d", "orig"
                    };
                    
                    int type = GameIni.startsWith(option,scalingList);
                    if(type==-1) type=0;
                    else option = option.substring(option.indexOf(' ')+1);
                    //default is fith
                    
                    
                    int layer = script.charAt(script.length()-1)-'0'; //Background layer
                    
                    if(option.equalsIgnoreCase("clear")) {
                        if(layer<0 || layer>9) clearBcks(); //Clear all backgrounds
                        else bckList[layer]=null; //Clear only one background
                    } else {
                        loadNewBck(type,option,layer); //Load new background into layer
                    }
                    
                    return nextText();
                } else if(script.startsWith("avatar")) {
                    //set avatar
                    
                    if(option.equalsIgnoreCase("clear")) {
                        avatar = null;
                    } else {
                        loadNewAvatar(option); //Load new background into layer
                    }
                    
                    return nextText();
                } else if(script.equals("go")) {
                    //go to line
                    
                    return goToLabel(option);
                } else if(script.equals("question")) {
                    //ask question
                    
                    String[] arguments = StringTools.cutOnStrings(option, ' '); // &question& length is 10
                    boolean generative = (arguments.length>=2 && arguments[0].equalsIgnoreCase("gen"));
                    itemListHasCaption = !(arguments.length>=2 && arguments[arguments.length-2].equalsIgnoreCase("nocap"));
                    int capLen = (itemListHasCaption?1:0);
                    
                    int answers = StringTools.parseInt(arguments[arguments.length-1]);
                    String[] items;
                    
                    if(generative) {
                        int newAnswers = 0;
                        String[] allItems = new String[answers];
                        answersGoIndex = new int[answers];
                        
                        for(int i=0;i<answers;i++) {
                            String answer = dialog[index+1+capLen+i];
                            String condition = dialog[index+1+capLen+i+answers];
                            
                            if(gs.readBooleanFromScript(condition)) {
                                allItems[newAnswers] = answer;
                                answersGoIndex[newAnswers] = index+1+capLen+i+answers*2;
                                newAnswers++;
                            }
                        }
                        
                        if(newAnswers == 0) {
                            index += capLen+answers*3;
                            return nextText();
                        }
                        
                        items = new String[ newAnswers + capLen ]; //Questions count + caption
                        System.arraycopy(allItems, 0, items, capLen, newAnswers); //Copy questions to itemList
                        
                    } else {
                        items = new String[ answers + capLen ]; //Questions count + caption
                        System.arraycopy(dialog, index+capLen+1, items, capLen, answers); //Copy questions to itemList
                    }
                    
                    if(itemListHasCaption) items[0] = dialog[index+1]; //Caption
                    
                    itemList = new ItemList(items, textView.getFont());
                    itemList.left = true;
                    itemList.setIndex(capLen);
                    
                    return true;
                } else if(script.equals("end")) {
                    //exit
                    
                    return false;
                } else {
                    return nextText();
                }
            }
            
        }
        return false;
    }
    
    private void loadNewBck(int scaling, String path, int layer) {
        try {
            bckList[layer] = null;
            
            Image img = Image.createImage(path);
            if(scaling != 5) {
                int w = img.getWidth();
                int h = img.getHeight();
                
                if(scaling == 0) { //fith
                    w = w*getHeight()/h;
                    h = getHeight();
                } else if(scaling == 1) { //fitw
                    h = h*getWidth()/w;
                    w = getWidth();
                } else if(scaling == 2) { //full
                    h = getHeight();
                    w = getWidth();
                } else if(scaling == 3) { //fit3dh
                    w = w*(getHeight()*Main.getDisplaySize()/100)/h;
                    h = getHeight()*Main.getDisplaySize()/100;
                } else if(scaling == 4) { //full3d
                    w = getWidth();
                    h = getHeight()*Main.getDisplaySize()/100;
                }
                
                img = ImageResize.bilinearResizeImage(img, w, h);
            }
            
            bckList[layer] = img;
            
        } catch(Exception e) {
            bckList[layer]=null;
            return;
        }
    }
    
    private void loadNewAvatar(String path) {
        try {
            avatar = Image.createImage(path);
        } catch(Exception e) {
            avatar=null;
            return;
        }
    }
    
    private void clearBcks() {
        for(int i=0;i<bckList.length;i++) {
            bckList[i]=null;
        }
    }
    
    private boolean goToLabel(String option) {
        for (int lineId = 0; lineId < dialog.length; lineId++) {
            String line = dialog[lineId];

            if (option.length() == line.length() - 2 && //Is line length equals label naming length
                    line.charAt(0) == '$' && line.charAt(line.length() - 1) == ':' && //Is this a label
                    line.indexOf(option) == 1) { //Is label name equals option
                index = lineId;
                return nextText();
            }
        }

        System.out.println("Can't find label '"+option+"'");
        return false;
    }

    protected void paint(Graphics g) {
        g.setColor(0);
        g.setClip(0, 0, getWidth(), getHeight());
        g.translate(0, 0);
        g.fillRect(0, 0, getWidth(), getHeight());

        int sy = getHeight() / 2 - g3d.getHeight() / 2;
        g.setColor(255, 255, 255);
        g3d.flush(g, 0, sy);
        
        for(int i=0;i<bckList.length;i++) {
            Image bck = bckList[i];
            if(bck==null) continue;
            g.drawImage(bck, getWidth()/2, getHeight()/2, 3);
        }

        int x = (getWidth() - textView.getWidth()) / 2;
        int y = getHeight() - textView.getHeight() - Main.bcks.getHeight() - 4;
        Main.drawBckDialog(g, y, textView.getHeight() + y);

        int textBegin, textEnd;
        if(itemList == null) {
            textBegin = y+textView.getY();
            textEnd = textBegin + textView.getTextHeight();
            textView.paint(g, x, y);
        } else { //Draw question
            textBegin = y+itemList.getPosY(textView.getHeight());
            textEnd = textBegin + itemList.getHeight();
            itemList.draw(g, x, y, textView.getWidth(), textView.getHeight());
        }
        
        if(avatar != null) g.drawImage(avatar, 0, y-avatar.getHeight(), 0);
        
        if((textView.getTextHeight() > textView.getHeight() && itemList==null) || 
               (itemList!=null && itemList.getHeight() > textView.getHeight())) {
                g.setColor(255, 255, 255);
                
                if(textEnd>y+textView.getHeight()) g.fillTriangle( //Down arrow
                        getWidth() - 13, y + textView.getHeight() - 13, 
                        getWidth() - 3, y + textView.getHeight() - 13, 
                        getWidth() - 8, y + textView.getHeight() - 3);
                
                if(textBegin<y) g.fillTriangle( //Up arrow
                        getWidth() - 13, y + 13, 
                        getWidth() - 3, y + 13, 
                        getWidth() - 8, y + 3);
        }
        
        
        try {
            Thread.sleep(20L);
        } catch (Exception exc) {}

        step();
    }

    private void step() {

        if(itemList == null) {
            if (scrollDown || keyboard.down) textView.move(-3);
            if (scrollUp || keyboard.up) textView.move(3);
        } else {
            if (scrollDown) {
                itemList.scrollDown();
                if(itemList.getIndex()==0 && itemListHasCaption) itemList.setIndex(1);
            }
            if (scrollUp) {
                itemList.scrollUp();
                if(itemList.getIndex()==0 && itemListHasCaption) itemList.setIndex(itemList.getItems().length-1);
            }
        }
        
        scrollDown = false;
        scrollUp = false;
        if (keyboard.fire) {
            keyboard.reset();
            if (!nextText()) {
                gs.start();
                gs.paused = false;
                clearBcks();
                avatar = null;
                itemList = null;
                answersGoIndex = null;
                dialog = null;

                QFPS.miniReset();
                Main.setCurrent(gs);
                QFPS.miniReset();
                return;
            }
        }
        repaint();
    }
    
    public static String loadTextFromFile(String text) {
        if(text==null) return null;
        
        if(text.charAt(0)!='/' || !text.toLowerCase().endsWith(".txt")) return text;
        
        text = StringTools.getStringFromResource(text);
        return text.replace('\n', '@');
    }

}
