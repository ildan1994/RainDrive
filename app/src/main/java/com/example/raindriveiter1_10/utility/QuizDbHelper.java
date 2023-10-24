package com.example.raindriveiter1_10.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.raindriveiter1_10.model.Question;
import com.example.raindriveiter1_10.utility.QuizContract.*;

import java.util.ArrayList;
import java.util.List;

public class QuizDbHelper  extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MyAwesomeQuiz.db";
    private static final int DATABASE_VERSION = 2;

    private SQLiteDatabase db;

    public QuizDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db =db;

        final String SQL_CREATE_QUESTIONS_TABLE = "CREATE TABLE " +
                QuestionsTable.TABLE_NAME + " ( " +
                QuestionsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuestionsTable.COLUMN_QUESTION + " TEXT, " +
                QuestionsTable.COLUMN_OPTION1 + " TEXT, " +
                QuestionsTable.COLUMN_OPTION2 + " TEXT, " +
                QuestionsTable.COLUMN_OPTION3 + " TEXT, " +
                QuestionsTable.COLUMN_HINT + " TEXT, " +
                QuestionsTable.COLUMN_ANSWER_NR + " INTEGER" +
                ")";

        db.execSQL(SQL_CREATE_QUESTIONS_TABLE);
        fillQuestionsTable();


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + QuestionsTable.TABLE_NAME);
        onCreate(db);
    }

    private void fillQuestionsTable() {
        Question q1 = new Question("You are under 21 and you need a minimum of __ hours driving experience", "80", "100", "120","you need a least 120 hours" ,3);
        addQuestion(q1);
        Question q2 = new Question("When you are driving during rainfall, you should brake __", "in advance and with more force", "just in time, with less force", "in advance and with less force","to avoid slamming on your brakes and slippery road, you need to brake in advance and with less force",3);

        //Question q2 = new Question("You can drive with a blood alcohol concentration (BAC) of _", "0.02", "0.01", "0",3);
        addQuestion(q2);
        Question q3 = new Question("It's good idea to use mobile phones and cruise control for navigation purposes", "No","Yes", "no for using mobile phone"," your car can actually accelerate when set to cruise control in wet condition." , 2);
        addQuestion(q3);
        Question q4 = new Question("When encounter large puddles and you must drive through it, you should __", "drive through as fast as possible", "just stop and wait in the road", "drive through slowly", "it'easy to lose control when hydroplaning", 3);
        addQuestion(q4);
        Question q5 = new Question("when you driving during the rainfall, you should __", "not using any headlights", "Avoid using high-beam lights ", "use light as often as possible so that others can notice me ","high-beam lights will reflect back at you off the water and it will distract you", 2);
        addQuestion(q5);
    }

    private void addQuestion(Question question) {
        ContentValues cv = new ContentValues();
        cv.put(QuestionsTable.COLUMN_QUESTION, question.getQuestion());
        cv.put(QuestionsTable.COLUMN_OPTION1, question.getOption1());
        cv.put(QuestionsTable.COLUMN_OPTION2, question.getOption2());
        cv.put(QuestionsTable.COLUMN_OPTION3, question.getOption3());
        cv.put(QuestionsTable.COLUMN_HINT, question.getHint());
        cv.put(QuestionsTable.COLUMN_ANSWER_NR, question.getAnswerNr());
        db.insert(QuestionsTable.TABLE_NAME, null, cv);
    }

    public List<Question> getAllQuestions() {
        List<Question> questionList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + QuestionsTable.TABLE_NAME, null);

        if (c.moveToFirst()) {
            do {
                Question question = new Question();
                question.setQuestion(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_QUESTION)));
                question.setOption1(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION1)));
                question.setOption2(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION2)));
                question.setOption3(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION3)));
                question.setHint(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_HINT)));
                question.setAnswerNr(c.getInt(c.getColumnIndex(QuestionsTable.COLUMN_ANSWER_NR)));
                questionList.add(question);
            } while (c.moveToNext());
        }

        c.close();
        return questionList;
    }
}
