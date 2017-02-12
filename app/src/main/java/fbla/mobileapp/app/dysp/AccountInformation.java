package fbla.mobileapp.app.dysp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AccountInformation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_information);
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Users");
        final DatabaseReference itemRef = database.getReference("MasterItems");
        FacebookSdk.sdkInitialize(getApplicationContext());
        final ShareDialog shareDialog = new ShareDialog(this);
        final DatabaseReference commentRef = database.getReference("Comments");
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
       // Typeface custom = Typeface.createFromAsset(getAssets(), "fonts/lettergothic.ttf");
        final TextView AboutYou = (TextView)findViewById(R.id.about_you);  AboutYou.setTextSize(25);
        TextView displayName = (TextView)findViewById(R.id.displayName);  displayName.setText("Name: "+ auth.getCurrentUser().getDisplayName());
        TextView displayEmail = (TextView)findViewById(R.id.displayEmail);  displayEmail.setText("Email: " +auth.getCurrentUser().getEmail()); displayEmail.setTextSize(16);
        final TextView displayObject = (TextView)findViewById(R.id.displayObjects);
        final ImageView dysp = (ImageView)findViewById(R.id.dyspimage);
        myRef.child(auth.getCurrentUser().getUid()).child("ItemsSent").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int num_items = (int)dataSnapshot.getChildrenCount();
                displayObject.setText("Objects Posted: " + String.valueOf(num_items));
                myRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Button changeEmail = (Button)findViewById(R.id.changeEmail);

        final EditText input = new EditText(AccountInformation.this);
        input.setHint("Enter email");
        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(AccountInformation.this);
                builder1.setTitle("Change Email");
                input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                builder1.setView(input);
                builder1.setPositiveButton("Change Email", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        String emailaddress = input.getText().toString();
                        user.updateEmail(emailaddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialog.dismiss();
                                Intent refreshPage = new Intent(AccountInformation.this, AccountInformation.class);
                                Toast.makeText(AccountInformation.this, "Email Updated!", Toast.LENGTH_SHORT).show();
                                startActivity(refreshPage);
                            }
                        });
                    }
                });
                builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        dialog.dismiss();
                        Intent refreshPage = new Intent(AccountInformation.this, AccountInformation.class);
                        startActivity(refreshPage);
                    }
                });
                builder1.show();
            }
        });
        Button changeName = (Button)findViewById(R.id.changeName);
        final EditText input2 = new EditText(AccountInformation.this);
        input2.setHint("Enter Full Name");
        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(AccountInformation.this);
                builder1.setTitle("Change Name");
                builder1.setView(input2);
                builder1.setPositiveButton("Change Name", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        String name = input2.getText().toString();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Intent refreshPage = new Intent(AccountInformation.this, AccountInformation.class);
                                            Toast.makeText(AccountInformation.this, "Name Updated!", Toast.LENGTH_SHORT).show();
                                            startActivity(refreshPage);
                                        }
                                    }
                                });
                    }
                });
                builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        dialog.dismiss();
                        Intent refreshPage = new Intent(AccountInformation.this, AccountInformation.class);
                        startActivity(refreshPage);
                    }
                });
                builder1.show();

            }
        });
        Button deleteItems = (Button)findViewById(R.id.deleteItems);
        try {
            deleteItems.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder builderSingle = new AlertDialog.Builder(AccountInformation.this);
                    builderSingle.setTitle("Delete Item");
                    myRef.child(auth.getCurrentUser().getUid()).child("ItemsSent").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {
                            ArrayList<Item> ObjectList = new ArrayList<Item>();
                            ArrayList<String> ObjectTitles = new ArrayList<String>();
                            ObjectTitles.add("Select an Item to Delete:");
                            for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                Item tempItem = dsp.getValue(Item.class);
                                ObjectList.add(tempItem);
                            }
                            for (Item item : ObjectList) {
                                String title = item.getTitle().toString();
                                //Toast.makeText(List_Item.this, title, Toast.LENGTH_SHORT).show();
                                ObjectTitles.add(title);
                            }
                            myRef.removeEventListener(this);
                            if (ObjectTitles.isEmpty()) {
                                Toast.makeText(AccountInformation.this, "No Items To Delete!", Toast.LENGTH_SHORT).show();
                                Intent refreshPage = new Intent(AccountInformation.this, NavigationView.class);
                                startActivity(refreshPage);
                            }
                            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(AccountInformation.this, android.R.layout.simple_list_item_1, ObjectTitles);
                            builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent refreshPage = new Intent(AccountInformation.this, AccountInformation.class);
                                    startActivity(refreshPage);
                                }
                            });
                            builderSingle.setAdapter(adapter, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String strName = adapter.getItem(which);
                                    if(strName.equals("Select an Item to Delete:")){
                                        Toast.makeText(AccountInformation.this, "That Is Not An Item", Toast.LENGTH_SHORT).show();
                                        Intent home12 = new Intent(AccountInformation.this, AccountInformation.class);
                                        startActivity(home12);
                                    }
                                    else {
                                        myRef.child(auth.getCurrentUser().getUid()).child("ItemsSent").child(strName).removeValue();
                                        itemRef.child(strName).removeValue();
                                        commentRef.child(strName).removeValue();
                                        Intent refreshPage = new Intent(AccountInformation.this, AccountInformation.class);
                                        Toast.makeText(AccountInformation.this, "Item Removed!", Toast.LENGTH_SHORT).show();
                                        startActivity(refreshPage);
                                    }
                                }
                            });
                            builderSingle.show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });
        }catch (Exception e){
            Toast.makeText(this, e.getStackTrace().toString(), Toast.LENGTH_LONG).show();
        }

        Button viewOffers = (Button)findViewById(R.id.ViewOffers);
        viewOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> OfferTitles = new ArrayList<String>();
                final ArrayList<String> FullTitles = new ArrayList<String>();
                OfferTitles.add("Select Item Offer:");
                final AlertDialog.Builder builderSingle = new AlertDialog.Builder(AccountInformation.this);
                builderSingle.setTitle("View Offers");
                myRef.child(auth.getCurrentUser().getUid()).child("ItemOffers").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                String tempOffer = dsp.getValue(String.class);
                                String title = tempOffer.substring(0, tempOffer.indexOf("/"));
                                FullTitles.add(tempOffer);
                                OfferTitles.add(title);
                            }
                        myRef.removeEventListener(this);
                        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(AccountInformation.this, android.R.layout.simple_list_item_1, OfferTitles);
                        final ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(AccountInformation.this, android.R.layout.simple_list_item_1, FullTitles);
                        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent refreshPage = new Intent(AccountInformation.this, AccountInformation.class);
                                startActivity(refreshPage);
                            }
                        });
                        builderSingle.setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String strName = adapter.getItem(which);
                                if(strName.equals("Select Item Offer:")){
                                    Toast.makeText(AccountInformation.this, "That Is Not An Offer", Toast.LENGTH_SHORT).show();
                                    Intent home12 = new Intent(AccountInformation.this, AccountInformation.class);
                                    startActivity(home12);
                                }
                                else{
                                    String full = adapter2.getItem(which-1);
                                    final String item_name = strName;
                                    final String item_price = full.substring(full.indexOf("/") + 1, full.indexOf("~"));
                                    final String user = full.substring(full.indexOf("~") + 1);
                                    myRef.child(user).child("DispayName").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            String name = dataSnapshot.getValue(String.class);
                                            myRef.removeEventListener(this);
                                            AlertDialog.Builder builderInner = new AlertDialog.Builder(AccountInformation.this);
                                            builderInner.setMessage(name + " has offered to buy " + item_name + " for the price of:" + "\n" + item_price);
                                            builderInner.setTitle("Confirm Offer");
                                            builderInner.setPositiveButton("Confirm Sell", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    itemRef.child(item_name).removeValue();
                                                    commentRef.child(item_name).removeValue();
                                                    myRef.child(user).child("ItemsBought").child(item_name).setValue(item_name);
                                                    myRef.child(auth.getCurrentUser().getUid()).child("ItemOffers").child(item_name).removeValue();
                                                    Toast.makeText(AccountInformation.this, "Item sold!", Toast.LENGTH_SHORT).show();
                                                    Intent refreshPage = new Intent(AccountInformation.this, AccountInformation.class);
                                                    startActivity(refreshPage);
                                                    dialog.dismiss();
                                                }
                                            }).setNegativeButton("Reject Offer", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    myRef.child(auth.getCurrentUser().getUid()).child("ItemOffers").child(item_name).removeValue();
                                                    Intent refreshPage = new Intent(AccountInformation.this, AccountInformation.class);
                                                    Toast.makeText(AccountInformation.this, "Offer Rejected!", Toast.LENGTH_SHORT).show();
                                                    startActivity(refreshPage);
                                                    dialog.dismiss();
                                                }
                                            }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent refreshPage = new Intent(AccountInformation.this, AccountInformation.class);
                                                    startActivity(refreshPage);
                                                    dialog.dismiss();
                                                }
                                            });
                                            builderInner.show();
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                                }
                            }
                        });
                        builderSingle.show();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });
        if(getIntent().hasExtra("seeoffers")) {
            viewOffers.performClick();
        }


        Button ModifyItems = (Button)findViewById(R.id.ModifyItems);
        try {
            ModifyItems.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder builderSingle = new AlertDialog.Builder(AccountInformation.this);
                    builderSingle.setTitle("Modify Item");
                    myRef.child(auth.getCurrentUser().getUid()).child("ItemsSent").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {
                            final ArrayList<Item> ObjectList = new ArrayList<Item>();
                            ArrayList<String> ObjectTitles = new ArrayList<String>();
                            ObjectTitles.add("Select an Item to Modify:");
                            for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                Item tempItem = dsp.getValue(Item.class);
                                ObjectList.add(tempItem);
                            }
                            for (Item item : ObjectList) {
                                String title = item.getTitle().toString();
                                //Toast.makeText(List_Item.this, title, Toast.LENGTH_SHORT).show();
                                ObjectTitles.add(title);
                            }
                            myRef.removeEventListener(this);
                            if (ObjectTitles.isEmpty()) {
                                Toast.makeText(AccountInformation.this, "No Items To Modify!", Toast.LENGTH_SHORT).show();
                                Intent refreshPage = new Intent(AccountInformation.this, NavigationView.class);
                                startActivity(refreshPage);
                            }
                            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(AccountInformation.this, android.R.layout.simple_list_item_1, ObjectTitles);
                            builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent refreshPage = new Intent(AccountInformation.this, AccountInformation.class);
                                    startActivity(refreshPage);
                                }
                            });
                            builderSingle.setAdapter(adapter, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String strName = adapter.getItem(which);
                                    if(strName.equals("Select an Item to Modify:")){
                                        Toast.makeText(AccountInformation.this, "That Is Not An Item", Toast.LENGTH_SHORT).show();
                                        Intent home12 = new Intent(AccountInformation.this, AccountInformation.class);
                                        startActivity(home12);
                                    }
                                    else {
                                       // myRef.child(auth.getCurrentUser().getUid()).child("ItemsSent").child(strName).removeValue();
                                        //itemRef.child(strName).removeValue();
                                        //commentRef.child(strName).removeValue();

                                        Intent modifyItem = new Intent(AccountInformation.this, Add_Object.class);
                                        //Toast.makeText(AccountInformation.this, ObjectList.get(which-1).getTitle().toString(), Toast.LENGTH_SHORT).show();
                                        //Toast.makeText(AccountInformation.this, "Item Removed!", Toast.LENGTH_SHORT).show();
                                        modifyItem.putExtra("modifyitem", ObjectList.get(which-1).getTitle().toString());
                                        startActivity(modifyItem);
                                    }
                                }
                            });
                            builderSingle.show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });
        }catch (Exception e){
            Toast.makeText(this, e.getStackTrace().toString(), Toast.LENGTH_LONG).show();
        }

        Button InputCreditCard = (Button)findViewById(R.id.InputCreditCard);
        InputCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent creditcard = new Intent(AccountInformation.this, CreditCard.class);
                startActivity(creditcard);
            }
        });
        Button InputPaypal = (Button)findViewById(R.id.InputPaypal);
        InputPaypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uriUrl = Uri.parse("https://www.paypal.com/signin?returnUri=http%3A%2F%2Furi.paypal.com%2FWeb%2FWeb%2Fcgi-bin%2Fwebscr%3Fvia%3Dul&state=%3fcmd%3d_account");
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });

        Button ConnectToFacebook = (Button)findViewById(R.id.ConnectToFacebook);
        ConnectToFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentTitle("Join DYSP FBLA!")
                        .setContentDescription(
                                "I just joined DYSP FBLA! This app is inteded for FBLA users to raise funds to attend state and national FBLA Conferences! You can join me too by following this link!")
                        .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=fbla.mobileapp.app.dysp"))
                        .build();

                shareDialog.show(linkContent);
            }
        });
        Button signOut = (Button)findViewById(R.id.SignOut);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent home = new Intent(AccountInformation.this, SignIn.class);
                startActivity(home);
            }
        });
        dysp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(AccountInformation.this, NavigationActivity.class);
                startActivity(home);
            }
        });
    }
}
