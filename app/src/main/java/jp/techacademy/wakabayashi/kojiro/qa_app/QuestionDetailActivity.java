package jp.techacademy.wakabayashi.kojiro.qa_app;


// Questionの詳細画面の側（中身は、questionDetailListAdapterとなる。）→これがややこい原因。

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class QuestionDetailActivity extends AppCompatActivity {

    private ListView mListView;
    private Question mQuestion;
    private Favorite mFavorite;

    private boolean mFavoriteFlag = false;
    FirebaseAuth mAuth;

    private QuestionDetailListAdapter mAdapter;

    private DatabaseReference mAnswerRef;


    private ChildEventListener mEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            Log.d("答えが追加","されました");
            HashMap map = (HashMap) dataSnapshot.getValue();

            String answerUid = dataSnapshot.getKey();

            for(Answer answer : mQuestion.getAnswers()) {
                // 同じAnswerUidのものが存在しているときは何もしない
                if (answerUid.equals(answer.getAnswerUid())) {
                    return;
                }
            }

            String body = (String) map.get("body");
            String name = (String) map.get("name");
            String uid = (String) map.get("uid");

            Answer answer = new Answer(body, name, uid, answerUid);
            mQuestion.getAnswers().add(answer);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

/*
    public View.OnClickListener mFavButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("クリック","されました");
        }
    };

*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);



        // 渡ってきたQuestionのオブジェクトを保持する
        Bundle extras = getIntent().getExtras();
        mQuestion = (Question) extras.get("question");

        setTitle(mQuestion.getTitle()); //質問のタイトルを画面のタイトルにセットする

        // ListViewの準備
        mListView = (ListView) findViewById(R.id.listView);
        mAdapter = new QuestionDetailListAdapter(this, mQuestion);
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();





        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ログイン済みのユーザーを収録する
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user == null) {
                    // ログインしていなければログイン画面に遷移させる
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    // Questionを渡して回答作成画面を起動する
                    // --- ここから ---
                    Intent intent = new Intent(getApplicationContext(), AnswerSendActivity.class);
                    intent.putExtra("question", mQuestion);
                    startActivity(intent);
                    // --- ここまで ---
                }
            }
        });


        final FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ログイン済みのユーザーを収録する
                Log.d("クリック","クリック");
                mFavoriteFlag = !mFavoriteFlag;
                if (mFavoriteFlag == true){
                    fab2.setImageResource(R.drawable.ic_action_heart);
                    Log.d("favorite", String.valueOf(mFavoriteFlag));
                    addFavorite();
                    View v = findViewById(android.R.id.content);
                    Snackbar.make(v,"「お気に入り」へ登録しました",Snackbar.LENGTH_LONG).show();
                } else {
                    fab2.setImageResource(R.drawable.ic_action_whiteheart);
                    Log.d("favorite", String.valueOf(mFavoriteFlag));
                    removeFavorite();
                    View v = findViewById(android.R.id.content);
                    Snackbar.make(v,"「お気に入り」から削除しました",Snackbar.LENGTH_LONG).show();
                }
            }

        });


        //この三行は何のために必要なのだろうか？？アンサーに変更があった場合のリスナー
        DatabaseReference dataBaseReference = FirebaseDatabase.getInstance().getReference();
        mAnswerRef = dataBaseReference.child(Const.ContentsPATH).child(String.valueOf(mQuestion.getGenre())).child(mQuestion.getQuestionUid()).child(Const.AnswersPATH);
        mAnswerRef.addChildEventListener(mEventListener);

    }

    public void addFavorite(){
        mAuth = FirebaseAuth.getInstance();
        //Log.d("", this.mQuestion);
        FirebaseUser user = mAuth.getCurrentUser();

        Log.d("User", String.valueOf(user));

        DatabaseReference dataBaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference favoriteRef = dataBaseReference.child(Const.UsersPATH).child(user.getUid()).child(Const.FavoritesPATH);

        Log.d("FavoriteRef", String.valueOf(favoriteRef));
        Map<String, String> data = new HashMap<String, String>();

        // questionID
        data.put("questionid", String.valueOf(mQuestion.getQuestionUid()));

        favoriteRef.push().setValue(data);
    }

    public void removeFavorite(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // ログインしていなければログイン画面に遷移させる
             Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
             startActivity(intent);
        } else {
            // ログインユーザーのFavoritePATHへ移動
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            DatabaseReference favoriteRef = databaseReference.child(Const.UsersPATH).child(user.getUid()).child(Const.FavoritesPATH).child(mFavorite.getFavoriteUid());

            favoriteRef.removeValue();
        }
    }


}
