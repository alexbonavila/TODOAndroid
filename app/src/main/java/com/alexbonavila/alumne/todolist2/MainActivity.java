package com.alexbonavila.alumne.todolist2;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String SHARED_PREFERENCES_TODOS ="SP_TODOS";
    private static final String TODO_LIST ="todo_list" ;
    private Gson gson;
    public TodoArrayList tasks;
    private CustomListAtapter adapter;
    private String taskName;

    @Override
    protected void onStop(){
        super.onStop();

        if (tasks == null) { return; }

        String tasksToSave = gson.toJson(tasks);

        SharedPreferences todos = getSharedPreferences(SHARED_PREFERENCES_TODOS, 0);
        SharedPreferences.Editor editor = todos.edit();
        editor.putString(TODO_LIST, tasksToSave);
        editor.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences todos = getSharedPreferences(SHARED_PREFERENCES_TODOS, 0);
        String todoList = todos.getString(TODO_LIST, null);

        if (todoList == null){
            String initial_json= "[{name:\"Example Task\", \"done\": false, \"priority\": 2}]";
            SharedPreferences.Editor editor = todos.edit();
            editor.putString(TODO_LIST, initial_json);
            editor.commit();
            todoList = todos.getString(TODO_LIST, null);
        }

        Type arrayTodoList = new TypeToken<TodoArrayList>(){}.getType();
        this.gson = new Gson();
        TodoArrayList temp = gson.fromJson(todoList, arrayTodoList);

        if (temp != null){
            tasks = temp;

        } else {
            //Error
        }

        ListView todoslv = (ListView) findViewById(R.id.todolistview);

        adapter = new CustomListAtapter(this, tasks);
        todoslv.setAdapter(adapter);

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
        // Handle navigation view item clicks here.
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

    View positiveAction;


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

    public void removeTask(View view){

        for (int i = tasks.size() -1; i >= 0; i--)
        {
            if (tasks.get(i).isDone()) { tasks.remove(i); }
        }

        adapter.notifyDataSetChanged();
    }
    public void editTask(View view) {
        //TODO
    }

}