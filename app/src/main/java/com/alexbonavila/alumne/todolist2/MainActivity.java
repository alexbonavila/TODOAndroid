package com.alexbonavila.alumne.todolist2;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import java.lang.reflect.Type;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String SHARED_PREFERENCES_TODOS = "SP_TODOS";
    private static final String TODO_LIST = "todo_list";
    private Gson gson;
    public TodoArrayList tasks;
    private CustomListAtapter adapter;
    private String taskName;
    private  View positiveAction;
    private SwipeRefreshLayout swipeContainer;
    private String todoList;
    private SharedPreferences todos;
    private NetworkInfo networkInfo;
    private boolean networkInfoWifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                loadJson();
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        loadJson();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        FloatingActionButton fabRemove = (FloatingActionButton) findViewById(R.id.fabremove);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (tasks == null) {
            return;
        }

        String tasksToSave = gson.toJson(tasks);

        SharedPreferences todos = getSharedPreferences(SHARED_PREFERENCES_TODOS, 0);
        SharedPreferences.Editor editor = todos.edit();
        editor.putString(TODO_LIST, tasksToSave);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void loadJson(){
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();
        networkInfoWifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
        if (networkInfo != null && networkInfo.isConnected()) {
            if (!networkInfoWifi) {
                Toast.makeText(getApplicationContext(), "You aren't having WiFi connection", Toast.LENGTH_LONG).show();
            }
            downloadJson();
        } else {
            swipeContainer.setRefreshing(false);
            Toast.makeText(getApplicationContext(), "Are you having internet connection?", Toast.LENGTH_LONG).show();
            todos = getSharedPreferences(SHARED_PREFERENCES_TODOS, 0);
            todoList = todos.getString(TODO_LIST, null);


            if (todoList == null) {
                String initial_json = "[{name:\"Example Task\", \"done\": false, \"priority\": 2}]";
                SharedPreferences.Editor editor = todos.edit();
                editor.putString(TODO_LIST, initial_json);
                editor.commit();
                todoList = todos.getString(TODO_LIST, null);
            }
            updateJson();
        }
    }

    public void downloadJson(){
        Ion.with(this)
                .load("http://acacha.github.io/json-server-todos/db_todos.json")
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        todoList = result.toString();
                        Log.d("TAG_PROVA AAAA ", todoList);
                        updateJson();
                    }
                });
    }

    public void updateJson(){
        Type arrayTodoList = new TypeToken<TodoArrayList>() {
        }.getType();
        this.gson = new Gson();
        TodoArrayList temp = gson.fromJson(todoList, arrayTodoList);

        if (temp != null) {
            tasks = temp;

        } else {
            //Error
        }

        ListView todoslv = (ListView) findViewById(R.id.todolistview);

        adapter = new CustomListAtapter(this, tasks);
        todoslv.setAdapter(adapter);

        swipeContainer.setRefreshing(false);
    }



    public void showAddForm(View view) {

        taskName = " ";

        final EditText taskNameText;

        MaterialDialog dialog = new MaterialDialog.Builder(this).
                title("Add new Task").
                customView(R.layout.form_add_task, true).
                negativeText("Cancel").
                positiveText("Add").
                negativeColor(Color.parseColor("#ff3333")).
                positiveColor(Color.parseColor("#2196F3")).
                onPositive(new MaterialDialog.SingleButtonCallback() {

                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        final TodoItem todoItem = new TodoItem();
                        todoItem.setName(taskName);
                        todoItem.setDone(false);

                        RadioGroup taskPriority = (RadioGroup) dialog.findViewById(R.id.task_priority);

                        switch (taskPriority.getCheckedRadioButtonId()) {
                            case R.id.task_priority_altisima:
                                todoItem.setPriority(1);
                                break;
                            case R.id.task_priority_alta:
                                todoItem.setPriority(2);
                                break;
                            case R.id.task_priority_baixa:
                                todoItem.setPriority(3);
                                break;
                        }
                        tasks.add(todoItem);
                        adapter.notifyDataSetChanged();
                    }
                }).


                build();

        dialog.show();

        taskNameText = (EditText) dialog.getCustomView().findViewById(R.id.task_tittle);

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        positiveAction.setEnabled(false);

        taskNameText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                taskName = s.toString();
                positiveAction.setEnabled(taskName.trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void removeTask(View view) {

        for (int i = tasks.size() - 1; i >= 0; i--) {
            if (tasks.get(i).isDone()) {
                tasks.remove(i);
            }
        }

        adapter.notifyDataSetChanged();
    }

    public void editTask(final int position) {

        final EditText taskNameText;
        RadioGroup checkPriority;

        MaterialDialog dialog = new MaterialDialog.Builder(this).
                title("Update Task").
                customView(R.layout.form_add_task, true).
                negativeText("Cancel").
                positiveText("Update").
                negativeColor(Color.parseColor("#ff3333")).
                positiveColor(Color.parseColor("#2196F3")).
                onPositive(new MaterialDialog.SingleButtonCallback() {

                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {

                        tasks.get(position).setName(taskName);
                        if (tasks.get(position).isDone() == true){tasks.get(position).setDone(true);}
                        else {tasks.get(position).setDone(false);}

                        RadioGroup taskPriority = (RadioGroup) dialog.findViewById(R.id.task_priority);

                        switch (taskPriority.getCheckedRadioButtonId()) {
                            case R.id.task_priority_altisima:
                                tasks.get(position).setPriority(1);
                                break;
                            case R.id.task_priority_alta:
                                tasks.get(position).setPriority(2);
                                break;
                            case R.id.task_priority_baixa:
                                tasks.get(position).setPriority(3);
                                break;
                        }

                        adapter.notifyDataSetChanged();
                    }
                }).


                build();

        dialog.show();

        taskNameText = (EditText) dialog.getCustomView().findViewById(R.id.task_tittle);
        taskNameText.append(tasks.get(position).getName());
        taskName = taskNameText.getText().toString();

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        positiveAction.setEnabled(false);

        taskNameText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                taskName = s.toString();
                positiveAction.setEnabled(taskName.trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        checkPriority = (RadioGroup) dialog.getCustomView().findViewById(R.id.task_priority);
        if (tasks.get(position).getPriority() == 1){checkPriority.check(R.id.task_priority_altisima);}
        if (tasks.get(position).getPriority() == 2){checkPriority.check(R.id.task_priority_alta);}
        if (tasks.get(position).getPriority() == 3){checkPriority.check(R.id.task_priority_baixa);}

        checkPriority.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup taskPriority, int checkedId) {
                positiveAction.setEnabled(true);
            }
        });
    }
}