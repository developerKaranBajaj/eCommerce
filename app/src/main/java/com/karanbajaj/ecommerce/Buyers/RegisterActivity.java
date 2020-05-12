package com.karanbajaj.ecommerce.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karanbajaj.ecommerce.R;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button CreateAccountBtn;
    private EditText InputName, InputPhoneNumber, InputPassword;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        CreateAccountBtn = findViewById(R.id.register_btn);
        InputName = findViewById(R.id.register_username_btn);
        InputPhoneNumber = findViewById(R.id.register_phone_number_btn);
        InputPassword = findViewById(R.id.register_password_input);
        loadingBar = new ProgressDialog(this);


        CreateAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createAccount();
            }
        });
    }

    private void createAccount() {

        String name = InputName.getText().toString();
        String phoneNumber = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();

        if (TextUtils.isEmpty(name)) {

            Toast.makeText(RegisterActivity.this, "Please Write Your Name....", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phoneNumber)) {

            Toast.makeText(RegisterActivity.this, "Please Write Your Phone Number....", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {

            Toast.makeText(RegisterActivity.this, "Please Write Your Password....", Toast.LENGTH_SHORT).show();
        } else {

            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait, While we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            validatephoneNumber(name, phoneNumber, password);
        }
    }

    private void validatephoneNumber(final String name, final String phoneNumber, final String password) {


        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!(dataSnapshot.child("Users").child(phoneNumber).exists())) {

                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("phone", phoneNumber);
                    userdataMap.put("name", name);
                    userdataMap.put("password", password);

                    RootRef.child("Users").child(phoneNumber).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        Toast.makeText(RegisterActivity.this, "Congratulations, your account is created.", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    } else {

                                        loadingBar.dismiss();
                                        Toast.makeText(RegisterActivity.this, "An error occurred. Please try again in a few minutes.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                } else {
                    Toast.makeText(RegisterActivity.this, "This" + phoneNumber + "Already exits", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Please try again with different phone number.", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
