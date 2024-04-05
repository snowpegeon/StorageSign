package wacky.storagesign;

import org.bukkit.Material;
import org.bukkit.potion.PotionType;
import org.bukkit.util.NumberConversions;
import wacky.storagesign.Exception.PotionException;
import wacky.storagesign.Logging.SSLogger;

import java.util.Arrays;

public class PotionInfo {

    private static String ENHANCE_NORMAL_CODE = "0";
    private static String ENHANCE_EXTENSION_CODE = "1";
    private static String ENHANCE_STRONG_CODE = "2";
    private static String ENHANCE_EXTENSION_PREF = "LONG_";
    private static String ENHANCE_STRONG_PREF = "STRONG_";
    private static String SHORT_NAME_HEAL = "HEAL";
    private static String SHORT_NAME_BREAT = "BREAT";
    private static String SHORT_NAME_DAMAG = "DAMAG";
    private static String SHORT_NAME_JUMP = "JUMP";
    private static String SHORT_NAME_SPEED = "SPEED";
    private static PotionType[] HEAL_POTIONS = {PotionType.INSTANT_HEAL, PotionType.STRONG_HEALING};
    private static PotionType[] BREATH_POTIONS = {PotionType.WATER_BREATHING, PotionType.LONG_WATER_BREATHING};
    private static PotionType[] DAMAGE_POTIONS = {PotionType.INSTANT_DAMAGE, PotionType.STRONG_HARMING};
    private static PotionType[] JUMP_POTIONS = {PotionType.JUMP, PotionType.LONG_LEAPING, PotionType.STRONG_LEAPING};
    private static PotionType[] SPEED_POTIONS = {PotionType.SPEED, PotionType.LONG_SWIFTNESS, PotionType.STRONG_SWIFTNESS};
    protected static String TYPE_SPLASH_PREF = "S";
    protected static String TYPE_LINGERING_PREF = "L";

    protected Material mat;
    protected PotionType pot;
    protected short damage = 0;
    protected SSLogger logger;

    // 旧メソッド
	public PotionInfo(Material mat, String[] str){
		this.mat = mat;
		if(str.length == 2){//ダメージ値
			short old = NumberConversions.toShort(str[1]);
			if(old >= 16384) this.mat = Material.SPLASH_POTION;
			switch(old % 16){
			case 1:
				pot = PotionType.REGEN;
			    break;
			case 2:
				pot = PotionType.SPEED;
			    break;
			case 3:
				pot = PotionType.FIRE_RESISTANCE;
			    break;
			case 4:
				pot = PotionType.POISON;
			    break;
			case 5:
				pot = PotionType.INSTANT_HEAL;
			    break;
			case 6:
				pot = PotionType.NIGHT_VISION;
			    break;
			case 8:
				pot = PotionType.WEAKNESS;
			    break;
			case 9:
				pot = PotionType.STRENGTH;
			    break;
			case 10:
				pot = PotionType.SLOWNESS;
			    break;
			case 11:
				pot = PotionType.JUMP;
			    break;
			case 12:
				pot = PotionType.INSTANT_DAMAGE;
			    break;
			case 13:
				pot = PotionType.WATER_BREATHING;
				break;
			case 14:
				pot = PotionType.INVISIBILITY;
				break;

			default:
				if(old == 16) pot = PotionType.AWKWARD;
				else if(old == 32) pot = PotionType.THICK;
				else if(old == 64 || old == 8192 || old == 16384) pot = PotionType.MUNDANE;
				else pot = PotionType.WATER;
			}

			if(old % 8192 > 64 && pot.isExtendable()) damage = 1;//延長
			else if(old % 64 > 32 && pot.isUpgradeable()) damage = 2;//強化

		}else if(str.length == 1) pot = PotionType.WATER;

		else{//タイプとレベル
			pot = getType(str[1]);
			damage = NumberConversions.toShort(str[2]);
		}
	}

    public PotionInfo(Material material, String type, String effName, String enhance) {
        logger = new SSLogger("");
        logger.debug("PotionItnfoConstructor:Start");
        this.mat = material;

        // ポーション種別の設定
        if (type.startsWith(TYPE_SPLASH_PREF)) {
            this.mat = Material.SPLASH_POTION;
        } else if (type.startsWith(TYPE_LINGERING_PREF)) {
            // 元のやつだと入ってないからいらない？
            this.mat = Material.LINGERING_POTION;
        }

        this.pot = getType(effName, enhance);
        this.damage = NumberConversions.toShort(enhance);

        // 一応前のロジックも入れておく
        if (damage % 8192 > 64 && pot.isExtendable()) this.damage = 1;//延長
        else if (damage % 64 > 32 && pot.isUpgradeable()) this.damage = 2;//強化

    }

    // 旧メソッド
    private PotionType getType(String substring) {
        if (substring.equals("BREAT")) return PotionType.WATER_BREATHING;//例外
        else if (substring.equals("HEAL")) return PotionType.INSTANT_HEAL;
        else if (substring.equals("DAMAG")) return PotionType.INSTANT_DAMAGE;
        else { //後ろ切れてるかも
            for (PotionType p : PotionType.values()) {
                if (p.toString().startsWith(substring)) return p;
            }
        }
		return null;
	}

