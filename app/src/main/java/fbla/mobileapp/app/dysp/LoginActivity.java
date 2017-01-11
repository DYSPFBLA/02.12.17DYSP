package fbla.mobileapp.app.dysp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class LoginActivity extends AppCompatActivity {
    private TextView WelcomeText;
    private EditText inputEmail, inputPassword, inputName;
    private FirebaseAuth auth;
    private FirebaseApp firebaseApp;
    private ProgressBar progressBar, progressBar4;
    private Button btnSignup, btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Users");
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(this, NavigationActivity.class));
            //finish();
            //auth.signOut();
            //Toast.makeText(LoginActivity.this, auth.getCurrentUser().getEmail().toString(), Toast.LENGTH_LONG).show();
        }

        // set the view now
        setContentView(R.layout.activity_login);
        //Typeface custom = Typeface.createFromAsset(getAssets(), "fonts/lettergothic.ttf");

        //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);
        WelcomeText = (TextView)findViewById(R.id.WelcomeText);
        inputName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar4 = (ProgressBar)findViewById(R.id.progressBar4);
        progressBar4.setVisibility(View.GONE);
        btnSignup = (Button) findViewById(R.id.sign_in_button);
        btnLogin = (Button) findViewById(R.id.sign_up_button);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignIn.class));
            }
        });

         btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar4.setVisibility(View.VISIBLE);
                final String name = inputName.getText().toString();
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }


                //authenticate user
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar4.setVisibility(View.GONE);
                        if(!task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Sign up Failed." + task.getException(), Toast.LENGTH_LONG).show();
                        }else{
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(!task.isSuccessful()){
                                                Toast.makeText(LoginActivity.this, task.getException().toString(), Toast.LENGTH_LONG);
                                            }
                                        }
                                    });
                            Toast.makeText(getApplicationContext(), "Welcome " + name.toString() + "!", Toast.LENGTH_LONG).show();
                            String uid = task.getResult().getUser().getUid().toString();
                           // String displayname = task.getResult().getUser().getEmail().toString();
                            myRef.child(uid).setValue(task.getResult().getUser().getEmail().toString());
                            long item = 0;
                            myRef.child(uid).child("Money Raised").setValue(item);
                            myRef.child(uid).child("Objects Sold").setValue(item);
                            myRef.child(uid).child("Location").setValue("Unspecified");
                            myRef.child(uid).child("MoneyGoal").setValue("0");
                            Intent signinactivity = new Intent(LoginActivity.this, SignIn.class);
                            signinactivity.putExtra("Name", name);
                            startActivity(signinactivity);
                           // finish();
                        }

                    }
                });

            }
        });
    }
}