package fbla.mobileapp.app.dysp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView;
    Toolbar toolbar;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Users");
        final DatabaseReference itemRef = database.getReference("MasterItems");
        setSupportActionBar(toolbar);
        setTitle("DYSP Home");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        FloatingActionButton location = (FloatingActionButton)findViewById(R.id.Location);
        FloatingActionButton seeItems = (FloatingActionButton)findViewById(R.id.seeItems);
        FloatingActionButton postItem = (FloatingActionButton)findViewById(R.id.postItem);
        FloatingActionButton myAccount = (FloatingActionButton)findViewById(R.id.myaccount);
        FloatingActionButton viewSaves = (FloatingActionButton)findViewById(R.id.viewSaves);
        final ProgressBar pb = (ProgressBar) findViewById(R.id.pb);
        final int progressStatus = 0;
        final EditText input = new EditText(this);
        input.setHint("Enter Location");
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(NavigationActivity.this);
                builder1.setTitle("Set Location");
                builder1.setView(input);
                builder1.setPositiveButton("Set Location", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String location = input.getText().toString();
                        myRef.child(auth.getCurrentUser().getUid()).child("Location").setValue(location);
                        dialog.dismiss();
                        Intent refresh = new Intent(NavigationActivity.this, NavigationActivity.class);
                        Toast.makeText(NavigationActivity.this, "Location Updated!", Toast.LENGTH_SHORT).show();
                        startActivity(refresh);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent refresh = new Intent(NavigationActivity.this, NavigationActivity.class);
                        startActivity(refresh);
                    }
                });
                builder1.show();

            }
        });
        seeItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToList = new Intent(NavigationActivity.this, List_Item.class);
                startActivity(goToList);
            }
        });
        postItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToPost = new Intent(NavigationActivity.this, Add_Object.class);
                startActivity(goToPost);
            }
        });
        myAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToAccount= new Intent(NavigationActivity.this, AccountInformation.class);
                startActivity(goToAccount);
            }
        });
        viewSaves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> SaveTitles = new ArrayList<String>();
                SaveTitles.add("Select a Saved Item:");
                final ArrayList<Item> InterestedItems = new ArrayList<Item>();
                final AlertDialog.Builder builderSingle = new AlertDialog.Builder(NavigationActivity.this);
                builderSingle.setTitle("View Saved Items");
                myRef.child(auth.getCurrentUser().getUid()).child("Interested In").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot dsp : dataSnapshot.getChildren()){
                            Item tempItem = dsp.getValue(Item.class);
                            String Item_Name = tempItem.getTitle();
                            InterestedItems.add(tempItem); SaveTitles.add(Item_Name);
                        }
                        myRef.removeEventListener(this);
                        if (SaveTitles.isEmpty()) {
                            Toast.makeText(NavigationActivity.this, "No Saved Items!", Toast.LENGTH_SHORT).show();
                            Intent refreshPage = new Intent(NavigationActivity.this, NavigationView.class);
                            startActivity(refreshPage);
                        }
                        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(NavigationActivity.this, android.R.layout.simple_list_item_1, SaveTitles);
                        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent refreshPage = new Intent(NavigationActivity.this, NavigationActivity.class);
                                startActivity(refreshPage);
                            }
                        });
                        builderSingle.setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String strName = adapter.getItem(which);
                                if(strName.equals("Select a Saved Item:")){
                                    Toast.makeText(NavigationActivity.this, "That Is Not A Saved Item", Toast.LENGTH_SHORT).show();
                                    Intent home12 = new Intent(NavigationActivity.this, NavigationActivity.class);
                                    startActivity(home12);
                                }
                                else {
                                    Intent refreshPage = new Intent(NavigationActivity.this, ViewItem.class);
                                    refreshPage.putExtra("itemname", strName);
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
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
       // Typeface custom = Typeface.createFromAsset(getAssets(), "fonts/lettergothic.ttf");
        final TextView WelcomeText = (TextView)findViewById(R.id.WelcomeText);
        //final TextView Welcome = (TextView)findViewById(R.id.textView7);
        final TextView Sold = (TextView)findViewById(R.id.items_posted);
       myRef.child(auth.getCurrentUser().getUid()).child("ItemsSent").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int numitems = (int) dataSnapshot.getChildrenCount();
                myRef.removeEventListener(this);
                Sold.setText(String.valueOf(numitems));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        final TextView Offers = (TextView)findViewById(R.id.current_offers);
       myRef.child(auth.getCurrentUser().getUid()).child("ItemOffers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long numOffers = dataSnapshot.getChildrenCount();
                myRef.removeEventListener(this);
                Offers.setText(String.valueOf(numOffers));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        final TextView MoneyEarned = (TextView)findViewById(R.id.moneyEarned);
       myRef.child(auth.getCurrentUser().getUid()).child("Money Raised").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int money = dataSnapshot.getValue(Integer.class);
                myRef.removeEventListener(this);
                MoneyEarned.setText("$" + String.valueOf(money));

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        final TextView Goal = (TextView)findViewById(R.id.goal);

        myRef.child(auth.getCurrentUser().getUid()).child("MoneyGoal").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String money = dataSnapshot.getValue(String.class);
                myRef.removeEventListener(this);
                Goal.setText("$" + money);
                myRef.child(auth.getCurrentUser().getUid()).child("Money Raised").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int moneyRaised = dataSnapshot.getValue(Integer.class);
                        int moneyGoal = Integer.parseInt(money);
                        int intermediate = 100* moneyRaised;
                        int guy =  intermediate/(moneyGoal+1);
                        pb.setProgress(guy);
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
        final Button SetGoal = (Button)findViewById(R.id.setGoal);
        final EditText input2 = new EditText(NavigationActivity.this);
        input2.setHint("Enter Goal");
        SetGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(NavigationActivity.this);
                builder1.setTitle("Set Goal");
                input2.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder1.setView(input2);
                builder1.setPositiveButton("Set Goal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        String goal = input2.getText().toString();
                        myRef.child(auth.getCurrentUser().getUid()).child("MoneyGoal").setValue(goal);
                        Toast.makeText(NavigationActivity.this, "Goal Set!", Toast.LENGTH_SHORT).show();
                        Intent refresh = new Intent(NavigationActivity.this, NavigationActivity.class);
                        startActivity(refresh);
                        dialog.dismiss();
                    }
                });
                builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        dialog.dismiss();
                        Intent refresh = new Intent(NavigationActivity.this, NavigationActivity.class);
                        startActivity(refresh);
                    }
                });
                builder1.show();

            }
        });




        TextView Username = (TextView)headerView.findViewById(R.id.username1);
        TextView useremail = (TextView)headerView.findViewById(R.id.User_email);
        TextView object_sold = (TextView)findViewById(R.id.textView2); //object_sold.setTypeface(custom);
        TextView money_raised = (TextView)findViewById(R.id.textView3); //money_raised.setTypeface(custom);
        TextView currentOffers = (TextView)findViewById(R.id.textView4);// currentOffers.setTypeface(custom);
        //Button add_item = (Button)findViewById(R.id.add_item);
       // Button view_item = (Button)findViewById(R.id.view_items);
       // Button view_account = (Button)findViewById(R.id.your_account);
       // WelcomeText.setTypeface(custom);
       //Username.setTypeface(custom);
       // useremail.setTypeface(custom);
       // add_item.setTypeface(custom);

      //  view_item.setTypeface(custom);

        //view_account.setTypeface(custom);

            //Toast.makeText(NavigationActivity.this, "Welcome " + auth.getCurrentUser().getDisplayName() + "!" , Toast.LENGTH_LONG).show();
            //Toast.makeText(this, auth.getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
            myRef.child(auth.getCurrentUser().getUid()).child("DispayName").setValue(auth.getCurrentUser().getDisplayName());
            myRef.child(auth.getCurrentUser().getUid()).child("Email").setValue(auth.getCurrentUser().getEmail());
              try {
                  String displayName = auth.getCurrentUser().getDisplayName().substring(0, auth.getCurrentUser().getDisplayName().indexOf(" "));
                  WelcomeText.setText("Welcome " + displayName + "!");
                //WelcomeText.setText(auth.getCurrentUser().getDisplayName());
                WelcomeText.setTypeface(null, Typeface.BOLD);
                //Welcome.setTypeface(null, Typeface.BOLD);
            }catch (Exception e){
                WelcomeText.setText(auth.getCurrentUser().getDisplayName());
                WelcomeText.setTypeface(null, Typeface.BOLD);
            }
             Username.setText(auth.getCurrentUser().getDisplayName());
             useremail.setText(auth.getCurrentUser().getEmail());

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent thankyou = new Intent(NavigationActivity.this, ThankYouTo.class);
            startActivity(thankyou);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_postItem) {
            Intent addObject =  new Intent(NavigationActivity.this, Add_Object.class);
            startActivity(addObject);

        } else if (id == R.id.nav_viewItems) {
            Intent viewItems = new Intent(NavigationActivity.this, List_Item.class);
            startActivity(viewItems);
        } else if (id == R.id.nav_youraccount) {
            Intent yourAccount = new Intent(NavigationActivity.this, AccountInformation.class);
            startActivity(yourAccount);
        } else if (id == R.id.nav_email) {
            sendEmail();
        } else if (id == R.id.nav_logout) {
            if(auth.getCurrentUser() != null)
                auth.signOut();
            Intent login = new Intent(NavigationActivity.this, SignIn.class);
            startActivity(login);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    protected void sendEmail() {
        Log.i("Send email", "");
        String[] TO = {"dyspfbla@gmail.com"};
        String[] CC = {"sharmanikhil99@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");

        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Toast.makeText(this, "Email sent!", Toast.LENGTH_SHORT).show();
            //Log.i("Finished send email...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(NavigationActivity.this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

}
