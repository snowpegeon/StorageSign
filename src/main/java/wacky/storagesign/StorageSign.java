package wacky.storagesign;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.bukkit.util.NumberConversions;
import com.github.teruteru128.logger.Logger;

/**
 * StrageSignの実体クラスです.
 */
public class StorageSign {

  /**
   *
   */
  protected Material smat;
  /**
   *
   */
  protected Material mat;
  /**
   *
   */
  protected short damage;
  /**
   *
   */
  protected Enchantment ench;
  /**
   *
   */
  protected PotionType pot;
  /**
   *
   */
  protected int amount;
  /**
   *
   */
  protected int stack;
  /**
   *
   */
  protected boolean isEmpty;

  /**
   *
   */
  private Logger _logger;

  // StorageSignだと確認してから使っちくりー

  /**
   *
   */
  public StorageSign(ItemStack item, Logger logger) {
    this._logger = logger;
    logger.debug("StorageSign:start");

    String[] str = item.getItemMeta().getLore().get(0).split(" ");

    if (str[0].matches("Empty")) {
      logger.debug("StorageSign is Empty");
      this.isEmpty = true;

    } else {
      logger.debug("StorageSign isn't Empty");
      this.mat = getMaterial(str[0].split(":")[0]);

      if (this.mat == Material.ENCHANTED_BOOK) {
        logger.debug("StorageSign have ENCHANTED_BOOK");
        EnchantInfo ei = new EnchantInfo(this.mat, str[0].split(":"), logger);
        this.damage = ei.getDamage();
        this.ench = ei.getEnchantType();

      } else if (this.mat == Material.POTION || this.mat == Material.SPLASH_POTION
          || this.mat == Material.LINGERING_POTION) {
        logger.debug("StorageSign have any Potion");
        // PotionInfo pi = new PotionInfo(mat, str[0].split(":"));
        String[] potionStr = str[0].split(":");
        PotionInfo pi = new PotionInfo(this.mat, potionStr[0],
            PotionInfo.convertNBTNameToShortName(potionStr[1]), potionStr[2], logger);
        this.mat = pi.getMaterial();
        this.damage = pi.getDamage();
        this.pot = pi.getPotionType();

      } else if (str[0].contains(":")) {
        logger.debug("str[0].contains\":\"");
        this.damage = NumberConversions.toShort(str[0].split(":")[1]);

      } else if (this.mat == Material.STONE_SLAB) {
        logger.debug("StorageSign have STONE_SLAB");
        // 1.13の滑らかハーフと1.14の石ハーフ区別
        this.mat = Material.SMOOTH_STONE_SLAB;

      }
      this.amount = NumberConversions.toInt(str[1]);
    }

    this.smat = item.getType();
    this.stack = item.getAmount();
    logger.debug("StorageSign:end");
  }

  // Sto(ry
  /*
   * public StorageSign(Sign sign) { this(sign,Material.OAK_SIGN); }
   */

