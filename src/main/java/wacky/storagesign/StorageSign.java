package wacky.storagesign;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
import wacky.storagesign.signdefinition.SignDefinition;
import wacky.storagesign.signdefinition.SignMatStringDefinition;

import static java.util.Map.entry;
import static org.bukkit.Material.*;

/**
 * StrageSignの実体クラスです.
 */
public class StorageSign {
  private  static final String storage_sign_name = "StorageSign";

  /**
   * block_entity_dataタグが付いて、単純な比較では収納ができないアイテム一覧.
   */
  private static final Set<Material> block_entity_data_Materials = Collections.unmodifiableSet(
      EnumSet.of(BEE_NEST, BEEHIVE));

  /**
   * SSの中でポーションと扱われるアイテム一覧.
   * ポーション種類が追加されたら追加する.
   */
  private static final Set<Material> potion_materials = Collections.unmodifiableSet(EnumSet.of(POTION, SPLASH_POTION, LINGERING_POTION));

  /**
   * 特殊なアイテム名とアイテム用マテリアルの変換一覧.
   * 特殊処理をするアイテムが追加されたら追加する.
   */
  private static final Map<String, Material> mat_string_material_maps = Map.ofEntries(
      entry("SIGN", OAK_SIGN),
      entry("ROSE_RED", RED_DYE),
      entry("DANDELION_YELLOW", YELLOW_DYE),
      entry("CACTUS_GREEN", GREEN_DYE),
      entry("ENCHBOOK", ENCHANTED_BOOK),
      entry("SPOTION", SPLASH_POTION),
      entry("LPOTION", LINGERING_POTION)
  );

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
  private final Logger _logger;

