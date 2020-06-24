package com.mnk.env;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Objects;

public class DatePickerFragment extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            Calendar calendar = Calendar.getInstance();
            return new DatePickerDialog(Objects.requireNonNull(getActivity()), (DatePickerDialog.OnDateSetListener) getActivity(),
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DATE));
        }
}
