package jp.techacademy.wakabayashi.kojiro.qa_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private int mGenre = 0;
    private DatabaseReference mQestion;
    private String genreid;

    private FirebaseAuth.AuthStateListener mAuthListener;

    // --- ここから ---
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mFavoriteRef;
    private DatabaseReference mGenreRef;
    private ListView mListView;
    private ArrayList<Question> mQuestionArrayList;
    private QuestionsListAdapter mAdapter;

   // private ValueEventListener mQueryListener;


    private ValueEventListener mQueryListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            String gid = dataSnapshot.getRef().getKey();
            int gii = Integer.parseInt(gid);

            Log.d("マップmap",String.valueOf(gid));
            HashMap map = (HashMap) dataSnapshot.getValue();
           // Log.d("マップmap",String.valueOf(map));

           // dataSnapshot.getRef().getParent();

            if(map!=null)
            {
                Iterator iter = map.keySet().iterator();

                while(iter.hasNext()==true)
                {
                    HashMap map_child = (HashMap)map.get(iter.next());
                   // Log.d("マップmap",String.valueOf(map_child));
                   // String genre = (String) map_child.get()
                    String title = (String) map_child.get("title");
                    String body = (String) map_child.get("body");
                    String name = (String) map_child.get("name");
                    String uid = (String) map_child.get("uid");
                    String imageString = (String) map_child.get("image");
                    Bitmap image = null;
                    byte[] bytes;
                    if (imageString != null) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        bytes = Base64.decode(imageString, Base64.DEFAULT);
                    } else {
                        bytes = new byte[0];
                    }

                    ArrayList<Answer> answerArrayList = new ArrayList<Answer>();
                    HashMap answerMap = (HashMap) map_child.get("answers");
                    if (answerMap != null) {
                        for (Object key : answerMap.keySet()) {
                            HashMap temp = (HashMap) answerMap.get((String) key);
                            String answerBody = (String) temp.get("body");
                            String answerName = (String) temp.get("name");
                            String answerUid = (String) temp.get("uid");
                            Answer answer = new Answer(answerBody, answerName, answerUid, (String) key);
                            answerArrayList.add(answer);
                        }
                    }

                    Question question = new Question(title, body, name, uid, dataSnapshot.getKey(), gii, bytes, answerArrayList);
                    mQuestionArrayList.add(question);
                    mAdapter.notifyDataSetChanged();
                }
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }

    };


    private ChildEventListener mFavoriteEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            HashMap map = (HashMap) dataSnapshot.getValue();

           // String questionUid = dataSnapshot.getKey
            String questionUid = (String) map.get("questionid");
            //Log.d("マップmap",String.valueOf(questionUid));

            View v = findViewById(android.R.id.content);
            Snackbar.make(v,"お気に入りの一覧です。",Snackbar.LENGTH_LONG).show();

            /*
            仮説１
            questionUidを元にFirebaseから直接質問を取得して、Arrayにぶち込む
            genreの情報がない状態で、どのようにデータにたどりつけるか。
            mDatabaseReference = FirebaseDatabase.getInstance().getReference();
            */

            mDatabaseReference.child("contents").child("1").orderByKey().equalTo(questionUid).addListenerForSingleValueEvent(mQueryListener);
            mDatabaseReference.child("contents").child("2").orderByKey().equalTo(questionUid).addListenerForSingleValueEvent(mQueryListener);
            mDatabaseReference.child("contents").child("3").orderByKey().equalTo(questionUid).addListenerForSingleValueEvent(mQueryListener);
            mDatabaseReference.child("contents").child("4").orderByKey().equalTo(questionUid).addListenerForSingleValueEvent(mQueryListener);

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


    private ChildEventListener mEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            HashMap map = (HashMap) dataSnapshot.getValue();
           // Log.d("マップmap",String.valueOf(map));
            String title = (String) map.get("title");
            String body = (String) map.get("body");
            String name = (String) map.get("name");
            String uid = (String) map.get("uid");
            String imageString = (String) map.get("image");
            Bitmap image = null;
            byte[] bytes;
            if (imageString != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                bytes = Base64.decode(imageString, Base64.DEFAULT);
            } else {
                bytes = new byte[0];
            }

            ArrayList<Answer> answerArrayList = new ArrayList<Answer>();
            HashMap answerMap = (HashMap) map.get("answers");
            if (answerMap != null) {
                for (Object key : answerMap.keySet()) {
                    HashMap temp = (HashMap) answerMap.get((String) key);
                    String answerBody = (String) temp.get("body");
                    String answerName = (String) temp.get("name");
                    String answerUid = (String) temp.get("uid");
                    Answer answer = new Answer(answerBody, answerName, answerUid, (String) key);
                    answerArrayList.add(answer);
                }
            }

            Question question = new Question(title, body, name, uid, dataSnapshot.getKey(), mGenre, bytes, answerArrayList);
            mQuestionArrayList.add(question);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            HashMap map = (HashMap) dataSnapshot.getValue();

            // 変更があったQuestionを探す
            for (Question question: mQuestionArrayList) {
                if (dataSnapshot.getKey().equals(question.getQuestionUid())) {
                    // このアプリで変更がある可能性があるのは回答(Answer)のみ
                    question.getAnswers().clear();
                    HashMap answerMap = (HashMap) map.get("answers");
                    if (answerMap != null) {
                        for (Object key : answerMap.keySet()) {
                            HashMap temp = (HashMap) answerMap.get((String) key);
                            String answerBody = (String) temp.get("body");
                            String answerName = (String) temp.get("name");
                            String answerUid = (String) temp.get("uid");
                            Answer answer = new Answer(answerBody, answerName, answerUid, (String) key);
                            question.getAnswers().add(answer);
                        }
                    }

                    mAdapter.notifyDataSetChanged();
                }
            }
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
    // --- ここまで追加する ---

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);




        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("ログイン", "しました");

                    // DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    // mFavoriteRef = databaseReference.child(Const.UsersPATH).child(user.getUid()).child(Const.FavoritesPATH);
                    //  mFavoriteRef.addChildEventListener(mFavoriteEventListener);


                } else {
                    // User is signed out
                    Log.d("ログアウト", "しました");
                    // DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    // mFavoriteRef = databaseReference.child(Const.UsersPATH).child(user.getUid()).child(Const.FavoritesPATH);
                    // mFavoriteRef.removeEventListener(mFavoriteEventListener);

                }
                // ...
            }
        };
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ジャンルを選択していない場合（mGenre == 0）はエラーを表示するだけ
                if (mGenre == 0) {
                    Snackbar.make(view, "ジャンルを選択して下さい", Snackbar.LENGTH_LONG).show();
                    return;
                }

                // ログイン済みのユーザーを収録する
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user == null) {
                    // ログインしていなければログイン画面に遷移させる
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    // ジャンルを渡して質問作成画面を起動する
                    Intent intent = new Intent(getApplicationContext(), QuestionSendActivity.class);
                    intent.putExtra("genre", mGenre);
                    startActivity(intent);
                }

            }
        });




        // ナビゲーションドロワーの設定
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar, R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_hobby) {
                    mToolbar.setTitle("趣味");
                    mGenre = 1;
                } else if (id == R.id.nav_life) {
                    mToolbar.setTitle("生活");
                    mGenre = 2;
                } else if (id == R.id.nav_health) {
                    mToolbar.setTitle("健康");
                    mGenre = 3;
                } else if (id == R.id.nav_compter) {
                    mToolbar.setTitle("コンピューター");
                    mGenre = 4;
                } else if (id == R.id.nav_favorite) {
                    mToolbar.setTitle("お気に入り");
                    mGenre = 99;

                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);

                // 質問のリストをクリアしてから再度Adapterにセットし、AdapterをListViewにセットし直す
                mQuestionArrayList.clear();
                mAdapter.setQuestionArrayList(mQuestionArrayList);
                mListView.setAdapter(mAdapter);

                // 選択したジャンルにリスナーを登録する
                if (mGenreRef != null) {
                    mGenreRef.removeEventListener(mEventListener);
                }

                if(mGenre == 99){
                    // ログイン済みのユーザーを収録する
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if (user == null) {
                        // ログインしていなければログイン画面に遷移させる
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);

                    } else {
                        //お気に入りに入っているQuesitionidを取得して、そこから新たなmQuestionArrayListを作る。

                        mFavoriteRef = mDatabaseReference.child(Const.UsersPATH).child(user.getUid()).child(Const.FavoritesPATH);
                      //  mGenreRef = mDatabaseReference.child(Const.ContentsPATH).child(String.valueOf(mGenre));
                       // mGenreRef = mDatabaseReference.child(Const.ContentsPATH);
                        Log.d("mFavoriteRef",String.valueOf(mFavoriteRef));

                        mFavoriteRef.addChildEventListener(mFavoriteEventListener);
                    }

                } else {
                    mGenreRef = mDatabaseReference.child(Const.ContentsPATH).child(String.valueOf(mGenre));
                    Log.d("mGenreRef",String.valueOf(mGenreRef));
                    mGenreRef.addChildEventListener(mEventListener);
                }
                return true;

            }
        });

        // Firebase
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(); //Rootのデータベース情報をとりあえず確保。
        Log.d("mDatabaseReference",String.valueOf(mDatabaseReference));

        // ListViewの準備
        mListView = (ListView) findViewById(R.id.listView);
        mAdapter = new QuestionsListAdapter(this);
        mQuestionArrayList = new ArrayList<Question>();
        mAdapter.notifyDataSetChanged();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Questionのインスタンスを渡して質問詳細画面を起動する
                Intent intent = new Intent(getApplicationContext(), QuestionDetailActivity.class);
                intent.putExtra("question", mQuestionArrayList.get(position));
                startActivity(intent);
                Log.d("doko","これ");
            }
        });
    }


    // menu表示の話
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user == null) {
                // ログインしていなければログイン画面に遷移させる
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);

            } else {

                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
                //return true; これがあるとfinish();が効かなかった。
            }
        }

        return super.onOptionsItemSelected(item);
    }
}