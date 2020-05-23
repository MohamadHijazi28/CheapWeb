package com.example.cheapweb;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements MainDialoge.MainDialogeListener {
    Intent intent;
    EditText searchtxt;
    Button searchbtn;
    ImageButton SearchVoice;
    String username;
    TextView itemName1, itemName2, itemName3, itemName4;
    TextView itemPrice1, itemPrice2, itemPrice3, itemPrice4;
    ImageView itemImage1, itemImage2, itemImage3, itemImage4;
    FirebaseDatabase mfirebaseDatabase;
    DatabaseReference mRef;
    Model[] itemsId, AllitemsId;
    int num=0, x=0, i=0,m=0, n=0, j;
    Random random=new Random();
    int[] rndnums;
    private FirebaseAuth mAuth;
    private long backPressedTime;
    private static final int REQUEST_CODE_SPEECH_INPUT=1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        searchtxt = findViewById(R.id.searchtxt);
        searchbtn = findViewById(R.id.searchbtn);
        //username=getIntent().getStringExtra("userEmail");
        username=mAuth.getCurrentUser().getEmail();
        mfirebaseDatabase=FirebaseDatabase.getInstance();
        SearchVoice=findViewById(R.id.search_voice);
        itemName1=findViewById(R.id.Item1_Name);
        itemName2=findViewById(R.id.Item2_Name);
        itemName3=findViewById(R.id.Item3_Name);
        itemName4=findViewById(R.id.Item4_Name);
        itemPrice1=findViewById(R.id.Item1_Price);
        itemPrice2=findViewById(R.id.Item2_Price);
        itemPrice3=findViewById(R.id.Item3_Price);
        itemPrice4=findViewById(R.id.Item4_Price);
        itemImage1=findViewById(R.id.Item1_image);
        itemImage2=findViewById(R.id.Item2_image);
        itemImage3=findViewById(R.id.Item3_image);
        itemImage4=findViewById(R.id.Item4_image);
        itemsId=new Model[4];
        rndnums=new int[4]; rndnums[0]=0; rndnums[1]=0; rndnums[2]=0; rndnums[3]=0;
        mRef=mfirebaseDatabase.getReference("items");

        SearchVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });
        itemImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent i=new Intent(MainActivity.this, ItemActivity.class);
               i.putExtra("itemid", itemsId[0].getIdItem());
               startActivity(i);
            }
        });
        itemImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this, ItemActivity.class);
                i.putExtra("itemid", itemsId[1].getIdItem());
                startActivity(i);
            }
        });
        itemImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this, ItemActivity.class);
                i.putExtra("itemid", itemsId[2].getIdItem());
                startActivity(i);
            }
        });
        itemImage4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this, ItemActivity.class);
                i.putExtra("itemid", itemsId[3].getIdItem());
                startActivity(i);
            }
        });

        //get the children count and put the items in the AllitemsId[] array
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                num=(int) dataSnapshot.getChildrenCount();
                m=num;
                AllitemsId=new Model[num];
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    num--;
                    AllitemsId[num]= ds.getValue(Model.class);
                }
                setIn(AllitemsId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        searchbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String sear = searchtxt.getText().toString();
                    if (!sear.matches("")) {
                        String searchtext = searchtxt.getText().toString();
                        Intent intent = new Intent(MainActivity.this, ItemResult.class);
                        intent.putExtra("Searchtext", searchtext);
                        startActivity(intent);
                        searchtxt.setText("");
                    } else {
                        Toast.makeText(MainActivity.this, "please select an item name", Toast.LENGTH_SHORT).show();
                    }
                }
                });

    }

    private void speak() {
        Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Item Name");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        }
        catch (Exception e){
            Toast.makeText(MainActivity.this, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null!=data){
                    ArrayList<String> result= data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    searchtxt.setText(result.get(0).toLowerCase().trim());
                }
                break;
            }
        }
    }

    //set the random numbers in int array
    //get the items from the ids[] and set them on the activity_main
     public void setIn(Model[] ids){
        rndnums[0]=randomNum(m);
        for (j=1; j<4; j++){
            n=randomNum(m);


        while (isIn(rndnums, n)){
            n=randomNum(m);
        }

                rndnums[j]=n;
        }
        for (i=0; i<itemsId.length; i++){
            itemsId[i]=ids[rndnums[i]];
        }

        itemName1.setText(itemsId[0].getItemName());
        itemPrice1.setText(itemsId[0].getItemPrice());
        Picasso.with(getApplicationContext()).load(itemsId[0].getImagePath()).into(itemImage1);
        itemName2.setText(itemsId[1].getItemName());
        itemPrice2.setText(itemsId[1].getItemPrice());
        Picasso.with(getApplicationContext()).load(itemsId[1].getImagePath()).into(itemImage2);
        itemName3.setText(itemsId[2].getItemName());
        itemPrice3.setText(itemsId[2].getItemPrice());
        Picasso.with(getApplicationContext()).load(itemsId[2].getImagePath()).into(itemImage3);
        itemName4.setText(itemsId[3].getItemName());
        itemPrice4.setText(itemsId[3].getItemPrice());
        Picasso.with(getApplicationContext()).load(itemsId[3].getImagePath()).into(itemImage4);
    }


    // return random number
    public int randomNum(int range){
        return random.nextInt(range-1)+1;

    }

    public boolean isIn(int[] a, int number){
        for (int w=0; w<a.length; w++){
            if (a[w]==number)
                return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.bottom_nav_menu, menu);
        return true;
    }

    //check if the administrator is sign in
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem register = menu.findItem(R.id.nawigation_add);
        if(!username.equals("aaaa.abbb253@gmail.com"))
        {
            register.setVisible(false);
        }
        else
        {
            register.setVisible(true);
        }
        return true;
    }

    //option item selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.navigation_logout:
                SharedPreferences preferences=getSharedPreferences("checkBox", MODE_PRIVATE);
                SharedPreferences.Editor editor=preferences.edit();
                editor.putString("remember", "false");
                editor.apply();
                openDialoge();
                break;
            case R.id.navigation_favorite:
                intent=new Intent(getApplicationContext(),Favorite.class);
                Toast.makeText(MainActivity.this,"favorite class", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                break;

           case R.id.nawigation_add:
                intent= new Intent(getApplicationContext(), UploadingItems.class);
                Toast.makeText(MainActivity.this, "you moved to upload items", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                break;
            case R.id.access:
                intent=new Intent(getApplicationContext(), ItemResult.class);
                intent.putExtra("Searchtext", "accessories");
                startActivity(intent);
                break;
            case R.id.elect:
                intent=new Intent(getApplicationContext(), ItemResult.class);
                intent.putExtra("Searchtext", "electronics");
                startActivity(intent);
                break;
            case R.id.clothes_shows:
                intent=new Intent(getApplicationContext(), ItemResult.class);
                intent.putExtra("Searchtext", "ClothesShows");
                startActivity(intent);
                break;
            case R.id.all_cate:
                intent=new Intent(getApplicationContext(), ItemResult.class);
                intent.putExtra("Searchtext", "allCategories");
                startActivity(intent);
                break;
        }
        return false;
    }


    //ask if the user want to log out
    public void openDialoge(){
        MainDialoge mainDialoge=new MainDialoge();
        mainDialoge.show(getSupportFragmentManager(), "Dialoge");
    }

    // if the user want to exit
    @Override
    public void onYesClickMain() {
        finish();
        moveTaskToBack(true);


    }

    //if the user click two clicks on the back click ho exit from the app
    @Override
    public void onBackPressed() {

        if (backPressedTime+2000>System.currentTimeMillis()) {
            super.onBackPressed();
            moveTaskToBack(true);
            return;
        }
        else {
            Toast.makeText(this, "Press Back to exit", Toast.LENGTH_SHORT).show();
        }
            backPressedTime=System.currentTimeMillis();

    }
}
