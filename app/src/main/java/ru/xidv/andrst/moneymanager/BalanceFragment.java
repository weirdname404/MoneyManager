package ru.xidv.andrst.moneymanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.xidv.andrst.sqlite.helper.DatabaseHelper;
import ru.xidv.andrst.sqlite.model.Wallet;


/**
 * Created by Александр on 03.12.2017.
 */

public class BalanceFragment extends Fragment {

    DatabaseHelper db;
    private static final String LOG = "BalanceFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.tab_balance, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Spinner walletSpinner = (Spinner) getView().findViewById(R.id.spinner);
        final TextView balanceLabel = getView().findViewById(R.id.balance);
        final List<String> walletNames = new ArrayList<String>();
        final Button expenseButton = getView().findViewById(R.id.expenseButton);
        final Button incomeButton = getView().findViewById(R.id.incomeButton);


        db = new DatabaseHelper(getActivity().getApplicationContext());

        final List<Wallet> WalletArray = db.getAllWallets();

        if (WalletArray.size() > 0) {
            for ( int i = 0; i < WalletArray.size(); i++ ) {
                walletNames.add(WalletArray.get(i).getWalletName());

            }

            walletSpinner.setVisibility(View.VISIBLE);
            expenseButton.setVisibility(View.VISIBLE);
            incomeButton.setVisibility(View.VISIBLE);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                    android.R.layout.simple_spinner_item, walletNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            walletSpinner.setAdapter(adapter);

            //spinner value changed
            walletSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @SuppressLint("SetTextI18n")
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                    if (walletNames.size() > 0) {
                        try {

                            balanceLabel.setText(String.valueOf(WalletArray.get(walletSpinner.
                                    getSelectedItemPosition()).getCurrentBalance()) + "$");

                        } catch (IndexOutOfBoundsException e) {
                            Context context = getActivity().getApplicationContext();
                            CharSequence text = "Error IndexOutOfBoundsException";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    } else {
                        balanceLabel.setText("Please add a wallet!");
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });

        } else {
            walletSpinner.setVisibility(View.INVISIBLE);
            expenseButton.setVisibility(View.INVISIBLE);
            incomeButton.setVisibility(View.INVISIBLE);
        }


        db.closeDB();
    }

}

