package gad.heartbeat.androidflask.easyupload;

import android.Manifest;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final Pattern IP_ADDRESS
            = Pattern.compile(
            "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                    + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                    + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                    + "|[1-9][0-9]|[0-9]))");
    final int GALLERY_CODE = 1;
    ArrayList<String> selectedImagesPaths; // Paths of the image(s) selected by the user.
    boolean imagesSelected = false; // Whether the user selected at least an image or not.
    private ImageView ivCurrency;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, 2);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        setContentView(R.layout.activity_main);
        ivCurrency = findViewById(R.id.iv);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(getApplicationContext(), "Access to Storage Permission Granted. Thanks.", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(getApplicationContext(), "Access to Storage Permission Denied.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(getApplicationContext(), "Access to Internet Permission Granted. Thanks.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Access to Internet Permission Denied.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public void connectServer(View v) {
        TextView responseText = findViewById(R.id.responseText);
        if (imagesSelected == false) { // This means no image is selected and thus nothing to upload.
            responseText.setText("No Image Selected to Upload. Select Image(s) and Try Again.");
            return;
        }
        responseText.setText("Sending the Files. Please Wait ...");

        EditText ipv4AddressView = findViewById(R.id.IPAddress);
        String ipv4Address = ipv4AddressView.getText().toString();
        EditText portNumberView = findViewById(R.id.portNumber);
        String portNumber = portNumberView.getText().toString();

        Matcher matcher = IP_ADDRESS.matcher(ipv4Address);
        if (!matcher.matches()) {
            responseText.setText("Invalid IPv4 Address. Please Check Your Inputs.");
            return;
        }

        String postUrl = "http://" + ipv4Address + ":" + portNumber + "/image";

        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        for (int i = 0; i < selectedImagesPaths.size(); i++) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            try {
                // Read BitMap by file path.
                Bitmap bitmap = BitmapFactory.decodeFile(selectedImagesPaths.get(i), options);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            } catch (Exception e) {
                responseText.setText("Please Make Sure the Selected File is an Image.");
                return;
            }
            byte[] byteArray = stream.toByteArray();

            multipartBodyBuilder.addFormDataPart("image" + i, "Android_Flask_" + i + ".jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray));
        }

        RequestBody postBodyImage = multipartBodyBuilder.build();

//        RequestBody postBodyImage = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("image", "androidFlask.jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray))
//                .build();

        postRequest(postUrl, postBodyImage);
    }

    void postRequest(String postUrl, RequestBody postBody) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure.
                call.cancel();
                Log.d("FAIL", e.getMessage());

                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView responseText = findViewById(R.id.responseText);
                        responseText.setText("Failed to Connect to Server. Please Try Again.");
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView responseText = findViewById(R.id.responseText);
                        try {
                            String res = response.body().string();
                            responseText.setText("Server's Response\n" + res);
                            JSONObject Jobject = new JSONObject(res);
                            String note = Jobject.getString("note");
                            audioPlayer(note);
                            Toast.makeText(MainActivity.this, note, Toast.LENGTH_SHORT).show();

                        } catch (IOException e) {
                            Toast.makeText(MainActivity.this, "error1", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        } catch (JSONException e) {
                            Toast.makeText(MainActivity.this, "error2", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void audioPlayer(String note) {
        //set up MediaPlayer
        try {
            MediaPlayer mPlayer= MediaPlayer.create(MainActivity.this, R.raw.speech);
            switch (note) {
                case "10":
                    mPlayer = MediaPlayer.create(MainActivity.this, R.raw.a10);
                    break;
                case "10_1":
                    mPlayer = MediaPlayer.create(MainActivity.this, R.raw.a10);
                    break;
                case "10back":
                    mPlayer = MediaPlayer.create(MainActivity.this, R.raw.a10back);
                    break;
                case "10_new":
                    mPlayer = MediaPlayer.create(MainActivity.this, R.raw.a10_new);
                    break;
                case "10_newback":
                    mPlayer = MediaPlayer.create(MainActivity.this, R.raw.a10_newback);
                    break;
                case "20":
                    mPlayer = MediaPlayer.create(MainActivity.this, R.raw.a20);
                    break;
                case "20back":
                    mPlayer = MediaPlayer.create(MainActivity.this, R.raw.a20back);
                    break;
                case "20_new":
                    mPlayer = MediaPlayer.create(MainActivity.this, R.raw.a20_new);
                    break;
                case "20_newback":
                    mPlayer = MediaPlayer.create(MainActivity.this, R.raw.a20_newback);
                    break;
                case "50":
                    mPlayer = MediaPlayer.create(MainActivity.this, R.raw.a50);
                    break;
                case "50back":
                    mPlayer = MediaPlayer.create(MainActivity.this, R.raw.a50back);
                    break;
                case "50_new":
                    mPlayer = MediaPlayer.create(MainActivity.this, R.raw.a50_new);
                    break;
                case "50_newback":
                    mPlayer = MediaPlayer.create(MainActivity.this, R.raw.a50_newback);
                    break;
                case "100":
                    mPlayer = MediaPlayer.create(MainActivity.this, R.raw.a100);
                    break;
                case "100back":
                    mPlayer = MediaPlayer.create(MainActivity.this, R.raw.a100back);
                    break;
                case "100_new":
                    mPlayer = MediaPlayer.create(MainActivity.this, R.raw.a100_new);
                    break;
                case "100_newback":
                    mPlayer = MediaPlayer.create(MainActivity.this, R.raw.a100_newback);
                    break;
                case "200":
                    mPlayer = MediaPlayer.create(MainActivity.this, R.raw.a200);
                    break;
                case "500":
                    mPlayer = MediaPlayer.create(MainActivity.this, R.raw.a500);
                    break;
                case "500back":
                    mPlayer = MediaPlayer.create(MainActivity.this, R.raw.a500back);
                    break;
                case "500_1":
                    mPlayer = MediaPlayer.create(MainActivity.this, R.raw.a500_1);
                    break;
                case "500_2":
                    mPlayer = MediaPlayer.create(MainActivity.this, R.raw.a500_2);
                    break;
                case "2000":
                    mPlayer = MediaPlayer.create(MainActivity.this, R.raw.a2000);
                    break;
                case "2000back":
                    mPlayer = MediaPlayer.create(MainActivity.this, R.raw.a2000back);
                    break;

            }
            mPlayer.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void selectImage(View v) {
//        Intent intent = new Intent();
//        intent.setType("*/*");
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_CODE);
//    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == GALLERY_CODE && resultCode == RESULT_OK && null != data) {
                // When a single image is selected.
                Uri myImageUri = data.getData();
                ivCurrency.setImageURI(myImageUri);
                String currentImagePath;
                selectedImagesPaths = new ArrayList<>();
                TextView numSelectedImages = findViewById(R.id.numSelectedImages);
                if (data.getData() != null) {
                    Uri uri = data.getData();
                    currentImagePath = getPath(getApplicationContext(), uri);
                    Log.d("ImageDetails", "Single Image URI : " + uri);
                    Log.d("ImageDetails", "Single Image Path : " + currentImagePath);
                    selectedImagesPaths.add(currentImagePath);
                    imagesSelected = true;
                    numSelectedImages.setText("Number of Selected Images : " + selectedImagesPaths.size());
                }
            } else {
                Toast.makeText(this, "You haven't Picked any Image.", Toast.LENGTH_LONG).show();
            }
            Toast.makeText(getApplicationContext(), selectedImagesPaths.size() + " Image(s) Selected.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Something Went Wrong.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    // Implementation of the getPath() method and all its requirements is taken from the StackOverflow Paul Burke's answer: https://stackoverflow.com/a/20559175/5426539
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}



