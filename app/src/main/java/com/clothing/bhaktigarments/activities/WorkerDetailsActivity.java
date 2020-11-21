package com.clothing.bhaktigarments.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.clothing.bhaktigarments.R;
import com.clothing.bhaktigarments.classes.Product;
import com.clothing.bhaktigarments.classes.Worker;
import com.clothing.bhaktigarments.config.AppConfig;
import com.clothing.bhaktigarments.helpers.LocalDatabase;
import com.clothing.bhaktigarments.helpers.ResponseHandler;

import java.util.UUID;

public class WorkerDetailsActivity extends AppCompatActivity {

    // components
    private EditText nameED, machineNoED, contactED;
    private Button editBtn, delBtn;
    private TextView messageTV;

    // data
    private static String uid;
    private Worker worker;

    // helpers
    private LocalDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_details);
        Toolbar toolbar = findViewById(R.id.toolbar_WorkerDetailsActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        declarations();
        listeners();
    }

    private void listeners() {
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkData()) {
                    editWorkerDetails();
                }
            }
        });

        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    private void editWorkerDetails() {
        ResponseHandler response = db.editWorker(worker);
        if (response.getErrorCode() == 0) {
            showSuccessComponents("Worker Edited");
        } else {
            showErrorComponents(response.getErrorMessage(2));
        }
    }


    private void declarations() {
        if (uid != null) {
            uid = getIntent().getStringExtra(AppConfig.UID);
        }

        nameED = findViewById(R.id.editText_name_WorkerDetailsActivity);
        contactED = findViewById(R.id.editText_contact_no_WorkerDetailsActivity);
        machineNoED = findViewById(R.id.editText_machine_no_WorkerDetailsActivity);
        messageTV = findViewById(R.id.textView_message_WorkerDetailsActivity);
        editBtn = findViewById(R.id.button_edit_WorkerDetailsActivity);
        delBtn = findViewById(R.id.button_delete_WorkerDetailsActivity);

        db = new LocalDatabase(WorkerDetailsActivity.this);
        messageTV = findViewById(R.id.textView_message_WorkerDetailsActivity);
        worker = db.getWorkerByUid(uid);
        if (worker != null) {
            loadUIComponents();
        }
    }

    private void loadUIComponents() {
        nameED.setText(worker.getName());
        contactED.setText(worker.getContactNo());
        machineNoED.setText(worker.getMachineNo());
    }

    private boolean checkData() {
        // worker = new Worker();
        if (nameED.getText().toString().trim().isEmpty()) {
            showErrorComponents("Please Enter Name");
            return false;
        }
        if (contactED.getText().toString().trim().isEmpty()) {
            showErrorComponents("Please Enter Contact No.");
            return false;
        }
        if (contactED.getText().toString().length() != 10) {
            showErrorComponents("Please Enter Valid Contact No. upto 10 digits");
            return false;
        }
        if (machineNoED.getText().toString().trim().isEmpty()) {
            showErrorComponents("Please Enter Machine No.");
            return false;
        }
        // worker.setUid(UUID.randomUUID().toString());
        worker.setName(nameED.getText().toString().trim());
        worker.setContactNo(contactED.getText().toString().trim());
        worker.setMachineNo(machineNoED.getText().toString().trim());
        return true;
    }

    private void showErrorComponents(String message) {
        messageTV.setText(message);
        messageTV.setTextColor(Color.RED);
    }

    private void showSuccessComponents(String message) {
        messageTV.setText(message);
        messageTV.setTextColor(getColor(R.color.blue_medium));
    }

    private void showDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(WorkerDetailsActivity.this).create();
        alertDialog.setTitle("Warning");
        alertDialog.setCancelable(true);
        alertDialog.setMessage("Are you sure to delete it?");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.deleteWorker(worker.getUid());
            }
        });
        alertDialog.show();
    }

}