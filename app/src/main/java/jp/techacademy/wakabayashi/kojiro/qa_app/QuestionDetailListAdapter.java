package jp.techacademy.wakabayashi.kojiro.qa_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;

public class QuestionDetailListAdapter extends BaseAdapter {
    private final static int TYPE_QUESTION = 0;
    private final static int TYPE_ANSWER = 1;

    private LayoutInflater mLayoutInflater = null;


    private ToggleButton togglebutton;

    /*
    LayoutInflaterは、指定したxmlのレイアウト(View)リソースを利用できる仕組み

    ちなみに
    inflateの一般的な意味は、膨らませる、ふくらませるのような意味
    第一引数は単純にContext情報を渡しています。
　※Contextとは…
　Contextとは、簡単に言うと、アプリケーションの親情報です。ActivityはContextの子クラスとなります。
　APIの中にはちょいちょいこのContextを必要とするものが存在します。
　ActivityもContextなんだったら、thisで良いじゃん！！とか思うかもしれませんが、
　Activityはメモリの逼迫等に引きずられて、簡単に死にます(インスタンスがnull)。
　そうなると、参照がなくなったActivityを持ち続けることになり、メモリリークの原因となります。
　その点、getApplicationContext()で取得できるContext情報はActivityのライフサイクルに依存せず、
　アプリケーションとしての純粋な情報となるため、メモリリークの危険性を回避できます。
　Contextを必要とするAPIには、可能な限りgetApplicationContext()を渡すようにしましょう。
http://yuki312.blogspot.jp/2012/02/thisgetapplicationcontextactivityapplic.html
    */
    private Question mQustion;

    private Favorite mFavorite;

    public String quesitionID ;
    boolean isFavorite = false;
    // FirebaseAuthのオブジェクトを取得する
    FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    //ここでQuestionを取得していると思われる。
    public QuestionDetailListAdapter(Context context, Question question) {
        mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mQustion = question;
    }


    //ListViewのカウント
    @Override
    public int getCount() {
        return 1 + mQustion.getAnswers().size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_QUESTION;
        } else {
            return TYPE_ANSWER;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return mQustion;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }



    //お気に入りを登録する
    public View.OnClickListener mFavButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("お気に入りを登録する","クリック");

            if(togglebutton.isChecked()) {


                togglebutton.setTextOn("お気に入り");

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    // ログインしていなければログイン画面に遷移させる
                    Intent intent = new Intent(getpplicationContext(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    // ログインユーザーのFavoritePATHへ移動
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference favoriteRef = databaseReference.child(Const.UsersPATH).child(user.getUid()).child(Const.FavoritesPATH).child(mFavorite.getFavoriteUid());

                    favoriteRef.removeValue();
                }

            } else {

                togglebutton.setTextOff("解除");
                // FirebaseAuthのオブジェクトを取得する
                mAuth = FirebaseAuth.getInstance();
                //Log.d("", this.mQuestion);
                FirebaseUser user = mAuth.getCurrentUser();

                Log.d("User", String.valueOf(user));

                DatabaseReference dataBaseReference = FirebaseDatabase.getInstance().getReference();
                DatabaseReference favoriteRef = dataBaseReference.child(Const.UsersPATH).child(user.getUid()).child(Const.FavoritesPATH);

                Log.d("FavoriteRef", String.valueOf(favoriteRef));
                Map<String, String> data = new HashMap<String, String>();

                // questionID
                data.put("questionid", String.valueOf(mQustion.getQuestionUid()));

                favoriteRef.push().setValue(data);


            }

        }
    };

    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


        if (databaseError == null) {
            Log.d("","かきくけこ");
        } else {
            Log.d("","あいうえお");
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("いつ","呼ばれているか");


        //質問側のView
        if (getItemViewType(position) == TYPE_QUESTION) {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.list_question_detail, parent, false);
            }

            togglebutton = (ToggleButton) convertView.findViewById(R.id.FavButton);
            togglebutton.setOnClickListener(mFavButtonClickListener);





            String body = mQustion.getBody();
            String name = mQustion.getName();

            TextView bodyTextView = (TextView) convertView.findViewById(R.id.bodyTextView);
            bodyTextView.setText(body);

            TextView nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
            nameTextView.setText(name);

            byte[] bytes = mQustion.getImageBytes();
            if (bytes.length != 0) {
                Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length).copy(Bitmap.Config.ARGB_8888, true);
                ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
                imageView.setImageBitmap(image);
            }


        } else {

         // 回答側のView（つまり写真とかない）
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.list_answer, parent, false);
            }

            Answer answer = mQustion.getAnswers().get(position - 1);
            String body = answer.getBody();
            String name = answer.getName();


            TextView bodyTextView = (TextView) convertView.findViewById(R.id.bodyTextView);
            bodyTextView.setText(body);

            TextView nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
            nameTextView.setText(name);
        }

        return convertView;
    }


}