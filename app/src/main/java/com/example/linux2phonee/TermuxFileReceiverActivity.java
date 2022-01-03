package com.example.linux2phonee;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.renderscript.ScriptGroup;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.activation.MimetypesFileTypeMap;

public class TermuxFileReceiverActivity extends Activity {

    public TextView textView3;
    public EditText url1;
    public EditText url2;
    public EditText url3;
    public Button refreshButton;
    private boolean uploadSuccess;
    private List<String> urls = new ArrayList<>();
    ;
    final int PERMISSION_REQ_CODE = 1;
    private boolean ok;

    public void checkPermissions() {
        // 要申请的权限列表
        final String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        // 检查本应用是否有了 WRITE_EXTERNAL_STORAGE 权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // 系统将弹出一个对话框，询问用户是否授权
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQ_CODE);
        }
    }

    // 权限申请的结果  // requestCode：请求码  // permissions: 申请的N个权限  // grantResults: 每个权限是否被授权
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (requestCode == PERMISSION_REQ_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    // 如果用户没给我们授权...这意味着有此功能就不能用了
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        findViewById(R.id.imageView)
        setContentView(R.layout.filerecierver);
        this.textView3 = findViewById(R.id.textView3);
        this.refreshButton = findViewById(R.id.button);
        refreshButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File configFileDir = new File("/data/data/com.example.linux2phonee/");
                        File configFile = new File(configFileDir,"config.txt");
                        try {
                            BufferedWriter brw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(configFile)));
                            brw.write(url1.getText().toString());
                            brw.write("\n");
                            brw.write(url2.getText().toString());
                            brw.write("\n");
                            brw.write(url3.getText().toString());
                            brw.write("\n");
                            brw.flush();
                            textView3.append("刷新url地址");



                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        this.url1 = findViewById(R.id.editText);
        this.url2 = findViewById(R.id.editText2);
        this.url3 = findViewById(R.id.editText3);

        File configFileDir = new File("/data/data/com.example.linux2phonee/");
        if (!configFileDir.isDirectory() && !configFileDir.mkdirs()){
            System.out.println("can't create dir for config file!");
            return ;
        }
        File configFile = new File(configFileDir,"config.txt");
        if (configFile.exists()){
            try (InputStreamReader filein = new InputStreamReader(new FileInputStream(configFile))) {
                BufferedReader br = new BufferedReader(filein);
//            byte[] buffer = new byte[4096];
                String readLine;
                for (int i = 0; i < 3; i++) {
                    readLine = br.readLine();
                    System.out.println("read--->"+readLine);
                    readLine = readLine.replace("\n","");
                    urls.add(i,readLine);
                }
                url1.setText(urls.get(0));
                url2.setText(urls.get(1));
                url3.setText(urls.get(2));


            }catch (IOException e){
                System.out.println("Error open file:\n\n" + e);
                return;
            }
        }else{
            Toast toast = Toast.makeText(this, "请刷新url", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

//
//        this.urls.add(0, url1.getText().toString());
//        this.urls.add(1, url2.getText().toString());
//        this.urls.add(2, url3.getText().toString());


        checkPermissions();
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        final String scheme = intent.getScheme();
        System.out.printf("action is:%s\n", action);
        System.out.printf("type is:%s\n", type);
        System.out.printf("scheme is:%s\n", scheme);


//        final TextView textView = findViewById(R.id.text_home);
//        textView.setText(action);

        if (action.equals("android.intent.action.SEND")) {      //action.equals("android.intent.action.VIEW") ||
//            final String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
            final Uri sharedUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
//            String path = intent.getData().getPathSegments().toString();
//            Uri uri = intent.getData();
//            String path1 = uri.getPath();

//            System.out.println(sharedText);
            System.out.printf("this intent is:\naction:android.intent.action.SEND\nsharedUri:%s\n",sharedUri);
            handleContentUri(sharedUri,type);
//            System.out.println(path);
//            System.out.println(path1);
//            File file = new File(path1);

//            MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();
//            type = fileTypeMap.getContentType(file);
//            Snackbar.make(findViewById(R.id.fab), file.toString(), Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show();


        }else if(action.equals("android.intent.action.VIEW")){
            Uri uri = intent.getData();
            handleContentUri(uri,type);

        }

    }
    void handleContentUri(Uri sharedUri,String type){
        try {
            String attachmentFileName = null;
            String[] projection = new String[]{OpenableColumns.DISPLAY_NAME};
            try (Cursor c = getContentResolver().query(sharedUri, projection, null, null, null)) {
                if (c != null && c.moveToFirst()) {
                    final int fileNameColumnId = c.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (fileNameColumnId >= 0)
                        attachmentFileName = c.getString(fileNameColumnId);
                }
            }

            if (attachmentFileName == null) attachmentFileName = "aaaaa";
//
            InputStream in = getContentResolver().openInputStream(sharedUri);
//                type = getContentResolver().getStreamTypes(uri,);
//                type = URLConnection.guessContentTypeFromStream(in);
            promptNameAndSave(in, attachmentFileName, type);
        } catch (Exception e) {
            System.out.println("Unable to handle shared content:\n\n" + e.getMessage());
            System.out.println("handleContentUri(uri=" + sharedUri + ") failed:" + e);
        }
    }
    void promptNameAndSave(final InputStream in, final String attachmentFileName, String type) {

        saveStreamWithName(in, attachmentFileName, type);
//        if (outFile == null) return;
//
//                    final File editorProgramFile = new File(EDITOR_PROGRAM);
//                    if (!editorProgramFile.isFile()) {
//                        showErrorDialogAndQuit("The following file does not exist:\n$HOME/bin/termux-file-editor\n\n"
//                                + "Create this file as a script or a symlink - it will be called with the received file as only argument.");
//                        return;
//                    }
//
//                    // Do this for the user if necessary:
//                    //noinspection ResultOfMethodCallIgnored
//                    editorProgramFile.setExecutable(true);
//
//                    final Uri scriptUri = new Uri.Builder().scheme("file").path(EDITOR_PROGRAM).build();
//
//                    Intent executeIntent = new Intent(TERMUX_SERVICE.ACTION_SERVICE_EXECUTE, scriptUri);
//                    executeIntent.setClass(TermuxFileReceiverActivity.this, TermuxService.class);
//                    executeIntent.putExtra(TERMUX_SERVICE.EXTRA_ARGUMENTS, new String[]{outFile.getAbsolutePath()});
//                    startService(executeIntent);
//                    finish();
//
//
//    }
    }

    public void saveStreamWithName(InputStream in, String attachmentFileName, String type) {
//        String fileName = "e:/username/textures/antimap_0017.png";
        Map<String, String> fileMap = new HashMap<String, String>();
        fileMap.put("file", attachmentFileName);
        String contentType = "";//image/png
        formUpload(null, fileMap, contentType, in, attachmentFileName, type);
    }

    @SuppressWarnings("rawtypes")
    public void formUpload(Map<String, String> textMap,
                           Map<String, String> fileMap, String contentType,
                           InputStream in, String filename, String type) {
//                try{
//            InputStreamReader isr = new InputStreamReader(in);
//            BufferedReader br = new BufferedReader(isr);
//            String lineTxt = null;
//            while ((lineTxt = br.readLine()) != null) {
//                System.out.println(lineTxt); // 逐行输出文件内容
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        for (int i = 0; i < this.urls.size(); i++) {
            Thread accessWebServiceThread = new Thread(new WebServiceHandler(in, filename, type, this.urls.get(i), this.textView3));
            accessWebServiceThread.start();
//            try {
//                accessWebServiceThread.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

        }

//        if (!this.uploadSuccess){
//            Toast toast = Toast.makeText(this, "请刷新uri!", Toast.LENGTH_SHORT);
//            toast.show();
//
//        }else{
//            Toast toast = Toast.makeText(this, "success!", Toast.LENGTH_SHORT);
//            toast.show();
//        }




    }


//        File receiveDir = new File(TERMUX_RECEIVEDIR);
//
//        if (DataUtils.isNullOrEmpty(attachmentFileName)) {
//            showErrorDialogAndQuit("File name cannot be null or empty");
//            return null;
//        }
//
//        if (!receiveDir.isDirectory() && !receiveDir.mkdirs()) {
//            showErrorDialogAndQuit("Cannot create directory: " + receiveDir.getAbsolutePath());
//            return null;
//        }
//
//        try {
//            final File outFile = new File(receiveDir, attachmentFileName);
//            try (FileOutputStream f = new FileOutputStream(outFile)) {
//                byte[] buffer = new byte[4096];
//                int readBytes;
//                while ((readBytes = in.read(buffer)) > 0) {
//                    f.write(buffer, 0, readBytes);
//                }
//            }
//            return outFile;
//        } catch (IOException e) {
//            showErrorDialogAndQuit("Error saving file:\n\n" + e);
//            Logger.logStackTraceWithMessage(LOG_TAG, "Error saving file", e);
//            return null;
//        }
//    }


    class WebServiceHandler implements Runnable {
        //    private HttpURLConnection conn;
//    private String urlStr;
        private String filename;
        private InputStream in;
        private String type;
        private TextView tv;
        private String uploadurl;


        //
        WebServiceHandler(InputStream in, String filename, String type, String url, TextView t) {
            this.filename = filename;
            this.in = in;
            this.type = type;
            this.tv = t;
            this.uploadurl = url;
        }

        @Override
        public void run() {
            String res = "";
            HttpURLConnection conn = null;
            // boundary就是request头和上传文件内容的分隔符
            String BOUNDARY = "---------------------------1847669293131545412491233756";
            try {
                URL url = new URL(uploadurl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(10000);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
                conn.setRequestProperty("Charset", "UTF-8");
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
                OutputStream out = new DataOutputStream(conn.getOutputStream());
//            String contentType = "application/octet-stream";
                String contentType = type;
                StringBuffer strBuf = new StringBuffer();
                strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                strBuf.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + filename + "\"\r\n");
                strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
                System.out.println(strBuf.toString());
                out.write(strBuf.toString().getBytes());


//                InputStreamReader isr = new InputStreamReader(in);
//                BufferedReader br = new BufferedReader(isr);
//                String lineTxt = null;
//                while ((lineTxt = br.readLine()) != null) {
////                    System.out.println(lineTxt); // 逐行输出文件内容
//                    out.write(lineTxt.getBytes());
//                }

                int bytes = 0;
                DataInputStream inn = new DataInputStream(in);

                byte[] bufferOut = new byte[2048];
                while ((bytes = inn.read(bufferOut)) != -1) {
//                System.out.println("---->>>>>>>>>>>"); // 逐行输出文件内容
//                System.out.println(bytes); // 逐行输出文件内容
                    out.write(bufferOut, 0, bytes);

                }
                in.close();
                byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
                out.write(endData);
                out.flush();

                out.close();
                // 读取返回数据
                strBuf = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    strBuf.append(line).append("\n");
                }
                res = strBuf.toString();
                JSONObject jb = new JSONObject(res);
                if ((jb.getInt("c") == 200) && (jb.getString("m").equals("ok"))) {
                    textView3.append(this.uploadurl.concat(" --上传成功！\n"));
                    uploadSuccess = true;
                }else{
                    textView3.append(this.uploadurl.concat(" --上传失败！\n"));

                }
                ;
                System.out.println();
                reader.close();
                reader = null;

            } catch (Exception e) {
                System.out.println("无法链接主机");

                try {
                    textView3.append(this.uploadurl.concat("--无法链接!\n"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                    conn = null;
                }
            }


        }

    }
}
