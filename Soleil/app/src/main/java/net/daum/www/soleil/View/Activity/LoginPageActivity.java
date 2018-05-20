package net.daum.www.soleil.View.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import net.daum.www.soleil.R;
import net.daum.www.soleil.Utils.GpsInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by thswl on 2018-02-15.
 */

public class LoginPageActivity extends AppCompatActivity {
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mAuth;
    Context mContext;
    EditText email;
    EditText pw;
    Button login;
    Button createAccount;

    Context context = this;
    GpsInfo mGpsInfo = new GpsInfo(context);

    final int REQUEST_ERROR = 0;
    final String TAG = "Soleil";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        mContext = this;
        mAuth = FirebaseAuth.getInstance();
        email = (EditText)findViewById(R.id.login_email);
        pw = (EditText)findViewById(R.id.login_pw);
        pw.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_PASSWORD);
        pw.setTransformationMethod(PasswordTransformationMethod.getInstance());
        login = (Button)findViewById(R.id.submit_button);
        createAccount = (Button) findViewById(R.id.create_account_btn);

        /* firebase listener */
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d("NO USER", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d("SIGN_OUT", "onQuthStateChanged:signed_out");
                }
            }
        };


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocation();
                loginAccount(email.getText().toString(), pw.getText().toString());
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(LoginPageActivity.this,CreateAccountActivity.class);
                startActivity(intent);

            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onStop(){
        super.onStop();

        if(mAuthStateListener != null){
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        FirebaseAuth.getInstance().signOut();
    }

    private boolean isValidPasswd(String target) {
        Pattern p = Pattern.compile("(^.*(?=.{6,100})(?=.*[0-9])(?=.*[a-zA-Z]).*$)");

        Matcher m = p.matcher(target);
        if (m.find() && !target.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")){
            return true;
        }else{
            return false;
        }
    }

    private boolean isValidEmail(String target) {
        if (target == null || TextUtils.isEmpty(target)){
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private void loginAccount(String email, String password) {
        if(!isValidEmail(email)){
            Log.e("EMAIL_VALID", "createAccount: email is not valid ");
            Toast.makeText(LoginPageActivity.this, "Email is not valid",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (isValidPasswd(password)){
            Log.e("PW_VALID", "createAccount: password is not valid ");
            Toast.makeText(mContext, "Password is not valid",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("LOGIN_COMPLETE", "signInWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w("LOGIN_FAIL", "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginPageActivity.this, "Authentication failed",
                                    Toast.LENGTH_SHORT).show();
                        }else if(task.isSuccessful()) {
                            Log.w("LOGIN_Success", "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginPageActivity.this, "Authentication success",
                                    Toast.LENGTH_SHORT).show();
                            if(mGpsInfo.isGetLocation()) {
                                Intent intent = new Intent(LoginPageActivity.this, MainActivity.class);
                                startActivity(intent);
                            }else{
                                mGpsInfo.showSettingsAlert();
                            }

                        }
                    }
                });
    }

    public void setLocation(){
        mGpsInfo.isGetLocation = false;
        mGpsInfo.getLocation(this);
    }
}
