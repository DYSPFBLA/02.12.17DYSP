package fbla.mobileapp.app.dysp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnTextChanged;

public class CreditCard extends AppCompatActivity {

    private static final int cardnumberdigits = 19; // Pattern of the Credit Card thingy 0000-0000-0000-0000
    private static final int cardnumbertotaldigits = 16;
    private static final int fifthmodulo = 5;
    private static final int dividerposition = fifthmodulo - 1;
    private static final char cardnumberdivide = '-';
    private static final int datesymbols = 5; // pattern MM/YY
    private static final int datedigits = 4; // pattern: MM + YY
    private static final int dividermodule2 = 3;
    private static final int dividerpos = dividermodule2 - 1;
    private static final char cardatedash = '/';

    private static final int securitysymbols = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card);
        ButterKnife.bind(this);
        Button submit = (Button)findViewById(R.id.Submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent account = new Intent(CreditCard.this, AccountInformation.class);
                Toast.makeText(CreditCard.this, "Credit Card Information Saved", Toast.LENGTH_SHORT).show();
                startActivity(account);
            }
        });
    }
    @OnTextChanged(value = R.id.cardNumberEditText, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void onCardNumberTextChanged(Editable s) {
        if (!isInputCorrect(s, cardnumberdigits, fifthmodulo, cardnumberdivide)) {
            s.replace(0, s.length(), concatString(getDigitArray(s, cardnumbertotaldigits), dividerposition, cardnumberdivide));
        }
    }
    @OnTextChanged(value = R.id.cardDateEditText, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void onCardDateTextChanged(Editable s) {
        if (!isInputCorrect(s, datesymbols, dividermodule2, cardatedash)) {
            s.replace(0, s.length(), concatString(getDigitArray(s, datedigits), dividerpos, cardatedash));
        }
    }
    @OnTextChanged(value = R.id.cardCVCEditText, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void onCardCVCTextChanged(Editable s) {
        if (s.length() > securitysymbols) {
            s.delete(securitysymbols, s.length());
        }
    }
    private boolean isInputCorrect(Editable s, int size, int dividerPosition, char divider) {
        boolean isCorrect = s.length() <= size;
        for (int i = 0; i < s.length(); i++) {
            if (i > 0 && (i + 1) % dividerPosition == 0) {
                isCorrect &= divider == s.charAt(i);
            } else {
                isCorrect &= Character.isDigit(s.charAt(i));
            }
        }
        return isCorrect;
    }
    private String concatString(char[] digits, int dividerPosition, char divider) {
        final StringBuilder formatted = new StringBuilder();

        for (int i = 0; i < digits.length; i++) {
            if (digits[i] != 0) {
                formatted.append(digits[i]);
                if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                    formatted.append(divider);
                }
            }
        }

        return formatted.toString();
    }
    private char[] getDigitArray(final Editable s, final int size) {
        char[] digits = new char[size];
        int index = 0;
        for (int i = 0; i < s.length() && index < size; i++) {
            char current = s.charAt(i);
            if (Character.isDigit(current)) {
                digits[index] = current;
                index++;
            }
        }
        return digits;
    }
}