  /**
   * ItemStackに設定されている看板の情報からStorageSignを生成する.
   */
  public StorageSign(ItemStack item, Logger logger) {
    this._logger = logger;
    logger.debug("StorageSign:start");

    List<String> lore = Objects.requireNonNull(item.getItemMeta()).getLore();
    String[] str = Objects.requireNonNull(lore).getFirst().split(" ");

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

      } else if (potion_materials.contains(this.mat)) {
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

    } else if (potion_materials.contains(this.mat)) {
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
    if (SignDefinition.wall_normal_sign_maps.containsKey(signmat)) {
      logger.debug("signmat is " + SignDefinition.wall_normal_sign_maps.get(signmat));
      this.smat = SignDefinition.wall_normal_sign_maps.get(signmat);
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
   * 引数に渡された情報を基にStorageSignを作成して、ItemStackとして取得します.
   *
   * @param type Material
   * @param amount Integer
   * @param list List
   * @return ItemStack StorageSign
   */
  public static ItemStack createStorageSign(Material type, Integer amount, List<String> list){
    ItemStack sign;
    if(Objects.isNull(amount)) {
      sign = new ItemStack(type);
    } else {
      sign = new ItemStack(type, amount);
    }

    ItemMeta meta = sign.getItemMeta();
    Objects.requireNonNull(meta).setDisplayName(storage_sign_name);
    meta.setLore(list);
    meta.setMaxStackSize(ConfigLoader.getMaxStackSize());
    sign.setItemMeta(meta);

    return sign;
  }
  /**
   * Materialの内容から、空のStorageSignを作成して、ItemStackとして取得します.
   *
   * @param signMat Material
   * @return ItemStack 空のStorageSign
   */
  public static ItemStack emptySign(Material signMat) {
    return emptySign(signMat, null);
  }

  /**
   * Materialの内容から、空のStorageSignを作成して、ItemStackとして取得します.
   *
   * @param smat Material
   * @param amount Integer
   * @return ItemStack 空のStorageSign
   */
  public static ItemStack emptySign(Material smat, Integer amount) {
    List<String> list = new ArrayList<>();
    list.add("Empty");
    return createStorageSign(smat, amount, list);
  }

  /**
   * 指定された文字列でMaterialに変換します.
   *
   * @param str String
   * @return Material
   */
  protected Material getMaterial(String str) {
    _logger.debug("getMaterial:start");
    if (str.matches("")) {
      _logger.debug("Material is AIR");
      return AIR;

    } else if (str.matches("EmptySign")) {
      _logger.debug("Material is OldOakStorageSign");
      this.damage = 1;
      return OAK_SIGN;
    } else if (SignMatStringDefinition.asStringMaterialMap().containsKey(str)) {
      _logger.debug("Material is " + SignMatStringDefinition.asStringMaterialMap().get(str));
      this.damage = 1;
      return SignMatStringDefinition.asStringMaterialMap().get(str);
    } else if (mat_string_material_maps.containsKey(str)) {
      _logger.debug("Material is " + mat_string_material_maps.get(str));
      this.damage = 1;
      return mat_string_material_maps.get(str);
    }
    if (str.matches("HorseEgg")) {
      _logger.debug("Material is HorseEgg");
      this.damage = 1;
      return END_PORTAL;
      // ガスト卵でよくね？
    }

    Material mat = matchMaterial(str);

    if (Objects.isNull(mat)) {
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
      // SS看板に当てはまるものなら、一覧から出力する.
    } else if (SignMatStringDefinition.asMaterialStringMap().containsKey(this.mat) && this.damage == 1) {
      _logger.debug(SignMatStringDefinition.asMaterialStringMap().get(this.mat));
      return SignMatStringDefinition.asMaterialStringMap().get(this.mat);
    } else if (ENCHANTED_BOOK.equals(this.mat)) {
      _logger.debug("ENCHBOOK + data");
      return "ENCHBOOK:" + EnchantInfo.getShortType(this.ench) + ":" + this.damage;

    } else if (potion_materials.contains(this.mat)) {
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
    List<String> list = new ArrayList<>();

    // IDとMaterial名が混ざってたり、エンチャ本対応したり
    if (this.isEmpty) {
      _logger.debug("Empty");
      return emptySign(this.smat, this.stack);

    } else if (ENCHANTED_BOOK.equals(this.mat)) {
      _logger.debug("ENCHANTED_BOOK");
      list.add(this.mat.toString() + ":" + this.ench.getKey().getKey() + ":" + this.damage + " "
          + this.amount);

    } else if (potion_materials.contains(this.mat)) {
      _logger.debug("any POTION");
      list.add(PotionInfo.getTagData(this.mat, pot, this.damage, this.amount));

    } else {
      _logger.debug("other");
      list.add(getShortName() + " " + this.amount);

    }
    _logger.debug("getStorageSign:end");
    return createStorageSign(this.smat, this.stack, list);
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

    Objects.requireNonNull(meta).setDisplayName("HorseEgg");
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

    sign[0] = storage_sign_name;
    sign[1] = getShortName();
    sign[2] = String.valueOf(this.amount);
    sign[3] = (this.amount / 3456) + "LC " + (this.amount % 3456 / 64)
        + "s " + (this.amount % 64);

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

    } else if (SignDefinition.sign_materials.contains(this.mat)) {
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
      Objects.requireNonNull(enchantMeta).addStoredEnchant(this.ench, this.damage, true);
      item.setItemMeta(enchantMeta);

      return item;

    } else if (potion_materials.contains(this.mat)) {
      _logger.debug("any POTION");

      ItemStack item = new ItemStack(this.mat, 1);
      PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
      Objects.requireNonNull(potionMeta).setBasePotionType(this.pot);
      item.setItemMeta(potionMeta);

      return item;

    } else if (this.mat == FIREWORK_ROCKET) {
      _logger.debug("FIREWORK_ROCKET");
      ItemStack item = new ItemStack(this.mat, 1);
      FireworkMeta fireworkMeta = (FireworkMeta) item.getItemMeta();
      Objects.requireNonNull(fireworkMeta).setPower(this.damage);
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
      // ないと思うけど、metaがnullならfalse.
      if(Objects.isNull(enchantMeta)) {
        return false;
      }
      if (enchantMeta.getStoredEnchants().size() == 1) {
        _logger.debug("enchantMeta.getStoredEnchants().size() = 1");
        Enchantment itemEnch =
            enchantMeta.getStoredEnchants().keySet().toArray(new Enchantment[0])[0];

        if (itemEnch.equals(this.ench) && enchantMeta.getStoredEnchantLevel(itemEnch) == this.damage) {
          _logger.debug("Item is Similar");
          return true;
        }
      }
      _logger.debug(" Item isn't Similar");
      return false;

    } else if (potion_materials.contains(this.mat)) {
      _logger.debug(" This mat is PotionSeries.");

      _logger.trace(" this.mat.equals(item.getType()): " + this.mat.equals(item.getType()));
      if (this.mat.equals(item.getType())) {
        PotionMeta pom = (PotionMeta) item.getItemMeta();
        // ないと思うけど、pomがnullならfalse
        if(Objects.isNull(pom) || Objects.isNull(pom.getBasePotionType())) {
          return false;
        }
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
    _logger.trace("isStorageSign: " + isStorageSign);
    _logger.trace("contents.getType() == item.getType(): " + (contents.getType() == item.getType()));
    if (isStorageSign && contentIsStorageSign) {
      if (contents.getType() == item.getType()) {
        StorageSign contentSign = new StorageSign(contents, _logger);
        StorageSign itemSign = new StorageSign(item, _logger);
        _logger.trace("cSign.isEmpty() == isign.isEmpty(): " + (contentSign.isEmpty() == itemSign.isEmpty()));
        return contentSign.isEmpty() == itemSign.isEmpty();
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

    boolean isSignPost =  SignDefinition.sign_materials.contains(item.getType());
    logger.trace(" isSignPost:" + isSignPost);
    if (isSignPost) {
      // ないと思うけど、metaがなかったら比較できないのでfalse.
      if(Objects.isNull(item.getItemMeta())) {
        return false;
      }

      logger.trace(" !item.getItemMeta().hasDisplayName(): " + !item.getItemMeta().hasDisplayName());
      if (!item.getItemMeta().hasDisplayName()) {
        logger.debug(" itemMeta hasn't displayName.");
        return false;
      }
      logger.trace(
          " !item.getItemMeta().getDisplayName().matches(\"StorageSign\"): " + !item.getItemMeta()
              .getDisplayName().matches(storage_sign_name));
      if (!item.getItemMeta().getDisplayName().matches(storage_sign_name)) {
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

    boolean isSignPost = SignDefinition.sign_materials.contains(block.getType());
    boolean isWallSign =  SignDefinition.wall_sign_materials.contains(block.getType());
    logger.trace(" block.getType(): " + block.getType());
    logger.trace(" isSignPost(block.getType()): " + isSignPost);
    logger.trace(" isWallSign(block.getType()) :" + isWallSign);
    if (isSignPost || isWallSign) {
      logger.debug(" This Block is Sign.");
      Sign sign = (Sign) block.getState();

      logger.trace(" sign.getSide(Side.FRONT).getLine(0).matches(\"StorageSign\"): " + sign.getSide(
          Side.FRONT).getLine(0).matches(storage_sign_name));
      if (sign.getSide(Side.FRONT).getLine(0).matches(storage_sign_name)) {
        logger.debug(" This Sign is StorageSign.");
        return true;
      }
    }

    logger.debug(" This Block isn't StorageSign.");
    return false;
  }
}
