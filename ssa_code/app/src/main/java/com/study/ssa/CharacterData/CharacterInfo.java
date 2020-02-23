package com.study.ssa.CharacterData;

/**
 * キャラクター情報
 */
public class CharacterInfo {

    private String mName;
    private int mPrice;
    private String mIcon;

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
}
