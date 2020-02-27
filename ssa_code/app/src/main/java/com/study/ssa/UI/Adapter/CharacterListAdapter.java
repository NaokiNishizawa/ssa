package com.study.ssa.UI.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.study.ssa.CharacterData.CharacterInfo;
import com.study.ssa.R;
import com.study.ssa.UI.ViewHolder.CharacterListViewHolder;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * キャラクターリスト RecyclerViewのAdapter
 */
public class CharacterListAdapter extends RecyclerView.Adapter<CharacterListViewHolder> {

    private Context mContext;
    private List<CharacterInfo> mAllCharacterList;
    private onClickListener mListener;

    public interface onClickListener {
        public void BuyCharacter(CharacterInfo info);
    }

    /**
     * コンストラクタ
     *
     * @param  context
     * @param list
     * @param listener
     */
    public CharacterListAdapter(Context context,List<CharacterInfo> list, onClickListener listener)  {
        mContext = context;
        setList(list);
        mListener = listener;
    }

    @Override
    public CharacterListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.character_list_row, parent,false);
        CharacterListViewHolder vh = new CharacterListViewHolder(inflate);
        return vh;
    }

    @Override
    public void onBindViewHolder(final CharacterListViewHolder holder, int position) {
        holder.mInfo = mAllCharacterList.get(position);
        CharacterInfo info = holder.mInfo;

        holder.mCharacterName.setText(info.getName());

        // 未購入の場合は値段、購入済・選択中のものはそれぞれのステータスを表示する
        if(CharacterInfo.STATUS_NOT_PURCHASED == info.getStatus()) {
            holder.mCoin.setVisibility(View.VISIBLE);
            holder.mPrice.setText(String.valueOf(info.getPrice()));
        } else if(CharacterInfo.STATUS_PURCHASED == info.getStatus()) {
            holder.mCoin.setVisibility(View.INVISIBLE);
            holder.mPrice.setText(mContext.getString(R.string.do_select));
        } else {
            holder.mCoin.setVisibility(View.INVISIBLE);
            holder.mPrice.setText(mContext.getString(R.string.selecting));
        }

        holder.mCharacterIcon.setImageResource(mContext.getResources().getIdentifier(info.getIcon(), "mipmap", mContext.getPackageName()));

        holder.mBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 選択中がクリックされた場合は何もしない
                if(CharacterInfo.STATUS_SELECTED == holder.mInfo.getStatus()) {
                    return;
                }

                // 購入したことを画面に通知
                if(null != mListener) {
                    mListener.BuyCharacter(holder.mInfo);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAllCharacterList.size();
    }

    /**
     * リストをセット
     *
     * @param list
     */
    public void setList(List<CharacterInfo> list) {
        mAllCharacterList = list;
    }
}
