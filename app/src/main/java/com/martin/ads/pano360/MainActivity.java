package com.martin.ads.pano360;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.martin.ads.vrlib.PanoPlayerActivity;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private Button playURL;
    private Button playDemo;
    private EditText url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        playURL=(Button)findViewById(R.id.play_url);
        url= (EditText) findViewById(R.id.edit_text_url);
        url.setText("http://cache.utovr.com/201508270528174780.m3u8");
        playURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filePath= url.getText().toString();
                Intent intent=new Intent(MainActivity.this,PanoPlayerActivity.class);
                intent.putExtra("videoPath",filePath);
                startActivity(intent);
            }
        });

        playDemo=(Button)findViewById(R.id.play_local_demo);
        playDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filePath= "android.resource://" + getPackageName() + "/" + R.raw.demo_video;
                Intent intent=new Intent(MainActivity.this,PanoPlayerActivity.class);
                intent.putExtra("videoPath",filePath);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String filePath= Environment.getExternalStorageDirectory().getPath()+"/360Video/video.mp4";
//                Intent intent=new Intent(MainActivity.this,PanoPlayerActivity.class);
//                intent.putExtra("videoPath",filePath);
//                startActivity(intent);

                Intent intent = new Intent(MainActivity.this, FilePickerActivity.class);
                intent.putExtra(FilePickerActivity.ARG_FILTER, Pattern.compile("(.*\\.mp4$)||(.*\\.avi$)||(.*\\.wmv$)"));
                startActivityForResult(intent, 1);
            }
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            Intent intent=new Intent(MainActivity.this,PanoPlayerActivity.class);
            //Intent intent=new Intent(MainActivity.this,DemoWithGLSurfaceView.class);
            intent.putExtra("videoPath",filePath);
            startActivity(intent);
        }
    }
}
