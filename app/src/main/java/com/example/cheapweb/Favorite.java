package com.example.cheapweb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Favorite extends AppCompatActivity  implements FavoriteDialoge.FavoriteDialogeListener{

    RecyclerView mrecycler_view;
    FirebaseDatabase mfirebaseDatabase, mfirebaseDatabaseFavorite;
    DatabaseReference mRef, mRefFavorite, mRefItemFav;
    private FirebaseAuth mAuth;
    TextView item_Name, item_Price, emptytxt;
    ImageView item_Image;
    String itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Favorite");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        mrecycler_view=findViewById(R.id.recycler_view);
        mrecycler_view.setHasFixedSize(true);

        mrecycler_view.setLayoutManager(new LinearLayoutManager(this));
        mAuth = FirebaseAuth.getInstance();
        mfirebaseDatabase= FirebaseDatabase.getInstance();
        mRef=mfirebaseDatabase.getReference("items");
        mfirebaseDatabaseFavorite= FirebaseDatabase.getInstance();
        mRefFavorite=mfirebaseDatabase.getReference("FavoritesId");
        mRefItemFav=mfirebaseDatabase.getReference("FavItem");
        item_Name=findViewById(R.id.ItNametxt);
        item_Price=findViewById(R.id.ItPricetxt);
        item_Image=findViewById(R.id.Itimage);
        emptytxt=findViewById(R.id.empty_view);



    }

   //load data into recycler view
    @Override
    protected void onStart() {
        super.onStart();
        final String usId=mAuth.getCurrentUser().getUid();
        mRefItemFav.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(usId).hasChildren())
                    emptytxt.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerAdapter<Model, ViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Model, ViewHolder>(Model.class
                ,R.layout.favorite_item, ViewHolder.class, mRefItemFav.child(usId)) {
            @Override
            protected void populateViewHolder(ViewHolder viewHolder, Model model, int i) {
                viewHolder.setDetailsFavorite(getApplicationContext(), model.getItemName(), model.getItemPrice(), model.getImagePath());
            }

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                ViewHolder viewHolder=super.onCreateViewHolder(parent,viewType);
                viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        itemId= getItem(position).getIdItem();
                        Intent intent=new Intent(Favorite.this,ItemActivity.class);
                        intent.putExtra("itemid", itemId);
                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        itemId= getItem(position).getIdItem();
                        openDialog();
                    }
                });
                return viewHolder;
            }
        };

        //set adapter to recycler view
        mrecycler_view.setAdapter(firebaseRecyclerAdapter);
    }


    //if the user click "yes" to remove the item from favorite
    @Override
    public void onYesClickFav() {
        deleteItem(itemId);
    }

    //ask if the user want to remove the item from favorite
    public void  openDialog(){
        FavoriteDialoge dialog=new FavoriteDialoge();
        dialog.show(getSupportFragmentManager(), "Dialoge");
    }

    //remove the item value from the favorite reference in the database
    private void deleteItem(String id){
        DatabaseReference Favitems= FirebaseDatabase.getInstance().getReference("FavItem").child(mAuth.getCurrentUser().getUid()).child(id);
        Favitems.removeValue();

        //check if the user id in the reference do not have children..
        mRefItemFav.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child(mAuth.getCurrentUser().getUid()).exists())
                    emptytxt.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