  /**
   *
   */
  public StorageSign(Sign sign, Material signmat, Logger logger) {
    this._logger = logger;
    logger.debug("StorageSign(Material signmat):start");
    // 上と統合したい
    String[] line2 = sign.getSide(Side.FRONT).getLine(1).trim().split(":");
    this.mat = getMaterial(line2[0]);
    this.isEmpty = (this.mat == null || this.mat == Material.AIR);

    if (this.mat == Material.ENCHANTED_BOOK) {
      logger.debug("ENCHANTED_BOOK");
      EnchantInfo ei = new EnchantInfo(this.mat, line2, logger);
      this.damage = ei.getDamage();
      this.ench = ei.getEnchantType();

    } else if (this.mat == Material.POTION || this.mat == Material.SPLASH_POTION
        || this.mat == Material.LINGERING_POTION) {
      logger.debug("any POTION");
      // PotionInfo pi = new PotionInfo(mat, line2);
      PotionInfo pi = new PotionInfo(this.mat, line2[0], line2[1], line2[2], logger);
      this.mat = pi.getMaterial();
      this.damage = pi.getDamage();
      this.pot = pi.getPotionType();

    } else if (line2.length == 2) {
      logger.debug("line2.length is 2");
      this.damage = NumberConversions.toShort(line2[1]);

    } else if (this.mat == Material.STONE_SLAB) {
      logger.debug("StorageSign2:STONE_SLAB");
      // 1.13の滑らかハーフと1.14の石ハーフ区別
      this.mat = Material.SMOOTH_STONE_SLAB;

    }

    this.amount = NumberConversions.toInt(sign.getSide(Side.FRONT).getLine(2));
    this.isEmpty = this.amount == 0;
    this.stack = 1;

    // 壁掛け看板のチェック
    if (signmat == Material.OAK_WALL_SIGN) {
      logger.debug("signmat is OAK_WALL_SIGN");
      this.smat = Material.OAK_SIGN;

    } else if (signmat == Material.BIRCH_WALL_SIGN) {
      logger.debug("signmat is BIRCH_WALL_SIGN");
      this.smat = Material.BIRCH_SIGN;

    } else if (signmat == Material.SPRUCE_WALL_SIGN) {
      logger.debug("signmat is SPRUCE_WALL_SIGN");
      this.smat = Material.SPRUCE_SIGN;

    } else if (signmat == Material.JUNGLE_WALL_SIGN) {
      logger.debug("signmat is JUNGLE_WALL_SIGN");
      this.smat = Material.JUNGLE_SIGN;

    } else if (signmat == Material.ACACIA_WALL_SIGN) {
      logger.debug("signmat is ACACIA_WALL_SIGN");
      this.smat = Material.ACACIA_SIGN;

    } else if (signmat == Material.DARK_OAK_WALL_SIGN) {
      logger.debug("signmat is DARK_OAK_WALL_SIGN");
      this.smat = Material.DARK_OAK_SIGN;

    } else if (signmat == Material.CRIMSON_WALL_SIGN) {
      logger.debug("signmat is CRIMSON_WALL_SIGN");
      this.smat = Material.CRIMSON_SIGN;

    } else if (signmat == Material.WARPED_WALL_SIGN) {
      logger.debug("signmat is WARPED_WALL_SIGN");
      this.smat = Material.WARPED_SIGN;

    } else if (signmat == Material.MANGROVE_WALL_SIGN) {
      logger.debug("signmat is MANGROVE_WALL_SIGN");
      this.smat = Material.MANGROVE_SIGN;

    } else if (signmat == Material.CHERRY_WALL_SIGN) {
      logger.debug("signmat is CHERRY_WALL_SIGN");
      this.smat = Material.CHERRY_SIGN;

    } else if (signmat == Material.BAMBOO_WALL_SIGN) {
      logger.debug("signmat is BAMBOO_WALL_SIGN");
      this.smat = Material.BAMBOO_SIGN;

    } else {
      logger.debug("Material isn't WALL_SIGN");
      this.smat = signmat;

    }

    logger.debug("StorageSign(Material signmat):end");
  }

  /**
   * 空のStorageSignを作成して、ItemStackとして取得します.
   *
   * @return ItemStack 空のStorageSign
   */
  public static ItemStack emptySign() {
    return emptySign(Material.OAK_SIGN);
  }

  /**
   * Materialの内容から、空のStorageSignを作成して、ItemStackとして取得します.
   *
   * @param smat Material
   * @return ItemStack 空のStorageSign
   */
  public static ItemStack emptySign(Material smat) {
    ItemStack emptySign = new ItemStack(smat);
    ItemMeta meta = emptySign.getItemMeta();
    List<String> list = new ArrayList<>();

    meta.setDisplayName("StorageSign");
    list.add("Empty");
    meta.setLore(list);
    emptySign.setItemMeta(meta);

    return emptySign;
  }

