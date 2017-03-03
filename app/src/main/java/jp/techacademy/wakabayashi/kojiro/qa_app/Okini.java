package jp.techacademy.wakabayashi.kojiro.qa_app;

import java.io.Serializable;

/**
 * Created by wkojiro on 2017/03/03.
 */


public class Okini implements Serializable {
    private String mOUid;
    private String mOkiniUid;


    public Okini(String ouid, String okiniUid) {
        mOUid = ouid;
        mOkiniUid = okiniUid;
    }


    public String getOUid() {
        return mOUid;
    }

    public String getOkiniUid() {
        return mOkiniUid;
    }
}