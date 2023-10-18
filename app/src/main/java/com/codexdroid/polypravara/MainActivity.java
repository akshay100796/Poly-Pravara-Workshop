package com.codexdroid.polypravara;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {



    //define ui elements to interact with
    ImageView profilePic;
    EditText fullName, email;
    Switch aSwitch;
    Button submit;


    //Input Data
    Uri imageUri;
    String inputFullName, inputEmail;
    Boolean isSelected = false;



    FirebaseDatabase database;
    DatabaseReference reference;
    StorageReference firebaseStorage;

    int index = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialisation();
        setOnClickedListener();



    }

    private void initialisation() {
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("User");
        firebaseStorage = FirebaseStorage.getInstance().getReference();

        //findid
        profilePic = findViewById(R.id.id_image_view);
        fullName = findViewById(R.id.id_edit_fullName);
        email = findViewById(R.id.id_edit_email);
        aSwitch= findViewById(R.id.id_switch);
        submit = findViewById(R.id.id_button);

    }
    private void setOnClickedListener() {

        //Clicked On Profile image
        profilePic.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent,1111);
        });

        //Click on Switch Button
        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> isSelected = isChecked);

        //Click on Submit Button
        submit.setOnClickListener(v -> {
            inputFullName = fullName.getText().toString();
            inputEmail = email.getText().toString();
//            imageUri;


            Users users = new Users();
            users.setName(inputFullName);
            users.setEmail(inputEmail);
            users.setSelected(isSelected);

            ++index;
            reference.child(""+index)
                    .setValue(users)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(MainActivity.this, "Data Stored", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })

            ;
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //We get image file in `Uri` format, hence we initialized to `imageUri` variable
        imageUri = data.getData();

        //we show image, to confirm this block is executed, and we got imageUri, surely
        profilePic.setImageURI(imageUri);

        //Hence, we are free to store image to firebase
        firebaseStorage.child(""+System.currentTimeMillis()).putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Upload Fail : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}