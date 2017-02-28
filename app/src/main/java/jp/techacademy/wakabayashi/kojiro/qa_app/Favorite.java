package jp.techacademy.wakabayashi.kojiro.qa_app;

import java.io.Serializable;

/**
 * Created by wkojiro on 2017/02/27.
 */

public class Favorite implements Serializable{
    private String mQuestionid;
    private String mFavoriteUid;


    public Favorite(String questionid ,String favoriteUid){
        mQuestionid = questionid;
        mFavoriteUid = favoriteUid;

    }

    public  String getQuestionid() {
        return mQuestionid;
    }

    public  String getFavoriteUid() {
        return mFavoriteUid;
    }

}


