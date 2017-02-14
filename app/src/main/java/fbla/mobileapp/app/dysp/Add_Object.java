package fbla.mobileapp.app.dysp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
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
import java.util.ArrayList;

public class Add_Object extends AppCompatActivity {
    private static final int RESULT_LOAD_IMAGE = 101;
    private static final int CAMERA_REQUEST = 1888;
    public static final String IMAGE_TYPE = "image/*";
    ImageView image_diplay, repeatPhoto; Button add_item;
    RelativeLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        image_diplay = (ImageView)findViewById(R.id.image_display) ;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference itemRef = database.getReference("MasterItems");
        if(getIntent().hasExtra("modifyitem")) {
            final String itemname = getIntent().getStringExtra("modifyitem"); //STRING BEING RECEIVED
            itemRef.child(itemname).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Item item = dataSnapshot.getValue(Item.class);
                    itemRef.removeEventListener(this);
                    byte[] decodedString = Base64.decode(item.getPic(), Base64.DEFAULT);
                    final Bitmap bit = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    add_item.setText("Modify Item");
                    image_diplay.setImageBitmap(bit);
                    add_item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent confirm_item = new Intent(Add_Object.this, Add_Details.class);
                            ByteArrayOutputStream bs = new ByteArrayOutputStream();
                            bit.compress(Bitmap.CompressFormat.JPEG, 99, bs);
                            confirm_item.putExtra("byteArray", bs.toByteArray());
                            confirm_item.putExtra("modifieditem", itemname);
                            startActivity(confirm_item);
                        }
                    });
                    repeatPhoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PromptUser();
                        }
                    });
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        if(!getIntent().hasExtra("modifyitem")){
            PromptUser();
        }
        setContentView(R.layout.activity_add__object);
        add_item = (Button)findViewById(R.id.add_item);
        image_diplay = (ImageView)findViewById(R.id.image_display) ;
        repeatPhoto = (ImageView)findViewById(R.id.repeatPhoto);
        repeatPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PromptUser();
            }
        });
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
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
                user = (Bitmap) data.getExtras().get("data");
                image_diplay.setImageBitmap(user);
                add_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent confirm_item = new Intent(Add_Object.this, Add_Details.class);
                        ByteArrayOutputStream bs = new ByteArrayOutputStream();
                        user.compress(Bitmap.CompressFormat.JPEG, 100, bs);
                        confirm_item.putExtra("byteArray", bs.toByteArray());
                        startActivity(confirm_item);
                    }
                });
            }
        } else {
            // report failure
            finish();
            //Toast.makeText(getApplicationContext(), R.string.msg_failed_to_get_intent_data, Toast.LENGTH_LONG).show();
        }
    }
    public void PromptUser(){
        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(Add_Object.this);
        builderSingle.setTitle("Choose Image:"); ArrayList<String> ImagePicker = new ArrayList<String>(); ImagePicker.add("Take Picture"); ImagePicker.add("Choose From Gallery");
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(Add_Object.this, android.R.layout.simple_list_item_1, ImagePicker);
        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builderSingle.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                dialog.cancel();
            }
        });
        builderSingle.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = adapter.getItem(which);
                if(strName.equals("Take Picture")){
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
                else if(strName.equals("Choose From Gallery")) {
                    Intent i = new Intent();
                    i.setType(IMAGE_TYPE);
                    i.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(i,
                            getString(R.string.select_picture)), RESULT_LOAD_IMAGE);
                }
            }
        });
        builderSingle.show();
    }

}