  /**
   * 指定された文字列でMaterialに変換します.
   *
   * @param str
   * @return Material
   */
  protected Material getMaterial(String str) {
    _logger.debug("getMaterial:start");
    // if (str.matches("EmptySign")) {
    // return Material.END_PORTAL;
    // }
    // if (str.startsWith("REDSTONE_TORCH")) return Material.REDSTONE_TORCH;
    // if (str.startsWith("RS_COMPARATOR")) return Material.COMPARATOR;
    // if (str.startsWith("STAINGLASS_P")) return Material.LEGACY_STAINED_GLASS_PANE;
    // if (str.startsWith("BROWN_MUSH_B")) return Material.BROWN_MUSHROOM_BLOCK;
    // if (str.startsWith("RED_MUSH_BLO")) return Material.RED_MUSHROOM_BLOCK;
    if (str.matches("")) {
      _logger.debug("Material is AIR");
      return Material.AIR;

    } else if (str.matches("EmptySign") || str.matches("OakStorageSign")) {
      _logger.debug("Material is OakStorageSign");
      this.damage = 1;
      return Material.OAK_SIGN;

    } else if (str.matches("SpruceStorageSign")) {
      _logger.debug("Material is SpruceStorageSign");
      this.damage = 1;
      return Material.SPRUCE_SIGN;

    } else if (str.matches("BirchStorageSign")) {
      _logger.debug("Material is BirchStorageSign");
      this.damage = 1;
      return Material.BIRCH_SIGN;

    } else if (str.matches("JungleStorageSign")) {
      _logger.debug("Material is JungleStorageSign");
      this.damage = 1;
      return Material.JUNGLE_SIGN;

    } else if (str.matches("AcaciaStorageSign")) {
      _logger.debug("Material is AcaciaStorageSign");
      this.damage = 1;
      return Material.ACACIA_SIGN;

    } else if (str.matches("DarkOakStorageSign")) {
      _logger.debug("Material is DarkOakStorageSign");
      this.damage = 1;
      return Material.DARK_OAK_SIGN;

    } else if (str.matches("CrimsonStorageSign")) {
      _logger.debug("Material is CrimsonStorageSign");
      this.damage = 1;
      return Material.CRIMSON_SIGN;

    } else if (str.matches("WarpedStorageSign")) {
      _logger.debug("Material is WarpedStorageSign");
      this.damage = 1;
      return Material.WARPED_SIGN;

    } else if (str.matches("MangroveStorageSign")) {
      _logger.debug("Material is MangroveStorageSign");
      this.damage = 1;
      return Material.MANGROVE_SIGN;

    } else if (str.matches("CherryStorageSign")) {
      _logger.debug("Material is CherryStorageSign");
      this.damage = 1;
      return Material.CHERRY_SIGN;

    } else if (str.matches("BambooStorageSign")) {
      _logger.debug("Material is BambooStorageSign");
      this.damage = 1;
      return Material.BAMBOO_SIGN;
    }
    if (str.matches("HorseEgg")) {
      _logger.debug("Material is HorseEgg");
      this.damage = 1;
      return Material.END_PORTAL;
      // ガスト卵でよくね？
    }
    // 1.13→1.14用
    if (str.startsWith("SIGN")) {
      _logger.debug("Material is SIGN");
      return Material.OAK_SIGN;
    }
    if (str.startsWith("ROSE_RED")) {
      _logger.debug("Material is ROSE_RED");
      return Material.RED_DYE;
    }
    if (str.startsWith("DANDELION_YELLOW")) {
      _logger.debug("Material is DANDELION_YELLOW");
      return Material.YELLOW_DYE;
    }
    if (str.startsWith("CACTUS_GREEN")) {
      _logger.debug("Material is CACTUS_GREEN");
      return Material.GREEN_DYE;
    }
    // 省略用
    if (str.startsWith("ENCHBOOK")) {
      _logger.debug("Material is ENCHBOOK");
      return Material.ENCHANTED_BOOK;
    }
    if (str.startsWith("SPOTION")) {
      _logger.debug("Material is SPOTION");
      return Material.SPLASH_POTION;
    }
    if (str.startsWith("LPOTION")) {
      _logger.debug("Material is LPOTION");
      return Material.LINGERING_POTION;
    }

    Material mat = Material.matchMaterial(str);

    if (mat == null) {
      _logger.debug("Material is null");
      // 後ろ切れる程度なら対応可
      for (Material m : Material.values()) {
        if (m.toString().startsWith(str)) {
          return m;
        }
      }
    }

    // nullなら空.
    _logger.debug("Material isn't bifurcation");
    return mat;

  }

  /**
   * StorageSignが保持しているMaterialを取得します.
   *
   * @return Material
   */
  public Material getMaterial() {
    _logger.debug("return this.mat");
    return this.mat;
  }

  /**
   * フィールド変数：matのsetter.
   */
  public void setMaterial(Material mat) {
    _logger.debug("setMaterial to Material mat");
    this.mat = mat;
  }

