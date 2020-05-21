package com.example.cheapweb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ItemResult extends AppCompatActivity {

    RecyclerView mrecyclerView;
    FirebaseDatabase mfirebaseDatabase;
    DatabaseReference mRef, mRefSearch;
    private FirebaseAuth mAuth;
    int x=0, q=0;
    LinearLayoutManager mLayoutManager;
    SharedPreferences mSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_result);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Items Result");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);


        mAuth = FirebaseAuth.getInstance();
        mrecyclerView = findViewById(R.id.recyclerView);
        mrecyclerView.setHasFixedSize(true);
        mrecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mRefSearch = mfirebaseDatabase.getReference("ItemResult");
        mRef = mfirebaseDatabase.getReference("items");
        String txtItem = getIntent().getStringExtra("Searchtext");
        DatabaseReference Results = FirebaseDatabase.getInstance().getReference("ItemResult");
        mLayoutManager = new LinearLayoutManager(this);
        mSharedPref = getSharedPreferences("SortSettings", MODE_PRIVATE);
        String mSorting = mSharedPref.getString("Sort", "Low Price");

        if (mSorting.equals("Low Price")) {
            mLayoutManager.setReverseLayout(false);
            mLayoutManager.setStackFromEnd(false);
            orderByLowPrice();
        } else if (mSorting.equals("High Price")) {
            mLayoutManager.setReverseLayout(true);
            mLayoutManager.setStackFromEnd(true);
            orderByHighPrice();
        }
        Results.removeValue();
        if (txtItem.equals("accessories")||txtItem.equals("electronics")||txtItem.equals("ClothesShows")){
            q=1;
            CategorySearch(txtItem);
        }
        else if (txtItem.equals("allCategories")){
            AllCategories();
        }
        else
            firebaseSearch(txtItem);

    }

    private void CategorySearch(final String categoryName){
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    if (ds.getValue(Model.class).getCategory().equals(categoryName)){
                        mRefSearch.child(mAuth.getCurrentUser().getUid()).child(ds.getValue(Model.class).getIdItem())
                                .setValue(ds.getValue(Model.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query query = mRef.orderByChild("category").equalTo(categoryName);
        FirebaseRecyclerAdapter<Model, ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Model, ViewHolder>(Model.class
                , R.layout.item_show, ViewHolder.class, query) {
            @Override
            protected void populateViewHolder(ViewHolder viewHolder, Model model, int i) {
                viewHolder.setDetails(getApplicationContext(), model.getItemName(), model.getItemPrice(), model.getImagePath());
            }

            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String idItem = getItem(position).getIdItem();
                        Intent intent = new Intent(ItemResult.this, ItemActivity.class);
                        intent.putExtra("itemid", idItem);
                        startActivity(intent);


                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                });
                return viewHolder;
            }
        };
        //set adapter to recycler view
        mrecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    //Search about the item name or items that contains this name
    private void firebaseSearch(final String SearchText){

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    if (snapshot.getValue(Model.class).getItemName().contains(SearchText)){
                        mRefSearch.child(mAuth.getCurrentUser().getUid()).child(snapshot.getValue(Model.class).getIdItem())
                                .setValue(snapshot.getValue(Model.class));
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Query query=mRefSearch.child(mAuth.getCurrentUser().getUid()).orderByChild("itemName");
        FirebaseRecyclerAdapter<Model, ViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Model, ViewHolder>(Model.class
                ,R.layout.item_show, ViewHolder.class, query) {
            @Override
            protected void populateViewHolder(ViewHolder viewHolder, Model model, int i) {
                viewHolder.setDetails(getApplicationContext(), model.getItemName(), model.getItemPrice(), model.getImagePath());
            }
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                ViewHolder viewHolder=super.onCreateViewHolder(parent,viewType);
                viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String idItem=getItem(position).getIdItem();
                        Intent intent=new Intent(ItemResult.this,ItemActivity.class);
                        intent.putExtra("itemid", idItem);
                        startActivity(intent);


                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                });
                return viewHolder;
            }



        };

        //set adapter to recycler view
        mrecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.sort_items, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.sort_items){
            showSortDialoge();
        }
        return false;
    }

    //the sort click will appear
    private void showSortDialoge() {
        String[] sortOption={"Low Price", "High Price"};
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Sort by").setIcon(R.drawable.sort_icon).setItems(sortOption, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //which=0 means low price
                //which=1 means high price
                if (which == 0) {
                    //order by low price
                    SharedPreferences.Editor editor=mSharedPref.edit();
                    editor.putString("Sort","Low Price");
                    editor.apply();
                    recreate();
                }
                if (which == 1){
                    //order by high price
                    SharedPreferences.Editor editor=mSharedPref.edit();
                    editor.putString("Sort","High Price");
                    editor.apply();
                    recreate();
                }
            }
        });
        builder.show();


    }

    //this func order the result items by the low price
    private void orderByLowPrice() {
        mRefSearch.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    x = (int) dataSnapshot.getChildrenCount();
                    Query qlow = mRefSearch.child(mAuth.getCurrentUser().getUid()).orderByChild("priceInLink1").limitToFirst(x);
                    FirebaseRecyclerAdapter<Model, ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Model, ViewHolder>(Model.class
                            , R.layout.item_show, ViewHolder.class, qlow) {
                        @Override
                        protected void populateViewHolder(ViewHolder viewHolder, Model model, int i) {
                            viewHolder.setDetails(getApplicationContext(), model.getItemName(), model.getItemPrice(), model.getImagePath());
                        }

                        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                            ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                            viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    String idItem = getItem(position).getIdItem();
                                    Intent intent = new Intent(ItemResult.this, ItemActivity.class);
                                    intent.putExtra("itemid", idItem);
                                    startActivity(intent);


                                }

                                @Override
                                public void onItemLongClick(View view, int position) {

                                }
                            });
                            return viewHolder;
                        }


                    };

                    //set adapter to recycler view
                    mrecyclerView.setAdapter(firebaseRecyclerAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //this func order the result items by the high price
    private void orderByHighPrice() {
        mRefSearch.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    x = (int) dataSnapshot.getChildrenCount();
                    Query qhigh = mRefSearch.child(mAuth.getCurrentUser().getUid()).orderByChild("priceInLink1").limitToFirst(x);
                    mLayoutManager.setReverseLayout(true);
                    mLayoutManager.setStackFromEnd(true);
                    mrecyclerView.setLayoutManager(mLayoutManager);
                    FirebaseRecyclerAdapter<Model, ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Model, ViewHolder>(Model.class
                            , R.layout.item_show, ViewHolder.class, qhigh) {
                        @Override
                        protected void populateViewHolder(ViewHolder viewHolder, Model model, int i) {
                            viewHolder.setDetails(getApplicationContext(), model.getItemName(), model.getItemPrice(), model.getImagePath());
                        }

                        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                            ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                            viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    String idItem = getItem(position).getIdItem();
                                    Intent intent = new Intent(ItemResult.this, ItemActivity.class);
                                    intent.putExtra("itemid", idItem);
                                    startActivity(intent);


                                }

                                @Override
                                public void onItemLongClick(View view, int position) {

                                }
                            });
                            return viewHolder;
                        }


                    };

                    //set adapter to recycler view
                    mrecyclerView.setAdapter(firebaseRecyclerAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    protected void AllCategories() {
        //super.onStart();
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    mRefSearch.child(mAuth.getCurrentUser().getUid()).child(dataSnapshot1.getValue(Model.class).getIdItem())
                            .setValue(dataSnapshot1.getValue(Model.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerAdapter<Model, ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Model, ViewHolder>(Model.class
                , R.layout.item_show, ViewHolder.class, mRef) {
            @Override
            protected void populateViewHolder(ViewHolder viewHolder, Model model, int i) {
                viewHolder.setDetails(getApplicationContext(), model.getItemName(), model.getItemPrice(), model.getImagePath());
            }

            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String idItem = getItem(position).getIdItem();
                        Intent intent = new Intent(ItemResult.this, ItemActivity.class);
                        intent.putExtra("itemid", idItem);
                        startActivity(intent);


                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                });
                return viewHolder;
            }


        };

        //set adapter to recycler view
        mrecyclerView.setAdapter(firebaseRecyclerAdapter);
    }


    //check if there is no result items and invisible the sort click
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        final MenuItem sort = menu.findItem(R.id.sort_items);
        mRefSearch.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()||q==1)
                    sort.setVisible(true);
                else
                    sort.setVisible(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return true;
    }

}
