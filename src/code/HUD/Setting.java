package code.HUD;

import code.HUD.Base.Selectable;
import code.utils.IniFile;
import code.utils.Main;
import code.utils.canvas.MyCanvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public final class Setting extends Selectable {

    private Main main;
    private MyCanvas menu;
    private Object background;
    private int hei;
    private int h;
    private int pr7 = 0;
    private int pr9 = 0;

    public Setting(Main main, MyCanvas menu, Object background) {
        this.main = main;
        this.menu = menu;
        this.background = background;

        IniFile lang = Main.getGameText();
		
        String[] options = new String[20 + (hasLangSelect() ? 1 : 0)];
        boolean[] center = new boolean[options.length];
        set(Main.getFont(), options, null, lang.get("BACK"), center);
        setItems();
		
        list.left = true;
        list.scrollDown();

        hei = this.getHeight() / 2 - getHeight() * Main.getDisplaySize() / 200;
        h = this.getHeight() * Main.getDisplaySize() / 100;
    }
	
	private boolean hasLangSelect() {
		return !(menu instanceof PauseScreen) && Main.langs.length > 1;
	}

    private void setItems() {
        IniFile lang = Main.getGameText();
        String[] var2;
        int i = 0;
        var2 = this.getItems();
        boolean[] ms = this.list.midSel;
        var2[i] = lang.get("AUDIO") + ":";
        ms[i] = true;
        i++;
        var2[i] = lang.get("MUSIC_VOLUME") + ":" + lang.get("OFF");
        ms[i] = false;
        if(Main.music > 0 && Main.isMusic) var2[i] = lang.get("MUSIC_VOLUME") + ":" + Main.music;
        ms[i] = false;
        i++;
        var2[i] = lang.get("FOOTSTEPS_VOLUME") + ":" + lang.get("OFF");
        ms[i] = false;
        if(Main.footsteps > 0 && Main.isFootsteps) var2[i] = lang.get("FOOTSTEPS_VOLUME") + ":" + Main.footsteps;
        i++;
        var2[i] = lang.get("SOUNDS_VOLUME") + ":" + lang.get("OFF");
        ms[i] = false;
        if(Main.sounds > 0 && Main.isSounds) var2[i] = lang.get("SOUNDS_VOLUME") + ":" + Main.sounds;
        i++;

        var2[i] = lang.get("CONTROLS") + ":";
        i++;
        ms[i - 1] = true;
        var2[i] = lang.get("CAM_SPEED") + ":" + Main.mouseSpeed;
        i++;
        ms[i - 1] = false;
        var2[i] = lang.get("CHANGE_KEYS");
        i++;
        ms[i - 1] = false;

        var2[i] = lang.get("PERF") + ":";
        i++;
        ms[i - 1] = true;
        var2[i] = lang.get("DISPLAY_SIZE") + ":" + Main.getDisplaySize();
        i++;
        ms[i - 1] = false;
        var2[i] = lang.get("PERSQ") + ":" + Main.persQ * 25;
        i++;

        var2[i] = lang.get("TEXQ") + ":" + lang.get("HQ");
        ms[i] = false;
        if(Main.pixelsQ == 0) var2[i] = lang.get("TEXQ") + ":" + lang.get("LLQ");
        if(Main.pixelsQ == 1) var2[i] = lang.get("TEXQ") + ":" + lang.get("LQ");
        i++;

        var2[i] = lang.get("FOGQ") + ":" + lang.get("HQ");
        ms[i] = false;
        if(Main.fogQ == 0) var2[i] = lang.get("FOGQ") + ":" + lang.get("OFF");
        if(Main.fogQ == 1) var2[i] = lang.get("FOGQ") + ":" + lang.get("LLQ");
        i++;
        var2[i] = lang.get("CORPSES") + ":" + (Main.isCorpses() ? lang.get("ON") : lang.get("OFF"));
        ms[i] = false;
        i++;
        var2[i] = lang.get("BLOOD") + ":" + (this.main.isBlood() ? lang.get("ON") : lang.get("OFF"));
        ms[i] = false;
        i++;
        var2[i] = lang.get("MIPMAPPING") + ":" + (Main.isMipMapping() ? lang.get("ON") : lang.get("OFF"));
        ms[i] = false;
        i++;
        var2[i] = lang.get("FRAMESKIP") + ":" + (Main.isFrameskip() ? lang.get("ON") : lang.get("OFF"));
        ms[i] = false;
        i++;

        var2[i] = lang.get("HUD") + ":";
        ms[i] = true;
        i++;
		if(hasLangSelect()) {
			String lng = Main.langs[Main.lang];
			if(Main.settings.getNoLang(lng) != null) {
				lng = Main.settings.getNoLang(lng);
			} else if(lang.get(lng) != null) {
				lng = lang.get(lng);
			}
			var2[i] = lang.get("LANG") + ":" + lng;
			ms[i] = false;
			i++;
		}
        var2[i] = lang.get("RESIZE_WEAPONS") + ":" + (Main.resizeWeapons ? lang.get("YES") : lang.get("NO"));
        ms[i] = false;
        i++;
        var2[i] = lang.get("HIDEHUD") + ":" + (Main.hideHud ? lang.get("YES") : lang.get("NO"));
        ms[i] = false;
        i++;
        var2[i] = lang.get("DEBUG") + ":" + (DeveloperMenu.debugMode ? lang.get("ON") : lang.get("OFF"));
        ms[i] = false;
        i++;
    }

    protected final void paint(Graphics g) {
        g.setColor(0);
        g.fillRect(0, 0, getWidth(), getHeight());

        if(background instanceof Image) {
            g.drawImage((Image) background, 0, 0, 0);
        } else if(this.background instanceof int[]) {
            g.drawRGB((int[]) background, 0, getWidth(), 0, hei, getWidth(), h, false);
        }

        if(menu instanceof PauseScreen) {
            list.drawBck(g, getWidth() / 8, Main.getFont().height(), getWidth() * 6 / 8, getHeight() - Main.getFont().height() * 2);
            Main.drawBckDialog(g, getHeight() - Main.getFont().height(), getHeight());
        }
        list.draw(g, getWidth() / 8, Main.getFont().height(), getWidth() * 6 / 8, getHeight() - Main.getFont().height() * 2);
        drawSoftKeys(g);
    }

    protected final void onRightSoftKey() {
        main.saveSettingToStore();
        Main.setCurrent(this.menu);
    }

    protected final void onLeftSoftKey() {
        onKey5();
    }

    protected final void onKey5() {
        int var1 = this.itemIndex();
        int id = 0;
        id++;//Audio
        id++;
        id++;
        id++;
        id++;//Conrols
        id++;
        if(var1 == id) {
            Main.setCurrent(new KeysSettings(main, this, background, (menu instanceof PauseScreen)));
            return;
        }
        id++;//KEYS
        id++;//Perfomance
        id++;
        id++;
        id++;
        id++;
        id++;
        id++;
        id++;
        id++;
        id++;//Hud
        if(hasLangSelect()) id++;
        id++;
        id++;
        id++;
    }

    protected final void onKey4() {
        int var1 = this.itemIndex();
        int id = 0;
        id++;//Audio
        if(var1 == id) {
            if(Main.isMusic) {
                Main.music -= 10;
                if(Main.music < 0) Main.music = 0;
                if(Main.music > 100) Main.music = 100;
                if(Main.music == 0) Main.isMusic = false;
            }
            Main.musicPlayer.setVolume(Main.isMusic ? Main.music : 0);
        }
        id++;

        if(var1 == id) {
            if(Main.isFootsteps) {
                Main.footsteps -= 10;
                if(Main.footsteps < 0) Main.footsteps = 0;
                if(Main.footsteps > 100) Main.footsteps = 100;
                if(Main.footsteps == 0) Main.isFootsteps = false;
            }
        }
        id++;

        if(var1 == id) {
            if(Main.isSounds) {
                Main.sounds -= 10;
                if(Main.sounds < 0) Main.sounds = 0;
                if(Main.sounds > 100) Main.sounds = 100;
                if(Main.sounds == 0) Main.isSounds = false;
            }
        }
        id++;

        id++;//Conrols
        if(var1 == id) Main.mouseSpeed -= 2;
        id++;
        id++;//KEYS
        id++;//Perfomance
        if(var1 == id) Main.setDisplaySize(Main.getDisplaySize() - 5);
        id++;
        if(var1 == id) Main.setPersQ(Math.max(0, Main.persQ - 1));
        id++;
        if(var1 == id) Main.pixelsQ = (Math.max(0, Main.pixelsQ - 1));
        id++;
        if(var1 == id) Main.fogQ = (Math.max(0, Main.fogQ - 1));
        id++;
        if(var1 == id) Main.corpses = false;
        id++;
        if(var1 == id) Main.blood = false;
        id++;
        if(var1 == id) Main.mipMapping = false;
        id++;
        if(var1 == id) Main.frameskip = false;
        id++;
        id++;//Hud
		if(hasLangSelect()) {
			if(var1 == id) {
				Main.lang -= 1;
				if(Main.lang < 0) Main.lang = Main.langs.length - 1;

				String path = "/languages/" + Main.langs[Main.lang].toLowerCase() + ".txt";
				main.setLanguage(path);
				if(menu instanceof Menu) ((Menu) menu).reloadText();
			}
			id++;
		}
        if(var1 == id) Main.resizeWeapons = false;
        id++;
        if(var1 == id) Main.hideHud = false;
        id++;
        if(var1 == id) DeveloperMenu.debugMode = false;
        id++;
        this.setItems();
        this.repaint();
    }

    protected final void onKey6() {
        int var1 = this.itemIndex();
        int id = 0;
        id++;//Audio
        if(var1 == id) {
            if(Main.isMusic) {
                Main.music += 10;
                if(Main.music < 0) Main.music = 0;
                if(Main.music > 100) Main.music = 100;
            }
            if(!Main.isMusic) Main.isMusic = true;
            Main.musicPlayer.setVolume(Main.isMusic ? Main.music : 0);
        }
        id++;

        if(var1 == id) {
            if(Main.isFootsteps) {
                Main.footsteps += 10;
                if(Main.footsteps < 0) Main.footsteps = 0;
                if(Main.footsteps > 100) Main.footsteps = 100;
            }
            if(!Main.isFootsteps) Main.isFootsteps = true;
        }
        id++;

        if(var1 == id) {
            if(Main.isSounds) {
                Main.sounds += 10;
                if(Main.sounds < 0) Main.sounds = 0;
                if(Main.sounds > 100) Main.sounds = 100;
            }
            if(!Main.isSounds) Main.isSounds = true;
        }
        id++;

        id++;//Conrols
        if(var1 == id) Main.mouseSpeed += 2;
        id++;
        id++;//KEYS
        id++;//Perfomance
        if(var1 == id) Main.setDisplaySize(Main.getDisplaySize() + 5);
        id++;
        if(var1 == id) Main.setPersQ(Math.min(4, Main.persQ + 1));
        id++;
        if(var1 == id) Main.pixelsQ = (Math.min(2, Main.pixelsQ + 1));
        id++;
        if(var1 == id) Main.fogQ = (Math.min(2, Main.fogQ + 1));
        id++;
        if(var1 == id) Main.corpses = true;
        id++;
        if(var1 == id) Main.blood = true;
        id++;
        if(var1 == id) Main.mipMapping = true;
        id++;
        if(var1 == id) Main.frameskip = true;
        id++;
        id++;//Hud
		if(hasLangSelect()) {
			if(var1 == id) {
				Main.lang += 1;
				if(Main.lang >= Main.langs.length) Main.lang = 0;

				String path = "/languages/" + Main.langs[Main.lang].toLowerCase() + ".txt";
				main.setLanguage(path);
				if(menu instanceof Menu) ((Menu) menu).reloadText();
			}
		}
        id++;
        if(var1 == id) Main.resizeWeapons = true;
        id++;
        if(var1 == id) Main.hideHud = true;
        id++;
        if(var1 == id) {
            DeveloperMenu.debugMode = true;
            if(menu instanceof Menu) ((Menu) menu).reloadText();
            if(menu instanceof PauseScreen) ((PauseScreen) menu).reloadText();
        }
        id++;
        this.setItems();
        this.repaint();
    }
}
