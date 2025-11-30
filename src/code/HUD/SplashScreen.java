package code.HUD;

import code.HUD.Base.ItemList;
import code.utils.ImageResize;
import code.utils.Main;
import code.utils.StringTools;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public final class SplashScreen extends GUIScreen {

	private final Main main;
	private int action;
	private Image[] splash;
	private Image background;
	private ItemList list;
	private final String[] langlist;
	private long splashBeginTime = -1L;

	public SplashScreen(Main var1) {
		langlist = StringTools.cutOnStrings(StringTools.getStringFromResource("/languages/languages.txt"), ',');
		main = var1;
		setFont(Main.getFont());
		onAction(1);
		if(Main.bcks != null)
			Main.bcks = ImageResize.createImage(Main.bcks, getWidth(), Main.bcks.getHeight());
		if(Main.bcks2 != null)
			Main.bcks2 = ImageResize.createImage(Main.bcks2, getWidth(), Main.bcks2.getHeight());
	}
	/*
	 Action 1 - Сплэш
	 Action 2 - Выбор языка
	 Action 3 - Выбор вкл/выкл звука
	 */

	public final void destroy() {
		super.destroy();
		splash = null;
		background = null;
	}

	private void onAction(int action) {
		this.action = action;

		if(action == 1) {
			if(splashBeginTime != -1) return;
			splash = new Image[Main.splash.length];
			for(int i = 0; i < Main.splash.length; i++) {
				splash[i] = ImageResize.createImage(Main.splash[i], (float) getHeight() / 320.0F, (float) getHeight() / 320.0F);
			}
			splashBeginTime = System.currentTimeMillis();
		} else {
			String[] langs;
			if(action == 2) {
				splash = null;

				background = ImageResize.createImage(Main.background_logo, getWidth(), getHeight());

				langs = new String[langlist.length];

				if(langs != null) {
					for(int i = 0; i < langs.length; i++) {
						langs[i] = langlist[i];
						if(langs[i] != null) {
							if(Main.settings.get(langs[i]) != null)
								langs[i] = Main.settings.getNoLang(langs[i]);
							if(Main.getGameText().get(langs[i]) != null)
								langs[i] = Main.settings.get(langs[i]);
						}
					}
				}

				list = new ItemList(langs, Main.getFont());
				setSoftKeysNames(Main.getGameText().get("SELECT"), (String) null);
			} else if(action == 3) {
				splash = null;
				if(background == null)
					background = ImageResize.createImage(Main.background_logo, this.getWidth(), this.getHeight());
				langs = new String[]{Main.getGameText().get("AUDIO") + ":" + Main.getGameText().get("ON"),
					Main.getGameText().get("AUDIO") + ":" + Main.getGameText().get("OFF")};
				list = new ItemList(langs, Main.getFont());
				setSoftKeysNames(Main.getGameText().get("SELECT"), (String) null);
			}
		}

		repaint();

	}

	protected final void paint(Graphics g) {
		if(action == 1) {
			g.setColor(16777215);
			g.fillRect(0, 0, getWidth(), getHeight());
			int splashIndex = (int) (System.currentTimeMillis() - splashBeginTime) / 3500;
			if(splash != null) if(splash.length > splashIndex)
					g.drawImage(splash[splashIndex], getWidth() / 2, getHeight() / 2, 3);
			try {
				Thread.sleep(20L);
			} catch(Exception exc) {
			}
			if(System.currentTimeMillis() - splashBeginTime >= 3500 * splash.length) {
				if(Main.lang == -1) {
					onAction(2);
				} else {
					onAction(3);
				}
			} else {
				repaint();
			}
		} else if(action == 2) {
			if(background != null) g.drawImage(background, 0, 0, 0);
			if(list != null) list.draw(g, 0, 0, getWidth(), getHeight());
		} else if(action == 3) {
			if(background != null) g.drawImage(this.background, 0, 0, 0);
			if(list != null) list.draw(g, 0, 0, getWidth(), getHeight());
		}

		if(action != 1) drawSoftKeys(g);
	}

	protected final void onKey2() {
		if(action == 2 || action == 3) {
			list.scrollUp();
			repaint();
		}
	}

	protected final void onKey8() {
		if(action == 2 || action == 3) {
			list.scrollDown();
			repaint();
		}
	}

	protected final void onKey5() {
		if(action == 1) {
			splashBeginTime = System.currentTimeMillis() - 3500 * splash.length;
		} else if(action == 2) {
			String var1 = "/languages/" + langlist[this.list.getIndex()].toLowerCase() + ".txt";
			main.setLanguage(var1);
			Main.lang = this.list.getIndex();
			main.saveSettingToStore();
			setFont(Main.getFont());
			onAction(3);
		} else if(action == 3) {
			boolean mus = list.getIndex() == 0;
			Main.isSounds = mus;
			Main.isMusic = mus;
			Main.isFootsteps = mus;
			Main.setCurrent(new Menu(main));
			destroy();
		}
	}

	protected final void onLeftSoftKey() {
		onKey5();
	}

	void reloadScreen() {
		try {
			Thread.sleep(3000L);

		} catch(Exception var2) {
		}
		onAction(3);
	}

	public void sizeChanged(int w, int h) {
		background = null;
		background = ImageResize.createImage(Main.background_logo, w, h);
	}
}