  /**
   * Materialの短い名前を文字列として取得します.
   *
   * @return Materialの短い名前
   */
  protected String getShortName() {
    _logger.debug("getShortName:start");
    // 2行目の記載内容
    _logger.trace("this.mat=" + mat);
    if (this.mat == null || this.mat == Material.AIR) {
      _logger.debug("empty");
      return "";

    } else if (this.mat == Material.END_PORTAL) {
      _logger.debug("Material.END_PORTAL");
      if (this.damage == 0) {
        _logger.debug("OakStorageSign");
        return "OakStorageSign";
      }

      if (this.damage == 1) {
        _logger.debug("HorseEgg");
        return "HorseEgg";
      }

    } else if (this.mat == Material.OAK_SIGN && this.damage == 1) {
      _logger.debug("OakStorageSign");
      return "OakStorageSign";

    } else if (this.mat == Material.SPRUCE_SIGN && this.damage == 1) {
      _logger.debug("SpruceStorageSign");
      return "SpruceStorageSign";

    } else if (this.mat == Material.BIRCH_SIGN && this.damage == 1) {
      _logger.debug("BirchStorageSign");
      return "BirchStorageSign";

    } else if (this.mat == Material.JUNGLE_SIGN && this.damage == 1) {
      _logger.debug("JungleStorageSign");
      return "JungleStorageSign";

    } else if (this.mat == Material.ACACIA_SIGN && this.damage == 1) {
      _logger.debug("AcaciaStorageSign");
      return "AcaciaStorageSign";

    } else if (this.mat == Material.DARK_OAK_SIGN && this.damage == 1) {
      _logger.debug("DarkOakStorageSign");
      return "DarkOakStorageSign";

    } else if (this.mat == Material.CRIMSON_SIGN && this.damage == 1) {
      _logger.debug("CrimsonStorageSign");
      return "CrimsonStorageSign";

    } else if (this.mat == Material.WARPED_SIGN && this.damage == 1) {
      _logger.debug("WarpedStorageSign");
      return "WarpedStorageSign";

    } else if (this.mat == Material.MANGROVE_SIGN && this.damage == 1) {
      _logger.debug("MangroveStorageSign");
      return "MangroveStorageSign";

    } else if (this.mat == Material.CHERRY_SIGN && this.damage == 1) {
      _logger.debug("CherryStorageSign");
      return "CherryStorageSign";

    } else if (this.mat == Material.BAMBOO_SIGN && this.damage == 1) {
      _logger.debug("BambooStorageSign");
      return "BambooStorageSign";

      // else if (mat == Material.LEGACY_STAINED_GLASS_PANE) return damage == 0 ? "STAINGLASS_PANE"
      // : "STAINGLASS_P:" + damage;
      // else if (mat == Material.LEGACY_REDSTONE_COMPARATOR) return "RS_COMPARATOR";
      // else if (mat == Material.LEGACY_REDSTONE_TORCH_ON) return "REDSTONE_TORCH";
      // else if (mat == Material.LEGACY_HUGE_MUSHROOM_1) return damage == 0 ? "BROWN_MUSH_BLOC" :
      // "BROWN_MUSH_B:" + damage;
      // else if (mat == Material.LEGACY_HUGE_MUSHROOM_2) return damage == 0 ? "RED_MUSH_BLOCK" :
      // "RED_MUSH_BLO:" + damage;

    } else if (this.mat == Material.ENCHANTED_BOOK) {
      _logger.debug("ENCHBOOK + data");
      return "ENCHBOOK:" + EnchantInfo.getShortType(this.ench) + ":" + this.damage;

    } else if (this.mat == Material.POTION || this.mat == Material.SPLASH_POTION
        || this.mat == Material.LINGERING_POTION) {
      _logger.debug("any POTION");
      return PotionInfo.getSignData(this.mat, pot, this.damage);
    }
    // リミットブレイク
    int limit = 99;
    if (this.damage != 0) {
      _logger.debug("limit break");
      limit -= String.valueOf(this.damage).length() + 1;
    }

    if (this.mat.toString().length() > limit) {
      _logger.debug("this.mat.toString().length() > limit");
      if (this.damage != 0) {
        _logger.debug("item damage isn't 0");
        return this.mat.toString().substring(0, limit) + ":" + this.damage;
      }

      _logger.debug("item damage is 0");
      return this.mat.toString().substring(0, limit);

    } else {
      _logger.debug("else this.mat.toString().length() > limit");
      if (this.damage != 0) {
        _logger.debug("item damage isn't 0");
        return this.mat.toString() + ":" + this.damage;
      }

      _logger.debug("item damage is 0");
      return this.mat.toString();
    }
  }

