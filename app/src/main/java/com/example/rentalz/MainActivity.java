package com.example.rentalz;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private ArrayAdapter<String> arrayAdapter;
    private AutoCompleteTextView propertyList_type, bedroomList_type, furnitureList_type;
    private ArrayList<String> bedroomArray, furnitureArrayList, propertyArrayList;
    private TextInputEditText datetime_type, price_type, note_type, name_type;
    private AppCompatButton btn_submit,btn_clear;

    int day, month, year, hour, minute;
    int mday, mMonth, mYear, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hook();
        setPropertyType();
        setBedroom();
        setFurnitureListtype();
        setDateTime();
        validate();
        Error();
        clear();


    }

    private void Error() {
        propertyList_type.addTextChangedListener(propertyError);
        bedroomList_type.addTextChangedListener(bedroomError);
        furnitureList_type.addTextChangedListener(furnitureError);
        datetime_type.addTextChangedListener(dateTimeError);
        price_type.addTextChangedListener(monthlyPriceError);
        name_type.addTextChangedListener(nameReporterError);

    }


    private void validate() {
        btn_submit.setOnClickListener(view -> {
            String property = Objects.requireNonNull(propertyList_type.getText()).toString().trim();
            String bedroom = Objects.requireNonNull(bedroomList_type.getText()).toString().trim();
            String furniture = Objects.requireNonNull(furnitureList_type.getText()).toString().trim();
            String dateTime = Objects.requireNonNull(datetime_type.getText()).toString().trim();
            String price = Objects.requireNonNull(price_type.getText()).toString().trim();
            String name = Objects.requireNonNull(name_type.getText()).toString().trim();
            String note = Objects.requireNonNull(note_type.getText()).toString().trim();

            if (TextUtils.isEmpty(property)) {
                TextInputLayout til = findViewById(R.id.propertyList_type);
                til.setError(getString(R.string.validate_property_error));
                til.requestFocus();
            }

            if (TextUtils.isEmpty(bedroom)) {
                TextInputLayout til = findViewById(R.id.bedroomList_type);
                til.setError(getString(R.string.validate_bedrooms_error));
                til.requestFocus();
            }
            if (TextUtils.isEmpty(furniture)) {
                TextInputLayout til = findViewById(R.id.furnitureList_type);
                til.setError(getString(R.string.validate_furniture_error));
                til.requestFocus();
            }

            if (TextUtils.isEmpty(dateTime)) {
                TextInputLayout til = findViewById(R.id.datetime);
                til.setError(getString(R.string.validate_date_time_error));
                til.requestFocus();
            }

            if (TextUtils.isEmpty(price)) {
                TextInputLayout til = findViewById(R.id.price_type);
                til.setError(getString(R.string.validate_price));
                til.requestFocus();
            }

            if (TextUtils.isEmpty(name)) {
                TextInputLayout til = findViewById(R.id.name_type);
                til.setError(getString(R.string.validate_reporter));
                til.requestFocus();
            }

            if (TextUtils.isEmpty(property) || TextUtils.isEmpty(furniture) || TextUtils.isEmpty(bedroom) || TextUtils.isEmpty(dateTime) || TextUtils.isEmpty(price) || TextUtils.isEmpty(name)) {
                Toast.makeText(MainActivity.this, getString(R.string.empty_form), Toast.LENGTH_SHORT).show();

            } else {
                submit(property, bedroom, dateTime, furniture, price, note, name);
            }
        });
    }
    private void clear() {
        btn_clear.setOnClickListener(view -> {
            propertyList_type.getText().clear();
            bedroomList_type.getText().clear();
            furnitureList_type.getText().clear();
            Objects.requireNonNull(datetime_type.getText()).clear();
            Objects.requireNonNull(price_type.getText()).clear();
            Objects.requireNonNull(name_type.getText()).clear();
            Objects.requireNonNull(note_type.getText()).clear();
        });
    }
        private void submit(String property, String bedroom,
                            String dateTime, String furniture, String price, String note, String name) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
        builder.setTitle("Are you sure you want to submit these fields?");
        builder.setMessage("Property: " + property + "\n" + "Bedroom: " + bedroom +
                "\n" + "DateTime: " + dateTime + "\n" + "Furniture: " + furniture
                + "\n" + "Price: " + price + "$" + "\n" + "Note: " + note + "\n" + "Name: " + name);
        builder.setCancelable(true);

        builder.setPositiveButton(getString(R.string.confirm_btn), (dialogInterface, i) -> {

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> rentalDetails = new HashMap<>();
            rentalDetails.put("propertyType", property);
            rentalDetails.put("bedroom", bedroom);
            rentalDetails.put("dateTime", dateTime);
            rentalDetails.put("furnitureType", furniture);
            rentalDetails.put("monthlyPrice", price);
            rentalDetails.put("note", note);
            rentalDetails.put("nameOfReporter", name);
            db.collection("rentaldb").add(rentalDetails).addOnSuccessListener(documentReference -> {
                Toast.makeText(MainActivity.this, getString(R.string.result_toast), Toast.LENGTH_SHORT).show();
                dialogInterface.cancel();

            }).addOnFailureListener(e -> {
                Toast.makeText(MainActivity.this, getString(R.string.fail_toast), Toast.LENGTH_SHORT).show();
                dialogInterface.cancel();
            });
        });
        builder.setNegativeButton(getString(R.string.back_btn), (dialogInterface, i) -> dialogInterface.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void setDateTime() {
        datetime_type.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, MainActivity.this, year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

    }
    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        mYear = year;
        mday = day;
        mMonth = month;
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, MainActivity.this, hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        mHour = i;
        mMinute = i1;
        String m;
        if (mMinute < 10) {
            m = "0" + mMinute + "";
        } else {
            m = mMinute + "";
        }
        datetime_type.setText(mYear + "/" + (mMonth + 1) + "/" + mday + " " + mHour + ":" + m);
    }
    private void setPropertyType() {
        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item_list, propertyArrayList);
        propertyList_type.setAdapter(arrayAdapter);
        propertyList_type.setThreshold(1);
    }
    private void setBedroom() {
        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item_list, bedroomArray);
        bedroomList_type.setAdapter(arrayAdapter);
        bedroomList_type.setThreshold(1);
    }
    private void setFurnitureListtype() {
        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item_list, furnitureArrayList);
        furnitureList_type.setAdapter(arrayAdapter);
        furnitureList_type.setThreshold(1);
    }

    //Property Error
    private final TextWatcher propertyError = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        @Override
        public void afterTextChanged(Editable editable) {
            TextInputLayout layout = findViewById(R.id.name_type);
            if (editable.length() == 0) {
                layout.setErrorEnabled(true);
                layout.setError(getString(R.string.validate_property_error));
                layout.requestFocus();
            } else {
                layout.setErrorEnabled(false);
            }

        }
    };

    //Bedroom Error
    private final TextWatcher bedroomError = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            TextInputLayout layout = findViewById(R.id.bedroomList_type);
            if (editable.length() == 0) {
                layout.setErrorEnabled(true);
                layout.setError(getString(R.string.validate_bedrooms_error));
                layout.requestFocus();
            } else {
                layout.setErrorEnabled(false);
            }

        }
    };

    //Date time Error
    private final TextWatcher dateTimeError = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            TextInputLayout layout = findViewById(R.id.datetime);
            if (editable.length() == 0) {
                layout.setErrorEnabled(true);
                layout.setError(getString(R.string.validate_date_time_error));
                layout.requestFocus();
            } else {
                layout.setErrorEnabled(false);
            }

        }
    };
    //Furniture Error
    private final TextWatcher furnitureError = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        @Override
        public void afterTextChanged(Editable editable) {
            TextInputLayout layout = findViewById(R.id.furnitureList_type);
            if (editable.length() == 0) {
                layout.setErrorEnabled(true);
                layout.setError(getString(R.string.validate_furniture_error));
                layout.requestFocus();
            } else {
                layout.setErrorEnabled(false);
            }

        }
    };

    //Price Error
    private final TextWatcher monthlyPriceError = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            TextInputLayout layout = findViewById(R.id.price_type);
            if (editable.length() == 0) {
                layout.setErrorEnabled(true);
                layout.setError(getString(R.string.validate_price));
                layout.requestFocus();
            } else {
                layout.setErrorEnabled(false);
            }

        }
    };

    //Name Error
    private final TextWatcher nameReporterError = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            TextInputLayout layout = findViewById(R.id.name_type);
            if (editable.length() == 0) {
                layout.setErrorEnabled(true);
                layout.setError(getString(R.string.validate_reporter));
                layout.requestFocus();
            } else {
                layout.setErrorEnabled(false);
            }

        }
    };

    private void hook() {
        propertyList_type = findViewById(R.id.propertyText);
        bedroomList_type = findViewById(R.id.bedroomText);
        datetime_type = findViewById(R.id.datetimeText);
        price_type = findViewById(R.id.priceText);
        note_type = findViewById(R.id.noteText);
        name_type = findViewById(R.id.nameText);
        furnitureList_type = findViewById(R.id.furnitureText);

        btn_submit = findViewById(R.id.btn_submit);
        btn_clear = findViewById(R.id.btn_clear);
        propertyArrayList = new ArrayList<>();
        propertyArrayList.add("House");
        propertyArrayList.add("Flat");
        propertyArrayList.add("Bungalow");

        bedroomArray = new ArrayList<>();
        bedroomArray.add("Studio");
        bedroomArray.add("One");
        bedroomArray.add("Two");
        bedroomArray.add("Three");
        bedroomArray.add("Four");
        bedroomArray.add("Five");
        bedroomArray.add("Six");
        bedroomArray.add("Seven");
        bedroomArray.add("Eight");
        bedroomArray.add("Nine");

        furnitureArrayList = new ArrayList<>();
        furnitureArrayList.add("Furnished");
        furnitureArrayList.add("Unfurnished");
        furnitureArrayList.add("Part Furnished");
    }
}