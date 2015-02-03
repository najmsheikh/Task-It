package me.najmsheikh.taskit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;


public class TaskListActivity extends ActionBarActivity {

    private static final int EDIT_TASK_REQUEST = 10;
    private static final int NEW_TASK_REQUEST = 20;
    private ArrayList<Task> mTasks;
    private int mLastPositionClicked;
    private TaskAdapter mTaskAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        mTasks = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            mTasks.add(new Task());
//            mTasks.get(i).setName("Task #" + i);
//        }

        listView = (ListView) findViewById(R.id.task_list);
        mTaskAdapter = new TaskAdapter(mTasks);
//        listView.addFooterView(LayoutInflater.from(this).inflate(R.layout.footer, null),null,false);
        listView.setAdapter(mTaskAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mLastPositionClicked = position;
//                Toast.makeText(getApplicationContext(), "Task " + (position + 1) + " was pressed", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(TaskListActivity.this, TaskActivity.class);
                Task task = (Task) parent.getAdapter().getItem(position);
                i.putExtra(TaskActivity.EXTRA, task);
                startActivityForResult(i, EDIT_TASK_REQUEST);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_up_out);
            }
        });
        listView.setEmptyView(findViewById(R.id.no_task_splash2));
//        registerForContextMenu(listView);


// BEGIN CONTEXTUAL ACTION BAR
        listView.getSelectedItemPosition();
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getMenuInflater().inflate(R.menu.menu_task_list_context, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                int id = item.getItemId();
                int ct = 0;
                SparseBooleanArray positions = listView.getCheckedItemPositions();
                if (id == R.id.delete_task_item) {
                    for (int i = positions.size() - 1; i >= 0; i--) {
                        if (positions.valueAt(i)) {
                            ct++;
                            mTasks.remove(positions.keyAt(i));
                        }
                    }
                    mode.finish();
                    mTaskAdapter.notifyDataSetChanged();
                    if (ct > 1)
                        Toast.makeText(getApplicationContext(), ct + " tasks deleted", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                }

                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
//END CONTEXTUAL ACTION BAR
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case EDIT_TASK_REQUEST:
                if (resultCode == RESULT_OK) {
                    Task task = (Task) data.getSerializableExtra(TaskActivity.EXTRA);
                    mTasks.set(mLastPositionClicked, task);
                    mTaskAdapter.notifyDataSetChanged();
                }
                break;
            case NEW_TASK_REQUEST:
                if (resultCode == RESULT_OK) {
                    Task task = (Task) data.getSerializableExtra(TaskActivity.EXTRA);
                    mTasks.add(task);
                    mTaskAdapter.notifyDataSetChanged();
                }
                break;
            default:
                Toast.makeText(getApplicationContext(), "Bad Result Code", Toast.LENGTH_SHORT).show();
        }
    }

    //POPULATE LISTVIEW WITH DATA
    public class TaskAdapter extends ArrayAdapter<Task> {
        TaskAdapter(ArrayList<Task> tasks) {
            super(TaskListActivity.this, R.layout.task_list_row, R.id.task_item_name, tasks);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = super.getView(position, convertView, parent);
            Task task = getItem(position);

            CheckedTextView taskName = (CheckedTextView) convertView.findViewById(R.id.task_item_name);
            taskName.setText(task.getName());
            taskName.setChecked(task.isDone());

            TextView taskDate = (TextView) convertView.findViewById(R.id.task_item_date);
            DateFormat df = DateFormat.getDateInstance();
            if (task.getDueDate() != null) taskDate.setText(df.format(task.getDueDate()));

//            CheckBox doneBox = (CheckBox) convertView.findViewById(R.id.task_item_done);
//            doneBox.setChecked(task.isDone());

            return convertView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_task_item) {
            Intent i = new Intent(getApplicationContext(), TaskActivity.class);
            startActivityForResult(i, NEW_TASK_REQUEST);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_up_out);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_task_list_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.delete_task_item) {
            AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            mTasks.remove(menuInfo.position);
            mTaskAdapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_up_out);
    }
}
