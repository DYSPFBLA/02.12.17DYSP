package fbla.mobileapp.app.dysp;

        import android.content.Intent;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.ImageView;
        import android.widget.TextView;

public class ThankYouTo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you_to);
        ImageView img = (ImageView)findViewById(R.id.imageView2);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(ThankYouTo.this, NavigationActivity.class);
                startActivity(home);
            }
        });
        TextView textView = (TextView)findViewById(R.id.textView6);
        textView.setText("This application with support from Android Studio Documents, Gary111's MaskedEditText and hdodenhof CircleImageView (On Github) and numerous, numerous helpful debugging forums on Stackoverflow.");
    }
}
