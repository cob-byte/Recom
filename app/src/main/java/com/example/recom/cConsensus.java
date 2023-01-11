package com.example.recom;

import java.util.HashMap;
import java.util.Map;

public class cConsensus {
    Boolean anon;
    int upvote, downvote, answer1Count, answer2Count, answer3Count,
            answer4Count;
    String author, name, title, question, answer1, answer2, answer3, answer4, date, time;
    public Map<String, Boolean> upVoters = new HashMap<>();
    public Map<String, Boolean> downVoters = new HashMap<>();
    public Map<String, Boolean> answer1Vote = new HashMap<>();
    public Map<String, Boolean> answer2Vote = new HashMap<>();
    public Map<String, Boolean> answer3Vote = new HashMap<>();
    public Map<String, Boolean> answer4Vote = new HashMap<>();

    public cConsensus() {
    }

    public cConsensus(Boolean anon, int upvote, int downvote, int answer1Count, int answer2Count, int answer3Count, int answer4Count, String author, String name, String title, String question, String answer1, String answer2, String answer3, String answer4, String date, String time) {
        this.anon = anon;
        this.upvote = upvote;
        this.downvote = downvote;
        this.author = author;
        this.name = name;
        this.title = title;
        this.question = question;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.answer4 = answer4;
        this.answer1Count = answer1Count;
        this.answer2Count = answer2Count;
        this.answer3Count = answer3Count;
        this.answer4Count = answer4Count;
        this.date = date;
        this.time = time;
    }

    public Boolean getAnon() {
        return anon;
    }

    public void setAnon(Boolean anon) {
        this.anon = anon;
    }

    public int getUpvote() {
        return upvote;
    }

    public void setUpvote(int upvote) {
        this.upvote = upvote;
    }

    public int getDownvote() {
        return downvote;
    }

    public void setDownvote(int downvote) {
        this.downvote = downvote;
    }

    public int getAnswer1Count() {
        return answer1Count;
    }

    public void setAnswer1Count(int answer1Count) {
        this.answer1Count = answer1Count;
    }

    public int getAnswer2Count() {
        return answer2Count;
    }

    public void setAnswer2Count(int answer2Count) {
        this.answer2Count = answer2Count;
    }

    public int getAnswer3Count() {
        return answer3Count;
    }

    public void setAnswer3Count(int answer3Count) {
        this.answer3Count = answer3Count;
    }

    public int getAnswer4Count() {
        return answer4Count;
    }

    public void setAnswer4Count(int answer4Count) {
        this.answer4Count = answer4Count;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public void setAnswer3(String answer3) {
        this.answer3 = answer3;
    }

    public String getAnswer4() {
        return answer4;
    }

    public void setAnswer4(String answer4) {
        this.answer4 = answer4;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
