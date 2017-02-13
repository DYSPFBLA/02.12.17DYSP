package fbla.mobileapp.app.dysp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class UserAccountActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView mImageView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference("Users");
    final FirebaseAuth auth = FirebaseAuth.getInstance();
    private String USERDISPLAY = auth.getCurrentUser().getUid();
    EditText donation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);
        FacebookSdk.sdkInitialize(getApplicationContext()); final ListView listView = (ListView) findViewById(R.id.listView);
        final ArrayList<ModifiedDisplayItems> arrayListItems = new ArrayList<ModifiedDisplayItems>(); final CustomListAdapter[] adapter = new CustomListAdapter[1]; final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar6);
        final TextView NoItems = (TextView) findViewById(R.id.NoItems); NoItems.setVisibility(View.INVISIBLE);
        ImageView individual_account_setting = (ImageView) findViewById(R.id.accountsettings);
        final TextView displayName = (TextView) findViewById(R.id.displayName); final TextView fblamember = (TextView) findViewById(R.id.fblamemberyear); final TextView locationtext = (TextView)findViewById(R.id.location);
        final TextView fundraisingfor = (TextView)findViewById(R.id.fundraisingfor); final TextView progressText = (TextView)findViewById(R.id.progressText); final ProgressBar pb = (ProgressBar)findViewById(R.id.progressBar9);
        final Button contact = (Button)findViewById(R.id.Contact);
        final Button DonateTo = (Button)findViewById(R.id.donateButton);
        mImageView = (ImageView) findViewById(R.id.profile_image);
        final DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        final Date today = Calendar.getInstance().getTime();
        donation = new EditText(UserAccountActivity.this);
        final Boolean[] member_year_set = {false};
        if (getIntent().hasExtra("username")) {
            USERDISPLAY = getIntent().getStringExtra("username");
        }

        myRef.child(USERDISPLAY).child("DispayName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String displayname = dataSnapshot.getValue(String.class);
                displayName.setText(displayname);
                displayName.setVisibility(View.VISIBLE);
                try{
                    String displayName = displayname.substring(0, displayname.indexOf(" "));
                    contact.setText("Contact " + displayName); DonateTo.setText("Donate To " + displayName);
                }catch (Exception e){
                    contact.setText("Contact "); DonateTo.setText("Donate To ");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        myRef.child(USERDISPLAY).child("MemberSince").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String yearmember = "";
                yearmember = dataSnapshot.getValue(String.class);
                myRef.removeEventListener(this);
                if(!dataSnapshot.exists()){
                    SpannableString content = new SpannableString("Not Set");
                   fblamember.setTypeface(null, Typeface.BOLD); content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    fblamember.setText(content);
                    member_year_set[0] = false;
                }
                else{
                    fblamember.setText(yearmember);
                    member_year_set[0] = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        myRef.child(USERDISPLAY).child("Location").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String location = dataSnapshot.getValue(String.class);
                locationtext.setText(location);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        myRef.child(USERDISPLAY).child("FundraisingFor").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String fundraising = ""; fundraising = dataSnapshot.getValue(String.class);
                if(!dataSnapshot.exists()){
                    return;
                }
                else{
                    fundraisingfor.setText(fundraising);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        myRef.child(USERDISPLAY).child("MoneyGoal").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final double[] totalmoneyraised = {0};
                final String money = dataSnapshot.getValue(String.class);
                myRef.removeEventListener(this);
                myRef.child(USERDISPLAY).child("ItemsSold").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<Item> ObjectList = new ArrayList<Item>();
                        if (dataSnapshot.getChildrenCount() == 0){

                        }
                        else {
                            for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                Item tempItem = dsp.getValue(Item.class);
                                ObjectList.add(tempItem);
                                String tempitemprice = tempItem.getPrice().replace("$", "");
                                totalmoneyraised[0] = totalmoneyraised[0] + Double.parseDouble(tempitemprice);
                            }
                        }
                        myRef.child(auth.getCurrentUser().getUid()).child("Donations").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getChildrenCount() == 0){
                                        return;
                                    }
                                    for(DataSnapshot dsp : dataSnapshot.getChildren()){
                                        String tempprice = dsp.getValue(String.class); String tempitemprice = tempprice.replace("$", "");
                                        Double tempdouble = Double.parseDouble(tempitemprice);
                                        totalmoneyraised[0] = totalmoneyraised[0] + tempdouble;
                                    }
                                    myRef.removeEventListener(this);
                                    Double moneyGoal = Double.parseDouble(money);
                                    String moneyraised = String.valueOf(totalmoneyraised[0]);
                                    progressText.setText("$" + moneyraised + "/$" + money + " Raised");
                                    double intermediate = 100.0* totalmoneyraised[0];
                                    double guy =  intermediate/(moneyGoal);
                                    pb.setProgress((int) guy);
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        individual_account_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(auth.getCurrentUser().getUid().equals(USERDISPLAY)) {
                    Intent accountsettings = new Intent(UserAccountActivity.this, AccountInformation.class);
                    startActivity(accountsettings);
                }
                else{
                    final EditText input = new EditText(UserAccountActivity.this);
                    input.setHint("Report User Misbehavior:");
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(UserAccountActivity.this);
                    builder1.setTitle("Reason For Report:");
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder1.setView(input);
                    builder1.setPositiveButton("Confirm Reason", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            String reason = input.getText().toString();
                            myRef.child(USERDISPLAY).child("Reports").child("Complaints").setValue(reason);
                            Toast.makeText(UserAccountActivity.this, "User Reported!", Toast.LENGTH_SHORT).show();
                            Intent refresh = new Intent(UserAccountActivity.this, NavigationActivity.class);
                            startActivity(refresh);
                        }
                    });
                    builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            dialog.dismiss();
                        }
                    });
                    builder1.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            dialog.dismiss();
                            dialog.cancel();
                        }
                    });
                    builder1.show();
                }
            }
        });
        myRef.child(USERDISPLAY).child("ItemsSent").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Item> ObjectList = new ArrayList<Item>();
                ArrayList<String> ObjectTitles = new ArrayList<String>();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    Item tempItem = dsp.getValue(Item.class);
                    ObjectList.add(tempItem);
                }
                myRef.removeEventListener(this);
                for (Item item : ObjectList) {
                    String title = item.getTitle();
                    ObjectTitles.add(title);
                    String image64 = item.getPic();
                    String price = item.getPrice();
                    String owner = item.getOwnedBy();
                    ModifiedDisplayItems tempitemmod = new ModifiedDisplayItems(title, image64, price, owner);
                    arrayListItems.add(tempitemmod);
                }
                if (ObjectTitles.isEmpty()) {
                    NoItems.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
                adapter[0] = new CustomListAdapter(getApplicationContext(), arrayListItems, UserAccountActivity.this);
                progressBar.setVisibility(View.INVISIBLE);
                listView.setAdapter(adapter[0]);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        myRef.child(USERDISPLAY).child("profilepic").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String picarray = dataSnapshot.getValue(String.class);
                if(dataSnapshot.exists()) {
                    byte[] decodedString = Base64.decode(picarray, Base64.DEFAULT);
                    Bitmap bit = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    mImageView.setImageBitmap(bit);
                }
                else
                    return;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });
        fblamember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(UserAccountActivity.this);
                input.setHint("Enter Year You Joined FBLA:");
                final AlertDialog.Builder builder1 = new AlertDialog.Builder(UserAccountActivity.this);
                builder1.setTitle("FBLA Member Since:");
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder1.setView(input);
                builder1.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        String membersince = input.getText().toString();
                        if(membersince.length() != 4) {
                            dialog.cancel();dialog.dismiss();
                            Toast.makeText(UserAccountActivity.this, "Please enter a valid 4 digit year!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        myRef.child(USERDISPLAY).child("MemberSince").setValue(membersince);
                        dialog.dismiss();dialog.cancel();
                        Toast.makeText(UserAccountActivity.this, "Year Set!", Toast.LENGTH_SHORT).show();
                        Intent refresh = new Intent(UserAccountActivity.this, NavigationActivity.class);
                        startActivity(refresh);
                    }
                });
                builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        dialog.dismiss();
                        builder1.setView(null);
                    }
                });
                builder1.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
                        dialog.cancel();
                    }
                });
                builder1.show();
            }
        });
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(USERDISPLAY.equals(auth.getCurrentUser().getUid())){
                    Toast.makeText(UserAccountActivity.this, "Trying to contact yourself?", Toast.LENGTH_SHORT).show();
                    return;
                }
                myRef.child(USERDISPLAY).child("Email").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String email = dataSnapshot.getValue(String.class);
                        sendEmail(email);
                        Toast.makeText(UserAccountActivity.this, "Email sent!", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });
        DonateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                donation.setHint("$");
                donation.setInputType(InputType.TYPE_CLASS_NUMBER);
                donation.addTextChangedListener(tw);
                if(USERDISPLAY.equals(auth.getCurrentUser().getUid())){
                    Toast.makeText(UserAccountActivity.this, "Trying to donate to yourself?", Toast.LENGTH_SHORT).show();
                    return;
                }
                final AlertDialog.Builder builder1 = new AlertDialog.Builder(UserAccountActivity.this);
                builder1.setTitle("Donate To:");
                builder1.setView(donation);
                builder1.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                       String donationtext = donation.getText().toString();
                        String date = df.format(today).replace("/", "").replace(" ", "").replace(":", "");
                        myRef.child(USERDISPLAY).child("Donations").child(date).setValue(donationtext);
                        Toast.makeText(UserAccountActivity.this, "Donation Given!", Toast.LENGTH_SHORT).show();
                        Intent refresh = new Intent(UserAccountActivity.this, NavigationActivity.class);
                        startActivity(refresh);
                    }
                });
                builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        dialog.dismiss();
                        Intent refresh = new Intent(UserAccountActivity.this, UserAccountActivity.class);
                        refresh.putExtra("username", USERDISPLAY);
                        startActivity(refresh);
                    }
                });
                builder1.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
                        dialog.cancel();
                        Intent refresh = new Intent(UserAccountActivity.this, UserAccountActivity.class);
                        refresh.putExtra("username", USERDISPLAY);
                        startActivity(refresh);
                    }
                });
                builder1.show();

            }
        });
        fundraisingfor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(UserAccountActivity.this);
                input.setHint("Enter reason for fundraising:");
                AlertDialog.Builder builder1 = new AlertDialog.Builder(UserAccountActivity.this);
                builder1.setTitle("Reason For Fundraising:");
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder1.setView(input);
                builder1.setPositiveButton("Confirm Reason", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        String reason = input.getText().toString();
                        if(reason.length() > 20){
                            Toast.makeText(UserAccountActivity.this, "Please shorten your reason to 20 characters", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        myRef.child(USERDISPLAY).child("FundraisingFor").setValue(reason);
                        Toast.makeText(UserAccountActivity.this, "Reason Updated!", Toast.LENGTH_SHORT).show();
                        Intent refresh = new Intent(UserAccountActivity.this, UserAccountActivity.class);
                        startActivity(refresh);
                    }
                });
                builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        dialog.dismiss();
                    }
                });
                builder1.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
                        dialog.cancel();
                    }
                });
                builder1.show();
            }
        });
        if(!auth.getCurrentUser().getUid().equals(USERDISPLAY)){
            individual_account_setting.setImageResource(R.drawable.redexclamation);
            mImageView.setClickable(false);
            fblamember.setClickable(false);
            fundraisingfor.setClickable(false);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data123 = baos.toByteArray();
            String imageFile = Base64.encodeToString(data123, Base64.DEFAULT);
            myRef.child(USERDISPLAY).child("profilepic").setValue(imageFile);

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
                donation.removeTextChangedListener(this);
                donation.setText(cashAmountBuilder.toString());
                donation.setTextKeepState("$" + cashAmountBuilder.toString());
                Selection.setSelection(donation.getText(), cashAmountBuilder.toString().length() + 1);
                donation.addTextChangedListener(this);
            }
        }
    };
    protected void sendEmail(String email) {
        String[] TO = {email};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contact Via DYSP");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello! I'd like to make contact with you about your profile on DYSP!");
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(UserAccountActivity.this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
}