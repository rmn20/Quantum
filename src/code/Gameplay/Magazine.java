package code.Gameplay;

// Магазин (для патронов)

import code.utils.FPS;


public final class Magazine {

    private final short capacity; // Максимальное кол-во патронов в магазине (вместимость)
    public short ammo; // Боезапас
    public short rounds; // Текущее кол-во патронов
    private final short reloadTime; // Продолжительность (кол-во циклов отрисовки) перезарядки
    private short frame = -1; // Текущее кол-во пройденных циклов перезарядки. Если -1, перезарядка не нужна, если >=0, начинается перезарядка

    public Magazine(int capacity, int reloadTime) {
        this.capacity = (short) capacity;
        this.reloadTime = (short) reloadTime;
    }

    public void setAmmo(int ammo) {
        this.ammo = (short) ammo;
    }

    public final void set(short ammo, short rounds) {
        this.ammo = (short) ammo;
        this.rounds = (short) rounds;
    }

    public void addAmmo(int number) {
        ammo = (short) (ammo + number);
        if (ammo < 0) ammo = 0;
    }

    // ? Если есть патроны, начать перезарядку
    final void reload() {
        if (ammo != 0 && frame == -1) frame = 0;
    }

    // ? Пересчет кол-ва пройденных циклов перезарядки
    final void update() {
        if (frame >= 0) frame++;
        

        if (frame > reloadTime * 50 / (FPS.frameTime==0?1:FPS.frameTime)) {
            frame = -1;
            recount();
        }
    }

    // ? Пересчет кол-ва патронов
    final void recount() {
        rounds = (short) Math.min(capacity, ammo);
        ammo -= rounds;
    }

    // ? true, если нужно перезаряжаться (продолжать перезарядку)
    final boolean isReloading() {
        return frame != -1;
    }

    // ? Процент перезарядки
    final int percentage() {
        return 100 * frame * FPS.frameTime / 50 / reloadTime;
    }

    // Пересчет кол-ва патронов в магазине
    final void takeRounds(int number) {
        rounds = (short) (rounds - number);
    }
}
