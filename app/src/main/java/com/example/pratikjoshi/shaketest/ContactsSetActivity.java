package com.example.pratikjoshi.shaketest;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ContactsSetActivity extends AppCompatActivity implements View.OnClickListener {
    private EmergContactDBHelper dbHelper;
    EditText editName;
    EditText editContact;
    Button btnSave, btnDeleteConfirm, btnEditConfirm;
    TextView txtAddNew;
    int personID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        personID = getIntent().getIntExtra(ContactsActivity.KEY_EXTRA_CONTACT_ID, 0);

        setContentView(R.layout.activity_contacts_set);
        txtAddNew=(TextView)findViewById(R.id.txtAddNew);
        editName = (EditText) findViewById(R.id.editName);
        editContact = (EditText) findViewById(R.id.editContact);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        btnEditConfirm = (Button) findViewById(R.id.btnEditConfirm);
        btnEditConfirm.setOnClickListener(this);
        btnDeleteConfirm = (Button) findViewById(R.id.btnDeleteConfirm);
        btnDeleteConfirm.setOnClickListener(this);

        dbHelper = new EmergContactDBHelper(this);

        if (personID > 0) {
            btnSave.setVisibility(View.GONE);
            txtAddNew.setVisibility(View.GONE);

            Cursor rs = dbHelper.getPerson(personID);
            rs.moveToFirst();
            String personName = rs.getString(rs.getColumnIndex(EmergContactDBHelper.PERSON_COLUMN_NAME));
            String personContact = rs.getString(rs.getColumnIndex(EmergContactDBHelper.PERSON_COLUMN_CONTACT));
            if (!rs.isClosed()) {
                rs.close();
            }

            editName.setText(personName);
            editName.setFocusable(false);
            editName.setClickable(false);

            editContact.setText(personContact);
            editContact.setFocusable(false);
            editContact.setClickable(false);

        }
        else{
            btnDeleteConfirm.setVisibility(View.GONE);
            btnEditConfirm.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSave:
                persistPerson();
                return;
            case R.id.btnEditConfirm:
                btnSave.setVisibility(View.VISIBLE);
                btnEditConfirm.setVisibility(View.GONE);
                btnDeleteConfirm.setVisibility(View.GONE);
                editName.setEnabled(true);
                editName.setFocusableInTouchMode(true);
                editName.setClickable(true);

                editContact.setEnabled(true);
                editContact.setFocusableInTouchMode(true);
                editContact.setClickable(true);

                return;
            case R.id.btnDeleteConfirm:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Delete Person")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dbHelper.deletePerson(personID);
                                Toast.makeText(getApplicationContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), ContactsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                AlertDialog d = builder.create();
                d.setTitle("Delete Person?");
                d.show();
                return;
        }

    }
    public void persistPerson() {
        if (personID > 0) {
            if (dbHelper.updatePerson(personID, editName.getText().toString(),
                    editContact.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Person Update Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), ContactsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Person Update Failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (dbHelper.insertPerson(editName.getText().toString(),
                    editContact.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Person Inserted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Could not Insert person", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(getApplicationContext(), ContactsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}
