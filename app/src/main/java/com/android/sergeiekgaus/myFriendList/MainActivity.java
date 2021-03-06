package com.android.sergeiekgaus.myFriendList;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.sergeiekgaus.myFriendList.databinding.ActivityMainBinding;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;


public class MainActivity extends AppCompatActivity {

    private MyContactsDatabase myContactsDatabase;
    private ArrayList<Contact> contactArrayList = new ArrayList<>();
    private ContactAdapter contactAdapter;

    private ActivityMainBinding activityMainBinding;
    private MainActivityButtonHandler buttonHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        activityMainBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_main);
        buttonHandler = new MainActivityButtonHandler(this);
        activityMainBinding.setButtonHandler(buttonHandler);


        RecyclerView recyclerView = activityMainBinding.layoutContentMain.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        contactAdapter = new ContactAdapter(contactArrayList, MainActivity.this);
        recyclerView.setAdapter(contactAdapter);

        myContactsDatabase = Room.databaseBuilder(getApplicationContext(),
                MyContactsDatabase.class, "ContactsDB").build();
        
        loadContacts();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Contact contact =
                        contactArrayList.get(viewHolder.getAdapterPosition());
                deleteContact(contact);
            }
        }).attachToRecyclerView(recyclerView);

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                addAndEditContact(false, null, -1);
//            }
//        });
    }

    public void addAndEditContact(final boolean  isUpdate, final Contact contact,
                                  final int position) {

        LayoutInflater layoutInflater = LayoutInflater
                .from(getApplicationContext());
        View view = layoutInflater.inflate(R.layout.add_edit_contact, null);

        AlertDialog.Builder builder =
                new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);

        TextView contactTitleTextView =
                view.findViewById(R.id.contactTitleTextView);
        final EditText firstNameEditText =
                view.findViewById(R.id.firstNameEditText);
        final EditText lastNameEditText =
                view.findViewById(R.id.lastNameEditText);
        final EditText emailEditText =
                view.findViewById(R.id.emailEditText);
        final EditText phoneNumberEditText =
                view.findViewById(R.id.phoneNumberEditText);

        contactTitleTextView.setText(!isUpdate ? "Add Contact" : "Edit contact");

        if (isUpdate && contact != null) {
            firstNameEditText.setText(contact.getFirstName());
            lastNameEditText.setText(contact.getLastName());
            emailEditText.setText(contact.getEmail());
            phoneNumberEditText.setText(contact.getPhoneNumber());
        }

        builder.setCancelable(false)
                .setPositiveButton(isUpdate ? "Update" : "Save",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface,
                                                int i) {
                                if (TextUtils.isEmpty(firstNameEditText.getText()
                                        .toString())) {
                                    Toast.makeText(MainActivity.this,
                                            "Enter first name",
                                            Toast.LENGTH_SHORT).show();
                                } else if (TextUtils.isEmpty(lastNameEditText.getText()
                                        .toString())) {
                                    Toast.makeText(MainActivity.this,
                                            "Enter last name",
                                            Toast.LENGTH_SHORT).show();
                                } else if (TextUtils.isEmpty(emailEditText.getText()
                                        .toString())) {
                                    Toast.makeText(MainActivity.this,
                                            "Enter email",
                                            Toast.LENGTH_SHORT).show();
                                } else if (TextUtils.isEmpty(phoneNumberEditText.getText()
                                        .toString())) {
                                    Toast.makeText(MainActivity.this,
                                            "Enter phone number",
                                            Toast.LENGTH_SHORT).show();
                                } else {

                                    if (isUpdate && contact != null) {

                                        updateContact(
                                                firstNameEditText.getText().toString(),
                                                lastNameEditText.getText().toString(),
                                                emailEditText.getText().toString(),
                                                phoneNumberEditText.getText().toString(),
                                                position);

                                    } else {

                                        addContact(
                                                firstNameEditText.getText().toString(),
                                                lastNameEditText.getText().toString(),
                                                emailEditText.getText().toString(),
                                                phoneNumberEditText.getText().toString()
                                        );

                                    }

                                }
                            }
                        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void loadContacts() {

        new GetAllContactsAsyncTask().execute();

    }

    private void deleteContact(Contact contact) {
        new DeleteContactAsyncTask().execute(contact);
    }

    private void addContact(String firstName, String lastName,
                            String email, String phoneNumber) {

        Contact contact = new Contact(
                0,
                firstName,
                lastName,
                email,
                phoneNumber
        );

        new AddContactAsyncTask().execute(contact);
    }

    private void updateContact(String firstName, String lastName,
                               String email, String phoneNumber, int position) {
        Contact contact = contactArrayList.get(position);

        contact.setFirstName(firstName);
        contact.setLastName(lastName);
        contact.setEmail(email);
        contact.setPhoneNumber(phoneNumber);

        new UpdateContactAsyncTask().execute(contact);

        contactArrayList.set(position, contact);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class GetAllContactsAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            contactArrayList = (ArrayList<Contact>) myContactsDatabase
                    .getContactDao().getAllContacts();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            contactAdapter.setContactArrayList(contactArrayList);
        }
    }

    private class DeleteContactAsyncTask extends AsyncTask<Contact, Void, Void> {


        @Override
        protected Void doInBackground(Contact... contacts) {

            myContactsDatabase.getContactDao().deleteContact(contacts[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            loadContacts();
        }
    }

    private class AddContactAsyncTask extends AsyncTask<Contact, Void, Void> {


        @Override
        protected Void doInBackground(Contact... contacts) {

            myContactsDatabase.getContactDao().insertContact(contacts[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            loadContacts();
        }
    }

    private class UpdateContactAsyncTask extends AsyncTask<Contact, Void, Void> {


        @Override
        protected Void doInBackground(Contact... contacts) {

            myContactsDatabase.getContactDao().updateContact(contacts[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            loadContacts();
        }
    }

    public class MainActivityButtonHandler {

        Context context;

        public MainActivityButtonHandler(Context context) {
            this.context = context;
        }

        public void onButtonClicked(View view) {
            addAndEditContact(false, null, -1);
        }
    }

}
