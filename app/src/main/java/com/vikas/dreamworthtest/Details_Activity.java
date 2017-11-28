package com.vikas.dreamworthtest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vikas.dreamworthtest.Model.User_Details;

public class Details_Activity extends AppCompatActivity {
    TextView txtName,txtEmail,txtDOB;
    Button btnUpload;
    User_Details user_details;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_);
     user_details= (User_Details) getIntent().getSerializableExtra("UserDetails");
        init();
        txtName.setText(user_details.getUserName());
        txtEmail.setText(user_details.getUserEmail());
        txtDOB.setText(user_details.getUserDOB());
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Details_Activity.this,Upload_Activity.class);
                startActivity(intent);
            }
        });
    }

    private void init() {
        txtName= (TextView) findViewById(R.id.txtName);
        txtEmail= (TextView) findViewById(R.id.txtEmail);
        txtDOB= (TextView) findViewById(R.id.txtDOB);
        btnUpload= (Button) findViewById(R.id.btnUpload);

    }
}
