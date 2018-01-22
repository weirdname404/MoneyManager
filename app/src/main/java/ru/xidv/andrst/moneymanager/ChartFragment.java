package ru.xidv.andrst.moneymanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


import ru.xidv.andrst.sqlite.helper.DatabaseHelper;
import ru.xidv.andrst.sqlite.model.Record;


public class ChartFragment extends Fragment {

    final String TAG = "States";

    DatabaseHelper db;
    String INCOME = "Income";
    String EXPENSE = "Expense";
    Float TOTAL_EXPENSES = 0f;
    Float TOTAL_INCOMES = 0f;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_chart, container, false);

        if (container == null) {
            return null;
        }

        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        FlurryAgent.logEvent("History and Chart Fragments are NOT Visible");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        // Flurry Analytics gathers time
        FlurryAgent.logEvent("History and Chart Fragments are Visible");

        super.onActivityCreated(savedInstanceState);

        final PieChart pieChart = getView().findViewById(R.id.pieChart);
        final Spinner walletSpinner = getView().findViewById(R.id.walletChartSpinner);
        final Spinner recordSpinner = getView().findViewById(R.id.expenseIncomeSpinner);
        final Set<String> uniqueWalletNames = new TreeSet<>();

        db = new DatabaseHelper(getActivity().getApplicationContext());
        final List<Record> RecordsArray = db.getAllWalletRecords();

        if (RecordsArray.size() > 0) {

            for ( int i = 0; i < RecordsArray.size(); i++ ) {
                uniqueWalletNames.add(RecordsArray.get(i).getWalletName());
            }

            final ArrayList<String> walletNames = new ArrayList<>(uniqueWalletNames);

            // Setting data for walletSpinner with wallet names
            ArrayAdapter<String> walletAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                    android.R.layout.simple_spinner_item, walletNames);
            walletAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            walletSpinner.setAdapter(walletAdapter);

            // Setting data for Expense/Income Spinner
            ArrayAdapter<CharSequence> recordAdapter = ArrayAdapter.createFromResource(getActivity(),
                    R.array.chartType, android.R.layout.simple_spinner_item);
            recordAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            recordSpinner.setAdapter(recordAdapter);

            walletSpinner.setVisibility(View.VISIBLE);
            recordSpinner.setVisibility(View.VISIBLE);

            // record spinner value changed
            recordSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @SuppressLint("SetTextI18n")
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                    addDataSet(pieChart, recordSpinner.getSelectedItem().toString().equals(INCOME) ? INCOME : EXPENSE);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });

            // wallet spinner value changed
            walletSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @SuppressLint("SetTextI18n")
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                    addDataSet(pieChart, recordSpinner.getSelectedItem().toString().equals(INCOME) ? INCOME : EXPENSE);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });


            pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

