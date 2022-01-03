package com.example.linux2phonee;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,TermuxFileReceiverActivity.class);
                intent.setAction("change");

                startActivity(intent);
            }
        });

        WebView webv = findViewById(R.id.webView);
        webv.getSettings().setJavaScriptEnabled(true);
        webv.loadUrl("http://192.168.50.168:9000");

        Button shareqq = findViewById(R.id.button_qq);
        Button sharewecaht = findViewById(R.id.button_wechat);
        shareqq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context ctx = getBaseContext();
                shareQQ(ctx,"qq消息");
            }
        });
        sharewecaht.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context ctx = getBaseContext();
                shareWechatFriend(ctx,"微信消息");
            }
        });
//          Uri uri = intent.getData();

//          String str = Uri.decode(uri.getEncodedPath());
//          try{
//              File file = new File(str);
//              int length = (int) file.length();
//              byte[] buff = new byte[length];
//              FileInputStream fileInput = new FileInputStream(file);
//              fileInput.read(buff);
//              String result = new String(buff, "UTF-8");
//              Log.d("debbb",result);
//              System.out.println(result);
//              Snackbar.make(findViewById(R.id.fab), result, Snackbar.LENGTH_LONG)
//                      .setAction("Action", null).show();
//              fileInput.close();
//          }catch (Exception e){
//              e.printStackTrace();
//          }
    }


//        System.out.printf("file path is:%s",str);


//    private promptNameAndSave(){
//
//    }
public static void shareQQ(Context mContext, String content) {
//    if (PlatformUtil.isInstallApp(mContext, PlatformUtil.PACKAGE_MOBILE_QQ)) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity"));
        mContext.startActivity(intent);
//    } else {
//        Toast.makeText(mContext, "您需要安装QQ客户端", Toast.LENGTH_LONG).show();
//    }
}
    public void shareWechatFriend(Context context, String content) {
//        if (PlatformUtil.isInstallApp(context, PlatformUtil.PACKAGE_WECHAT)) {
            Intent intent = new Intent();
            ComponentName cop = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
            intent.setComponent(cop);
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra("android.intent.extra.TEXT", content);
//            intent.putExtra("sms_body", content);
            intent.putExtra("Kdescription", !TextUtils.isEmpty(content) ? content : "");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
//        } else {
//            Toast.makeText(context, "您需要安装微信客户端", Toast.LENGTH_LONG).show();
//        }
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


}
