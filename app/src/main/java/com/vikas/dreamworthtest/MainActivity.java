package com.vikas.dreamworthtest;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.vikas.dreamworthtest.Model.User_Details;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener ,
        GoogleApiClient.ConnectionCallbacks{
    public static String TAG="MainActivity";
    private LoginButton btnLogin;
    private CallbackManager callbackManager;
    private static final List<String> PERMISSIONS = Arrays.asList("public_profile","email","user_birthday");
    User_Details user_details;

    // Google Integration
    //Signin button
    private SignInButton signInButton;

    // Sign out Button
    private Button sign_out_button;
    //Signing Options
    private GoogleSignInOptions gso;

    //google api client
    private GoogleApiClient mGoogleApiClient;

    //Signin constant to check the activity result
    private int RC_SIGN_IN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        init();
        // Hash Key

        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.vikas.dreamworthtest", PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String sign= Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.e("MY KEY HASH:", sign);
                // Toast.makeText(getApplicationContext(),sign,     Toast.LENGTH_LONG).show();
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {

        }

        //Google
        //Initializing google signin option
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_ME), new Scope(Scopes.PLUS_LOGIN),new Scope(Scopes.PROFILE))
                .build();

        //Initializing signinbutton
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setScopes(gso.getScopeArray());

        //Initializing google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .build();


        //Setting onclick listener to signing button
        signInButton.setOnClickListener(this);
        // Setting onClick Listener to Signout button
       // sign_out_button.setOnClickListener(this);

        // Facebook
        btnLogin.setReadPermissions(PERMISSIONS);
        btnLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v(TAG, response.toString());


                                JSONObject jsonObject=response.getJSONObject();
                                Log.v(TAG, "========="+jsonObject);

                                try {
                                    Log.d(TAG,"name="+jsonObject.getString("name"));
                                    Log.d(TAG,"Emailid="+jsonObject.getString("email"));
                                  //  Log.d(TAG,"birthday="+jsonObject.getString("birthday"));

                                     user_details=new User_Details();

                                    if(jsonObject.getString("name")!=null){
                                        user_details.setUserName(jsonObject.getString("name"));
                                    }else {
                                        user_details.setUserName("NA");
                                    }

                                    if(jsonObject.getString("email")!=null){
                                        user_details.setUserEmail(jsonObject.getString("email"));
                                    }else {
                                        user_details.setUserEmail("NA");
                                    }
                                    if(jsonObject.getString("birthday")!=null){
                                        user_details.setUserDOB(jsonObject.getString("birthday"));

                                    }else {
                                        user_details.setUserDOB("NA");

                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.d(TAG,"JSONException="+e);
                                    Toast.makeText(MainActivity.this, "error to Login Facebook", Toast.LENGTH_SHORT).show();

                                }

                                Intent intent1=new Intent(MainActivity.this,Details_Activity.class);
                                intent1.putExtra("UserDetails",user_details) ;
                                startActivity(intent1);
                                FbsignOut();



                            }
                        });
                Bundle parameters = new Bundle();
                //user_mobile_phone
                parameters.putString("fields","id,name,email,gender,locale,timezone,first_name,last_name,age_range,birthday,verified");
                request.setParameters(parameters);
                request.executeAsync();
            }
            @Override
            public void onCancel() {
                // App code
            }
            @Override
            public void onError(FacebookException exception) {
                Log.v(TAG, "exception ========="+exception);
                Toast.makeText(MainActivity.this, "error to Login Facebook", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void FbsignOut() {
        LoginManager.getInstance().logOut();
        //Toast.makeText(MainActivity.this,"Fb Logout",Toast.LENGTH_SHORT).show();
    }


    private void init() {
        callbackManager = CallbackManager.Factory.create();
        btnLogin = (LoginButton)findViewById(R.id.login_button);
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        callbackManager.onActivityResult(requestCode, responseCode, intent);

        // Google
        //If signin
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            //Calling a new function to handle signin
           handleSignInResult(result);
            if (result.isSuccess()) {
                mGoogleApiClient.connect(GoogleApiClient.SIGN_IN_MODE_OPTIONAL);
                GoogleSignInAccount acct = result.getSignInAccount();
                Log.d(TAG,"GoogleSignInAccount="+acct);
                Log.d(TAG,"GoogleSignInAccount email="+acct.getEmail());

                // G+
                if (mGoogleApiClient.hasConnectedApi(Plus.API)) {
                    Person person  = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                    if (person != null) {
                        Log.i(TAG, "--------------------------------");
                        Log.i(TAG, "Display Name: " + person.getDisplayName());
                        Log.i(TAG, "Gender: " + person.getGender());
                        Log.i(TAG, "About Me: " + person.getAboutMe());
                        Log.i(TAG, "Birthday: " + person.getBirthday());
                        Log.i(TAG, "Current Location: " + person.getCurrentLocation());
                        Log.i(TAG, "Language: " + person.getLanguage());
                        user_details=new User_Details();

                        if(person.getDisplayName()!=null){
                            user_details.setUserName(person.getDisplayName());
                        }else {
                            user_details.setUserName("NA");
                        }

                        if(acct.getEmail()!=null){
                            user_details.setUserEmail(acct.getEmail());
                        }else {
                            user_details.setUserEmail("NA");
                        }

                        if (person.getBirthday()!=null){
                            user_details.setUserDOB(person.getBirthday());
                        }else {
                            user_details.setUserDOB("NA");
                        }


                        Intent intent1=new Intent(MainActivity.this,Details_Activity.class);
                        intent1.putExtra("UserDetails",user_details) ;
                        startActivity(intent1);
                        signOutGoogle();
                    } else {
                        Log.e(TAG, "Error!");
                    }
                } else {
                    Log.e(TAG, "Google+ not connected");
                }
            }else {
                //If login fails
                Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
            }

        }

    }

    private void signOutGoogle() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.d("TAG","status="+status);


                    }
                });

    }

    //After the signing we are calling this function
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR1)
    private void handleSignInResult(GoogleSignInResult result) {
        //If the login succeed
        if (result.isSuccess()) {
            //Getting google account
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.d(TAG,"GoogleSignInAccount="+acct);
            Log.d(TAG,"GoogleSignInAccount email="+acct.getEmail());

            //Displaying name and email
          //  textViewName.setText(acct.getDisplayName());
          //  textViewEmail.setText(acct.getEmail());


           /* //Initializing image loader
            imageLoader = CustomVolleyRequest.getInstance(this.getApplicationContext())
                    .getImageLoader();

            imageLoader.get(acct.getPhotoUrl().toString(),
                    ImageLoader.getImageListener(profilePhoto,
                            R.mipmap.ic_launcher,
                            R.mipmap.ic_launcher));

            //Loading image
            profilePhoto.setImageUrl(acct.getPhotoUrl().toString(), imageLoader);*/

        } else {
            //If login fails
            Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == signInButton) {
            //Calling signin
            signIn();
        }
    }

    private void signIn() {
        //Creating an intent
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);

        //Starting intent for result
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
