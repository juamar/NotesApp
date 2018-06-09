package es.lhdev.notes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;

public class OpenFileActivity extends AppCompatActivity {

    private String[] files;
    private ListView filesListView;
    private String resultingFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_file);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString(), "LHNotes");
        dir.mkdirs();

        files = dir.list();

        filesListView = findViewById(R.id.filesListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, files);
        filesListView.setAdapter(adapter);

        filesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                resultingFileName = files[position];
                finish();
            }
        });
    }

    @Override
    public void finish()
    {
        Intent data = new Intent();

        data.putExtra("fileName", resultingFileName);
        setResult(RESULT_OK, data);

        super.finish();
    }


}
