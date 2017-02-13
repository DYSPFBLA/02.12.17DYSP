package fbla.mobileapp.app.dysp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.share.widget.ShareDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class List_Item extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list__item);
        FacebookSdk.sdkInitialize(getApplicationContext());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference itemRef = database.getReference("MasterItems");
        final ListView listView = (ListView)findViewById(R.id.list_view);
        final EditText editsearch = (EditText)findViewById(R.id.searchItem);
        final ArrayList<ModifiedDisplayItems> arrayListItems = new ArrayList<ModifiedDisplayItems>();
        final CustomListAdapter[] adapter = new CustomListAdapter[1];

        final ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar6);
        final TextView NoItems = (TextView)findViewById(R.id.NoItems);
        NoItems.setVisibility(View.INVISIBLE);
        itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Item> ObjectList = new ArrayList<Item>();
                ArrayList<String> ObjectTitles = new ArrayList<String>();
                for(DataSnapshot dsp : dataSnapshot.getChildren()){
                    Item tempItem = dsp.getValue(Item.class);
                    if(tempItem.getBought() != true)
                        ObjectList.add(tempItem);
                }
                itemRef.removeEventListener(this);
                for(Item item : ObjectList){
                    String title = item.getTitle().toString();
                    ObjectTitles.add(title);
                    String image64 = item.getPic().toString();
                    String price = item.getPrice().toString();
                    String owner = item.getOwnedBy().toString();
                    ModifiedDisplayItems tempitemmod = new ModifiedDisplayItems(title, image64, price, owner);
                    arrayListItems.add(tempitemmod);
                }
                if(ObjectTitles.isEmpty()) {
                    NoItems.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
                Activity act = List_Item.this;
                adapter[0] = new CustomListAdapter(getApplicationContext(), arrayListItems, act);
                progressBar.setVisibility(View.INVISIBLE);
                listView.setAdapter(adapter[0]);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        editsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                adapter[0].filter(text);
            }
        });
    }
}
