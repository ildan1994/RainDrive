package com.example.raindriveiter1_10.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.raindriveiter1_10.R;
import com.example.raindriveiter1_10.model.Question;
import com.example.raindriveiter1_10.utility.QuizDbHelper;

import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_SCORE = "extraScore";
    private TextView tvQuestion;
    private TextView tvScore;
    private TextView tvQuestionCount;
    private Button btn_opt1;
    private Button btn_opt2;
    private Button btn_opt3;
    private Button btnConfirm;
    private ColorStateList textColorDefaultRb;
    private ColorStateList textColorDefaultCd;
    private long backPressedTime;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;

    private List<Question> questionList;
    private int questionCounter;
    private int questionCountTotal;
    private Question currentQuestion;
    private boolean isAnswered;
    private static final long COUNTDOWN_IN_MILLIS = 30000;
    private int score;
    private TextView tv_hint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        score = 0;
        setContentView(R.layout.activity_quiz);
        tvQuestion = findViewById(R.id.tv_question);
        tvScore = findViewById(R.id.tv_score);
        tvQuestionCount = findViewById(R.id.tv_question_count);
        btn_opt1 = findViewById(R.id.btn_opt1);
        btn_opt2 = findViewById(R.id.btn_opt2);
        btn_opt3 = findViewById(R.id.btn_opt3);
        tv_hint = findViewById(R.id.tv_hint);
        btn_opt1.setOnClickListener(this);
        btn_opt2.setOnClickListener(this);
        btn_opt3.setOnClickListener(this);
        textColorDefaultRb = btn_opt1.getTextColors();
        QuizDbHelper dbHelper = new QuizDbHelper(this);
        questionList = dbHelper.getAllQuestions();
        questionCountTotal = questionList.size();
        Collections.shuffle(questionList);
        showNextQuestion();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        int selectedOption = 0;

        switch (v.getId())
        {
            case R.id.btn_opt1 :
                selectedOption = 1;
                break;

            case R.id.btn_opt2:
                selectedOption = 2;
                break;

            case R.id.btn_opt3:
                selectedOption = 3;
                break;


            default:
        }
        checkAnswer(selectedOption, v);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void checkAnswer(int selectedOption, View view)
    {

        if(selectedOption == currentQuestion.getAnswerNr())
        {
            //Right Answer
            ((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
            score++;

        }
        else
        {
            //Wrong Answer
            ((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.RED));

            switch (currentQuestion.getAnswerNr())
            {
                case 1:
                    btn_opt1.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case 2:
                    btn_opt2.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case 3:
                    btn_opt3.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;

            }

        }
        tv_hint.setText(currentQuestion.getHint());

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showNextQuestion();
            }
        }, 2000);




    }
    private void playAnim(final View view, final int value, final int viewNum)
    {

        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500)
                .setStartDelay(100).setInterpolator(new DecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if(value == 0)
                        {
                            switch (viewNum)
                            {
                                case 0:
                                    ((TextView)view).setText(currentQuestion.getQuestion());
                                    break;
                                case 1:
                                    ((Button)view).setText(currentQuestion.getOption1());
                                    break;
                                case 2:
                                    ((Button)view).setText(currentQuestion.getOption2());
                                    break;
                                case 3:
                                    ((Button)view).setText(currentQuestion.getOption3());
                                    break;

                            }


                            if(viewNum != 0)
                                ((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E99C03")));


                            playAnim(view,1,viewNum);

                        }

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

    }

    private void showNextQuestion() {
        //rb1.setTextColor(textColorDefaultRb);
        //rb2.setTextColor(textColorDefaultRb);
        //rb3.setTextColor(textColorDefaultRb);
        //rbGroup.clearCheck();

        if (questionCounter < questionCountTotal) {
            currentQuestion = questionList.get(questionCounter);
            tv_hint.setText("");

//            tvQuestion.setText(currentQuestion.getQuestion());
//            rb1.setText(currentQuestion.getOption1());
//            rb2.setText(currentQuestion.getOption2());
//            rb3.setText(currentQuestion.getOption3());
            playAnim(tvQuestion,0,0);
            playAnim(btn_opt1,0,1);
            playAnim(btn_opt2,0,2);
            playAnim(btn_opt3,0,3);
            questionCounter++;
            tvQuestionCount.setText("Question: " + questionCounter + "/" + questionCountTotal);
            tvScore.setText("Score: " + score);
            isAnswered = false;
            //btnConfirm.setText("Confirm");

            timeLeftInMillis = COUNTDOWN_IN_MILLIS;
            //startCountDown();
        } else {

            finishQuiz();

        }
    }

//    private void checkAnswer(int se) {
//        isAnswered = true;
//
//        //countDownTimer.cancel();
//
//        RadioButton rbSelected = findViewById(rbGroup.getCheckedRadioButtonId());
//        int answerNr = rbGroup.indexOfChild(rbSelected) + 1;
//
//        if (answerNr == currentQuestion.getAnswerNr()) {
//            score++;
//            tvScore.setText("Score: " + score);
//        }
//        showSolution();
//
//    }

//    private void showSolution() {
//        rb1.setTextColor(Color.RED);
//        rb2.setTextColor(Color.RED);
//        rb3.setTextColor(Color.RED);
//
//        switch (currentQuestion.getAnswerNr()) {
//            case 1:
//                rb1.setTextColor(Color.GREEN);
//                tvQuestion.setText("Answer 1 is correct");
//                break;
//            case 2:
//                rb2.setTextColor(Color.GREEN);
//                tvQuestion.setText("Answer 2 is correct");
//                break;
//            case 3:
//                rb3.setTextColor(Color.GREEN);
//                tvQuestion.setText("Answer 3 is correct");
//                break;
//        }
//
//        if (questionCounter < questionCountTotal) {
//            btnConfirm.setText("Next");
//        } else {
//            btnConfirm.setText("Finish");
//        }
//    }

    private void finishQuiz() {
        level();
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_SCORE, score);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void level() {
        if (score >= 3) {
            Toast.makeText(this, "good!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "you'd better improve your knowledge otherwise it would be risky driveing in rainfall", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            finish();
        } else {
            Toast.makeText(this, "Press back again to finish", Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
