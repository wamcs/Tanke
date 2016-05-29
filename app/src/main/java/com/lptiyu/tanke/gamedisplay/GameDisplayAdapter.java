package com.lptiyu.tanke.gamedisplay;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.recyclerview.BaseAdapter;
import com.lptiyu.tanke.pojo.GAME_TYPE;
import com.lptiyu.tanke.pojo.GameDisplayEntity;
import com.lptiyu.tanke.utils.TimeUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/18
 *
 * @author ldx
 */
public class GameDisplayAdapter extends BaseAdapter<GameDisplayAdapter.ViewHolder, GameDisplayEntity> {

  private List<GameDisplayEntity> dataList;

  private GameDisplayFragment fragment;

  public GameDisplayAdapter(GameDisplayFragment fragment) {
    this.fragment = fragment;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ViewHolder(parent, viewType);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    holder.bind(dataList.get(position));
  }

  @Override
  public int getItemCount() {
    return dataList == null ? 0 : dataList.size();
  }

  @Override
  public void addData(List<GameDisplayEntity> data) {
    if (dataList == null) {
      setData(data);
      return;
    }
    int i = dataList.size();
    dataList.addAll(data);
    notifyItemInserted(i);
  }

  @Override
  public void setData(List<GameDisplayEntity> data) {
    this.dataList = data;
    notifyDataSetChanged();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.image_view)
    ImageView imageView;

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.team_type)
    TextView teamType;

    @BindView(R.id.location)
    TextView location;

    @BindView(R.id.time)
    TextView timeView;

    @BindView(R.id.tag)
    TextView tag;

    GameDisplayEntity gameDisplayEntity;

    ViewHolder(ViewGroup parent, int viewType) {
      super(fromResLayout(parent, R.layout.item_game_display));
      ButterKnife.bind(this, itemView);
    }

    private void bind(GameDisplayEntity entity) {
      this.gameDisplayEntity = entity;
      parseImage(entity);
      parseTitle(entity);
      parseLocation(entity);
      parseTime(entity);
      parseTag(entity);
      parseTeamType(entity);
    }

    private void parseImage(GameDisplayEntity entity) {
      Glide.with(fragment).load(entity.getImg()).asBitmap().into(imageView);
    }

    private void parseTitle(GameDisplayEntity entity) {
      title.setText(entity.getTitle());
    }

    private void parseLocation(GameDisplayEntity entity) {
      location.setText(entity.getArea());
    }

    private void parseTime(GameDisplayEntity entity) {
      String result;
      Calendar calendar = Calendar.getInstance();

      Date date = TimeUtils.parseDate(entity.getStartDate());
      if (date == null) {
        result = "";
      } else {
        calendar.setTime(date);
        int startMonth = calendar.get(Calendar.MONTH);
        int startDate = calendar.get(Calendar.DATE);
        date = TimeUtils.parseDate(entity.getEndDate());
        calendar.setTime(date);
        int endMonth = calendar.get(Calendar.MONTH);
        int endDate = calendar.get(Calendar.DATE);
        result = String.format(Locale.CHINA, fragment.getString(R.string.main_page_date_formatter),
            startMonth, startDate, endMonth, endDate);
      }

      Date time = TimeUtils.parseTime(entity.getStartTime());
      if (time == null) {
        result += fragment.getString(R.string.main_page_forever);
      } else {
        result += TimeUtils.formatHourMinute(entity.getStartTime());
        result += "-";
        result += TimeUtils.formatHourMinute(entity.getEndTime());
      }

      timeView.setText(result);
    }

    private void parseTag(GameDisplayEntity entity) {
      switch (entity.getRecommend()) {
        case RECOMMENDED:
          tag.setText("推荐");
          return;
        default:
      }

    }

    private void parseTeamType(GameDisplayEntity entity) {
      if (entity.getType() == GAME_TYPE.TEAMS) {
        teamType.setText(fragment.getString(R.string.team_type_team));
      } else {
        teamType.setText(fragment.getString(R.string.team_type_individule));
      }
    }

    @OnClick(R.id.item_root)
    void onItemClick() {
      GameDisplayController controller = fragment.getController();
      if (controller == null) {
        Timber.e("GameDisplayFragment get Controller is null");
        return;
      }

      controller.onItemClick(gameDisplayEntity);
    }
  }

}
