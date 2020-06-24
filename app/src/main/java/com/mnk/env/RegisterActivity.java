package com.mnk.env;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Patterns;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    //register the account in the

    private EditText fnameET, lnameET, emailET, passwordET, confirmpasswordET, phonenumberET, dobET;
    private EditText HAaddress1ET, HAaddress2ET, HAcountryET, HAstateET, HAcityET, HApincodeET;
    private EditText WAaddress1ET, WAaddress2ET, WAcountryET, WAstateET, WAcityET, WApincodeET;

    private String fname, lname, email, password, confirmPassword, phoneNumber, dateOfBirth;
    private String HAaddress1, HAaddress2, HAcountry, HAstate, HAcity, HApincode;
    private String WAaddress1, WAaddress2, WAcountry, WAstate, WAcity, WApincode;
    private static final String EMPTY_STRING = "";
    boolean isRegisterSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FirebaseApp.initializeApp(this);
        dobET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        findViewsById();
    }

    private void findViewsById() {
        fnameET = findViewById(R.id.fnameET);
        lnameET = findViewById(R.id.lnameET);
        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        confirmpasswordET = findViewById(R.id.confirmpasswordET);
        phonenumberET = findViewById(R.id.pnumberET);
        dobET = findViewById(R.id.dobET);

        HAaddress1ET = findViewById(R.id.HAaddress1ET);
        HAaddress2ET = findViewById(R.id.HAaddress2ET);
        HAcountryET = findViewById(R.id.HAcountryET);
        HAstateET = findViewById(R.id.HAstateET);
        HAcityET = findViewById(R.id.HAcityET);
        HApincodeET = findViewById(R.id.HApincodeET);

        WAaddress1ET = findViewById(R.id.WAaddress1ET);
        WAaddress2ET = findViewById(R.id.WAaddress2ET);
        WAcountryET = findViewById(R.id.WAcountryET);
        WAstateET = findViewById(R.id.WAstateET);
        WAcityET = findViewById(R.id.WAcityET);
        WApincodeET = findViewById(R.id.WApincodeET);
    }

    private void getTextfromViews() {
        fname = fnameET.getText().toString().trim();
        lname = lnameET.getText().toString().trim();
        email = emailET.getText().toString().trim();
        password = passwordET.getText().toString().trim();
        confirmPassword = confirmpasswordET.getText().toString().trim();
        phoneNumber = phonenumberET.getText().toString().trim();
        dateOfBirth = dobET.getText().toString().trim();

        HAaddress1 = HAaddress1ET.getText().toString().trim();
        HAaddress2 = HAaddress2ET.getText().toString().trim();
        HAcountry = HAcountryET.getText().toString().trim();
        HAstate = HAstateET.getText().toString().trim();
        HAcity = HAcityET.getText().toString().trim();
        HApincode = HApincodeET.getText().toString().trim();

        WAaddress1 = WAaddress1ET.getText().toString().trim();
        WAaddress2 = WAaddress2ET.getText().toString().trim();
        WAcountry = WAcountryET.getText().toString().trim();
        WAstate = WAstateET.getText().toString().trim();
        WAcity = WAcityET.getText().toString().trim();
        WApincode = WApincodeET.getText().toString().trim();
    }

    public void showDatePickerDialog() {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int m = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        DatePickerDialog pickerDialog = new DatePickerDialog(RegisterActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String s = dayOfMonth + "/" + (month+1) + "/" + year;
                        dobET.setText(s);
                    }
                }, year, m, day);
        pickerDialog.show();
    }

    private boolean setErrorOnField() {

        getTextfromViews();

        //first name check
        if (fname.equals(EMPTY_STRING)) {
            fnameET.setError("First Name Required");
            fnameET.requestFocus();
            return true;
        }
        //last name check
        if (lname.equals(EMPTY_STRING)) {
            lnameET.setError("Last Name Required");
            lnameET.requestFocus();
            return true;
        }
        //email check
        if (email.equals(EMPTY_STRING) || Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailET.setError("Incorrect Email Address");
            emailET.requestFocus();
            return true;
        }
        //password check
        if (password.equals(EMPTY_STRING)) {
            passwordET.setError("Password Required");
            passwordET.requestFocus();
            return true;
        }
        //confirm Passwrod check
        if (!confirmPassword.equals(password)) {
            confirmpasswordET.setError("Password should match");
            confirmpasswordET.requestFocus();
            return true;
        }
        //phone number check
        if (phoneNumber.length() != 10) {
            phonenumberET.setError("Phone Number should be of length 10");
            phonenumberET.requestFocus();
            return true;
        }
        /*/Date of birth check
        if (dateOfBirth.equals(EMPTY_STRING)) {
            dobET.setError("Date of Birth Required");
            dobET.requestFocus();
            return true;
        }*/

        //Home Address check
            //address Line 1
        if (HAaddress1.equals(EMPTY_STRING)) {
            HAaddress1ET.setError("Address Required");
            HAaddress1ET.requestFocus();
            return true;
        }
            //address line 2
        if (HAaddress2.equals(EMPTY_STRING)) {
            HAaddress2ET.setError("Address Required");
            HAaddress2ET.requestFocus();
            return true;
        }
            //country
        if (HAcountry.equals(EMPTY_STRING)) {
            HAcountryET.setError("Country Required");
            HAcountryET.requestFocus();
            return true;
        }
            //state
        if (HAstate.equals(EMPTY_STRING)) {
            HAstateET.setError("State Required");
            HAstateET.requestFocus();
            return true;
        }
            //city
        if (HAcity.equals(EMPTY_STRING)) {
            HAcityET.setError("City Required");
            HAcityET.requestFocus();
            return true;
        }
            //pincode
        if (HApincode.length() != 6) {
            HApincodeET.setError("Pincode Code should be of length 6");
            HApincodeET.requestFocus();
            return true;
        }

        //Work Address check
            //address Line 1
        if (WAaddress1.equals(EMPTY_STRING)) {
            WAaddress1ET.setError("Address Required");
            WAaddress1ET.requestFocus();
            return true;
        }
            //address line 2
        if (WAaddress2.equals(EMPTY_STRING)) {
            WAaddress2ET.setError("Address Required");
            WAaddress2ET.requestFocus();
            return true;
        }
            //country
        if (WAcountry.equals(EMPTY_STRING)) {
            WAcountryET.setError("Country Required");
            WAcountryET.requestFocus();
            return true;
        }
            //state
        if (WAstate.equals(EMPTY_STRING)) {
            WAstateET.setError("State Required");
            WAstateET.requestFocus();
            return true;
        }
            //city
        if (WAcity.equals(EMPTY_STRING)) {
            WAcityET.setError("City Required");
            WAcityET.requestFocus();
            return true;
        }
            //pincode
        if (WApincode.length() != 6) {
            WApincodeET.setError("Pincode Code should be of length 6");
            WApincodeET.requestFocus();
            return true;
        }
        return false;
    }

    public void uploadDetails(View view) {
        if (!setErrorOnField() && registerUserInFirebaseAuth()) {
            String name = fname + " " + lname;
            String homeAddress = HAaddress1 + ", " + HAaddress2 + ", " + HAcity + ", " + HAstate + ", " + HAcountry + " - " + HApincode;
            String workAddress = WAaddress1 + ", " + WAaddress2 + ", " + WAcity + ", " + WAstate + ", " + WAcountry + " - " + WApincode;

            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("User Info");
            String _id = userReference.push().getKey();
            userReference.child(_id).setValue(new UserData(_id, name, email, phoneNumber, dateOfBirth, homeAddress, workAddress));

            Toast.makeText(this, "User Registered Successfully", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
    }

    public boolean registerUserInFirebaseAuth() {
        isRegisterSuccess = false;
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).
        addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    isRegisterSuccess = true;
                    Toast.makeText(RegisterActivity.this, "User Authentication Successfull", Toast.LENGTH_SHORT).show();
                } else {
                    isRegisterSuccess = false;
                    Toast.makeText(RegisterActivity.this, "User Authentication Failed\n", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                isRegisterSuccess = false;
                Toast.makeText(RegisterActivity.this, "User Authentication Failed\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return isRegisterSuccess;
    }
}
