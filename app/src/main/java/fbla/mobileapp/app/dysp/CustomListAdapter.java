package fbla.mobileapp.app.dysp;

/**
 * Created by nikhil on 1/9/2017.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.facebook.FacebookSdk.getApplicationContext;

public class CustomListAdapter extends BaseAdapter {

    //private final Activity context;
    Context mContext;
    LayoutInflater inflater;
    private List<ModifiedDisplayItems> modifiedDisplayItemsList = null;
    private ArrayList<ModifiedDisplayItems> arraylist;
    Activity activity;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference("Users");
    final DatabaseReference commentRef = database.getReference("Comments");
    final DatabaseReference itemRef = database.getReference("MasterItems");
    final FirebaseAuth auth = FirebaseAuth.getInstance();


    public CustomListAdapter(Context context, List<ModifiedDisplayItems> modifiedDisplayItemses, Activity act) {
       // super(context, R.layout.listwithpic, ItemNames);
        // TODO Auto-generated constructor stub
       // this.context=context;
        mContext = context;
        this.modifiedDisplayItemsList =modifiedDisplayItemses;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<ModifiedDisplayItems>();
        this.arraylist.addAll(modifiedDisplayItemsList);
        activity = act;

    }
    @Override
    public int getCount() {
        return modifiedDisplayItemsList.size();
    }

    @Override
    public ModifiedDisplayItems getItem(int position) {
        return modifiedDisplayItemsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        View rowView=inflater.inflate(R.layout.listwithpic, null,true);
        FacebookSdk.sdkInitialize(getApplicationContext());
        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView extratxt = (TextView) rowView.findViewById(R.id.textView1);
        ImageView edititem = (ImageView)rowView.findViewById(R.id.edititem);
        edititem.setEnabled(false);
        String owner = modifiedDisplayItemsList.get(position).getOwner();
        if (owner.equals(auth.getCurrentUser().getUid())){
            edititem.setImageResource(R.drawable.moresettings);
            edititem.setEnabled(true);

        }
        final ShareDialog shareDialog;
        shareDialog = new ShareDialog(activity);
        txtTitle.setText(modifiedDisplayItemsList.get(position).getTitle());
        byte[] decode = Base64.decode(modifiedDisplayItemsList.get(position).getImage_string().getBytes(), 0);
        Bitmap bit = BitmapFactory.decodeByteArray(decode, 0, decode.length);
        imageView.setImageBitmap(bit);
        extratxt.setText("Price: "+ modifiedDisplayItemsList.get(position).getPricelist());
        edititem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              final AlertDialog.Builder builderSingle = new AlertDialog.Builder(activity);
                builderSingle.setTitle("More Options");
                ArrayList<String> Settings = new ArrayList<String>();
                Settings.add("Delete Item"); Settings.add("Modify Item"); Settings.add("Share Item");
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, Settings);
                builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent refreshPage = new Intent(mContext, List_Item.class);
                        mContext.startActivity(refreshPage);
                    }
                });
                builderSingle.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = adapter.getItem(which);
                        if (strName.equals("Delete Item")){
                            myRef.child(auth.getCurrentUser().getUid()).child("ItemsSent").child(modifiedDisplayItemsList.get(position).getTitle()).removeValue();
                            itemRef.child(modifiedDisplayItemsList.get(position).getTitle()).removeValue();
                            commentRef.child(modifiedDisplayItemsList.get(position).getTitle()).removeValue();
                            Intent refreshPage = new Intent(mContext, NavigationActivity.class);
                            Toast.makeText(mContext, "Item Removed!", Toast.LENGTH_SHORT).show();
                            mContext.startActivity(refreshPage);
                        }
                        else if(strName.equals("Modify Item")){
                            Intent modifyItem = new Intent(mContext, Add_Object.class);
                            //Toast.makeText(AccountInformation.this, ObjectList.get(which-1).getTitle().toString(), Toast.LENGTH_SHORT).show();
                            //Toast.makeText(AccountInformation.this, "Item Removed!", Toast.LENGTH_SHORT).show();
                            modifyItem.putExtra("modifyitem", modifiedDisplayItemsList.get(position).getTitle());
                            mContext.startActivity(modifyItem);
                        }
                        else if(strName.equals("Share Item")){
                            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                    .setContentTitle("Share " + modifiedDisplayItemsList.get(position).getTitle() + " from DYSP FBLA")
                                    .setContentDescription(
                                            "I just posted a(n) " + modifiedDisplayItemsList.get(position).getTitle() + " on the DYSP FBLA App! You can view it by dowloading the app from the Play Store!")
                                    .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=fbla.mobileapp.app.dysp"))
                                    .build();

                            shareDialog.show(linkContent);
                            //Uri uriUrl = Uri.parse("https://www.facebook.com/");
                           // Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                           // mContext.startActivity(launchBrowser);
                        }
                    }
                });
                builderSingle.show();

            }
        });
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewItem = new Intent(mContext, ViewItem.class);
                viewItem.putExtra("itemname", modifiedDisplayItemsList.get(position).getTitle());
                mContext.startActivity(viewItem);
            }
        });
        return rowView;

    };
    public void filter(String search){
        search = search.toLowerCase(Locale.getDefault());
        modifiedDisplayItemsList.clear();
        if(search.length() == 0){
            modifiedDisplayItemsList.addAll(arraylist);
        }else{
            for(ModifiedDisplayItems mi : arraylist){
                if(mi.getTitle().toLowerCase(Locale.getDefault()).contains(search)){
                    modifiedDisplayItemsList.add(mi);
                }
            }
        }
        notifyDataSetChanged();
    }

 }