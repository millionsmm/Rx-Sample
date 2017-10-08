package millionsmm.rx_sample;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

public class ServiceTestActivity extends AppCompatActivity {

    private Button mExecutor;
    private Button mStop;
    private ProgressBar mProgressBar;
    private MyService myService;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            myService = ((MyService.MyBinder) iBinder).getService();
            myService.setOnProgressListener(new MyService.OnProgressListener() {
                @Override
                public void onProgress(int progress) {
                    mProgressBar.setProgress(progress);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_test);
        mExecutor = (Button) findViewById(R.id.btn_exe);
        mStop = (Button) findViewById(R.id.btn_stop);
        mProgressBar = (ProgressBar) findViewById(R.id.pro_test);
        mExecutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myService.startDownload();
            }
        });
        Intent intent = new Intent(this, MyService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
    }
}
