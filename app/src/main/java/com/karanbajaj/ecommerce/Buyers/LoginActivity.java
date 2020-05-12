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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karanbajaj.ecommerce.Admin.AdminHomeActivity;
import com.karanbajaj.ecommerce.Seller.SellerProductCategoryActivity;
import com.karanbajaj.ecommerce.Model.User;
import com.karanbajaj.ecommerce.Prevalent.Prevalent;
import com.karanbajaj.ecommerce.R;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText inputNumber, inputPassword;
    private Button loginButton;
    private ProgressDialog loadingBar;

    private String parentDbName = "Users";
    private CheckBox chkboxRememberMe;
    private TextView AdminLink, NotAdminLink, ForgetPasswordLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputNumber = findViewById(R.id.login_phone_number_btn);
        inputPassword = findViewById(R.id.login_password_input);
        loginButton = findViewById(R.id.login_btn);
        loadingBar = new ProgressDialog(this);
        chkboxRememberMe = findViewById(R.id.remember_me_chkb);

        AdminLink = findViewById(R.id.admin_panel_link);
        NotAdminLink = findViewById(R.id.not_admin_panel_link);
        ForgetPasswordLink = findViewById(R.id.forget_password_link);

        Paper.init(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginUser();
            }
        });

        ForgetPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
                intent.putExtra("check", "login");
                startActivity(intent);
            }
        });

        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginButton.setText("Login Admin");
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);

                parentDbName = "Admins";
            }
        });


        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginButton.setText("Login");
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);

                parentDbName = "Users";
            }
        });
    }


    private void loginUser() {

                String phone = inputNumber.getText().toString();
                String password = inputPassword.getText().toString();

                  if(TextUtils.isEmpty(phone)){

                    Toast.makeText(LoginActivity.this, "Please Write Your Phone Number....", Toast.LENGTH_SHORT).show();
                }

                else if(TextUtils.isEmpty(password)){

                    Toast.makeText(LoginActivity.this, "Please Write Your Password....", Toast.LENGTH_SHORT).show();
                }
                else{

                    loadingBar.setTitle("Login");
                    loadingBar.setMessage("Please wait, While we are checking the credentials.");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    AllowAccessToAccount(phone, password);
                }
            }

            private void AllowAccessToAccount(final String phone, final String password) {

                if(chkboxRememberMe.isChecked()){

                    Paper.book().write(Prevalent.userPhoneKey, phone);
                    Paper.book().write(Prevalent.userPasswordKey, password);
                }

                final DatabaseReference RootRef;
                RootRef = FirebaseDatabase.getInstance().getReference();

                RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.child(parentDbName).child(phone).exists()){

                            User usersData = dataSnapshot.child(parentDbName).child(phone).getValue(User.class);

                            if(usersData.getPhone().equals(phone)) {

                                if (usersData.getPassword().equals(password)) {

                                    if (parentDbName.equals("Admins")) {

                                        Toast.makeText(LoginActivity.this, "Admin Area", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Intent intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                                        startActivity(intent);

                                    } else if(parentDbName.equals("Users")) {

                                        Toast.makeText(LoginActivity.this, "Loggged in successfull", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                        Prevalent.currentOnlineUser = usersData;
                                        startActivity(intent);
                                    }
                                } else {
                                    loadingBar.dismiss();
                                    Toast.makeText(LoginActivity.this, "Incorrect Password! Try Again", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }else{

                            Toast.makeText(LoginActivity.this, "Account not exits", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Create a new account", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

    }
