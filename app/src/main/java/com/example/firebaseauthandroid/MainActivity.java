package com.example.firebaseauthandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 7321;
    List<AuthUI.IdpConfig> providers;
    Button btn_sign_out;

    ImageView imageView;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        editText = findViewById(R.id.editText);

        btn_sign_out = (Button)findViewById(R.id.btn_sign_out);
        btn_sign_out.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                AuthUI.getInstance().signOut(MainActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                btn_sign_out.setEnabled(false);
                                showSignInOptions();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this,"" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build()

        );
        showSignInOptions();
    }

    public void viewDataActiv(View V)
    {
        Intent intent = new Intent(MainActivity.this, ViewDataActivity.class);
        startActivity(intent);
    }

    public final void uploadData(View v)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        EditText fileName = findViewById(R.id.fileNameText);

        myRef.child("users").child(user.getUid()).child("Data").child(fileName.getText().toString()).setValue(editText.getText().toString());
        Toast.makeText((Context)this, (CharSequence)"Successfully uploaded", Toast.LENGTH_SHORT).show();
    }

    public final void selectImage(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        this.startActivityForResult(Intent.createChooser(intent, (CharSequence) "Select Picture"), 1);
    }

    private void showSignInOptions(){
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.MyTheme)
                        .build(), MY_REQUEST_CODE
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MY_REQUEST_CODE)
        {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if(resultCode == RESULT_OK)
            {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Toast.makeText(this, "" + user.getEmail(), Toast.LENGTH_SHORT).show();
                btn_sign_out.setEnabled(true);
            }
            else
            {
                Toast.makeText(this, "" + response.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        else         if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            imageView.setImageURI(data.getData());

        }
    }

    public final void startRecognizing(final View v) {
        ImageView var10000 = this.imageView;


        if (var10000.getDrawable() != null) {
            EditText var5 = this.editText;

            var5.setText((CharSequence)"");
            v.setEnabled(false);
            var10000 = this.imageView;
            Drawable var6 = var10000.getDrawable();

            Bitmap bitmap = ((BitmapDrawable)var6).getBitmap();
            FirebaseVisionImage var7 = FirebaseVisionImage.fromBitmap(bitmap);
            FirebaseVisionImage image = var7;
            FirebaseVision var8 = FirebaseVision.getInstance();
            FirebaseVisionTextRecognizer var9 = var8.getOnDeviceTextRecognizer();
            FirebaseVisionTextRecognizer detector = var9;
            detector.processImage(image).addOnSuccessListener((OnSuccessListener)(new OnSuccessListener() {
                // $FF: synthetic method
                // $FF: bridge method
                public void onSuccess(Object var1) {
                    this.onSuccess((FirebaseVisionText)var1);
                }

                public final void onSuccess(FirebaseVisionText firebaseVisionText) {
                    v.setEnabled(true);
                    MainActivity var10000 = MainActivity.this;
                    var10000.processResultText(firebaseVisionText);
                }
            }));
        } else {
            Toast.makeText((Context)this, (CharSequence)"Select an Image First", Toast.LENGTH_SHORT).show();
        }

    }


    private final void processResultText(FirebaseVisionText resultText) {
        if (resultText.getTextBlocks().size() == 0) {
            editText.setText("No Text Found");
            return;
        }
        //String FILENAME = "hello_file"
        //String string = "hello world!"

        //val fos = openFileOutput(FILENAME, Context.MODE_PRIVATE)
        //fos.write(string.toByteArray())
        ///fos.close()
        String blockTxt;
        resultText.getText();
        String lines[] = resultText.getText().split("\\r?\\n");
        for (String line : lines) {
            if (line.toLowerCase().contains("nr. factura") || line.toLowerCase().contains("factura nr") || line.toLowerCase().contains("cnp") || line.toLowerCase().contains("cif") || line.toLowerCase().contains("serie") || line.toLowerCase().contains("seria") || line.toLowerCase().contains("c.u.i")) {
                editText.append(line + "\n");
                continue;
            }
            if (line.toLowerCase().contains("data") || line.toLowerCase().contains("adresa") || line.toLowerCase().contains("sediu") || line.toLowerCase().contains("tva"))
            {       editText.append(line + "\n");
                    continue;
            }
            if(line.matches("[0-9]{1,6},[0-9]{0,3}.[0-9]{0,3}"))
                editText.append("Valoare posibila" + line + "\n");
        }
       /* for (FirebaseVisionText.TextBlock block :resultText.getTextBlocks()) {
            blockTxt = block.getText();
            //if((blockTxt.toLowerCase().contains("nr. factura")) ||(blockTxt.toLowerCase().contains("factura nr")) || (blockTxt.toLowerCase().contains("cnp")) || (blockTxt.toLowerCase().contains("numar factura")) || (blockTxt.toLowerCase().contains("serie")) || (blockTxt.toLowerCase().contains("seria")) || (blockTxt.toLowerCase().contains("cif")) || (blockTxt.toLowerCase().contains("facturii")) || (blockTxt.toLowerCase().contains("factura")) || (blockTxt.toLowerCase().contains("cui")) || (blockTxt.toLowerCase().contains("data")))
            if(blockTxt.toLowerCase().contains("nr"))
                editText.append(block.getText() + "\n\n\n");
            //else if(blockTxt.toLowerCase().contains("tva") || blockTxt.toLowerCase().contains("cota") || blockTxt.toLowerCase().contains("total") || blockTxt.toLowerCase().contains("pret"))
            //    editText.append(block.getText() + "\n");
            //else if(blockTxt.toLowerCase().contains("adresa") || blockTxt.toLowerCase().contains("bulevard") || blockTxt.toLowerCase().contains("blvd") || blockTxt.toLowerCase().contains("strada") || (blockTxt.toLowerCase().contains("str.")))
            //    editText.append(block.getText() + "\n");
        }*/
        // Write a message to the database
        /*
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        myRef.child("users").child(user.getUid()).child("Data").child("Data1").setValue(editText.getText().toString());
        */
        //myRef.setValue("Hello, World!");

    }


}
