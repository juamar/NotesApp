package es.lhdev.notes;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import es.lhdev.notes.singletons.TempData;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int OPEN_REQUEST = 1;
    private static final String LHNOTESFOLDER = "LHNotes";
    private TextView text;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(this.getClass().getSimpleName(),"onCreate");

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleOnNewClicked();
            }
        });

        text = findViewById(R.id.text);
        title = findViewById(R.id.title);

        if (TempData.getInstance().getName() != null || TempData.getInstance().getText() != null)
        {
            Log.i(this.getClass().getSimpleName(), "Recovering state");
            text.setText(TempData.getInstance().getText());
            title.setText(TempData.getInstance().getName());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case (R.id.action_new):
                handleOnNewClicked();
                return true;
            case (R.id.action_save):
                handleOnSaveClicked();
                return true;
            case (R.id.action_open):
                handleOnOpenClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void handleOnOpenClicked()
    {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) && arePermitionsOk())
        {
            TempData.getInstance().setText(text.getText().toString());
            TempData.getInstance().setName(title.getText().toString());

            Intent i = new Intent(getApplicationContext(), OpenFileActivity.class);
            startActivityForResult(i, OPEN_REQUEST);
        }
    }

    public void handleOnNewClicked() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setMessage(R.string.question_sure);
        b.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                TempData.getInstance().setText(null);
                TempData.getInstance().setName(null);

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                getApplicationContext().startActivity(i);
                finish();
            }
        });
        b.setNegativeButton(R.string.no, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        b.create().show();
    }

    public void handleOnSaveClicked()
    {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) && arePermitionsOk())
        {
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString(), LHNOTESFOLDER);
            dir.mkdirs();

            String filename = title.getText().toString() + ".txt";
            String textStr = text.getText().toString();

            File file = new File(dir, filename);

            Log.i(this.getClass().getSimpleName(), "File is at: " + file.getAbsolutePath());
            Toast.makeText(this, "File is at: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();

            try {
                FileOutputStream o = new FileOutputStream(file);
                o.write(textStr.getBytes());
                o.close();
                Snackbar.make(findViewById(R.id.rootContainer), R.string.note_saved, Snackbar.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Snackbar.make(findViewById(R.id.rootContainer), R.string.note_not_saved, Snackbar.LENGTH_LONG).show();
            }
        }
        else
        {
            Snackbar.make(findViewById(R.id.rootContainer), R.string.note_not_saved, Snackbar.LENGTH_LONG).show();
        }
    }

    public boolean arePermitionsOk()
    {
        if (Build.VERSION.SDK_INT >= 23 &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
        {

            Log.d("permission", "permission denied to Write Files on External storage - requesting it");
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

            requestPermissions(permissions, 1);

            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Snackbar.make(findViewById(R.id.rootContainer), R.string.try_again, Snackbar.LENGTH_LONG).show();
                } else {
                    //ToDo: show Alert for error.
                    finish();
                }
                return;
            }
            default:
            {

            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OPEN_REQUEST && resultCode == RESULT_OK)
        {
            openFile(data.getStringExtra("fileName"));
        }
    }

    public void openFile(String fileName)
    {
        TempData.getInstance().setName(fileName.replace(".txt", ""));

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data;
        int nRead;

        try {
            File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/" + LHNOTESFOLDER, fileName);
            FileInputStream i = new FileInputStream(f);
            data = new byte[i.available()];
            //data = new byte[0];
            //Log.i(getClass().getSimpleName(), String.valueOf(i.available()));

            while ((nRead = i.read(data, 0, data.length)) != -1)
            {
                buffer.write(data, 0, nRead);
            }

        } catch (Exception e) {
            //Todo: this is not properly managed.
            data = new byte[0];
            e.printStackTrace();
        }

        TempData.getInstance().setText(new String(data));

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
