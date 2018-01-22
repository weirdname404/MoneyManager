package ru.xidv.andrst.moneymanager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.xidv.andrst.sqlite.helper.DatabaseHelper;
import ru.xidv.andrst.sqlite.model.Wallet;

/**
 * Created by Александр on 03.12.2017.
 */

public class AddWalletActivity extends Activity {

    int mCounter = 0;
    DatabaseHelper db;
    String buttonHint = "Fill Name and Balance";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wallet);

        EditText mNameEditText = findViewById(R.id.editWalletName);
        EditText mBalanceEditText = findViewById(R.id.editCurrentBalance);
        Button mSaveButton = findViewById(R.id.saveButton);

        mNameEditText.addTextChangedListener(nameTextWatcher);
        mBalanceEditText.addTextChangedListener(balanceTextWatcher);
        mSaveButton.setEnabled(false);
        mSaveButton.setText(buttonHint);

    }

    private TextWatcher nameTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
            mCounter++;
            dataCheck();
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }
    };

    private TextWatcher balanceTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
            mCounter++;
            dataCheck();
            dataCheck();

        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }
    };

    @SuppressLint("SetTextI18n")
    public void dataCheck() {

        EditText mNameEditText = findViewById(R.id.editWalletName);
        EditText mBalanceEditText = findViewById(R.id.editCurrentBalance);
        Button mSaveButton = findViewById(R.id.saveButton);

        if (mCounter >= 2) {
            boolean success = false;
            if (mNameEditText.getText().length() > 0 && mBalanceEditText.getText().length() > 0) {
                try {
                    Double input = Double.parseDouble(mBalanceEditText.getText().toString());

                    if (mBalanceEditText.getText().length() >= 7) {

                        Context context = getApplicationContext();
                        CharSequence text = "Only 7 digits max";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                    }

                    if (input > -9999999.99 && input < 9999999.99) {
                        mSaveButton.setEnabled(true);
                        mSaveButton.setText("SAVE");
                    } else {
                        mSaveButton.setEnabled(false);
                        mSaveButton.setText(buttonHint);
                    }

                } catch (Exception ignored) {
                }
            }
        } else {
            mSaveButton.setEnabled(false);
            mSaveButton.setText(buttonHint);
        }
    }

    public void saveWallet(View view) {
        boolean success = true;
        EditText mNameEditText = findViewById(R.id.editWalletName);
        EditText mBalanceEditText = findViewById(R.id.editCurrentBalance);
        EditText mNoteEditText = findViewById(R.id.editNote);

        db = new DatabaseHelper(getApplicationContext());

        final List<Wallet> allWallets = db.getAllWallets();


        if (allWallets.size() > 0) {
            for ( int i = 0; i < allWallets.size(); i++ ) {
                if (mNameEditText.getText().toString().equals(allWallets.get(i).getWalletName())) {

                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(this);
                    }
                    builder.setTitle("Attention")
                            .setMessage("Wallet Name you are trying to add already exists, please give it another name.")
                            .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    success = false;
                    break;
                } else {
                    success = true;
                }
            }
        }


        if (success) {

            String walletName = mNameEditText.getText().toString();
            String walletBalance = mBalanceEditText.getText().toString();
            String walletNote = mNoteEditText.getText().toString();

            // Flurry Analytics gathering wallet info
            Map<String, String> walletParams = new HashMap<String, String>();
            walletParams.put("Wallet name", walletName);
            walletParams.put("Wallet balance", walletBalance);
            walletParams.put("User note", walletNote);
            FlurryAgent.logEvent("Wallet was added", walletParams);

            Wallet wallet = new Wallet(walletName, Double.parseDouble(walletBalance), walletNote);

            long wallet_id = db.createWallet(wallet);


            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }


        db.closeDB();
    }


}
