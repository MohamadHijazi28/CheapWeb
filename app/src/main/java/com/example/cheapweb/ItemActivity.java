package com.example.cheapweb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ItemActivity extends AppCompatActivity {


    TextView NameOfItem, PriceOfItem, InfoOfItem, Link_1, Link_2, Link_3, PriceLink1, PriceLink2, PriceLink3;
    ImageView ImageOfItem, Link1Image, Link2Image, Link3Image;
    Button maddFavorite, mShareBtn;
    private static final int WRITE_EXTERNAL_STORAGE_CODE = 1;
    FirebaseDatabase mfirebaseDatabase, mfirebaseDatabaseFavorite;
    DatabaseReference mRef, mRefItemFav;
    public String id;
    public FirebaseAuth mAuth;
    Model img;
    int num;
    String url1, url2, url3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Item");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        //recyclerView=findViewById(R.id.recycler_View);
        NameOfItem = findViewById(R.id.ItemNm);
        PriceOfItem = findViewById(R.id.ItemPric);
        InfoOfItem = findViewById(R.id.ItemInf);
        ImageOfItem = findViewById(R.id.Itemimg);
        Link_1 = findViewById(R.id.Link1);
        Link_2 = findViewById(R.id.Link2);
        Link_3 = findViewById(R.id.Link3);
        PriceLink1 = findViewById(R.id.price_link1);
        PriceLink2 = findViewById(R.id.price_link2);
        PriceLink3 = findViewById(R.id.price_link3);
        Link1Image=findViewById(R.id.Link1_image);
        Link2Image=findViewById(R.id.Link2_image);
        Link3Image=findViewById(R.id.Link3_image);

        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mfirebaseDatabase.getReference("items");
        mfirebaseDatabaseFavorite = FirebaseDatabase.getInstance();
        mRefItemFav = mfirebaseDatabase.getReference("FavItem");
        id = getIntent().getStringExtra("itemid");

            SearchById(id);

        Link_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url1 = Link_1.getText().toString();
                OpenWeb(url1);
            }
        });

        Link_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url2 = Link_2.getText().toString();
                OpenWeb(url2);
            }
        });

        Link_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url3 = Link_3.getText().toString();
                OpenWeb(url3);
            }
        });

        Link1Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url1 = Link_1.getText().toString();
                OpenWeb(url1);
            }
        });
        Link2Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url2 = Link_2.getText().toString();
                OpenWeb(url2);
            }
        });
        Link3Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url3 = Link_3.getText().toString();
                OpenWeb(url3);
            }
        });
        maddFavorite = findViewById(R.id.addFavorite);
        mShareBtn = findViewById(R.id.sharebtn);
        //check if the item in favorite class or not
        maddFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Query query= mRefItemFav.child(mAuth.getCurrentUser().getUid()).orderByChild("idItem").equalTo(id);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        num=(int) dataSnapshot.getChildrenCount();
                        if (num==0) {
                            mRefItemFav.child(mAuth.getCurrentUser().getUid()).child(id).setValue(img);
                            Toast.makeText(ItemActivity.this, "added to favorite...", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(ItemActivity.this, "The item is in Favorite", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                }

        });
        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareImage();
            }
        });


    }


    //get the item that the user click and set him in activity_item
    public void SearchById(String idItem){
    Query query = mRef.orderByChild("idItem").equalTo(idItem);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange (@NonNull DataSnapshot dataSnapshot){
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                img = ds.getValue(Model.class);
                NameOfItem.setText(img.getItemName());
                PriceOfItem.setText(img.getItemPrice());
                Picasso.with(getBaseContext()).load(img.getImagePath()).into(ImageOfItem);
                InfoOfItem.setText(img.getItemInfo());
                Link_1.setText(img.getLink1());
                Link_2.setText(img.getLink2());
                Link_3.setText(img.getLink3());
                if (!isEmpty(img.getPriceInLink1())) {
                    PriceLink1.setText("₪" +img.getPriceInLink1());
                }
                else{
                    Link_1.setVisibility(View.GONE);
                    PriceLink1.setVisibility(View.GONE);
                    Link1Image.setVisibility(View.GONE);
                }

                if (!isEmpty(img.getPriceInLink2())) {
                    PriceLink2.setText("₪" +img.getPriceInLink2());
                }
                else{
                    Link_2.setVisibility(View.GONE);
                    PriceLink2.setVisibility(View.GONE);
                    Link2Image.setVisibility(View.GONE);
                }

                if (!isEmpty(img.getPriceInLink3())) {
                    PriceLink3.setText("₪" +img.getPriceInLink3());
                }
                else{
                    Link_3.setVisibility(View.GONE);
                    PriceLink3.setVisibility(View.GONE);
                    Link3Image.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void onCancelled (@NonNull DatabaseError databaseError){

        }

    } );
}

    //check if the text is null..
    public boolean isEmpty(int text){
        if (text==0){
            return true;
        }
        return false;
    }


    //open website of item..
    public void OpenWeb(String url){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    //share item func...
    private void shareImage() {
        try {
            String s=NameOfItem.getText().toString()+"\n"+ PriceOfItem.getText().toString();
            File file= new File(getExternalCacheDir(),"sample.png");
            Intent intent=new Intent(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_TEXT,s);
            intent.putExtra(Intent.EXTRA_SUBJECT, Uri.fromFile(file));
            intent.setType("image/png");
            startActivity(Intent.createChooser(intent,"share via"));
        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case WRITE_EXTERNAL_STORAGE_CODE:{
                if (grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    //saveImage();
                }
                else {
                    Toast.makeText(this, "enable permission to save image", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

}

