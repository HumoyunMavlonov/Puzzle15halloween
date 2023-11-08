package uz.gita.puzzle15halloween;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.saadahmedsoft.popupdialog.PopupDialog;
import com.saadahmedsoft.popupdialog.Styles;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;

import java.util.ArrayList;
import java.util.Collections;

import uz.gita.puzzle15halloween.Preference.Pref;

public class PlayActivity extends AppCompatActivity {

    private RelativeLayout relativeLayoutPlay;
    private AppCompatButton[][] views = new AppCompatButton[4][4];
    private ArrayList<Integer> numbers = new ArrayList<>();
    private ArrayList<Integer> getNumbers;
    private Chronometer chronometer;
    private boolean isChronometerRunning;
    private AppCompatTextView tvCount;
    private Boolean checkTwo = true;
    private AppCompatImageView btnExit;
    private AppCompatImageView btnRestart;
    AppCompatButton pause;
    private Boolean check = true;
    private Boolean checkPause = true;
    private MediaPlayer mediaPlayer;
    private AppCompatImageView btnVolume;
    private SharedPreferences myPref;
    private int count;
    private int indexI;
    private int indexJ;
    private long pauseOffset;
    private long timeOffset;
    private boolean isSoundOn = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        myPref = Pref.getShared();
        mediaPlayer = MediaPlayer.create(this, R.raw.click);
        setAllViewsId();
        setAllBtnId();
        setNumbers();
        shuffle(numbers);
        onClick();
        isWin();
    }

    private void setAllViewsId() {
        chronometer = findViewById(R.id.chronometer);
        tvCount = findViewById(R.id.tv_count);
        btnExit = findViewById(R.id.btn_exit);
        btnRestart = findViewById(R.id.restart);
        btnVolume = findViewById(R.id.btn_volume);
        pause = findViewById(R.id.pause);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (myPref.getInt("indexJ", 0) == 0 || !myPref.getBoolean("shuffle", true)) {
            tvCount.setText(String.valueOf(count));
            setChronometerTime();
            views[indexI][indexJ].setVisibility(View.VISIBLE);
            setValue(numbers);
        } else {
            count = myPref.getInt("count", 0);
            tvCount.setText(String.valueOf(count));
            String[] numbersS = myPref.getString("numbers", "").split("/");
            long time = myPref.getLong("time", 0);
            timeOffset = myPref.getLong("timeOffset", 0);
            chronometer.setBase(SystemClock.elapsedRealtime() - time + timeOffset);
            isChronometerRunning = myPref.getBoolean("isChronometerRunning", false);
            if (isChronometerRunning) {
                chronometer.start();
            } else {
                pauseOffset = myPref.getLong("pauseOffset", 0);
                chronometer.setBase(SystemClock.elapsedRealtime() - time + pauseOffset);
            }
            isSoundOn = myPref.getBoolean("isSoundOn", true);
            updateSoundButtonState();
            getNumbers = new ArrayList<>();
            for (int i = 0; i < numbersS.length; i++) {
                if (!numbersS[i].equals("")) {
                    getNumbers.add(Integer.parseInt(numbersS[i]));
                } else {
                    getNumbers.add(0);
                }
            }
            numbers = getNumbers;
            setValue(getNumbers);
            if (myPref.getBoolean("checkPause", false)) {
                showDialogPause();
            }
        }
    }

    private void setAllBtnId() {
        relativeLayoutPlay = findViewById(R.id.relativePlay);
        for (int i = 0; i < relativeLayoutPlay.getChildCount(); i++) {
            views[i / 4][i % 4] = (AppCompatButton) relativeLayoutPlay.getChildAt(i);
            saveDataId(views[i / 4][i % 4], i / 4, i % 4, i);
            views[i / 4][i % 4].setOnClickListener(v -> {
                DataId dataId = (DataId) v.getTag();
                changeStep(dataId.i, dataId.j);
                saveChanges();
            });
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putIntegerArrayList("list", numbers);
        outState.putInt("count", count);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        getNumbers = new ArrayList<>(savedInstanceState.getIntegerArrayList("list"));
        count = savedInstanceState.getInt("count");
    }

    private void setNumbers() {
        for (int i = 0; i < 16; i++) {
            numbers.add(i);
        }
    }

    private void shuffle(ArrayList<Integer> numbers) {
        Collections.shuffle(numbers);
    }

    private boolean isSolvable(ArrayList<Integer> list) {
        int countInversions = 0;
        for (int i = 0; i < 15; i++) {
            for (int j = i + 1; j < 15; j++) {
                if (list.get(i) > list.get(j) && j > i) {
                    countInversions++;
                }
            }
        }
        return countInversions % 2 == 0;
    }

    private void setValue(ArrayList<Integer> numbers) {
        if (!isSolvable(numbers) && myPref.getBoolean("shuffle", true)) {
            shuffle(numbers);
            setValue(numbers);
        }
        for (int i = 0; i < numbers.size(); i++) {
            if (numbers.get(i) == 0) {
                views[i / 4][i % 4].setVisibility(View.INVISIBLE);
                indexI = i / 4;
                indexJ = i % 4;
            } else {
                views[i / 4][i % 4].setText(String.valueOf(numbers.get(i)));
            }
        }
    }

    private void saveChanges() {
        myPref.edit().putBoolean("shuffle", false).apply();
        myPref.edit().putBoolean("isSoundOn", isSoundOn).apply(); // Сохраняем состояние звука
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            if (!views[i / 4][i % 4].getText().toString().equals("")) {
                stringBuilder.append(views[i / 4][i % 4].getText().toString()).append("/");
            } else {
                stringBuilder.append("0");
            }
        }
        myPref.edit().putInt("count", count).apply();
        long time = chronometer.getBase() - SystemClock.elapsedRealtime();
        myPref.edit().putLong("time", time).apply();
        myPref.edit().putLong("pauseOffset", pauseOffset).apply();
        myPref.edit().putLong("timeOffset", timeOffset).apply();
        myPref.edit().putBoolean("isChronometerRunning", isChronometerRunning).apply();
        myPref.edit().putString("numbers", stringBuilder.toString()).apply();
    }

    private void saveDataId(AppCompatButton appCompatButton, int i, int j, int a) {
        appCompatButton.setTag(new DataId(i, j, a));
    }

    private void changeStep(int i, int j) {
        if (canAccess(i, j) && checkPause) {
            views[indexI][indexJ].setText(views[i][j].getText());
            views[i][j].setText("0");
            views[i][j].setVisibility(View.INVISIBLE);
            views[indexI][indexJ].setVisibility(View.VISIBLE);
            indexI = i;
            indexJ = j;
            if (checkTwo) {
                mediaPlayer.start();
            }
            count++;
            tvCount.setText(String.valueOf(count));
            loadNumbersChange();
            isWin();
        }
    }

    private void loadNumbersChange() {
        if (views != null) {
            ArrayList<Integer> list = new ArrayList<>();
            for (int i = 0; i < 16; i++) {
                int a = Integer.parseInt(views[i / 4][i % 4].getText().toString());
                list.add(a);
            }
            numbers = list;
        }
    }

    private boolean canAccess(int i, int j) {
        return (((Math.abs(indexI - i) == 1) && indexJ == j) || ((Math.abs(indexJ - j) == 1) && indexI == i));
    }

    private void setChronometerTime() {
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
    }

    private void onClick() {
        btnExit.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
        btnRestart.setOnClickListener(v -> {
            shuffle(numbers);
            views[indexI][indexJ].setVisibility(View.VISIBLE);
            count = 0;
            tvCount.setText(String.valueOf(0));
            chronometer.setBase(SystemClock.elapsedRealtime());
            setValue(numbers);
        });

        btnVolume.setOnClickListener(v -> {
            if (check) {
                btnVolume.setImageResource(R.drawable.mute);
                checkTwo = false;
                check = false;
                myPref.edit().putBoolean("firstCheck", false).apply();
            } else {
                btnVolume.setImageResource(R.drawable.volume);
                checkTwo = true;
                check = true;
                myPref.edit().putBoolean("firstCheck", true).apply();
            }
        });
        pause.setOnClickListener(v -> {
            showDialogPause();
        });
    }

    private void updateSoundButtonState() {
        if (isSoundOn) {
            btnVolume.setImageResource(R.drawable.volume);
            checkTwo = true;
            check = true;
        } else {
            btnVolume.setImageResource(R.drawable.mute);
            checkTwo = false;
            check = false;
        }
    }

    private void showDialogPause() {
        if (!isChronometerRunning) {
            pauseOffset = myPref.getLong("pauseOffset", 0);
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
        }
        chronometer.stop();
        myPref.edit().putBoolean("checkPause", true).apply();
        PopupDialog.getInstance(this)
                .setStyle(Styles.ALERT)
                .setHeading("Pause")
                .setDescription("PAUSED.\nPress button to\nContinue")
                .setCancelable(false)
                .showDialog(new OnDialogButtonClickListener() {
                    @Override
                    public void onDismissClicked(Dialog dialog) {
                        if (isChronometerRunning) {
                            timeOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
                            chronometer.setBase(SystemClock.elapsedRealtime() - timeOffset);
                        }
                        chronometer.start();
                        dialog.dismiss();
                        myPref.edit().putBoolean("checkPause", false).apply();
                    }
                });
    }

    private void isWin() {
        int check = 0;
        for (int i = 0; i < 15; i++) {
            if (views[i / 4][i % 4].getText().equals(String.valueOf(i + 1))) {
                check++;
            }
        }
        if (check == 15) {
            chronometer.stop();
            View view = LayoutInflater.from(this).inflate(R.layout.activity_win, null, false);
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setView(view)
                    .create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            AppCompatButton button = view.findViewById(R.id.retry);
            button.setOnClickListener(v -> {
                shuffle(numbers);
                views[indexI][indexJ].setVisibility(View.VISIBLE);
                count = 0;
                tvCount.setText(String.valueOf(0));
                chronometer.setBase(SystemClock.elapsedRealtime());
                setValue(numbers);
                dialog.dismiss();
            });
            dialog.show();
        }
    }
}