  /**
   * 空のStorageSignを作成して、ItemStackとして取得します.
   *
   * @return ItemStack 空のStorageSign
   */
  public ItemStack getStorageSign() {
    _logger.debug("getStorageSign:stert");
    ItemStack item = new ItemStack(this.smat, this.stack);
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName("StorageSign");
    List<String> list = new ArrayList<>();

    // IDとMaterial名が混ざってたり、エンチャ本対応したり
    if (this.isEmpty) {
      _logger.debug("Empty");
      list.add("Empty");

    } else if (this.mat == Material.ENCHANTED_BOOK) {
      _logger.debug("ENCHANTED_BOOK");
      list.add(this.mat.toString() + ":" + this.ench.getKey().getKey() + ":" + this.damage + " "
          + this.amount);

    } else if (this.mat == Material.POTION || this.mat == Material.SPLASH_POTION
        || this.mat == Material.LINGERING_POTION) {
      _logger.debug("any POTION");
      list.add(PotionInfo.getTagData(this.mat, pot, this.damage, this.amount));

    } else {
      _logger.debug("other");
      list.add(getShortName() + " " + this.amount);

    }

    meta.setLore(list);
    item.setItemMeta(meta);

    _logger.debug("getStorageSign:end");
    return item;
  }

  /**
   * 空のHorseEggを作成して、ItemStackとして取得します.
   *
   * @return ItemStack 空のHoerseEgg
   */
  private ItemStack emptyHorseEgg() {
    _logger.debug("emptyHorseEgg:start");
    ItemStack emptyHorseEgg = new ItemStack(Material.GHAST_SPAWN_EGG);
    ItemMeta meta = emptyHorseEgg.getItemMeta();
    List<String> list = new ArrayList<>();

    meta.setDisplayName("HorseEgg");
    list.add("Empty");
    meta.setLore(list);
    emptyHorseEgg.setItemMeta(meta);

    _logger.debug("emptyHorseEgg:end");
    return emptyHorseEgg;
  }

  /**
   * StrageSignから、設置されている看板として表示される文字列を取得します.
   *
   * @param i 看板の何行目の文字列を取得したいか指定します<br>
   *        (ex.)１行目⇒0
   * @return 取得した文字列
   */
  public String getSigntext(int i) {
    _logger.debug("getSigntext:start");
    String[] sign = getSigntexts();
    _logger.trace("sign[]=" + String.join(" / ", sign));
    _logger.debug("getSigntext:end");
    return sign[i];
  }

  /**
   * StrageSignから、設置されている看板として表示される文字列配列を取得します.
   *
   * @return 取得した文字列
   */
  public String[] getSigntexts() {
    _logger.debug("getSigntexts:start");
    String[] sign = new String[4];

    sign[0] = "StorageSign";
    sign[1] = getShortName();
    sign[2] = String.valueOf(this.amount);
    sign[3] = String.valueOf(this.amount / 3456) + "LC " + String.valueOf(this.amount % 3456 / 64)
        + "s " + String.valueOf(this.amount % 64);

    _logger.debug("getSigntext:end");
    return sign;
  }

