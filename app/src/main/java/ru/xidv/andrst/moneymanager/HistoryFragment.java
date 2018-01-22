package ru.xidv.andrst.moneymanager;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.xidv.andrst.sqlite.helper.DatabaseHelper;
import ru.xidv.andrst.sqlite.model.Income;
import ru.xidv.andrst.sqlite.model.Record;
import ru.xidv.andrst.sqlite.model.Wallet;

/**
 * Created by Александр on 03.12.2017.
 */

public class HistoryFragment extends Fragment {

    DatabaseHelper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_history, container, false);
        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Spinner periodSpinner = getView().findViewById(R.id.periodSpinner);
        final TextView periodLabel = getView().findViewById(R.id.periodLabel);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.period, android.R.layout.simple_spinner_item);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        periodSpinner.setAdapter(spinnerAdapter);


        periodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        final List<String> walletRecords = new ArrayList<String>();

        final ListView historyList = getView().findViewById(R.id.historyList);

        db = new DatabaseHelper(getActivity().getApplicationContext());

        final List<Record> RecordsArray = db.getAllWalletRecords();

        if (RecordsArray.size() > 0) {
            for ( int i = 0; i < RecordsArray.size(); i++ ) {
                Record record = RecordsArray.get(i);
                if (record.getExpenseAmount() != null) {
                    walletRecords.add('-' + record.getExpenseAmount() + "$   " + record.getWalletName()
                            + "  " + record.getExpenseSource() + "  " + record.getDate());
                }
                if (record.getIncomeAmount() != null) {
                    walletRecords.add('+' + record.getIncomeAmount() + "$   " + record.getWalletName()
                            + "  " + record.getIncomeSource() + "  " + record.getDate());
                }

            }

            ArrayAdapter<String> listAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                    android.R.layout.simple_list_item_1, walletRecords);

            historyList.setAdapter(listAdapter);

            listAdapter.notifyDataSetChanged();

            periodSpinner.setVisibility(View.VISIBLE);
            periodLabel.setVisibility(View.VISIBLE);


            historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {

                    Context context = getActivity().getApplicationContext();
                    int duration = Toast.LENGTH_SHORT;
                    String text = "Note:\n" + RecordsArray.get(position).getRecordNote();
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            });
        } else {
            periodSpinner.setVisibility(View.INVISIBLE);
            periodLabel.setVisibility(View.INVISIBLE);
        }

        db.closeDB();
    }

}
