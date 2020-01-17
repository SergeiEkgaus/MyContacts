package com.android.uraall.mycontacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactAdapter
        extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private ArrayList<Contact> contactArrayList = new ArrayList<>();
    private MainActivity mainActivity;

    public ContactAdapter(ArrayList<Contact> contactArrayList,
                          MainActivity mainActivity) {
        this.contactArrayList = contactArrayList;
        this.mainActivity = mainActivity;
    }

    public void setContactArrayList(ArrayList<Contact> contactArrayList) {
        this.contactArrayList = contactArrayList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list_item, parent, false);

        return new ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, final int position) {

        final Contact contact = contactArrayList.get(position);

        holder.firstNameTextView.setText(contact.getFirstName());
        holder.lastNameTextView.setText(contact.getLastName());
        holder.emailTextView.setText(contact.getEmail());
        holder.phoneNumberTextView.setText(contact.getPhoneNumber());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.addAndEditContact(true, contact, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactArrayList.size();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {

        private TextView firstNameTextView;
        private TextView lastNameTextView;
        private TextView emailTextView;
        private TextView phoneNumberTextView;


        public ContactViewHolder(@NonNull View itemView) {

            super(itemView);

            firstNameTextView = itemView.findViewById(R.id.firstNameTextView);
            lastNameTextView = itemView.findViewById(R.id.lastNameTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            phoneNumberTextView = itemView.findViewById(R.id.phoneNumberTextView);


        }
    }


}
