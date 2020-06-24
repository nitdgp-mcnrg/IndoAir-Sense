package com.mnk.env;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends Activity {

    private AlertDialog alertDialog = null;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;

    private EditText userNameEditText, passwordEditText;


    private String userName, passWord;
    private static final String EMPTYSTRING = "";
    private String CHANNEL_ID = "channel_reminder_1";
    private int RC_SIGN_IN = 9001;
    private boolean isLoginSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();

        //Google sign In Integration
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        findViewById(R.id.googleSignInButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        //Toast toast = Toast.makeText(LoginActivity.this, "Password Doesn't Matches", Toast.LENGTH_SHORT);
        //toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        //toast.show();

        userNameEditText = findViewById(R.id.userEmail);
        passwordEditText = findViewById(R.id.userPassword);
    }

    /*
    private void showNotification() {
        createNotificationChannel();

        //Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, FetchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.sprout)
                .setContentTitle("My Notification")
                .setContentText("This is a notification")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("This is a Big Text Style Notification"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define

        notificationManagerCompat.notify(888, builder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    */

    //remaining check username is valid and username password matches;
    private boolean setErrorOnField() {
        userName = userNameEditText.getText().toString().trim();
        passWord = passwordEditText.getText().toString().trim();

        //check whether the user name matches with the EMAIL type

        if (userName.equals(EMPTYSTRING) || Patterns.EMAIL_ADDRESS.matcher(userName).matches()) {
            userNameEditText.setError("Incorrect Email Address");
            userNameEditText.requestFocus();
            return true;
        } else if (passWord.equals(EMPTYSTRING)) {
            passwordEditText.setError("Password Required");
            passwordEditText.requestFocus();
            return true;
        }
        return false;
    }

    public void openHint(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("There is only limited number of functionalities provided for normal user/ who doesn't login");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.create().show();
    }

    public void openMainPage(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("User_Data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (view.getId() == R.id.skipButton) {
            //showNotification();
            editor.putString("UserType", "NormalUser");
            editor.putString("Email", EMPTYSTRING);
            editor.apply();

            startActivityIntent();
        } else if (!setErrorOnField() && loginSuccesFull()) {
            editor.putString("UserType", "BetaUser");
            editor.putString("Email", userName);
            editor.apply();

            startActivityIntent();
        }
    }

    private boolean loginSuccesFull() {
        //check that is user a valid user
        isLoginSuccess = false;
        firebaseAuth.signInWithEmailAndPassword(userName, passWord)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            isLoginSuccess = true;
                            Toast.makeText(LoginActivity.this, "Login SuccessFull", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Login UnSuucessFull\nIncorrect Login Details", Toast.LENGTH_LONG).show();
                            userNameEditText.requestFocus();
                        }
                    }
                });
        return isLoginSuccess;
    }

    //remaining register the user
    public void registerYourself(View view) {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
    }

    //remaining to check that  the mail exist or not, if exist send verification link, enter verification code, and update the password in database.
    public void resetPassword(View view) {
        //reset user password

        //create an AlertBuilder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // builder.setTitle("Reset Password");

        //set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.reset_dialog, null);

        final EditText email = customLayout.findViewById(R.id.verifyEmail);
        Button verify = customLayout.findViewById(R.id.verify);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String verifyEmail = email.getText().toString().trim();
                //verify Email that Email exist or not
                if (!TextUtils.isEmpty(verifyEmail) && Patterns.EMAIL_ADDRESS.matcher(verifyEmail).matches()) {
                    updatePassword(verifyEmail);
                    //hide the verify layout, change the title and show the passwordEnterLayout
                    //customLayout.findViewById(R.id.verifyEmailLayout).setVisibility(View.GONE);
                    //customLayout.findViewById(R.id.passwordEnterLayout).setVisibility(View.VISIBLE);
                    //TextView textView = customLayout.findViewById(R.id.forgotDialogTitle);
                    //textView.setText("Change Password");

                } else {
                    Toast.makeText(LoginActivity.this, "Enter Valid Email Address", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final EditText resetPasswordEditText = customLayout.findViewById(R.id.enter_reset_Password);
        final EditText reenterPasswordEditText = customLayout.findViewById(R.id.confirm_reset_Password);
        customLayout.findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mPassword = resetPasswordEditText.getText().toString().trim();
                String remPassword = reenterPasswordEditText.getText().toString().trim();

                if (!TextUtils.isEmpty(mPassword) && mPassword.equals(remPassword)) {
                    //reset password
                    Toast.makeText(LoginActivity.this, "Passowrd Changed Succesfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Password Doesn't Matches", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setView(customLayout);
        //create and show the alert Dialog
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void updatePassword(String emailAddress) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            alertDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Verification Email Sent to\nEntered Email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void openHiddenLayout(View view) {
        LinearLayout linearLayout = findViewById(R.id.signInWithEmail);
        linearLayout.setVisibility(View.VISIBLE);
        LinearLayout l = findViewById(R.id.hideTheLayout);
        l.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
                if (googleSignInAccount != null) {
                    Toast.makeText(this, "Account is NOT NULL", Toast.LENGTH_SHORT).show();
                    firebaseAuthWithGoogle(googleSignInAccount);
                } else {
                    Toast.makeText(this, "Account is Null", Toast.LENGTH_SHORT).show();
                }
            } catch (ApiException e) {
                Toast.makeText(this, "Google SignIn Failed\n" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Google SignIn Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount signInAccount) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Google SignIn Successful", Toast.LENGTH_LONG).show();
                    startActivityIntent();
                } else {
                    Toast.makeText(LoginActivity.this, "Google SignIn Failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void startActivityIntent() {
        Intent intent = new Intent(LoginActivity.this, FunctionalitiesActivity.class);
        startActivity(intent);
        finish();
    }
}
