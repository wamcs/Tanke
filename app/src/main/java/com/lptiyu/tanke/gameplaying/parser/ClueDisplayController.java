package com.lptiyu.tanke.gameplaying.parser;

import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.utils.Display;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.ViewGroup.*;

/**
 * author:wamcs
 * date:2016/7/11
 * email:kaili@hustunique.com
 */
public class ClueDisplayController extends ActivityController {


  @BindView(R.id.clue_display_main_layout)
  LinearLayout mainLayout;

  private static final float heightRate = 0.75f;
  private static final float widthRate = 0.65f;

  private Parser parser;

  private static final String TEXT_CONTENT = "<tanke><p>anlkcnklkaslnd<b>dsahjkcbsa</b>cbhjsda</p><img>http://7xt1ey.com1.z0.glb.clouddn.com/AIDL%E9%A1%B9%E7%9B%AE%E7%BB%93%E6%9E%84.png</img>" +
      "<p>cajskjsal<br></br>cjksajdjkc</p><video>http://7xt1ey.com1.z0.glb.clouddn.com/132c1f415a2adda8dbed1f8dd7f1f04f.mp4</video><p>fds<br></br><br></br><br></br>dsa</p><audio>http://7xt1ey.com1.z0.glb.clouddn.com/You_Raise_Me_Up_Westlife%5B1%5D.mp3</audio>" +
      "<p>sjkdasljsxa</p><img>http://7xt1ey.com1.z0.glb.clouddn.com/AIDL%E9%A1%B9%E7%9B%AE%E7%BB%93%E6%9E%84.png</img><audio>http://7xt1ey.com1.z0.glb.clouddn.com/Sean_Dagher_-_Randy_Dandy_Oh%5B1%5D.mp3</audio></tanke>";

  public ClueDisplayController(AppCompatActivity activity, View view) {
    super(activity, view);
    ButterKnife.bind(this, view);
    init(view);

  }

  private void init(View view) {

    Window window = getWindow();
    WindowManager.LayoutParams wlp= window.getAttributes();
    wlp.height = Display.height();
    wlp.width = Display.width();
    window.setAttributes(wlp);

    LayoutParams params= mainLayout.getLayoutParams();
    params.height = (int) (Display.height() * heightRate);
    params.width = (int) (Display.width() * widthRate);
    mainLayout.setLayoutParams(params);
    mainLayout.setGravity(Gravity.CENTER);

    parser = new Parser(getContext(), view);
    parser.setSignText(TEXT_CONTENT,1 );
    parser.generateView();
  }

  @OnClick(R.id.clue_display_begin_button)
  void beginDecoed(){}

  @OnClick(R.id.clue_display_close_button)
  void close() {
    finish();
  }

  @OnClick(R.id.clue_display_background)
  void back(){
   finish();
  }

  @Override
  public boolean onBackPressed() {
    return parser.stop();
  }
}
