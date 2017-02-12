package fbla.mobileapp.app.dysp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class ViewComments extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_comments);
       // final Typeface custom = Typeface.createFromAsset(getAssets(), "fonts/lettergothic.ttf");
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Users");
        final DatabaseReference itemRef = database.getReference("MasterItems");
        final DatabaseReference commentRef = database.getReference("Comments");
        final String itemValue = getIntent().getExtras().getString("itemname");
        final String itemOwner = getIntent().getExtras().getString("ItemOwner");
        final Button addComment = new Button(this);
        addComment.setText("Add Comment");
        final Button returnHome = new Button(this);
        returnHome.setText("Return Home");
        final LinearLayout linear = (LinearLayout) findViewById(R.id.linear);
        final TextView owner_comment = new TextView(ViewComments.this);
       // final LinearLayout otherLinear = new LinearLayout(ViewComments.this);
        final LinearLayout newLinear = new LinearLayout(ViewComments.this);
        final ArrayList<String> CommentList = new ArrayList<String>();
        final DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        final Date today = Calendar.getInstance().getTime();
        //  linear.addView(otherLinear);
        linear.addView(newLinear);
        newLinear.addView(addComment);
        newLinear.addView(returnHome);
        addComment.setLayoutParams(new LinearLayout.LayoutParams(500, 150));
        returnHome.setLayoutParams(new LinearLayout.LayoutParams(500, 150));

        itemRef.child(itemValue).child("additionalComments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                owner_comment.setTypeface(null, Typeface.BOLD);
                owner_comment.setText(itemOwner + " (Owner):" + "\n" + dataSnapshot.getValue(String.class));
                owner_comment.setPadding(40, 40, 40, 40);
                owner_comment.setBackground(getDrawable(R.drawable.back));
                linear.addView(owner_comment);
                itemRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final ArrayList<String> Comments = new ArrayList<String>();
        commentRef.child(itemValue).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dsp : dataSnapshot.getChildren()){
                    String tempMessage = dsp.getValue(String.class);
                    Comments.add(tempMessage);
                    //Toast.makeText(ViewComments.this, tempMessage, Toast.LENGTH_SHORT).show();
                }
                commentRef.removeEventListener(this);
                TextView[] comments = new TextView[Comments.size()];
                for(int i = 0; i < Comments.size(); i++){
                 comments[i] = new TextView(ViewComments.this);
                    String date = Comments.get(i).substring(0, 7);
                    String actual_date = date.substring(0,2) + "/" + date.substring(2, 4) + "/" + date.substring(4) + "7";
                 String username = Comments.get(i).substring(Comments.get(i).indexOf("=")+1, Comments.get(i).indexOf("~"));
                  String message = Comments.get(i).substring(Comments.get(i).indexOf("~")+1);
                 comments[i].setText(username + " (" + actual_date + " )" + " :" + "\n" + message);
                  comments[i].setPadding(20, 20, 20, 20);
                  comments[i].setBackground(getDrawable(R.drawable.backv2));
                   linear.addView(comments[i]);
                 }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //newLinear.setOrientation(LinearLayout.VERTICAL);
        //linear.addView(newLinear);
        returnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(ViewComments.this, NavigationActivity.class);
                startActivity(home);
            }
        });
        final EditText input = new EditText(this);
        input.setHint("Enter Comment Here");
        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder1 = new AlertDialog.Builder(ViewComments.this);
                builder1.setTitle("Add Comment");
                builder1.setView(input);
                builder1.setPositiveButton("Post Comment", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String comment = input.getText().toString();
                        //THE PURPOSE OF THIS IS TO FILTER OUR COMMENTS SECTION. WE WANT TO MAKE SURE THAT USERS OF ALL AGES WILL FIND APPOPRIATE AND RELEVENT DISCUSSION IN THE COMMENTS SECTION. PLEASE FORGIVE US FOR THE EXPLICIT WORDS.
                        List<String> words = Arrays.asList("damn", "ass", "fuck", "Fuck", "bitch", "bastard", "cunt", "shit", "crap", "hell", "bitches", "boob", "boobs", "bullshit", "dick", "cock", "nude", "naked");
                        for (String word : words) {
                            Pattern rx = Pattern.compile("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE);
                            comment = rx.matcher(comment).replaceAll(new String(new char[word.length()]).replace('\0', '*'));
                        }
                        String date = df.format(today).replace("/", "").replace(" ", "").replace(":", "");
                        String message = auth.getCurrentUser().getDisplayName() + "~" + comment;
                        commentRef.child(itemValue).child(date).setValue(date + "=" + message);
                        //commentRef.child(itemValue).child(date).child("Date").setValue(date);
                        //commentRef.child(itemValue).child(date).child("Message").setValue();
                        TextView newComment = new TextView(ViewComments.this);
                        newComment.setText(auth.getCurrentUser().getDisplayName() + " :" + "\n" + comment);
                        newComment.setPadding(20, 20, 20, 20);
                        newComment.setBackground(getDrawable(R.drawable.backv2));
                        //newComment.setLayoutParams(layoutParamsWidth50);
                        linear.addView(newComment);
                        dialog.dismiss();
                        Intent refresh = new Intent(ViewComments.this, ViewComments.class);
                        refresh.putExtra("itemname", itemValue);
                        refresh.putExtra("ItemOwner", itemOwner);

                        Toast.makeText(ViewComments.this, "Your Comment Has Been Added", Toast.LENGTH_SHORT).show();
                        startActivity(refresh);
                        //Intent refresh =  new Intent(ViewComments.this, List_Item.class);
                       // startActivity(refresh);

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        dialog.dismiss();
                        Intent refresh =  new Intent(ViewComments.this, List_Item.class);
                        startActivity(refresh);
                    }
                });
                builder1.show();
            }
        });
    }

    }
