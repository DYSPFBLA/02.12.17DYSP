package fbla.mobileapp.app.dysp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class Add_Object extends AppCompatActivity {
    private static final int RESULT_LOAD_IMAGE = 101;
    private static final int CAMERA_REQUEST = 1888;
    public static final String IMAGE_TYPE = "image/*";
    TextView select_image, take_image, textView7, textView8;
    ImageView image_diplay;
    Button add_item;
    TextView textView10;
    RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__object);
        image_diplay = (ImageView)findViewById(R.id.image_display) ;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Users");
        final DatabaseReference itemRef = database.getReference("MasterItems");
        textView10 = (TextView)findViewById(R.id.textView10);
        add_item = (Button)findViewById(R.id.add_item);
        if(getIntent().hasExtra("modifyitem")) {
            final String itemname = getIntent().getStringExtra("modifyitem"); //STRING BEING RECEIVED
            itemRef.child(itemname).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Item item = dataSnapshot.getValue(Item.class);
                    itemRef.removeEventListener(this);
                    byte[] decodedString = Base64.decode(item.getPic(), Base64.DEFAULT);
                    final Bitmap bit = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    textView10.setVisibility(View.INVISIBLE);
                    add_item.setText("Modify Item");
                    image_diplay.setImageBitmap(bit);
                    add_item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent confirm_item = new Intent(Add_Object.this, Add_Details.class);
                            ByteArrayOutputStream bs = new ByteArrayOutputStream();
                            bit.compress(Bitmap.CompressFormat.JPEG, 50, bs);
                            confirm_item.putExtra("byteArray", bs.toByteArray());
                            confirm_item.putExtra("modifieditem", itemname);
                            startActivity(confirm_item);
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            //Toast.makeText(this, imagestring, Toast.LENGTH_SHORT).show();

        }
        FloatingActionButton takeimage = (FloatingActionButton)findViewById(R.id.imagefab);
        FloatingActionButton fromgallery = (FloatingActionButton)findViewById(R.id.galleryfab);
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.activity_add__object);
        textView7 = (TextView)findViewById(R.id.textView7);
        textView8 = (TextView)findViewById(R.id.textView8);

        image_diplay = (ImageView)findViewById(R.id.image_display) ;
        fromgallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView10.setVisibility(View.INVISIBLE);
                textView7.setVisibility(View.INVISIBLE);
                textView8.setVisibility(View.INVISIBLE);
                Intent i = new Intent();
                i.setType(IMAGE_TYPE);
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i,
                        getString(R.string.select_picture)), RESULT_LOAD_IMAGE);
            }
        });
        takeimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView7.setVisibility(View.INVISIBLE);
                textView8.setVisibility(View.INVISIBLE);
                textView10.setVisibility(View.INVISIBLE);
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
       // Typeface custom = Typeface.createFromAsset(getAssets(), "fonts/lettergothic.ttf");




    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        image_diplay = (ImageView)findViewById(R.id.image_display) ;
       final Bitmap user; final Bitmap user1;
        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_LOAD_IMAGE) {

                final Uri selectedImageUri = data.getData();
                try {
                    user1 = new UserPicture(selectedImageUri, getContentResolver()).getBitmap();
                    image_diplay.setImageBitmap(user1);
                    add_item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent confirm_item = new Intent(Add_Object.this, Add_Details.class);
                            ByteArrayOutputStream bs = new ByteArrayOutputStream();
                            user1.compress(Bitmap.CompressFormat.JPEG, 50, bs);
                            confirm_item.putExtra("byteArray", bs.toByteArray());
                            startActivity(confirm_item);
                        }
                    });

                } catch (IOException e) {
                    //Log.e(Add_Object.class.getSimpleName(), "Failed to load image", e);
                }
                // original code
//                String selectedImagePath = getPath(selectedImageUri);
//                selectedImagePreview.setImageURI(selectedImageUri);
            }
            if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
                user = (Bitmap) data.getExtras().get("data");
                image_diplay.setImageBitmap(user);
                //final Uri selectedImageUri = data.getData();
                add_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent confirm_item = new Intent(Add_Object.this, Add_Details.class);
                        ByteArrayOutputStream bs = new ByteArrayOutputStream();
                        user.compress(Bitmap.CompressFormat.JPEG, 99, bs);
                        confirm_item.putExtra("byteArray", bs.toByteArray());
                        startActivity(confirm_item);
                    }
                });
            }
        } else {
            // report failure
            Toast.makeText(getApplicationContext(), R.string.msg_failed_to_get_intent_data, Toast.LENGTH_LONG).show();
           //Log.d(Add_Object.class.getSimpleName(), "Failed to get intent data, result code is " + resultCode);
        }
    }

}
