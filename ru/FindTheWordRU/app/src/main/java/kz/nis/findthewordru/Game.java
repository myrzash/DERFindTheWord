package kz.nis.findthewordru;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;


/**
 * Created by myrza on 12/8/15.
 */
public class Game extends Activity implements View.OnClickListener {

    private static final float TEXT_SIZE_CARD = 36;

    private static final String INTENT = "intent";
    private static final short INTENT_REPEAT = 1;
    private static final String KEY_BEST_SCORE = "bestscore";
    private TableLayout tableLetters;

    private TableRow rowCorrect;
    private TableRow rowLetters;
    private TableLayout table1;
    private TableLayout table2;

    private static final TableLayout.LayoutParams lpRow = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);

    //    private static final int marginCard = 10;
    private static final int maxRows = 10;
    private ArrayList<String> list = new ArrayList<String>();
    private static TableRow.LayoutParams lpCard = null;
    static int textColorDark = 0;
    static int textColorRed = 0;
    private String letters;
    private static Typeface typeface = null;
    private static String[] datas = null;
    private Timer timer;
    private static int current = 0;
    private SharedPreferences sp;
    private TextView scoreView;
    private int score = 0;
    private int bestScore = 0;
    private Animation animFadeInOut;
    private ImageView imageFinish;
    private boolean keyRepeat = false;
    private MediaPlayer mediaClick;
    private MediaPlayer mediaSound;
    private int numberOfWords = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        if (typeface == null)
            typeface = FontFactory.getFont1(getApplicationContext());
        if (textColorDark == 0)
            textColorDark = getResources().getColor(R.color.text_dark);
        if (textColorRed == 0)
            textColorRed = getResources().getColor(R.color.text_error);
        if (datas == null)
            datas = getResources().getStringArray(R.array.Words);
        if (lpCard == null) {
            lpCard = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
//            lpCard.setMargins(marginCard, marginCard, marginCard, marginCard);
        }
        mediaClick = MediaPlayer.create(this, R.raw.click);


        animFadeInOut = AnimationUtils.loadAnimation(this, R.anim.alpha_bird);

        if (getIntent().hasExtra(INTENT) && getIntent().getExtras().getInt(INTENT) == INTENT_REPEAT) {
            dialogFootnote();
            keyRepeat = true;
        } else {
            int currentNew = 0;
            do {
                currentNew = new Random().nextInt(datas.length);
            } while (currentNew == current);
            current = currentNew;
        }
        Log.d("Log", "current = " + current + "str[current]: " + datas[current]);


        tableLetters = (TableLayout) findViewById(R.id.tableLetters);
        table1 = (TableLayout) findViewById(R.id.tableLeft);
        table2 = (TableLayout) findViewById(R.id.tableRight);
// DATAS
        String[] array = datas[current].split("#");
        for (int i = 0; i < array.length; i++) {
            array[i] = array[i].trim();
        }

        letters = array[0].toUpperCase();
        letters = shuffle(letters);
        Log.d("Log", "letters=" + letters);
        populateLetters(tableLetters, letters);
        for (int i = 0; i < array.length; i++) {
            list.add(array[i].toUpperCase());
        }
//DATAS END

//POPULATE
        String[] words = list.toArray(new String[list.size()]);
        if (words.length > maxRows) {
            String[] words1 = new String[maxRows];
            String[] words2 = new String[list.size() - maxRows];
            int k = 0, j = 0;
            for (int i = 0; i < list.size(); i++) {
                if (i < maxRows) {
                    words1[j] = list.get(i);
                    j++;
                } else {
                    words2[k] = list.get(i);
                    k++;
                }
            }

            populateTable(table1, words1);
            populateTable(table2, words2);
        } else {
            populateTable(table1, words);
        }
