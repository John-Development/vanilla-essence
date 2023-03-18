package net.vanillaEssence.util;

public enum TweaksEnum {
  BETTER_BEACONS("beacons-enabled"),
  DO_END_CRYSTALS_LIMIT_SPAWN("crystal-enabled"),
  END_CRYSTAL_RADIUS("crystal-radius"),
  END_CRYSTAL_LOWER_LIMIT_DISTANCE("crystal-lower-limit-distance"),
  END_CRYSTAL_NAME("crystal-name"),
  HUSK_DROP_SAND("sand-enabled"),
  MODIFY_VILLAGERS("vill-enabled"),
  DAILY_VILLAGER_RESTOCKS("vill-daily-restocks"),
  TIME_BETWEEN_VILLAGER_RESTOCKS("vill-time-between-restocks"),
  MAGNETIC_LURE("magnetic-lure-enabled"),
  SPLASH_OXIDIZE("oxidation-enabled"),
  ONE_LVL_RENAMING("one-lvl-renaming-enabled"),
  INFINITE_ENCHANTING("infinite-enchanting-enabled");

  private final String name;

  TweaksEnum(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public String getString() {
    return Tweaks.getInstance().getProperty(this);
  }

  public String getString(Tweaks instance) {
    return instance.getProperty(this);
  }

  public Boolean getBoolean() {
    return Tweaks.getInstance().getBoolProperty(this);
  }

  public Integer getInt() {
    return Tweaks.getInstance().getIntProperty(this);
  }

  public Long getLong() {
    return Tweaks.getInstance().getLongProperty(this);
  }

  public void set(String value, Tweaks instance) {
    instance.setProperty(this, value);
  }
}
