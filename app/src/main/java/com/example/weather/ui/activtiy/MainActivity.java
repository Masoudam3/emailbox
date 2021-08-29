package com.example.weather.ui.activtiy;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.weather.model.Message;
import com.example.weather.R;
import com.example.weather.ui.adapter.MessageAdapter;
import com.example.weather.utils.App;
import com.example.weather.utils.MessageUtils;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements  MessageAdapter.MessageAdapterListener, SwipeRefreshLayout.OnRefreshListener {

    List<Message> messageList;
    CoordinatorLayout coordinatorLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    MessageAdapter messageAdapter;

    ActionMode actionMode;
    ActionMode.Callback actionModeCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar( findViewById(R.id.toolbar));
        init();
        swipeRefreshLayout.post(this::getMessages);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_search){
            Snackbar.make(coordinatorLayout, "Search !!!", Snackbar.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void init(){
        messageList = new ArrayList<>();
        coordinatorLayout =  findViewById(R.id.coordinator_layout);
        swipeRefreshLayout =  findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView =  findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        actionModeCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);
                swipeRefreshLayout.setEnabled(false);   //disable
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if(item.getItemId() == R.id.action_delete){
                    deleteMessages();
                    mode.finish();
                    return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                messageAdapter.clearSelections();
                swipeRefreshLayout.setEnabled(true);
                actionMode = null;
                recyclerView.post(() -> messageAdapter.resetAnimations());
            }
        };
    }

    @SuppressLint("NotifyDataSetChanged")
    private void deleteMessages() {
        List<Integer> items = messageAdapter.getSelectedItems();
        for(int index : items){
            messageAdapter.removeData(index);
        }
        messageAdapter.notifyDataSetChanged();
    }


    private void updateDisplay(){
        messageAdapter = new MessageAdapter( messageList, this);
        recyclerView.setAdapter(messageAdapter);
    }



    private void getMessages(){
        JsonArrayRequest request = new JsonArrayRequest(
                App.BASE_URL_CLOUD,
                response -> {
                    swipeRefreshLayout.setRefreshing(false);
                    messageList = MessageUtils.jsonArrayToMessageList(response);
                    updateDisplay();
                },
                error -> {
                    //messageList.clear();
                    swipeRefreshLayout.setRefreshing(false);
                    Snackbar sb = Snackbar.make(
                            coordinatorLayout, "Error Loading Messages", Snackbar.LENGTH_SHORT);
                    sb.getView().setBackgroundColor(Color.RED);
                    sb.show();
                }
        );
        swipeRefreshLayout.setRefreshing(true);
        App.getRequestQueue().add(request);
    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onMessageClicked(int position) {
        if(messageAdapter.getSelectedItemCount() > 0){
            enableActionMode(position);
        } else {
            Message msg = messageList.get(position);
            msg.setRead(true);
            messageList.set(position, msg);
            messageAdapter.notifyDataSetChanged();

            Snackbar.
                    make(coordinatorLayout, "Read : " + msg.getMessage(), Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onIconImportantClicked(int position) {
        Message msg = messageList.get(position);
        msg.setImportant(! msg.isImportant());
        messageList.set(position, msg);
        messageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRowLongClicked(int position) {
        enableActionMode(position);
    }

    @Override
    public void onIconClicked(int position) {
        enableActionMode(position);
    }

    private void toggleSelection(int position){
        messageAdapter.toggleSelection(position);
        int count = messageAdapter.getSelectedItemCount();
        if(count == 0){
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    private void enableActionMode(int position){
        if(actionMode == null){
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }
    @Override
    public void onRefresh() {
        getMessages();
    }
}