//POPULATE END
        imageFinish = initImage(R.id.imageViewFinish);
        ImageView imageCheck = initImage(R.id.imageViewCheck);
        ImageView imageMix = initImage(R.id.imageViewShuffle);
        ImageView imageRemove = initImage(R.id.imageViewDelete);
        ImageView imageRepeatLetters = initImage(R.id.imageViewRepeatLetters);
        ImageView imageNewLetters = initImage(R.id.imageViewNewLetters);

        scoreView = initText(R.id.textViewPoints);

        TextView bestScoreView = initText(R.id.textViewMaxPoints);
        bestScoreView.setText(loadScore() + "");
        TextView timeView = initText(R.id.textViewTime);
        timer = new Timer(timeView);
        timer.start();
    }

    private boolean isEmpty(ArrayList<String> list) {

        boolean empty = true;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) != "13") {
                empty = false;
                break;
            }
        }
        if (empty)
            Log.d("Log", "empty");
        else
            Log.d("Log", "!empty");

        return empty;
    }

    private void finishGame(boolean win) {

        ImageView imageBird = (ImageView) findViewById(R.id.imageViewBird);
        int imageResId = (win == true) ? R.mipmap.bird_win : R.mipmap.bird_lose;
        int rawId = (win == true) ? R.raw.battle_win : R.raw.battle_lose;
        playSound(rawId);
        imageBird.setImageResource(imageResId);
        imageBird.startAnimation(animFadeInOut);
        tableRowEnabled(rowLetters, false);
        findViewById(R.id.linearLayoutBtns1).setVisibility(View.INVISIBLE);
        findViewById(R.id.linearLayoutBtns2).setVisibility(View.VISIBLE);
        showAllRows(table1);
        showAllRows(table2);
        imageFinish.setVisibility(View.INVISIBLE);
        timer.cancel();
    }

    private ImageView initImage(int ID) {
        ImageView img = (ImageView) findViewById(ID);
        img.setOnClickListener(this);
        return img;
    }

    private TextView initText(int id) {
        TextView textView = (TextView) findViewById(id);
        textView.setTypeface(typeface);
        return textView;
    }

    private void populateTable(TableLayout tableLayout, String[] words) {

        for (int i = 0; i < words.length; i++) {
            TableRow row = new TableRow(Game.this);
            String word = words[i];
            for (int j = 0; j < word.length(); j++) {
                TextView card = new TextView(Game.this);
                card.setText(word.charAt(j) + "");
                card.setBackgroundResource(R.drawable.draw_card_white);
                card.setTextSize(TEXT_SIZE_CARD);
                card.setTypeface(typeface);
                card.setGravity(Gravity.CENTER);
                row.addView(card, lpCard);
            }
            tableLayout.addView(row, lpRow);
        }
    }

    private void populateLetters(TableLayout tableLayout, String letters) {

        rowCorrect = new TableRow(Game.this);
        for (int j = 0; j < letters.length(); j++) {
            Button card = new Button(Game.this);
            card.setBackgroundResource(R.drawable.draw_card_white);
            card.setTextColor(textColorDark);
            card.setTypeface(typeface);
            card.setTextSize(TEXT_SIZE_CARD);
            card.setGravity(Gravity.CENTER);
            rowCorrect.addView(card, lpCard);
        }

        tableLayout.addView(rowCorrect, lpRow);

        rowLetters = new TableRow(Game.this);
        for (int j = 0; j < letters.length(); j++) {
            Card card = new Card(Game.this);
            card.setTag(j);
            card.setText(letters.charAt(j) + "");
            card.setBackgroundResource(R.drawable.draw_card_dark);
            card.setTextColor(Color.WHITE);
            card.setTextSize(TEXT_SIZE_CARD);
            card.setTypeface(typeface);
            card.setGravity(Gravity.CENTER);
            card.setOnClickListener(new onClickCard());
            rowLetters.addView(card, lpCard);
        }
        tableLayout.addView(rowLetters, lpRow);

    }

    private void clearTableRow(TableRow tableRow) {
        for (int i = 0; i < tableRow.getChildCount(); i++) {
            Button card = (Button) tableRow.getChildAt(i);
            card.setText("");
        }
    }

    private void shuffleTableRow(TableRow tableRow) {
        letters = shuffle(letters);
        for (int i = 0; i < tableRow.getChildCount(); i++) {
            Card card = (Card) tableRow.getChildAt(i);
            card.setText(letters.charAt(i) + "");
        }
    }

    private void tableRowEnabled(TableRow tableRow, boolean enable) {
        for (int i = 0; i < tableRow.getChildCount(); i++) {
            Button card = (Button) tableRow.getChildAt(i);
            card.setEnabled(enable);
        }
    }

    private String shuffle(String str) {

        char[] newLetters = str.toCharArray();
        str = "";
        Random rnd = new Random();
        for (int i = newLetters.length - 1; i >= 0; i--) {
            int index = rnd.nextInt(i + 1);
            char a = newLetters[index];
            newLetters[index] = newLetters[i];
            newLetters[i] = a;
            str = str + a;
        }

        return str;
    }

    @Override
    public void onClick(View v) {

        if (mediaClick != null)
            mediaClick.start();
        switch (v.getId()) {
            case R.id.imageViewFinish:
                showAlertDialog();
                break;
            case R.id.imageViewShuffle:
                clearTableRow(rowCorrect);
                tableRowEnabled(rowLetters, true);
                shuffleTableRow(rowLetters);
                break;
            case R.id.imageViewCheck:
                boolean exist = checkTableRow(rowCorrect);
                if (exist) {
                    ImageView imageBird = (ImageView) findViewById(R.id.imageViewBird);
//                    int imageResId = (action.equals(ACTION_LOSE)) ? R.mipmap.bird_lose : R.mipmap.bird_win;
                    imageBird.setImageResource(R.mipmap.bird_good_1);
                    imageBird.startAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha_bird_short));
                    scoreView.setText(score + "");
                    playSound(R.raw.ding);
                } else {
                    error2(rowCorrect);
                }
                clearTableRow(rowCorrect);
                tableRowEnabled(rowLetters, true);

                if (isEmpty(list)) {
                    finishGame(true);
                }
                break;
            case R.id.imageViewDelete:
                removeLetter();
                break;
            case R.id.imageViewNewLetters:
                restartActivity(0);
                break;
            case R.id.imageViewRepeatLetters:
                restartActivity(INTENT_REPEAT);
                break;
        }
    }

    private void removeLetter() {
        for (int i = rowCorrect.getChildCount() - 1; i >= 0; i--)
            if (((Button) rowCorrect.getChildAt(i)).getText() != "") {
                int posRecoveryCard = (int) rowCorrect.getChildAt(i).getTag();
                ((Button) rowCorrect.getChildAt(i)).setText("");
                rowLetters.getChildAt(posRecoveryCard).setEnabled(true);
                Log.d("Log", "k=" + i + ", posRecoveryCard=" + posRecoveryCard);
                break;
            }
    }

    private void showAlertDialog() {

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(R.string.dialog_finish_title);
        adb.setMessage(R.string.dialog_finish_message);
        adb.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mediaClick != null)
                    mediaClick.start();
                finishGame(false);

            }
        });
        adb.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mediaClick != null)
                    mediaClick.start();
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = adb.create();
        alertDialog.show();
    }


    private static long back_pressed;

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            finish();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else
            Toast.makeText(Game.this, R.string.toast_exit, Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }

    private boolean checkTableRow(TableRow tableRow) {
        String word = "";
        for (int i = 0; i < tableRow.getChildCount(); i++) {
            Button card = (Button) tableRow.getChildAt(i);
            String letter = (String) card.getText();
            if (letter.equals("")) break;
            else word += letter;
        }

        if (list.contains(word)) {
            int pos = list.indexOf(word);
            if (pos < maxRows) {
                TableRow tr = (TableRow) table1.getChildAt(pos);// showRow(table1, pos);
                success(tr);
            } else {
                int pos2 = pos % maxRows;
                TableRow tr = (TableRow) table2.getChildAt(pos2);
                success(tr);
            }
            list.set(pos, "13");
            Log.d("Log", "exist " + word + "->" + pos + " list:" + list);
            addScore(word);
            return true;
        }
        return false;
    }

    private void addScore(String word) {
//        switch (word.length()) {
//            case 2:
//            case 3:
//                score += 50;
//                break;
//            case 4:
//                score += 60;
//                break;
//            case 5:
//                score += 80;
//                break;
//            case 6:
//            case 7:
//            case 8:
//                score += 100;
//                break;
//            default:
//                break;
//        }
        int pointsValuePerSecond = ((int) Math.pow(numberOfWords, 1.80) / 10) - (numberOfWords - 10);
        score += pointsValuePerSecond;
        Log.d("Log", "score=" + score);
        numberOfWords++;
    }

    private void success(TableRow tableRow) {
        tableRow.setSelected(true);

        for (int i = 0; i < tableRow.getChildCount(); i++) {
            final TextView textView = (TextView) tableRow.getChildAt(i);
            textView.setTextColor(Game.textColorDark);
            textView.setBackgroundResource(R.drawable.draw_card_success);
//            textView.startAnimation(animFadeInOut);

            new Thread(new Runnable() {


                @Override
                public void run() {

                    try {
                        TimeUnit.MILLISECONDS.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            textView.setBackgroundResource(R.drawable.draw_card_white);
                        }
                    });
                }
            }).start();
        }
    }

    private void error(TableRow tableRow) {
        tableRow.setSelected(false);
        for (int i = 0; i < tableRow.getChildCount(); i++) {
            final TextView textView = (TextView) tableRow.getChildAt(i);
            textView.setTextColor(textColorRed);
            textView.setBackgroundResource(R.drawable.draw_card_error);
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        TimeUnit.MILLISECONDS.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            textView.setBackgroundResource(R.drawable.draw_card_white);
                        }
                    });
                }
            }).start();
        }
    }


    private void error2(TableRow tableRow) {
        for (int i = 0; i < tableRow.getChildCount(); i++) {
            final Button textView = (Button) tableRow.getChildAt(i);
            textView.setBackgroundResource(R.drawable.draw_card_error);
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        TimeUnit.MILLISECONDS.sleep(800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            textView.setBackgroundResource(R.drawable.draw_card_white);
                        }
                    });
                }
            }).start();
        }
    }

    private void showAllRows(TableLayout tableDest) {

        for (int i = 0; i < tableDest.getChildCount(); i++) {
            TableRow tableRow = (TableRow) tableDest.getChildAt(i);
            if (!tableRow.isSelected()) {
                error(tableRow);
            }
        }
    }

    private void dialogFootnote() {

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(R.string.dialog_footnote_title);
        adb.setMessage(R.string.dialog_footnote_message);
        adb.setCancelable(false);
        adb.setPositiveButton(R.string.start, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = adb.create();
        alertDialog.show();
    }

    private class onClickCard implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            if (mediaClick != null)
                mediaClick.start();

            Card v2 = (Card) v;
            v2.setEnabled(false);

            int k = 0;
            while (((Button) rowCorrect.getChildAt(k)).getText() != "") {
                k++;
            }
            Button card = (Button) rowCorrect.getChildAt(k);
            card.setText(v2.getText());
            card.setTag(v2.getTag());
        }
    }

    //
    private void restartActivity(int extra) {
        Intent intent = new Intent();
        intent.setClass(this, this.getClass());
        if (extra != 0)
            intent.putExtra(INTENT, extra);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//        overridePendingTransition(0, 0);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (mediaSound != null) {
            if (mediaSound.isPlaying()) {
                mediaSound.stop();
            }

            mediaSound.release();
            mediaSound = null;
        }

        super.onDestroy();
    }

    private void savePref(int score) {
        sp = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putInt(KEY_BEST_SCORE, score);
        e.commit();
        Log.d("Log", "savePref=" + score);
    }

    @Override
    protected void onPause() {
        Log.d("Log", "onPause");

        if (bestScore < score && !keyRepeat) savePref(score);
        super.onPause();
    }

    private int loadScore() {
        sp = getPreferences(MODE_PRIVATE);
        bestScore = sp.getInt(KEY_BEST_SCORE, bestScore);
        Log.d("Log", "loadScore=" + bestScore);
        return bestScore;
    }


    private class Timer extends CountDownTimer {

        private static final long countDownInterval = 1000;
        private static final long millisInFuture = 120000;
        private TextView textView;

        public Timer(TextView textView) {
            super(millisInFuture, countDownInterval);
            this.textView = textView;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long sec = millisUntilFinished / 1000;
            String str = String.format("%2d : %02d", sec / 60, sec % 60);
            textView.setText(str);
        }

        @Override
        public void onFinish() {
            textView.setText("0 : 00");
            finishGame(false);
        }
    }

    private void playSound(int rawId) {
//        if(mediaSound.isPlaying() && mediaSound!=null) mediaSound.stop();
        mediaSound = MediaPlayer.create(Game.this, rawId);
        mediaSound.start();
    }
}
