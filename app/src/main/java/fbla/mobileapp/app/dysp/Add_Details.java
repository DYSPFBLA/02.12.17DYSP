package fbla.mobileapp.app.dysp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class Add_Details extends AppCompatActivity {
    public EditText title_text4;
    public Bitmap b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__details);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Users");
        final DatabaseReference commentRef = database.getReference("Comments");
        final DatabaseReference itemRef = database.getReference("MasterItems");
        final FirebaseAuth auth = FirebaseAuth.getInstance();
       LinearLayout linear = (LinearLayout) findViewById(R.id.linear);
        final float[] num_of_stars = new float[1];
        final String[] num = new String[1];
        final RatingBar rate = new RatingBar(this);
        final TextView additional_text = new TextView(this);
        TextView additiona_text2 = new TextView(this);TextView additional_text3 = new TextView(this);TextView additional_text4 = new TextView(this);
        TextView additional_text5 = new TextView(this);TextView additional_text6 = new TextView(this);final TextView results = new TextView(this);
        final EditText title_text = new EditText(this);final EditText title_text2 = new EditText(this);final EditText title_text3 = new EditText(this);
        title_text4 = new EditText(this);
        Button submit = new Button(this);
        submit.setText("Submit Item");
        if(getIntent().hasExtra("byteArray")) {
             b = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("byteArray"),0,getIntent()
                            .getByteArrayExtra("byteArray").length);
        }
        title_text3.setHint("Any additional comments?");
        title_text4.setHint("$");
        additional_text.setText("Title:");
        additiona_text2.setText("Description:");
        additional_text3.setText("Category: ");
        additional_text4.setText("Rating:");
        additional_text5.setText("Additional Comments:");
        additional_text6.setText("Suggested Price:");
        results.setVisibility(View.INVISIBLE);
        final Spinner category_choose = new Spinner(this);
        final ArrayList<String> spinnerArray = new ArrayList<String>();
        spinnerArray.add("Select A Category");spinnerArray.add("Baby Items");spinnerArray.add("Books and Papers");spinnerArray.add("Clothing");spinnerArray.add("Electronics/Entertainment");spinnerArray.add("Furniture/Craft or Self-Made Items");spinnerArray.add("Health/Beauty");spinnerArray.add("Holidays, Parties, Special Occasions");spinnerArray.add("Home Items/Tools");spinnerArray.add("Jewelry/Purses/Other Accessories");spinnerArray.add("Shoes");spinnerArray.add("Sports");spinnerArray.add("Toys");spinnerArray.add("Vehicle/Automotive");spinnerArray.add("Other Category");
        rate.setNumStars(5);
        rate.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, spinnerArray) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                return v;
            }
            public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                View v =super.getDropDownView(position, convertView, parent);
                return v;
            }
        };
        category_choose.setAdapter(adapter);
        final ImageView confirm_img = new ImageView(this);
        confirm_img.setMinimumHeight(1000);
        try {
            final Drawable c = new BitmapDrawable(getResources(), b);
            confirm_img.setImageDrawable(c);
            linear.addView(confirm_img);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.topMargin = 30;
            additional_text.setLayoutParams(layoutParams);
            linear.addView(additional_text);linear.addView(title_text);linear.addView(additional_text3);linear.addView(category_choose);
            linear.addView(additiona_text2);linear.addView(title_text2);linear.addView(additional_text4);linear.addView(rate);
            linear.addView(additional_text5);linear.addView(title_text3);
            linear.addView(additional_text6);title_text4.setInputType(InputType.TYPE_CLASS_NUMBER);
            title_text4.addTextChangedListener(tw);
            linear.addView(title_text4);
            linear.addView(submit);
            linear.addView(results);
            rate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    num_of_stars[0] = rating;
                    num[0] = String.valueOf(rating);
                }
            });
            if(getIntent().hasExtra("modifieditem")) {
                String itemname = getIntent().getStringExtra("modifieditem");
                itemRef.child(itemname).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final Item item = dataSnapshot.getValue(Item.class);
                        itemRef.removeEventListener(this);
                        title_text.setText(item.getTitle());
                        category_choose.setSelection(spinnerArray.indexOf(item.getCategory()));
                        title_text2.setText(item.getDescription());
                        String star = item.getRating(); rate.setRating(Float.parseFloat(star));
                        title_text3.setText(item.getAdditionalComments());
                        title_text4.setText(item.getPrice());
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(category_choose.getSelectedItem().toString() != "Select A Category" ) {
                        final Item new_Object = new Item();
                        new_Object.setTitle(title_text.getText().toString().replace('.', ' '));
                        new_Object.setDescription(title_text2.getText().toString());
                        new_Object.setPrice(title_text4.getText().toString());
                        new_Object.setRating(num[0]);
                        new_Object.setCategory(category_choose.getSelectedItem().toString());
                        new_Object.setBought(false);
                        new_Object.setCreatedBy(auth.getCurrentUser().getUid());
                        new_Object.setAdditionalComments(title_text3.getText().toString());
                        new_Object.setObjectNumber((long)0);
                        new_Object.setOwnedBy(auth.getCurrentUser().getUid());
                        ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
                        b.compress(Bitmap.CompressFormat.JPEG, 100, bYtE);
                        byte[] byteArray = bYtE.toByteArray();
                        String imageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        new_Object.setPic(imageFile);
                        myRef.child(auth.getCurrentUser().getUid()).child("Location").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String location = dataSnapshot.getValue(String.class);
                                new_Object.setLocation(location);
                                myRef.removeEventListener(this);
                                myRef.child(auth.getCurrentUser().getUid()).child("ItemsSent").child(new_Object.getTitle()).setValue(new_Object);
                                itemRef.child(new_Object.getTitle()).setValue(new_Object);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                        commentRef.child(new_Object.getTitle()).setValue("ItemAdded");
                        Toast.makeText(Add_Details.this, "Item Submitted!", Toast.LENGTH_SHORT).show();
                        Intent homescreen = new Intent(Add_Details.this, NavigationActivity.class);
                        startActivity(homescreen);
                    }
                    else{
                        Toast.makeText(Add_Details.this, "Please select a category", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        catch (DatabaseException e){
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    TextWatcher tw = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void afterTextChanged(Editable s) {
            if (!s.toString().matches("^\\$(\\d{1,3}(\\,\\d{3})*|(\\d+))(\\.\\d{2})?$")) {
                String userInput = "" + s.toString().replaceAll("[^\\d]", "");
                StringBuilder cashAmountBuilder = new StringBuilder(userInput);

                while (cashAmountBuilder.length() > 3 && cashAmountBuilder.charAt(0) == '0') {
                    cashAmountBuilder.deleteCharAt(0);
                }
                while (cashAmountBuilder.length() < 3) {
                    cashAmountBuilder.insert(0, '0');
                }
                cashAmountBuilder.insert(cashAmountBuilder.length() - 2, '.');
                title_text4.removeTextChangedListener(this);
                title_text4.setText(cashAmountBuilder.toString());
                title_text4.setTextKeepState("$" + cashAmountBuilder.toString());
                Selection.setSelection(title_text4.getText(), cashAmountBuilder.toString().length() + 1);
                title_text4.addTextChangedListener(this);
            }
        }
    };
}
