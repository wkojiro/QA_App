package jp.techacademy.wakabayashi.kojiro.qa_app;

//Firebaseから取ってきた質問（Question)を受けるためのモデル

import java.io.Serializable;
import java.util.ArrayList;

public class Question implements Serializable {
    private String mTitle;
    private String mBody;
    private String mName;
    private String mUid;
    private String mQuestionUid;
    private int mGenre;
    private byte[] mBitmapArray;
    private ArrayList<Answer> mAnswerArrayList;
    private ArrayList<Okini> mOkiniArrayList;

    public String getTitle() {
        return mTitle;
    }

    public String getBody() {
        return mBody;
    }

    public String getName() {
        return mName;
    }

    public String getUid() {
        return mUid;
    }

    public String getQuestionUid() {
        return mQuestionUid;
    }
    public int getGenre() {
        return mGenre;
    }

    public byte[] getImageBytes() {
        return mBitmapArray;
    }

    public ArrayList<Answer> getAnswers() {
        return mAnswerArrayList;
    }
    public ArrayList<Okini> getOkinis(){
        return  mOkiniArrayList;
    }

    public Question(String title, String body, String name, String uid, String questionUid, int genre, byte[] bytes, ArrayList<Answer> answers, ArrayList<Okini> okinis ) {
        mTitle = title;
        mBody = body;
        mName = name;
        mUid = uid;
        mQuestionUid = questionUid;
        mGenre = genre;
        mBitmapArray = bytes.clone();
        mAnswerArrayList = answers;
        mOkiniArrayList = okinis;

    }
}
