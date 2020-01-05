package com.example.firebaseauthandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class ViewDataActivity extends AppCompatActivity {
ArrayList myArr;
int currPos = 0;
    FirebaseUser user;
    DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                currPos = 0;
                user = FirebaseAuth.getInstance().getCurrentUser();
                myArr = new ArrayList();
                Iterator myIt = dataSnapshot.child("users").child(user.getUid()).child("Data").getChildren().iterator();
                while(myIt.hasNext())
                    myArr.add(myIt.next());
                String val1 = dataSnapshot.child("users").child(user.getUid()).child("Data").getChildren().iterator().next().getValue().toString();
                EditText editTextData = findViewById(R.id.textView);
                TextView editTextName = findViewById(R.id.textView3);
                editTextData.setText(((DataSnapshot)myArr.get(0)).getValue().toString());
                editTextName.setText(((DataSnapshot)myArr.get(0)).getKey());

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }
    public void getNextDoc(View V)
    {
        if(myArr.size() > currPos + 1)
            currPos++;
        else
            currPos = 0;
        EditText editTextData = findViewById(R.id.textView);
        TextView editTextName = findViewById(R.id.textView3);
        editTextData.setText(((DataSnapshot)myArr.get(currPos)).getValue().toString());
        editTextName.setText(((DataSnapshot)myArr.get(currPos)).getKey());
    }
    public void delDoc(View V)
    {
        myRef.child("users").child(user.getUid()).child("Data").child(((DataSnapshot)myArr.get(currPos)).getKey()).setValue(null);
        
    }
}
