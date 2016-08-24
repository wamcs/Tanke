package com.lptiyu.tanke.gamedata;

import android.view.ViewGroup;

import com.lptiyu.tanke.base.recyclerview.BaseAdapter;
import com.lptiyu.tanke.base.recyclerview.BaseViewHolder;
import com.lptiyu.tanke.pojo.GameDataEntity;
import com.lptiyu.tanke.pojo.GameDataFinishEntity;
import com.lptiyu.tanke.pojo.GameDataNormalEntity;
import com.lptiyu.tanke.pojo.GameDataStartEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : xiaoxiaoda
 *         date: 16-6-6
 *         email: wonderfulifeel@gmail.com
 */
public class GameDataAdapter extends BaseAdapter<GameDataEntity> {

  private GameDataStartEntity mGameDataStartEntity;
  private GameDataFinishEntity mGameDataFinishEntity;
  private List<GameDataEntity> gameDataEntities;

  private static final int GAME_DATA_VIEW_TYPE_START = 0;
  private static final int GAME_DATA_VIEW_TYPE_NORMAL = 1;
  private static final int GAME_DATA_VIEW_TYPE_FINISH = 2;

  @Override
  public BaseViewHolder<GameDataEntity> onCreateViewHolder(ViewGroup parent, int viewType) {
    BaseViewHolder<GameDataEntity> holder;
    switch (viewType) {
      case GAME_DATA_VIEW_TYPE_START:
        holder = new GameDataViewHolderStart(parent);
        break;

      case GAME_DATA_VIEW_TYPE_NORMAL:
        holder = new GameDataViewHolderNormal(parent);
        break;

      case GAME_DATA_VIEW_TYPE_FINISH:
        holder = new GameDataViewHolderFinish(parent);
        break;

      default:
        holder = new GameDataViewHolderNormal(parent);
    }
    return holder;
  }

  @Override
  public void onBindViewHolder(BaseViewHolder<GameDataEntity> holder, int position) {
    int type = getItemViewType(position);
    switch (type) {
      case GAME_DATA_VIEW_TYPE_START:
        holder.bind(mGameDataStartEntity);
        break;

      case GAME_DATA_VIEW_TYPE_NORMAL:
        holder.bind(gameDataEntities.get(position - 1));
        break;

      case GAME_DATA_VIEW_TYPE_FINISH:
        holder.bind(mGameDataFinishEntity);
        break;
    }
  }

  @Override
  public int getItemViewType(int position) {
    if (position == 0) {
      return GAME_DATA_VIEW_TYPE_START;
    }
    if (gameDataEntities.size() + 1 == position) {
      return GAME_DATA_VIEW_TYPE_FINISH;
    }
    return GAME_DATA_VIEW_TYPE_NORMAL;
  }

  @Override
  public int getItemCount() {
    int result = 0;
    if (mGameDataFinishEntity != null) {
      result += 1;
    }
    if (mGameDataStartEntity != null) {
      result += 1;
    }
    if (gameDataEntities == null) {
      return result;
    }
    return result + gameDataEntities.size();
  }

  @Override
  public void addData(List<GameDataEntity> data) {
    if (gameDataEntities == null) {
      gameDataEntities = new ArrayList<>();
    }
    gameDataEntities.addAll(data);
  }

  @Override
  public void setData(List<GameDataEntity> data) {
    gameDataEntities = data;
  }

  public void bindStartEntityAndFinishEntity(GameDataStartEntity startEntity, GameDataFinishEntity finishEntity) {
    mGameDataStartEntity = startEntity;
    mGameDataFinishEntity = finishEntity;
  }

}
