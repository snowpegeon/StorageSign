package wacky.storagesign;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
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

import static org.bukkit.Material.*;

/**
 * StrageSignの実体クラスです.
 */
public class StorageSign {

  /**
   * block_entity_dataタグが付いて、単純な比較では収納ができないアイテム一覧.
   */
  private static final Set<Material> block_entity_data_Materials = Collections.unmodifiableSet(
      EnumSet.of(BEE_NEST, BEEHIVE));

  /**
   * 看板のアイテム素材.
   */
  protected Material smat;

  /**
   * 看板に収納されるアイテム素材.
   */
  protected Material mat;

  /**
   * 看板に設定されるダメージ値.
   */
  protected short damage;
  /**
   * 看板に収納されているエンチャント本のエンチャント情報.
   */
  protected Enchantment ench;
  /**
   * 看板に収納されているポーションの種類.
   */
  protected PotionType pot;

  /**
   * 看板に収納されているアイテム数.
   */
  protected int amount;

  /**
   * SSのアイテムスタック数.
   */
  protected int stack;
  /**
   * 看板の中身が空か.
   */
  protected boolean isEmpty;

  /**
   * ログ出力用ロガー.
   */
  private Logger _logger;

  /**
   * ItemStackに設定されている看板の情報からStorageSignを生成する.
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

      if (this.mat == ENCHANTED_BOOK) {
        logger.debug("StorageSign have ENCHANTED_BOOK");
        EnchantInfo ei = new EnchantInfo(this.mat, str[0].split(":"), logger);
        this.damage = ei.getDamage();
        this.ench = ei.getEnchantType();

      } else if (this.mat == POTION || this.mat == SPLASH_POTION
          || this.mat == LINGERING_POTION) {
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

      } else if (this.mat == STONE_SLAB) {
        logger.debug("StorageSign have STONE_SLAB");
        // 1.13の滑らかハーフと1.14の石ハーフ区別
        this.mat = SMOOTH_STONE_SLAB;

      }
      this.amount = NumberConversions.toInt(str[1]);
    }

    this.smat = item.getType();
    this.stack = item.getAmount();
    logger.debug("StorageSign:end");
  }

  /**
   * ブロックとして存在している看板の情報からStorageSignを生成する.
   */
  public StorageSign(Sign sign, Material signmat, Logger logger) {
    this._logger = logger;
    logger.debug("StorageSign(Material signmat):start");
    // 上と統合したい
    String[] line2 = sign.getSide(Side.FRONT).getLine(1).trim().split(":");
    this.mat = getMaterial(line2[0]);
    this.isEmpty = (this.mat == null || this.mat == AIR);

    if (this.mat == ENCHANTED_BOOK) {
      logger.debug("ENCHANTED_BOOK");
      EnchantInfo ei = new EnchantInfo(this.mat, line2, logger);
      this.damage = ei.getDamage();
      this.ench = ei.getEnchantType();

    } else if (this.mat == POTION || this.mat == SPLASH_POTION
        || this.mat == LINGERING_POTION) {
      logger.debug("any POTION");
      // PotionInfo pi = new PotionInfo(mat, line2);
      PotionInfo pi = new PotionInfo(this.mat, line2[0], line2[1], line2[2], logger);
      this.mat = pi.getMaterial();
      this.damage = pi.getDamage();
      this.pot = pi.getPotionType();

    } else if (line2.length == 2) {
      logger.debug("line2.length is 2");
      this.damage = NumberConversions.toShort(line2[1]);

    } else if (this.mat == STONE_SLAB) {
      logger.debug("StorageSign2:STONE_SLAB");
      // 1.13の滑らかハーフと1.14の石ハーフ区別
      this.mat = SMOOTH_STONE_SLAB;

    }

    this.amount = NumberConversions.toInt(sign.getSide(Side.FRONT).getLine(2));
    this.isEmpty = this.amount == 0;
    this.stack = 1;

    // 壁掛け看板のチェック
    if (signmat == OAK_WALL_SIGN) {
      logger.debug("signmat is OAK_WALL_SIGN");
      this.smat = OAK_SIGN;

    } else if (signmat == BIRCH_WALL_SIGN) {
      logger.debug("signmat is BIRCH_WALL_SIGN");
      this.smat = BIRCH_SIGN;

    } else if (signmat == SPRUCE_WALL_SIGN) {
      logger.debug("signmat is SPRUCE_WALL_SIGN");
      this.smat = SPRUCE_SIGN;

    } else if (signmat == JUNGLE_WALL_SIGN) {
      logger.debug("signmat is JUNGLE_WALL_SIGN");
      this.smat = JUNGLE_SIGN;

    } else if (signmat == ACACIA_WALL_SIGN) {
      logger.debug("signmat is ACACIA_WALL_SIGN");
      this.smat = ACACIA_SIGN;

    } else if (signmat == DARK_OAK_WALL_SIGN) {
      logger.debug("signmat is DARK_OAK_WALL_SIGN");
      this.smat = DARK_OAK_SIGN;

    } else if (signmat == CRIMSON_WALL_SIGN) {
      logger.debug("signmat is CRIMSON_WALL_SIGN");
      this.smat = CRIMSON_SIGN;

    } else if (signmat == WARPED_WALL_SIGN) {
      logger.debug("signmat is WARPED_WALL_SIGN");
      this.smat = WARPED_SIGN;

    } else if (signmat == MANGROVE_WALL_SIGN) {
      logger.debug("signmat is MANGROVE_WALL_SIGN");
      this.smat = MANGROVE_SIGN;

    } else if (signmat == CHERRY_WALL_SIGN) {
      logger.debug("signmat is CHERRY_WALL_SIGN");
      this.smat = CHERRY_SIGN;

    } else if (signmat == BAMBOO_WALL_SIGN) {
      logger.debug("signmat is BAMBOO_WALL_SIGN");
      this.smat = BAMBOO_SIGN;

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
    return emptySign(OAK_SIGN);
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
    meta.setMaxStackSize(ConfigLoader.getMaxStackSize());
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
      return AIR;

    } else if (str.matches("EmptySign") || str.matches("OakStorageSign")) {
      _logger.debug("Material is OakStorageSign");
      this.damage = 1;
      return OAK_SIGN;

    } else if (str.matches("SpruceStorageSign")) {
      _logger.debug("Material is SpruceStorageSign");
      this.damage = 1;
      return SPRUCE_SIGN;

    } else if (str.matches("BirchStorageSign")) {
      _logger.debug("Material is BirchStorageSign");
      this.damage = 1;
      return BIRCH_SIGN;

    } else if (str.matches("JungleStorageSign")) {
      _logger.debug("Material is JungleStorageSign");
      this.damage = 1;
      return JUNGLE_SIGN;

    } else if (str.matches("AcaciaStorageSign")) {
      _logger.debug("Material is AcaciaStorageSign");
      this.damage = 1;
      return ACACIA_SIGN;

    } else if (str.matches("DarkOakStorageSign")) {
      _logger.debug("Material is DarkOakStorageSign");
      this.damage = 1;
      return DARK_OAK_SIGN;

    } else if (str.matches("CrimsonStorageSign")) {
      _logger.debug("Material is CrimsonStorageSign");
      this.damage = 1;
      return CRIMSON_SIGN;

    } else if (str.matches("WarpedStorageSign")) {
      _logger.debug("Material is WarpedStorageSign");
      this.damage = 1;
      return WARPED_SIGN;

    } else if (str.matches("MangroveStorageSign")) {
      _logger.debug("Material is MangroveStorageSign");
      this.damage = 1;
      return MANGROVE_SIGN;

    } else if (str.matches("CherryStorageSign")) {
      _logger.debug("Material is CherryStorageSign");
      this.damage = 1;
      return CHERRY_SIGN;

    } else if (str.matches("BambooStorageSign")) {
      _logger.debug("Material is BambooStorageSign");
      this.damage = 1;
      return BAMBOO_SIGN;
    }
    if (str.matches("HorseEgg")) {
      _logger.debug("Material is HorseEgg");
      this.damage = 1;
      return END_PORTAL;
      // ガスト卵でよくね？
    }
    // 1.13→1.14用
    if (str.startsWith("SIGN")) {
      _logger.debug("Material is SIGN");
      return OAK_SIGN;
    }
    if (str.startsWith("ROSE_RED")) {
      _logger.debug("Material is ROSE_RED");
      return RED_DYE;
    }
    if (str.startsWith("DANDELION_YELLOW")) {
      _logger.debug("Material is DANDELION_YELLOW");
      return YELLOW_DYE;
    }
    if (str.startsWith("CACTUS_GREEN")) {
      _logger.debug("Material is CACTUS_GREEN");
      return GREEN_DYE;
    }
    // 省略用
    if (str.startsWith("ENCHBOOK")) {
      _logger.debug("Material is ENCHBOOK");
      return ENCHANTED_BOOK;
    }
    if (str.startsWith("SPOTION")) {
      _logger.debug("Material is SPOTION");
      return SPLASH_POTION;
    }
    if (str.startsWith("LPOTION")) {
      _logger.debug("Material is LPOTION");
      return LINGERING_POTION;
    }

    Material mat = matchMaterial(str);

    if (mat == null) {
      _logger.debug("Material is null");
      // 後ろ切れる程度なら対応可
      for (Material m : values()) {
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
    _logger.debug(" return this.mat");
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
    if (this.mat == null || this.mat == AIR) {
      _logger.debug("empty");
      return "";

    } else if (this.mat == END_PORTAL) {
      _logger.debug("Material.END_PORTAL");
      if (this.damage == 0) {
        _logger.debug("OakStorageSign");
        return "OakStorageSign";
      }

      if (this.damage == 1) {
        _logger.debug("HorseEgg");
        return "HorseEgg";
      }

    } else if (this.mat == OAK_SIGN && this.damage == 1) {
      _logger.debug("OakStorageSign");
      return "OakStorageSign";

    } else if (this.mat == SPRUCE_SIGN && this.damage == 1) {
      _logger.debug("SpruceStorageSign");
      return "SpruceStorageSign";

    } else if (this.mat == BIRCH_SIGN && this.damage == 1) {
      _logger.debug("BirchStorageSign");
      return "BirchStorageSign";

    } else if (this.mat == JUNGLE_SIGN && this.damage == 1) {
      _logger.debug("JungleStorageSign");
      return "JungleStorageSign";

    } else if (this.mat == ACACIA_SIGN && this.damage == 1) {
      _logger.debug("AcaciaStorageSign");
      return "AcaciaStorageSign";

    } else if (this.mat == DARK_OAK_SIGN && this.damage == 1) {
      _logger.debug("DarkOakStorageSign");
      return "DarkOakStorageSign";

    } else if (this.mat == CRIMSON_SIGN && this.damage == 1) {
      _logger.debug("CrimsonStorageSign");
      return "CrimsonStorageSign";

    } else if (this.mat == WARPED_SIGN && this.damage == 1) {
      _logger.debug("WarpedStorageSign");
      return "WarpedStorageSign";

    } else if (this.mat == MANGROVE_SIGN && this.damage == 1) {
      _logger.debug("MangroveStorageSign");
      return "MangroveStorageSign";

    } else if (this.mat == CHERRY_SIGN && this.damage == 1) {
      _logger.debug("CherryStorageSign");
      return "CherryStorageSign";

    } else if (this.mat == BAMBOO_SIGN && this.damage == 1) {
      _logger.debug("BambooStorageSign");
      return "BambooStorageSign";
    } else if (this.mat == ENCHANTED_BOOK) {
      _logger.debug("ENCHBOOK + data");
      return "ENCHBOOK:" + EnchantInfo.getShortType(this.ench) + ":" + this.damage;

    } else if (this.mat == POTION || this.mat == SPLASH_POTION
        || this.mat == LINGERING_POTION) {
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
    _logger.debug("getStorageSign:start");
    ItemStack item = new ItemStack(this.smat, this.stack);
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName("StorageSign");
    List<String> list = new ArrayList<>();

    // IDとMaterial名が混ざってたり、エンチャ本対応したり
    if (this.isEmpty) {
      _logger.debug("Empty");
      list.add("Empty");

    } else if (this.mat == ENCHANTED_BOOK) {
      _logger.debug("ENCHANTED_BOOK");
      list.add(this.mat.toString() + ":" + this.ench.getKey().getKey() + ":" + this.damage + " "
          + this.amount);

    } else if (this.mat == POTION || this.mat == SPLASH_POTION
        || this.mat == LINGERING_POTION) {
      _logger.debug("any POTION");
      list.add(PotionInfo.getTagData(this.mat, pot, this.damage, this.amount));

    } else {
      _logger.debug("other");
      list.add(getShortName() + " " + this.amount);

    }

    meta.setLore(list);
    meta.setMaxStackSize(ConfigLoader.getMaxStackSize());
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
    ItemStack emptyHorseEgg = new ItemStack(GHAST_SPAWN_EGG);
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

    if (this.mat == END_PORTAL) {
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

    if (this.mat == STONE_SLAB) {
      _logger.debug("STONE_SLAB");
      // ダメージ値0にする
      return new ItemStack(this.mat, 1);

    } else if (this.mat == OAK_SIGN || this.mat == SPRUCE_SIGN
        || this.mat == BIRCH_SIGN || this.mat == JUNGLE_SIGN
        || this.mat == ACACIA_SIGN || this.mat == DARK_OAK_SIGN
        || this.mat == CRIMSON_SIGN || this.mat == WARPED_SIGN
        || this.mat == MANGROVE_SIGN || this.mat == CHERRY_SIGN
        || this.mat == BAMBOO_SIGN) {
      _logger.debug("any SIGN");

      if (this.damage == 0) {
        _logger.debug("damage is 0");
        return new ItemStack(this.mat, 1);

      } else {
        _logger.debug("damage isn't 0");
        return emptySign(this.mat);
      }

    } else if (this.mat == ENCHANTED_BOOK) {
      _logger.debug("ENCHANTED_BOOK");
      ItemStack item = new ItemStack(this.mat, 1);

      EnchantmentStorageMeta enchantMeta = (EnchantmentStorageMeta) item.getItemMeta();
      enchantMeta.addStoredEnchant(this.ench, this.damage, true);
      item.setItemMeta(enchantMeta);

      return item;

    } else if (this.mat == POTION || this.mat == SPLASH_POTION
        || this.mat == LINGERING_POTION) {
      _logger.debug("any POTION");

      ItemStack item = new ItemStack(this.mat, 1);
      PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
      // potionMeta.setBasePotionData(new PotionData(pot, damage == 1, damage == 2));

      potionMeta.setBasePotionType(this.pot);
      item.setItemMeta(potionMeta);

      return item;

    } else if (this.mat == FIREWORK_ROCKET) {
      _logger.debug("FIREWORK_ROCKET");
      ItemStack item = new ItemStack(this.mat, 1);
      FireworkMeta fireworkMeta = (FireworkMeta) item.getItemMeta();

      fireworkMeta.setPower(this.damage);
      item.setItemMeta(fireworkMeta);

      return item;

    } else if (this.mat == WHITE_BANNER && this.damage == 8) {
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
    _logger.debug(" isSimilar:start");
    if (item == null) {
      _logger.debug(" Item isn't Similar");
      return false;

    }

    if (this.mat == ENCHANTED_BOOK && item.getType() == ENCHANTED_BOOK) {
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
      _logger.debug(" Item isn't Similar");
      return false;

    } else if (this.mat == POTION || this.mat == SPLASH_POTION
        || this.mat == LINGERING_POTION) {
      _logger.debug(" This mat is PotionSeries.");

      _logger.trace(" this.mat.equals(item.getType()): " + this.mat.equals(item.getType()));
      if (this.mat.equals(item.getType())) {
        PotionMeta pom = (PotionMeta) item.getItemMeta();
        _logger.trace(" pom.getBasePotionType().equals(this.pot): " + pom.getBasePotionType().equals(this.pot));
        if (pom.getBasePotionType().equals(this.pot)) {
          return true;
        }
      }
    } else if (block_entity_data_Materials.contains(this.mat)) {
      // block_entity_data_Materialsに入ってるものは、isSimilarで比較できないので、Materialが一緒かで判定
      _logger.debug(" This mat is block_entity_data_Materials.");
      return this.mat.equals(item.getType());
    }

    // SSなのかだけ、別ロジックで判定
    ItemStack contents = getContents();
    boolean isStorageSign = isStorageSign(item, _logger);
    boolean contentIsStorageSign = isStorageSign(contents, _logger);
    boolean isSignPost = isSignPost(item.getType(), _logger);
    _logger.trace("isStorageSign: " + isStorageSign);
    _logger.trace("contents.getType() == item.getType(): " + (contents.getType() == item.getType()));
    if(isStorageSign && contentIsStorageSign){
      if(contents.getType() == item.getType()){
        StorageSign cSign = new StorageSign(contents, _logger);
        StorageSign iSign = new StorageSign(item, _logger);
        _logger.trace("cSign.isEmpty() == iSign.isEmpty(): " + (cSign.isEmpty() == iSign.isEmpty()));
        return cSign.isEmpty() == iSign.isEmpty();
      }
    }

    // それ以外のItemはisSimilarで判定
    boolean isSimilar = contents.isSimilar(item);
    _logger.trace(" isSimilar: " + isSimilar);
    return isSimilar;
  }

  /**
   * フィールド変数：damageのgetter.
   */
  public short getDamage() {
    _logger.debug(" getDamage");
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
    _logger.debug(" getSmat");
    return this.smat;
  }

  /**
   * 渡されたItemStackがStorageSignか判断.
   */
  public static boolean isStorageSign(ItemStack item, Logger logger) {
    logger.debug(" isStorageSign(ItemStack):Start");
    if (item == null) {
      logger.debug(" item is null.");
      return false;
    }

    boolean isSignPost =  isSignPost(item.getType(), logger);
    logger.trace(" isSignPost:" +isSignPost);
    if (isSignPost) {

      logger.trace(" !item.getItemMeta().hasDisplayName(): " + !item.getItemMeta().hasDisplayName());
      if (!item.getItemMeta().hasDisplayName()) {
        logger.debug(" itemMeta hasn't displayName.");
        return false;
      }
      logger.trace(
          " !item.getItemMeta().getDisplayName().matches(\"StorageSign\"): " + !item.getItemMeta()
              .getDisplayName().matches("StorageSign"));
      if (!item.getItemMeta().getDisplayName().matches("StorageSign")) {
        logger.debug(" itemMetaName hasn't StorageSign.");
        return false;
      }
      logger.trace(" item.getItemMeta().hasLore(): " + item.getItemMeta().hasLore());
      return item.getItemMeta().hasLore();
    }
    logger.debug(" isSignPost is false.");
    return false;
  }

  /**
   * 渡されたBlockがStorageSignか判断.
   */
  public static boolean isStorageSign(Block block, Logger logger) {
    logger.debug(" isStorageSign(Block):Start");

    boolean isSignPost = isSignPost(block.getType(), logger);
    boolean isWallSign =  isWallSign(block.getType(), logger);
    logger.trace(" block.getType(): " + block.getType());
    logger.trace(" isSignPost(block.getType()): " + isSignPost);
    logger.trace(" isWallSign(block.getType()) :" + isWallSign);
    if (isSignPost || isWallSign) {
      logger.debug(" This Block is Sign.");
      Sign sign = (Sign) block.getState();

      logger.trace(" sign.getSide(Side.FRONT).getLine(0).matches(\"StorageSign\"): " + sign.getSide(
          Side.FRONT).getLine(0).matches("StorageSign"));
      if (sign.getSide(Side.FRONT).getLine(0).matches("StorageSign")) {
        logger.debug(" This Sign is StorageSign.");
        return true;
      }
    }

    logger.debug(" This Block isn't StorageSign.");
    return false;
  }

  /**
   * 渡されたBlockが看板か判断.
   */
  public static boolean isSignPost(Block block, Logger logger) {
    logger.debug("  isSignPost(Block)");
    Material mat = block.getType();
    return isSignPost(mat, logger);
  }

  /**
   * 渡されたBlockが壁掛け看板か判断.
   */
  public static boolean isWallSign(Block block, Logger logger) {
    Material mat = block.getType();
    return isWallSign(mat, logger);
  }

  /**
   * 渡されたMaterialが看板か判断.
   */
  //看板も8種類になったし、mat版おいとく
  public static boolean isSignPost(Material mat, Logger logger) {
    logger.debug("  isSignPost(Material): Start");

    logger.trace("  mat: " + mat);
    switch (mat) {
      case OAK_SIGN:
      case BIRCH_SIGN:
      case SPRUCE_SIGN:
      case JUNGLE_SIGN:
      case ACACIA_SIGN:
      case DARK_OAK_SIGN:
      case CRIMSON_SIGN:
      case WARPED_SIGN:
      case MANGROVE_SIGN:
      case CHERRY_SIGN:
      case BAMBOO_SIGN:
        logger.debug("  this Material is Sign.");
        return true;
      default:
    }
    logger.debug("  this Material isn't Sign.");
    return false;
  }

  /**
   * 渡されたMaterialが壁掛け看板か判断.
   */
  public static boolean isWallSign(Material mat, Logger logger) {
    logger.debug("  isWallSign(Material): Start");

    logger.trace("  mat: " + mat);
    switch (mat) {
      case OAK_WALL_SIGN:
      case BIRCH_WALL_SIGN:
      case SPRUCE_WALL_SIGN:
      case JUNGLE_WALL_SIGN:
      case ACACIA_WALL_SIGN:
      case DARK_OAK_WALL_SIGN:
      case CRIMSON_WALL_SIGN:
      case WARPED_WALL_SIGN:
      case MANGROVE_WALL_SIGN:
      case CHERRY_WALL_SIGN:
      case BAMBOO_WALL_SIGN:
        logger.debug("  this Material is WallSign.");
        return true;
      default:
    }
    logger.debug("  this Material isn't WallSign.");
    return false;
  }

}
