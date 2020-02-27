package com.study.ssa.CharacterData;

/**
 * キャラクター情報
 */
public class CharacterInfo {

    /** 未購入 */
    public static final int STATUS_NOT_PURCHASED = 0;
    /** 購入済 */
    public static final int STATUS_PURCHASED = 1;
    /** 選択中 */
    public static final int STATUS_SELECTED = 2;

    private String mName;
    private int mPrice;
    private String mIcon;
    private int mStatus;

    // セッター・ゲッター
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getPrice() {
        return mPrice;
    }

    public void setPrice(int price) {
        mPrice = price;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }
}
