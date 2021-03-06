package fbla.mobileapp.app.dysp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ViewItem extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Users");
        final DatabaseReference itemRef = database.getReference("MasterItems");
        final String itemValue = getIntent().getExtras().getString("itemname");

        final LinearLayout linear = (LinearLayout) findViewById(R.id.linear);
        final LinearLayout linear2 = new LinearLayout(this);
        LinearLayout.LayoutParams llparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        linear2.setLayoutParams(llparams);
        linear2.setOrientation(LinearLayout.HORIZONTAL);
        final TextView displayTitle = new TextView(this);
        final ImageView displayImage = new ImageView(this);
        final TextView diplayDescription = new TextView(this);
        final RatingBar displayRating = new RatingBar(this);
        final TextView displayPrice = new TextView(this);
        final TextView displayOwner = new TextView(this);
        final TextView displayLocation = new TextView(this);
        final TextView displayCategory = new TextView(this);
        final Button displayComments = new Button(this);
        final Button buyItem = new Button(this);
        final Button addtoInterested = new Button(this);
        displayImage.setMinimumHeight(600); //1000 for App Store
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        displayRating.setLayoutParams(params);
        displayRating.setNumStars(5);
        itemRef.child(itemValue).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    final Item item = dataSnapshot.getValue(Item.class);
                    itemRef.removeEventListener(this);
                    byte[] decodedString = Base64.decode(item.getPic(), Base64.DEFAULT);
                    Bitmap bit = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    displayTitle.setText(item.getTitle()); displayTitle.setGravity(Gravity.CENTER_HORIZONTAL); displayTitle.setTextSize(25); displayTitle.setPadding(10, 10, 10, 10); displayTitle.setTypeface(null,Typeface.BOLD);displayTitle.setTextColor(Color.BLACK);
                    displayImage.setImageBitmap(bit);
                    diplayDescription.setText(item.getDescription() + "."); diplayDescription.setGravity(Gravity.LEFT); diplayDescription.setTextSize(15); diplayDescription.setPadding(0, 10, 0, 60); diplayDescription.setTextColor(Color.BLACK);
                    String star = item.getRating();
                    displayRating.setRating(Float.parseFloat(star));
                    displayRating.setIsIndicator(true);
                    displayPrice.setText("Price: " + item.getPrice()); displayPrice.setTextSize(18); displayPrice.setPadding(10,10, 0, 30); displayLocation.setGravity(Gravity.LEFT); displayPrice.setTextColor(Color.BLACK); displayLocation.setTextColor(Color.BLACK);
                    final DatabaseReference owner = myRef.child(item.getOwnedBy()).child("DispayName");
                    owner.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            SpannableString content = new SpannableString("Owner: " + dataSnapshot.getValue(String.class));
                            displayOwner.setTextColor(Color.parseColor("#0000FF")); displayOwner.setTypeface(null, Typeface.BOLD); content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                            displayOwner.setText(content);
                            displayOwner.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent userPage = new Intent(ViewItem.this, UserAccountActivity.class);
                                    userPage.putExtra("username", item.getOwnedBy());
                                    startActivity(userPage);
                                }
                            });
                            displayOwner.setPadding(0,10, 0, 30); displayOwner.setTextSize(16); displayLocation.setGravity(Gravity.LEFT);
                            owner.removeEventListener(this);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    displayLocation.setText("Location: " + item.getLocation());displayLocation.setGravity(Gravity.LEFT); displayLocation.setTextSize(16); displayLocation.setPadding(0, 10, 0, 30); displayCategory.setTextColor(Color.BLACK);
                    displayCategory.setText("Category: " + item.getCategory());displayCategory.setGravity(Gravity.LEFT); displayCategory.setTextSize(16); displayCategory.setPadding(0, 10, 0, 20); //120 for App Store. 85 for Linear Layout for Home Screen
                    displayComments.setText("View Comments");
                    itemRef.removeEventListener(this);
                    buyItem.setText("Buy Item");
                    addtoInterested.setText("Save Item");
                    linear.addView(displayTitle);
                    linear.addView(displayImage);
                    linear.addView(diplayDescription);
                    linear.addView(displayRating);
                    linear.addView(displayPrice);
                    linear.addView(displayOwner);
                    linear.addView(displayLocation);
                    linear.addView(displayCategory);
                    linear.addView(linear2);
                    linear2.addView(displayComments);
                    linear2.addView(buyItem);
                    linear2.addView(addtoInterested);
                    if(item.getOwnedBy().equals(auth.getCurrentUser().getUid())){
                        buyItem.setEnabled(false);
                        addtoInterested.setEnabled(false);
                        displayOwner.setTextSize(20);
                        displayOwner.setTypeface(null, Typeface.BOLD);
                    }
                    addtoInterested.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myRef.child(auth.getCurrentUser().getUid()).child("Interested In").child(item.getTitle()).setValue(item);
                            Toast.makeText(ViewItem.this, "Item added to Interests!", Toast.LENGTH_SHORT).show();
                            //Intent backto = new Intent(ViewItem.this, NavigationActivity.class);
                            //startActivity(backto);
                            //addtoInterested.setOnClickListener(null);
                        }
                    });
                    buyItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           final EditText input = new EditText(ViewItem.this);
                            input.setHint("Enter your price here");
                            input.setInputType(InputType.TYPE_CLASS_NUMBER);
                            input.addTextChangedListener(new TextWatcher() {
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

                                        input.removeTextChangedListener(this);
                                        input.setText(cashAmountBuilder.toString());

                                        input.setTextKeepState("$" + cashAmountBuilder.toString());
                                        Selection.setSelection(input.getText(), cashAmountBuilder.toString().length() + 1);

                                        input.addTextChangedListener(this);
                                    }
                                }
                            });
                            AlertDialog.Builder builder = new AlertDialog.Builder(ViewItem.this);
                            builder.setTitle("Buy Item").setMessage("The seller has indicated this price: " + "\n" + item.getPrice()).setView(input).setPositiveButton("Post Price", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (TextUtils.isEmpty(input.getText())) {
                                        Toast.makeText(ViewItem.this, "Please Enter A Monetary Value:", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    final int posted_pricenum = Integer.valueOf(item.getPrice().replace("$", "").replace(".", "")); //Price set by seller.
                                    int price_postednum = Integer.valueOf(input.getText().toString().replace("$", "").replace(".", "")); //Price offered by user.
                                    if(price_postednum >= posted_pricenum) {
                                        Toast.makeText(ViewItem.this, "Item Bought", Toast.LENGTH_SHORT).show();
                                        myRef.child(item.getOwnedBy()).child("ItemsSold").child(itemValue).setValue(item);
                                        myRef.child(item.getOwnedBy()).child("ItemsSent").child(itemValue).removeValue();
                                        itemRef.child(item.getTitle()).child("bought").setValue(true);
                                        myRef.child(auth.getCurrentUser().getUid()).child("ItemsBought").child(item.getTitle()).setValue(item);
                                        itemRef.child(item.getTitle()).child("ownedBy").setValue(auth.getCurrentUser().getUid());
                                        Intent home = new Intent(ViewItem.this, NavigationActivity.class);
                                        startActivity(home);
                                        dialog.dismiss();
                                    }
                                    else{
                                        Toast.makeText(ViewItem.this, "Offer Sent", Toast.LENGTH_SHORT).show();
                                        myRef.child(item.getOwnedBy()).child("ItemOffers").child(item.getTitle()).setValue(item.getTitle() + "/" + input.getText().toString() + "~" + auth.getCurrentUser().getUid());
                                        Intent home = new Intent(ViewItem.this, NavigationActivity.class);
                                        startActivity(home);
                                        dialog.dismiss();
                                    }
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    dialog.dismiss();
                                }
                            });
                            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    dialog.dismiss();
                                    dialog.cancel();
                                }
                            });
                            builder.show();
                        }
                    });
                    displayComments.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                final Intent viewComment = new Intent(ViewItem.this, ViewComments.class);
                                viewComment.putExtra("itemname", itemValue);
                                String ownedBy = item.getOwnedBy();
                                myRef.child(ownedBy).child("DispayName").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String owner = dataSnapshot.getValue(String.class);
                                        myRef.removeEventListener(this);
                                        viewComment.putExtra("ItemOwner", owner);
                                        startActivity(viewComment);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }catch (Exception e){
                                Toast.makeText(ViewItem.this, e.getCause().toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
