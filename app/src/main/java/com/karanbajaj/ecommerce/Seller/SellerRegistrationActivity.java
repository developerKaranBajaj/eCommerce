package com.karanbajaj.ecommerce.Seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karanbajaj.ecommerce.Buyers.MainActivity;
import com.karanbajaj.ecommerce.R;

import java.util.HashMap;

public class SellerRegistrationActivity extends AppCompatActivity {

    private Button sellerLoginBtn;
    private EditText nameInput, phoneInput, emailInput, passwordInput, addressInput;
    private Button registerButton;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_registration);

        mAuth = FirebaseAuth.getInstance();

        sellerLoginBtn = findViewById(R.id.seller_already_have_account_btn);
        registerButton = findViewById(R.id.seller_register_btn);
        nameInput = findViewById(R.id.seller_name);
        phoneInput = findViewById(R.id.seller_phone);
        emailInput = findViewById(R.id.seller_email);
        passwordInput = findViewById(R.id.seller_password);
        addressInput = findViewById(R.id.seller_address);

        loadingBar = new ProgressDialog(this);


        sellerLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SellerLoginActivity.class));
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerSeller();
            }
        });
    }

    private void registerSeller() {

        final String name  = nameInput.getText().toString();
        final String phone  = phoneInput.getText().toString();
        final String email  = emailInput.getText().toString();
        String password  = passwordInput.getText().toString();
        final String address  = addressInput.getText().toString();

        if (!name.equals("") && !phone.equals("") && !email.equals("") && !password.equals("") && !address.equals("")) {

            loadingBar.setTitle("Creating seller Account");
            loadingBar.setMessage("Please wait, While we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                final DatabaseReference rootRef;
                                rootRef = FirebaseDatabase.getInstance().getReference();

                                String sid = mAuth.getCurrentUser().getUid();
                                HashMap<String, Object> sellerMap = new HashMap<>();
                                sellerMap.put("sid", sid);
                                sellerMap.put("phone", phone);
                                sellerMap.put("email", email);
                                sellerMap.put("name", name);
                                sellerMap.put("address", address);

                                rootRef.child("Sellers").child(sid).updateChildren(sellerMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                loadingBar.dismiss();
                                                Toast.makeText(SellerRegistrationActivity.this, "Register successfully", Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(getApplicationContext(), SellerHomeActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                            }
                        }
                    });

        }else{
            Toast.makeText(this, "Fields are empty", Toast.LENGTH_SHORT).show();
        }

    }

}
