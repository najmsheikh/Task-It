package me.najmsheikh.taskit;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;


public class TaskActivity extends ActionBarActivity {
    public static final String EXTRA = "TaskExtra";

    private Calendar mCal;
    private Task mTask;
    private Button mDateButton;
    private EditText mTaskNameInput;
    private CheckBox mDoneBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTask = (Task) getIntent().getSerializableExtra(EXTRA);
        if (mTask == null)
            mTask = new Task();

        mCal = Calendar.getInstance();
        mTaskNameInput = (EditText) findViewById(R.id.task_name);
        mDateButton = (Button) findViewById(R.id.task_date);
        mDoneBox = (CheckBox) findViewById(R.id.task_done);
        Button saveButton = (Button) findViewById(R.id.task_savebutton);

        mTaskNameInput.setText(mTask.getName());
        if (mTask.getDueDate() == null) {
            mCal.setTime(new Date());
            mDateButton.setText(getResources().getText(R.string.no_date));
        } else {
            mCal.setTime(mTask.getDueDate());
            DateFormat df = DateFormat.getDateInstance();
            mDateButton.setText(df.format(mTask.getDueDate()));
        }

        mDoneBox.setChecked(mTask.isDone());


        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dp = new DatePickerDialog(TaskActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                        Toast.makeText(getApplicationContext(),"Day is " + dayOfMonth,Toast.LENGTH_SHORT);
                        mCal.set(Calendar.YEAR, year);
                        mCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        mCal.set(Calendar.MONTH, monthOfYear);

                        DateFormat df = DateFormat.getDateInstance();
                        mDateButton.setText(df.format(mCal.getTime()));
                    }
                }, mCal.get(Calendar.YEAR), mCal.get(Calendar.MONTH), mCal.get(Calendar.DAY_OF_MONTH));
                dp.show();

            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTask.setName(mTaskNameInput.getText().toString());
                mTask.setDueDate(mCal.getTime());
                mTask.setDone(mDoneBox.isChecked());

                Intent i = new Intent();
                i.putExtra(EXTRA, mTask);
                setResult(RESULT_OK, i);
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_up_out);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_up_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
