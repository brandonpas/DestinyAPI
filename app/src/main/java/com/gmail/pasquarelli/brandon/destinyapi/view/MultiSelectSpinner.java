package com.gmail.pasquarelli.brandon.destinyapi.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import com.gmail.pasquarelli.brandon.destinyapi.R;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.subjects.PublishSubject;

public class MultiSelectSpinner extends android.support.v7.widget.AppCompatSpinner implements
        DialogInterface.OnMultiChoiceClickListener {

    PublishSubject<Boolean[]> selectedItemsObservable;

    String[] itemValuesForSelection = null;
    boolean[] selectedItems = null;
    boolean[] priorSelections = null;
    String spinnerDisplayText = null;

    ArrayAdapter<String> dataAdapter;

    public MultiSelectSpinner(Context context) {
        super(context);

        dataAdapter = new ArrayAdapter<>(context, R.layout.weapon_perk_filter_row_item);
        super.setAdapter(dataAdapter);
        selectedItemsObservable = PublishSubject.create();
    }

    /**
     * Required constructor for Android to inflate the layout.
     * @param context Context reference
     * @param attrs XML attributes
     */
    public MultiSelectSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);

        dataAdapter = new ArrayAdapter<>(context, R.layout.weapon_perk_filter_row_item);
        super.setAdapter(dataAdapter);
    }

    public PublishSubject<Boolean[]> getSelectedItemsObservable() {
        return selectedItemsObservable;
    }

    /**
     * Handle item selection clicks
     * @param dialog The dialog interface screen
     * @param which The position in the array
     * @param isChecked The item state
     */
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (selectedItems != null && which < selectedItems.length) {
            selectedItems[which] = isChecked;
            dataAdapter.clear();
            dataAdapter.add(getSelectedValues());
        } else {
            throw new IllegalArgumentException("Argument 'which' is out of bounds.");
        }
    }

    /**
     * Build and display the dialog box of selected items/items available for selection.
     * Intentionally not calling super.performClick() because doing so leaves a drop shadow
     * around the spinner requiring a second click in order to hide.
     *
     * @return Always true
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean performClick() {
        //
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.weapon_perk_filter_dialog_title));
        builder.setMultiChoiceItems(itemValuesForSelection, selectedItems, this);
        spinnerDisplayText = getSelectedValues();
        builder.setPositiveButton("Filter", (dialog, which) -> {
            System.arraycopy(selectedItems, 0, priorSelections, 0, selectedItems.length);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dataAdapter.clear();
            dataAdapter.add(spinnerDisplayText);
            System.arraycopy(priorSelections, 0, selectedItems, 0, priorSelections.length);
        });
        builder.show();
        return true;
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        throw new RuntimeException("setAdapter should not be called directly.");
    }

    /**
     * Set the list of values to display in the multi-select list.
     * @param items Array of values
     */
    public void setValueList(String[] items) {
        itemValuesForSelection = items;
        selectedItems = new boolean[itemValuesForSelection.length];
        priorSelections = new boolean[itemValuesForSelection.length];
        dataAdapter.clear();
        dataAdapter.add(itemValuesForSelection[0]);
        Arrays.fill(selectedItems, false);
        selectedItems[0] = true;
        priorSelections[0] = true;
    }

    /**
     * Initialize the selected items from a list.
     * @param selectionList Array of integers representing positions in the array to
     *                      make selected.
     */
    public void setSelection(int[] selectionList) {
        Arrays.fill(selectedItems, false);
        Arrays.fill(priorSelections, false);
        for (int row : selectionList) {
            selectedItems[row] = true;
            priorSelections[row] = true;
        }
        dataAdapter.clear();
        dataAdapter.add(getSelectedValues());
    }

    /**
     * Initialize the selected items for a single row.
     * @param index Position in the array to select
     */
    public void setSelection(int index) {
        for (int i = 0; i < selectedItems.length; i++) {
            selectedItems[i] = false;
            priorSelections[i] = false;
        }
        if (index >= 0 && index < selectedItems.length) {
            selectedItems[index] = true;
            priorSelections[index] = true;
        }
        dataAdapter.clear();
        dataAdapter.add(getSelectedValues());
    }

    /**
     * Return a List of the selected items (positions) in the array of values for the spinner.
     * @return The List.
     */
    public List<Integer> getSelectedItems() {
        List<Integer> selection = new LinkedList<>();
        for (int i = 0; i < itemValuesForSelection.length; ++i) {
            if (selectedItems[i])
                selection.add(i);
        }
        return selection;
    }

    /**
     * Returns the values of the selected items as a string for display purposes
     * @return String of selected values
     */
    public String getSelectedValues() {
        StringBuilder sb = new StringBuilder();
        boolean found = false;

        for (int i = 0; i < itemValuesForSelection.length; ++i) {
            if (selectedItems[i]) {
                if (found)
                    sb.append(", ");
                found = true;
                sb.append(itemValuesForSelection[i]);
            }
        }
        return sb.toString();
    }
}