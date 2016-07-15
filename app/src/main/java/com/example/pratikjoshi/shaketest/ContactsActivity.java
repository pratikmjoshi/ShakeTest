package com.example.pratikjoshi.shaketest;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class ContactsActivity extends AppCompatActivity {

    public final static String KEY_EXTRA_CONTACT_ID = "KEY_EXTRA_CONTACT_ID";

    private ListView listView;
    EmergContactDBHelper dbHelper;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        button= (Button)findViewById(R.id.addNew);
        dbHelper = ((ContactDB)getApplication()).dbHelper;

       button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dbHelper.checkCount()<1) {
                    Intent intent = new Intent(ContactsActivity.this, ContactsSetActivity.class);
                    intent.putExtra(KEY_EXTRA_CONTACT_ID, 0);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(ContactsActivity.this,"Only max 1 contact allowed",Toast.LENGTH_SHORT).show();
                }
            }
        });



        final Cursor cursor = dbHelper.getAllPersons();
        String [] columns = new String[] {
                EmergContactDBHelper.PERSON_COLUMN_ID,
                EmergContactDBHelper.PERSON_COLUMN_NAME,
                EmergContactDBHelper.PERSON_COLUMN_CONTACT
        };
        int [] widgets = new int[] {
                R.id.personID,
                R.id.personName,
                R.id.personContact
        };

        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.contact_info,
                cursor, columns, widgets, 0);
        listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(cursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView listView, View view,
                                    int position, long id) {
                Cursor itemCursor = (Cursor) ContactsActivity.this.listView.getItemAtPosition(position);
                int personID = itemCursor.getInt(itemCursor.getColumnIndex(EmergContactDBHelper.PERSON_COLUMN_ID));
                Intent intent = new Intent(getApplicationContext(), ContactsSetActivity.class);
                intent.putExtra(KEY_EXTRA_CONTACT_ID, personID);
                startActivity(intent);
            }
        });

    }

}