  /**
   * StrageSignの内容を取得します.
   *
   * @return ItemStack
   */
  public ItemStack getContents() {
    // 中身取得、一部アイテム用の例外
    _logger.debug("getContents:start");
    if (this.mat == null) {
      _logger.debug("StrageSign haven't item");
      return null;
    }

    if (this.mat == Material.END_PORTAL) {
      _logger.debug("Contents is END_PORTAL");
      if (this.damage == 0) {
        _logger.debug("emptySign");
        return emptySign();
      }

      if (this.damage == 1) {
        _logger.debug("emptyHorseEgg");
        return emptyHorseEgg();
      }

    }

    if (this.mat == Material.STONE_SLAB) {
      _logger.debug("STONE_SLAB");
      // ダメージ値0にする
      return new ItemStack(this.mat, 1);

    } else if (this.mat == Material.OAK_SIGN || this.mat == Material.SPRUCE_SIGN
        || this.mat == Material.BIRCH_SIGN || this.mat == Material.JUNGLE_SIGN
        || this.mat == Material.ACACIA_SIGN || this.mat == Material.DARK_OAK_SIGN
        || this.mat == Material.CRIMSON_SIGN || this.mat == Material.WARPED_SIGN
        || this.mat == Material.MANGROVE_SIGN || this.mat == Material.CHERRY_SIGN
        || this.mat == Material.BAMBOO_SIGN) {
      _logger.debug("any SIGN");

      if (this.damage == 0) {
        _logger.debug("damage is 0");
        return new ItemStack(this.mat, 1);

      } else {
        _logger.debug("damage isn't 0");
        return emptySign(this.mat);
      }

    } else if (this.mat == Material.ENCHANTED_BOOK) {
      _logger.debug("ENCHANTED_BOOK");
      ItemStack item = new ItemStack(this.mat, 1);

      EnchantmentStorageMeta enchantMeta = (EnchantmentStorageMeta) item.getItemMeta();
      enchantMeta.addStoredEnchant(this.ench, this.damage, true);
      item.setItemMeta(enchantMeta);

      return item;

    } else if (this.mat == Material.POTION || this.mat == Material.SPLASH_POTION
        || this.mat == Material.LINGERING_POTION) {
      _logger.debug("any POTION");

      ItemStack item = new ItemStack(this.mat, 1);
      PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
      // potionMeta.setBasePotionData(new PotionData(pot, damage == 1, damage == 2));

      potionMeta.setBasePotionType(this.pot);
      item.setItemMeta(potionMeta);

      return item;

    } else if (this.mat == Material.FIREWORK_ROCKET) {
      _logger.debug("FIREWORK_ROCKET");
      ItemStack item = new ItemStack(this.mat, 1);
      FireworkMeta fireworkMeta = (FireworkMeta) item.getItemMeta();

      fireworkMeta.setPower(this.damage);
      item.setItemMeta(fireworkMeta);

      return item;

    } else if (this.mat == Material.WHITE_BANNER && this.damage == 8) {
      _logger.debug("WHITE_BANNER & damage is 8");

      ItemStack item = new ItemStack(this.mat, 1);
      // どこかからコピー
      item.setItemMeta(StorageSignCore.ominousBannerMeta);

      return item;

    }

    if (this.damage == 0) {
      _logger.debug("damage is 0");
      // 大半はダメージなくなった
      return new ItemStack(this.mat, 1);
    }

    var itemStack = new ItemStack(this.mat, 1);
    var meta = itemStack.getItemMeta();
    if (meta instanceof Damageable dam) {
      _logger.debug("meta instanceof Damageable dam");
      dam.setDamage(this.damage);
      itemStack.setItemMeta(meta);
    }

    // ツール系のみダメージあり
    _logger.debug("Item is tools");
    return itemStack;
  }

  /**
   * 回収可能か判定、エンチャ本は本自身の合成回数を問わない.
   *
   * @return true：回収できるよ/false：回収できないよ
   */
  public boolean isSimilar(ItemStack item) {
    _logger.debug("isSimilar:start");
    if (item == null) {
      _logger.debug("Item isn't Similar");
      return false;

    }

    if (this.mat == Material.ENCHANTED_BOOK && item.getType() == Material.ENCHANTED_BOOK) {
      _logger.debug("ENCHANTED_BOOK");

      EnchantmentStorageMeta enchantMeta = (EnchantmentStorageMeta) item.getItemMeta();

      if (enchantMeta.getStoredEnchants().size() == 1) {
        _logger.debug("enchantMeta.getStoredEnchants().size() = 1");
        Enchantment itemEnch =
            enchantMeta.getStoredEnchants().keySet().toArray(new Enchantment[0])[0];

        if (itemEnch == this.ench && enchantMeta.getStoredEnchantLevel(itemEnch) == this.damage) {
          _logger.debug("Item is Similar");
          return true;

        }
      }
      _logger.debug("Item isn't Similar");
      return false;

    } else if (isShulker(this.mat)) {
      _logger.debug("Shulker");
      // 後回し

    } else if (this.mat == Material.POTION || this.mat == Material.SPLASH_POTION
        || this.mat == Material.LINGERING_POTION) {
      _logger.debug("This mat is PotionSeries.");

      if (item.getItemMeta() instanceof PotionMeta) {
        PotionMeta pom = (PotionMeta) item.getItemMeta();
        if (pom.getBasePotionType().equals(this.pot)) {
          return true;
        }
      }
    }

    return getContents().isSimilar(item);
  }

