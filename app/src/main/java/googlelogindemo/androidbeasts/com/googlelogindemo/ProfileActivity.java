package googlelogindemo.androidbeasts.com.googlelogindemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.PlusShare;
import com.squareup.picasso.Picasso;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import googlelogindemo.androidbeasts.com.googlelogindemoadapter.CircleTransform;
import googlelogindemo.androidbeasts.com.googlelogindemoadapter.CustomAdapter;

/**
 * Created by aakash on 21/08/15.
 */
public class ProfileActivity extends AppCompatActivity{
    SharedPreferences shared;
    ImageView userImage;
    TextView userName;
    ArrayList<String> userNames,userImages;
    CustomAdapter usersAdapter;
    ListView usersList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        shared=getSharedPreferences("demoGoogleLogin", Context.MODE_PRIVATE);
        userImage=(ImageView) findViewById(R.id.imageView);
        userName=(TextView) findViewById(R.id.textView);
        usersList=(ListView) findViewById(R.id.userslist);
        userNames=new ArrayList<>();
        userImages=new ArrayList<>();
        Picasso.with(ProfileActivity.this).load(shared.getString("userImageUrl","")).transform(new CircleTransform()).placeholder(R.drawable.icon_profile).error(R.drawable.icon_profile).resize(200,200).into(userImage);
        userName.setText("You are logged in as: "+shared.getString("userName",""));
        userNames=getIntent().getStringArrayListExtra("friendsNamesList");
        userImages=getIntent().getStringArrayListExtra("friendsImagesList");
        usersAdapter=new CustomAdapter(ProfileActivity.this,userNames,userImages);
        usersList.setAdapter(usersAdapter);

    }

}
