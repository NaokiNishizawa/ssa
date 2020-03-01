package com.study.ssa.UI.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.study.ssa.CharacterData.CharacterInfo;
import com.study.ssa.R;

import androidx.recyclerview.widget.RecyclerView;

/**
 * キャラクターリストのRecyclerViewHolder
 */
public class CharacterListViewHolder extends RecyclerView.ViewHolder {

    public CharacterInfo mInfo;
    public TextView mCharacterName;
    public ImageView mCoin;
    public TextView mPrice;
    public LinearLayout mBuy;
    public ImageView mCharacterIcon;

    public CharacterListViewHolder(View itemView) {
        super(itemView);

        mCharacterName = itemView.findViewById(R.id.character_name);
        mCoin = itemView.findViewById(R.id.character_coin_icon);
        mPrice = itemView.findViewById(R.id.character_price_text);
        mBuy = itemView.findViewById(R.id.character_buy);
        mCharacterIcon = itemView.findViewById(R.id.character_icon);
    }
}