//                (e.getX()/TOTAL_INCOMES) * 100f)+ "%\n"

                @SuppressLint("DefaultLocale")
                @Override
                public void onValueSelected(Entry e, Highlight h) {

                    if (recordSpinner.getSelectedItem().equals(INCOME)) {
                        Toast.makeText(getActivity(), String.valueOf(String.format("%.2f",
                                (e.getY() / TOTAL_INCOMES) * 100.0f)) + "% of your total " + INCOME, Toast.LENGTH_LONG).show();
                    }

                    if (recordSpinner.getSelectedItem().equals(EXPENSE)) {
                        Toast.makeText(getActivity(), String.valueOf(String.format("%.2f",
                                (e.getY() / TOTAL_EXPENSES) * 100.0f)) + "% of your total " + EXPENSE, Toast.LENGTH_LONG).show();
                    }


                }

                @Override
                public void onNothingSelected() {

                }
            });


        } else {
            recordSpinner.setVisibility(View.INVISIBLE);
            walletSpinner.setVisibility(View.INVISIBLE);
        }


        db.closeDB();
    }

    private void addDataSet(PieChart pieChart, String request) {

        db = new DatabaseHelper(getActivity().getApplicationContext());


        final List<Record> RecordsArray = db.getAllWalletRecords();

//        TODO refactoring
//        if (request.equals(income)) {}
        TOTAL_INCOMES = sumAllRecordAmount(RecordsArray, false);
        TOTAL_EXPENSES = sumAllRecordAmount(RecordsArray, true);

        List<PieEntry> expenseEntries = new ArrayList<>();
        List<PieEntry> incomeEntries = new ArrayList<>();

        final Spinner walletSpinner = getView().findViewById(R.id.walletChartSpinner);


        List<String> entryIncomeSourceArray = new ArrayList<>();
        List<String> entryExpenseSourceArray = new ArrayList<>();

        for ( int i = 0; i < RecordsArray.size(); i++ ) {
            float accIncomeValue = 0f;
            float accExpenseValue = 0f;
            boolean incomeSourceShouldBeAdded = false;
            boolean expenseSourceShouldBeAdded = false;

            String incomeSourceToBeAdded = RecordsArray.get(i).getIncomeSource();
            String expenseSourceToBeAdded = RecordsArray.get(i).getExpenseSource();

            if (entryIncomeSourceArray.contains(incomeSourceToBeAdded) ||
                    entryExpenseSourceArray.contains(expenseSourceToBeAdded)) {
                continue;
            }


            for ( int j = 0; j < RecordsArray.size(); j++ ) {

                Record currentRecord = RecordsArray.get(j);
                String comparedIncomeSource = RecordsArray.get(j).getIncomeSource();
                String comparedExpenseSource = RecordsArray.get(j).getExpenseSource();

                if (currentRecord.getIncomeAmount() != null
                        && currentRecord.getWalletName().equals(walletSpinner.getSelectedItem().toString())
                        && comparedIncomeSource.equals(incomeSourceToBeAdded)) {

                    accIncomeValue += Float.parseFloat(currentRecord.getIncomeAmount());
                    incomeSourceShouldBeAdded = true;
                }


                if (currentRecord.getExpenseAmount() != null
                        && currentRecord.getWalletName().equals(walletSpinner.getSelectedItem().toString())
                        && comparedExpenseSource.equals(expenseSourceToBeAdded)) {

                    accExpenseValue += Float.parseFloat(currentRecord.getExpenseAmount());
                    expenseSourceShouldBeAdded = true;

                }

//                else {continue;}

            }

//            (accIncomeValue/totalIncomes) * 100f
//            accExpenseValue/totalExpenses) * 100f

            if (incomeSourceToBeAdded != null && incomeSourceShouldBeAdded) {
                entryIncomeSourceArray.add(incomeSourceToBeAdded);
                incomeEntries.add(new PieEntry(accIncomeValue, incomeSourceToBeAdded));
            }

            if (expenseSourceToBeAdded != null && expenseSourceShouldBeAdded) {
                entryExpenseSourceArray.add(expenseSourceToBeAdded);
                expenseEntries.add(new PieEntry(accExpenseValue, expenseSourceToBeAdded));

            }


        }


        PieDataSet set = new PieDataSet(request.equals(INCOME) ? incomeEntries : expenseEntries,
                (request.equals(INCOME) ? INCOME : EXPENSE) + " Sources in $");

        pieChart.setRotationEnabled(false);
        //pieChart.setUsePercentValues(true);
        //pieChart.setHoleColor(Color.BLUE);
        //pieChart.setCenterTextColor(Color.BLACK);
        pieChart.setHoleRadius(22f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setCenterTextSize(10);
        //pieChart.setDrawEntryLabels(true);
//        pieChart.setEntryLabelTextSize(20);

        set.setSliceSpace(2);
        set.setValueTextSize(12);

        //add colors to dataset
        final int[] colors = {
                rgb("#ff6961"), rgb("#f5880d"), rgb("#fbcc38"),
                rgb("#77dd77"), rgb("#009a00"), rgb("#d562be"),
                rgb("#61a8ff"), rgb("#ada1e6"), rgb("#6d8008")
        };


        set.setColors(colors);


        //add legend to chart
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);


        PieData data = new PieData(set);
        pieChart.setData(data);
        pieChart.animateY(1000);
        pieChart.invalidate(); // refresh

        db.closeDB();
    }

    public static int rgb(String hex) {
        int color = (int) Long.parseLong(hex.replace("#", ""), 16);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        return Color.rgb(r, g, b);
    }


    public float sumAllRecordAmount(List<Record> array, boolean expenseAmount) {

        Spinner walletSpinner = getView().findViewById(R.id.walletChartSpinner);

        if (array.size() > 0) {
            float acc = 0;
            for ( int i = 0; i < array.size(); i++ ) {

                if (array.get(i).getWalletName().equals(walletSpinner.getSelectedItem().toString())) {

                    if (expenseAmount && array.get(i).getExpenseAmount() != null) {
                        acc += Float.parseFloat(array.get(i).getExpenseAmount());
                    }

                    if (!expenseAmount && array.get(i).getIncomeAmount() != null) {
                        acc += Float.parseFloat(array.get(i).getIncomeAmount());
                    }
                }
            }

            return acc;
        } else {
            return 0f;
        }
    }

}