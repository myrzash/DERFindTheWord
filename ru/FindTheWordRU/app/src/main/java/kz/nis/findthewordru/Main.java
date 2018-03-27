package kz.nis.findthewordru;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity {

    private static long back_pressed;
    private TextView textViewTask;
    private Animation animScale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewTask = (TextView)findViewById(R.id.textViewTask);
        textViewTask.setTypeface(FontFactory.getFont1(getApplicationContext()));
        ImageView imageViewStart  = (ImageView)findViewById(R.id.imageViewStart);
        animScale = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_scale_repeat);
        imageViewStart.startAnimation(animScale);
    }

    public void onClickStart(View v){
        startActivity(new Intent(Main.this, Game.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    protected void onDestroy() {
        animScale.cancel();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            finish();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else
            Toast.makeText(Main.this, R.string.toast_exit,Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }
}