  /**
   * フィールド変数：damageのgetter.
   */
  public short getDamage() {
    _logger.debug("getDamage");
    return this.damage;
  }

  /**
   * フィールド変数：damageのsetter.
   */
  public void setDamage(short damage) {
    _logger.debug("setDamage");
    this.damage = damage;
  }

  /**
   * フィールド変数：amountのgetter.
   */
  public int getAmount() {
    _logger.debug("getAmount");
    return this.amount;
  }

  /**
   * フィールド変数：amountのsetter.
   */
  public void setAmount(int amount) {
    _logger.debug("setAmount");
    this.amount = amount;
    this.isEmpty = amount == 0;
  }

  /**
   * フィールド変数：amountへ加算をします.
   *
   * @param amount 加算対象の値
   */
  public void addAmount(int amount) {
    _logger.debug("addAmount:start");
    if (this.amount < -amount) {
      _logger.debug("set amount 0");
      this.amount = 0;

    } else {
      _logger.debug("add Amount");
      this.amount += amount;

    }

    if (this.amount < 0) {
      _logger.debug("set amount to MAX_VALUE");
      this.amount = Integer.MAX_VALUE;

    }

    this.isEmpty = this.amount == 0;
  }

  /**
   * StorageSignが保持しているStackSizeを取得します.
   *
   * @return StackSize
   */
  public int getStackSize() {
    _logger.debug("get StorageSign StackSize");
    return this.stack;
  }

  /**
   * StrageSignが保持している内容か空かを取得します.
   *
   * @return true：空っぽ/false：入ってます
   */
  public boolean isEmpty() {
    _logger.debug("Check StrageSign is Empty");
    return this.isEmpty;
  }

  /**
   * フィールド変数：enchのgetter.
   */
  public Enchantment getEnchant() {
    _logger.debug("getEnchant");
    return this.ench;
  }

  /**
   * フィールド変数：enchのsetter.
   */
  public void setEnchant(Enchantment ench) {
    _logger.debug("setEnchant");
    this.ench = ench;
  }

  /**
   * フィールド変数：potのgetter.
   */
  public PotionType getPotion() {
    _logger.debug("getPotion");
    return this.pot;
  }

  /**
   * フィールド変数：potのsetter.
   */
  public void setPotion(PotionType pot) {
    _logger.debug("setPotion");
    this.pot = pot;
  }

  /**
   * フィールド変数：smatのgetter.
   */
  public Material getSmat() {
    _logger.debug("getSmat");
    return this.smat;
  }

  /**
   * マテリアルがシュルカーボックスかを検査します.
   *
   * @return true：シュルカーボックスにだよ/false：シュルカーボックスじゃないよ
   */
  private boolean isShulker(Material mat) {
    _logger.debug("isShulker:start");
    switch (mat) {
      case SHULKER_BOX:
      case WHITE_SHULKER_BOX:
      case ORANGE_SHULKER_BOX:
      case MAGENTA_SHULKER_BOX:
      case LIGHT_BLUE_SHULKER_BOX:
      case YELLOW_SHULKER_BOX:
      case LIME_SHULKER_BOX:
      case PINK_SHULKER_BOX:
      case GRAY_SHULKER_BOX:
      case LIGHT_GRAY_SHULKER_BOX:
      case CYAN_SHULKER_BOX:
      case PURPLE_SHULKER_BOX:
      case BLUE_SHULKER_BOX:
      case BROWN_SHULKER_BOX:
      case GREEN_SHULKER_BOX:
      case RED_SHULKER_BOX:
      case BLACK_SHULKER_BOX:
        _logger.debug("Item is Shulker");
        return true;

      default:

    }
    _logger.debug("Item isn't Shulker");
    return false;
  }
}