    private PotionType getType(String effName, String enhance) {

        // 表記上例外のケースたちの判定
        if (effName.equals(SHORT_NAME_HEAL)) {
            if (enhance.equals(ENHANCE_NORMAL_CODE)) {
                return PotionType.INSTANT_HEAL;
            } else if (enhance.equals(ENHANCE_STRONG_CODE)) {
                return PotionType.STRONG_HEALING;
            } else {
                throw new PotionException("治癒のポーションに対応する種類が存在しません");
            }
        } else if (effName.equals(SHORT_NAME_BREAT)) {
            if (enhance.equals(ENHANCE_NORMAL_CODE)) {
                return PotionType.WATER_BREATHING;
            } else if (enhance.equals(ENHANCE_EXTENSION_CODE)) {
                return PotionType.LONG_WATER_BREATHING;
            } else {
                throw new PotionException("水中呼吸のポーションに対応する種類が存在しません");
            }
        } else if (effName.equals(SHORT_NAME_DAMAG)) {
            if (enhance.equals(ENHANCE_NORMAL_CODE)) {
                return PotionType.INSTANT_DAMAGE;
            } else if (enhance.equals(ENHANCE_STRONG_CODE)) {
                return PotionType.STRONG_HARMING;
            } else {
                throw new PotionException("ダメージのポーションに対応する種類が存在しません");
            }
        } else if (effName.equals(SHORT_NAME_JUMP)) {
            if (enhance.equals(ENHANCE_NORMAL_CODE)) {
                return PotionType.JUMP;
            } else if (enhance.equals(ENHANCE_EXTENSION_CODE)) {
                return PotionType.LONG_LEAPING;
            } else if (enhance.equals(ENHANCE_STRONG_CODE)) {
                return PotionType.STRONG_LEAPING;
            } else {
                throw new PotionException("跳躍のポーションに対応する種類が存在しません");
            }
        } else if (effName.equals(SHORT_NAME_SPEED)) {
            if (enhance.equals(ENHANCE_NORMAL_CODE)) {
                return PotionType.SPEED;
            } else if (enhance.equals(ENHANCE_EXTENSION_CODE)) {
                return PotionType.LONG_SWIFTNESS;
            } else if (enhance.equals(ENHANCE_STRONG_CODE)) {
                return PotionType.STRONG_SWIFTNESS;
            } else {
                throw new PotionException("俊敏のポーションに対応する種類が存在しません");
            }
        } else {
            // それ以外は命名ルールに従って検索をかける
            // 延長、強化があったら先頭にプレフィックスをつけて検索
            String name = effName;
            if (enhance.equals(ENHANCE_EXTENSION_CODE)) {
                name = ENHANCE_EXTENSION_PREF + name;
            } else if (enhance.equals(ENHANCE_STRONG_CODE)) {
                name = ENHANCE_STRONG_PREF + name;
            }
            for (PotionType p : PotionType.values()) {
                if (p.toString().startsWith(name)) return p;
            }
            throw new PotionException("ポーションデータが存在しません");
        }
    }

    public static String getShortType(PotionType pot) {
        if (pot == PotionType.WATER_BREATHING) return "BREAT";
        else if (pot == PotionType.INSTANT_HEAL) return "HEAL";
        else if (pot == PotionType.INSTANT_DAMAGE) return "DAMAG";
        else if (pot.toString().length() <= 5) return pot.toString();
        return pot.toString().substring(0, 5);
    }

    public static String getShortName(PotionType pot) {
        String name = pot.toString();

        // 特殊命名ルールの取り出し
        if (Arrays.asList(HEAL_POTIONS).contains(pot)) {
            return SHORT_NAME_HEAL;
        } else if (Arrays.asList(BREATH_POTIONS).contains(pot)) {
            return SHORT_NAME_BREAT;
        } else if (Arrays.asList(DAMAGE_POTIONS).contains(pot)) {
            return SHORT_NAME_DAMAG;
        } else if (Arrays.asList(JUMP_POTIONS).contains(pot)) {
            return SHORT_NAME_JUMP;
        } else if (Arrays.asList(SPEED_POTIONS).contains(pot)) {
            return SHORT_NAME_SPEED;
        } else {
            // それ以外のもの
            // 延長・強化のプレフィックス除去
            if (name.startsWith(ENHANCE_EXTENSION_PREF)) {
                name = name.substring(ENHANCE_EXTENSION_PREF.length());
            } else if (name.startsWith(ENHANCE_STRONG_PREF)) {
                name = name.substring(ENHANCE_STRONG_PREF.length());
            }

            // 5文字以内の名前なら、そのまま使う。それ以外なら５文字に切り出す
            if (name.length() <= 5) {
                return name;
            }
        }

        return name.substring(0, 5);
    }

    public static String getSignData(Material mat, PotionType pot, short damage) {
        String prefix = "";
        if (mat == Material.SPLASH_POTION) prefix = PotionInfo.TYPE_SPLASH_PREF;
        else if (mat == Material.LINGERING_POTION) prefix = PotionInfo.TYPE_LINGERING_PREF;

        return prefix + "POTION:" + PotionInfo.getShortName(pot) + ":" + damage;
    }

    public static String getTagData(Material mat, PotionType pot, short damage, int amount) {
        return mat.toString() + ":" + pot.toString() + ":" + damage + " " + amount;
    }

    public static String getPotionTypeCode(PotionType pot){
        String name = pot.toString();
        if (name.startsWith(ENHANCE_EXTENSION_PREF)) {
            return ENHANCE_EXTENSION_CODE;
        } else if (name.startsWith(ENHANCE_STRONG_PREF)) {
            return ENHANCE_STRONG_CODE;
        }
        return ENHANCE_NORMAL_CODE;
    }

	public short getDamage() {
		return damage;
	}

	public Material getMaterial() {
		return mat;
	}

	public PotionType getPotionType() {
		return pot;
	}

}
