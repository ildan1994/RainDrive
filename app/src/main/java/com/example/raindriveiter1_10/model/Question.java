package com.example.raindriveiter1_10.model;

public class Question {
    private String question;
    private String option1;
    private String option2;
    private String option3;
    private String hint;
    private int answerNr;

    public Question() {

    }

    public Question(String question, String option1, String option2, String option3, String hint, int answerNr) {
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.hint = hint;
        this.answerNr = answerNr;
    }

    public String getQuestion() {
        return question;
    }

    public String getOption1() {
        return option1;
    }

    public String getOption2() {
        return option2;
    }

    public String getOption3() {
        return option3;
    }

    public int getAnswerNr() {
        return answerNr;
    }

    public String getHint() {
        return hint;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public void setHint(String hint) { this.hint = hint; }

    public void setAnswerNr(int answerNr) { this.answerNr = answerNr; }


}
