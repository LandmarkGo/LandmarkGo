package com.OPSC.OPSC7312;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Favorites extends AppCompatActivity
{

    String uid;
    DatabaseReference dbRef, dbItemRef;
    FirebaseUser user;
    private FirebaseDatabase rootNode;
    String TAG = "Favorites";
    ListView lsOutput;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        getSupportActionBar().hide();

        dbRef = FirebaseDatabase.getInstance().getReference().child("Favorite");
        rootNode = FirebaseDatabase.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        ArrayList<String> list = new ArrayList<>();
        ArrayList<Double> listLat = new ArrayList<>();
        ArrayList<Double> listLong = new ArrayList<>();
        ArrayList<Double> rating = new ArrayList<>();
        ArrayList<String> favID = new ArrayList<>();
        //readFavoritesDatabase();


        lsOutput = findViewById(R.id.lsOutput);

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.listviewfavorites, R.id.CategoryNameListView, list);
        lsOutput.setAdapter(adapter);

        dbRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    FavoriteClass favoriteClass = ds.getValue(FavoriteClass.class);
                    if (favoriteClass.getUid().equals(uid))
                    {
                        list.add(favoriteClass.getAddress());
                        listLat.add(favoriteClass.getLatitude());
                        listLong.add(favoriteClass.getLongitude());
                        rating.add(favoriteClass.getRating());
                        favID.add(favoriteClass.getFavID());
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "onDataChange: " + favoriteClass.getLatitude() + " Intent" + favoriteClass.getLongitude() + " Intent" + favoriteClass.getAddress());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });

        lsOutput.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(Favorites.this, MapsActivity.class);

                intent.putExtra("Lat", listLat.get(position));
                intent.putExtra("Long", listLong.get(position));
                intent.putExtra("Rating", rating.get(position));
                intent.putExtra("ID", favID.get(position));
                Log.d(TAG, "onItemClick: " + listLat.get(position));
                startActivity(intent);
            }

        });


    }
}