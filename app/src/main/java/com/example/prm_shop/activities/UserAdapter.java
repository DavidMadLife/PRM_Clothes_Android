package com.example.prm_shop.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prm_shop.R;
import com.example.prm_shop.models.response.UserResponse;

import java.util.List;

public class UserAdapter extends BaseAdapter {

    private Context context;
    private List<UserResponse> userList;
    private SparseBooleanArray itemVisibilityArray = new SparseBooleanArray();
    public static final int REQUEST_CODE_EDIT_USER = 1;

    public UserAdapter(Context context, List<UserResponse> userList) {
        this.context = context;
        this.userList = userList;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return userList.get(position).getMemberId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        }

        UserResponse user = userList.get(position);

        TextView userNumber = convertView.findViewById(R.id.userNumber);
        TextView userName = convertView.findViewById(R.id.userName);
        Button editButton = convertView.findViewById(R.id.editButton);
        Button deleteButton = convertView.findViewById(R.id.deleteButton);

        userNumber.setText(String.valueOf(position + 1) + ".");
        userName.setText(user.getUserName());

        // Set initial visibility based on the state in SparseBooleanArray
        boolean isVisible = itemVisibilityArray.get(position, false);
        editButton.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        deleteButton.setVisibility(isVisible ? View.VISIBLE : View.GONE);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isVisible = itemVisibilityArray.get(position, false);
                itemVisibilityArray.put(position, !isVisible);
                notifyDataSetChanged();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditUserActivity.class);
                intent.putExtra("USER_ID", user.getMemberId());
                intent.putExtra("USER_NAME", user.getUserName());
                intent.putExtra("EMAIL", user.getEmail());
                intent.putExtra("PHONE", user.getPhone());
                intent.putExtra("ADDRESS", user.getAddress());
                ((Activity) context).startActivityForResult(intent, REQUEST_CODE_EDIT_USER);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog(user.getMemberId());
            }
        });

        return convertView;
    }

    private void showDeleteConfirmationDialog(final long userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete User");
        builder.setMessage("Are you sure you want to delete this user?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((UserListActivity) context).deleteUser(userId);
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }
}
