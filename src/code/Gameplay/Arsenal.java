package code.Gameplay;

import code.utils.WeaponCreator;
import javax.microedition.lcdui.Graphics;

public class Arsenal {

    public Weapon[] weapons; // Всё купленное оружие
    public int current;  // Номер выбранного оружия
    public boolean hasHand = false;

    public final void destroy() {
        for (int var1 = 0; var1 < weapons.length; ++var1) {
            if (weapons[var1] != null) {
                weapons[var1].reset();
                weapons[var1] = null;
            }
        }

        weapons = null;
    }

    public final Weapon currentWeapon() {
        if (current >= 0 && current < weapons.length) return weapons[current];
        return null;
    }

    public final Weapon[] getWeapons() {
        return weapons;
    }

    // Смена оружия
    public final void next() {
        if (currentWeapon() != null) currentWeapon().reset();
        
        ++current;
        if (current >= weapons.length) {
            current = 0;
            if (hasHand) current = -1;
        }

        if (current != -1 && weapons[current] == null) next();
    }
    public final void previous() {
        if (currentWeapon() != null) currentWeapon().reset();
        
        current -= 1;
        if (current < (hasHand?-1:0) ) current = weapons.length - 1;
        

        if (current != -1 && weapons[current] == null) previous();
    }

    // ? Прорисовка оружия и полоски перезарядки
    public final void drawWeapon(Graphics g, int y, int width, int height, GameScreen gs) {
        if (currentWeapon() == null) return;
        
        Weapon weapon = currentWeapon();
        if (!gs.player.zoom || !weapon.hasZoom) {
            weapon.draw(g, 0, y, width, height);
        } else {
            weapon.drawSight(g, 0, y, width, height);
        }
        if (weapon.isReloading()) {
            width /= 2;
            int maxY = Math.max(height / 50, 6);
            int x = width - width / 2;
            int y2 = height - maxY - 2 + y;
            int perc = weapon.reloadingPercentage();
            g.setColor(16777215);
            g.drawRect(x, y2, width, maxY);
            g.fillRect(x, y2, width * perc / 100, maxY);
        }

    }

    
    public Arsenal(int width_g3d, int height_g3d) {
        current = -1;
        weapons = new Weapon[Shop.weapon_count];
        
        if (Shop.defaultArsenal != null) {
            for (int i = 0; i < Shop.defaultArsenal.length; i++) {
                if (Shop.defaultArsenal[i] != -1) {
                    weapons[Shop.defaultArsenal[i]] = WeaponCreator.createWeapon(Shop.defaultArsenal[i]);
                    if (current == -1) current = Shop.defaultArsenal[i];
                }
                if (Shop.defaultArsenal[i] == -1) hasHand = true;
            }
        } else {
            hasHand = true;
            current = -1;
        }
        
        for (int i = 0; i < weapons.length; ++i) {
            if (weapons[i] != null) {
                weapons[i].reset();
                if (!weapons[i].isTwoHands()) {
                    weapons[i].setAmmo(200);
                } else {
                    weapons[i].setAmmo(400);
                }
            }
        }

        if (currentWeapon() != null) currentWeapon().createSprite(width_g3d, height_g3d);
        
    }

    public Arsenal() {
    }

}
