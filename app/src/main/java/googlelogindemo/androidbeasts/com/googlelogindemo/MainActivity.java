package googlelogindemo.androidbeasts.com.googlelogindemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;

import java.util.ArrayList;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<People.LoadPeopleResult> {

    Button loginButton;
    SharedPreferences shared;
    ArrayList<String> friendNames,friendImages;
    ProgressDialog pdialog;
    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    /* A flag indicating that a PendingIntent is in progress and prevents
     * us from starting further intents.
     */
    private boolean mIntentInProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();
        setContentView(R.layout.activity_main);
        shared=getSharedPreferences("demoGoogleLogin", Context.MODE_PRIVATE);
        loginButton=(Button) findViewById(R.id.loginButton);
        friendNames=new ArrayList<>();
        friendImages=new ArrayList<>();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleApiClient.connect();
            }
        });

    }
//    protected void onStart() {
//        super.onStart();
//        mGoogleApiClient.connect();
//    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
       // loginButton.setText("Signout");
//        Set<String> sets=
        pdialog=new ProgressDialog(MainActivity.this);
        pdialog.setMessage("Loading..");
        pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdialog.setCancelable(false);
        pdialog.show();
        Plus.PeopleApi.loadVisible(mGoogleApiClient, null)
                .setResultCallback(this);
        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);

            String personName = currentPerson.getDisplayName();
            String personPhoto = currentPerson.getImage().getUrl();
            System.out.println(personPhoto);
            //We can adjust size of image according to our requirements: here 200 is the size of image
            String personImage=personPhoto.substring(0,personPhoto.lastIndexOf("=")+1)+"200";
            System.out.println(personImage);
            String personGooglePlusProfile = currentPerson.getUrl();
            SharedPreferences.Editor edit=shared.edit();
            edit.putString("userName",personName);
            edit.putString("userImageUrl",personImage);
            edit.putString("userProfile",personGooglePlusProfile);
            edit.commit();


        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!mIntentInProgress && result.hasResolution()) {
            try {
                mIntentInProgress = true;
                startIntentSenderForResult(result.getResolution().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onResult(People.LoadPeopleResult peopleData) {
        if (peopleData.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
            PersonBuffer personBuffer = peopleData.getPersonBuffer();
            try {
                int count = personBuffer.getCount();
                for (int i = 0; i < count; i++) {
                    Log.d("MAINACTIVITY", "Display name: " + personBuffer.get(i).getImage().getUrl());
                    friendNames.add(personBuffer.get(i).getDisplayName());
                    String usrImage=personBuffer.get(i).getImage().getUrl().substring(0, personBuffer.get(i).getImage().getUrl().lastIndexOf("=") + 1)+"60";
                    friendImages.add(usrImage);
                }
                personBuffer.close();
                if(pdialog!=null){
                    pdialog.dismiss();
                }
                startActivity(new Intent(MainActivity.this,ProfileActivity.class).putStringArrayListExtra("friendsNamesList",friendNames).putStringArrayListExtra("friendsImagesList",friendImages));
            } finally {
                personBuffer.release();
            }
        } else {
            Log.e("MAINACTIVITY", "Error requesting visible circles: " + peopleData.getStatus());
        }
    }
}
