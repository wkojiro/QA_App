package jp.techacademy.wakabayashi.kojiro.qa_app;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by wkojiro on 2017/02/27.
 */

public class Favorite implements Serializable{
    private String mFavQuestionUid;
    private String mFavoriteUid;


    public Favorite(String favquestionUid ,String favoriteUid){
        mFavQuestionUid = favquestionUid;
        mFavoriteUid = favoriteUid;
    }

    public  String getfavQuestionUid() {
        return mFavQuestionUid;
    }

    public  String getFavoriteUid() {
        return mFavoriteUid;
    }


}


