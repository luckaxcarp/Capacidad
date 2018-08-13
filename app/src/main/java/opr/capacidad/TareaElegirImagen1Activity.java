package opr.capacidad;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import opr.capacidad.Data.WebServer;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission.CAMERA;



public class TareaElegirImagen1Activity extends AppCompatActivity {

    private static String APP_DIRECTORY = "MyPictureApp/";
    private static String MEDIA_DIRECTORY = APP_DIRECTORY + "PictureApp/";

    private final int MY_PERMISSIONS = 100;
    private final int PHOTO_CODE = 200;
    private final int SELECT_PICTURE = 300;

    private String idTerapia;

    private TextInputEditText etConsigna;
    private EditText viewFProgramada;
    private ImageView mSetImage;
    private ImageView mSetImage2;
    private ImageView mSetImage3;
    private ImageView imageViewAuxiliar;
    private Button mOptionButton;
    private Button mOptionButton2;
    private Button mOptionButton3;
    private RadioButton rbImage1;
    private RadioButton rbImage2;
    private RadioButton rbImage3;
    private RadioGroup radioGroup;

    private static final int RB1_ID = 1;
    private static final int RB2_ID = 2;
    private static final int RB3_ID = 3;

    private String imageString;
    private String imageString2;
    private String imageString3;
    private String rightChoice;

    private JsonObjectRequest jsonObjectRequest;
    private RequestQueue request;

    private Button Volver;
    private Button Crear;

    private ConstraintLayout layout;

    private String mPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_tarea_elegir_imagen1);

        etConsigna = findViewById(R.id.text_input_consigna);
        mSetImage = findViewById(R.id.imageView);
        mSetImage2 = findViewById(R.id.imageView2);
        mSetImage3 = findViewById(R.id.imageView3);
        mOptionButton = findViewById(R.id.btnCargarImagen);
        mOptionButton2 = findViewById(R.id.btnCargarImagen2);
        mOptionButton3 = findViewById(R.id.btnCargarImagen3);
        rbImage1 = findViewById(R.id.radio_img1);
        rbImage2 = findViewById(R.id.radio_img2);
        rbImage3 = findViewById(R.id.radio_img3);
        radioGroup = findViewById(R.id.radioGroupResolver);
        viewFProgramada = findViewById(R.id.etDate);
        Volver = findViewById(R.id.btnVolver);
        Crear = findViewById(R.id.btnCrear);

        layout = findViewById(R.id.content_tarea_elegir_imagen1);

        if (mayRequestStoragePermission()){
            mOptionButton.setEnabled(true);
            mOptionButton2.setEnabled(true);
            mOptionButton3.setEnabled(true);


        }else{
            mOptionButton.setEnabled(false);
            mOptionButton2.setEnabled(false);
            mOptionButton3.setEnabled(false);

        }
        mOptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("INFO","btn1");
                imageViewAuxiliar = mSetImage;
                showOptions();
            }
        });
        mOptionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("INFO","btn2");
                imageViewAuxiliar = mSetImage2;
                showOptions();
            }
        });
        mOptionButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("INFO","btn3");
                imageViewAuxiliar = mSetImage3;
                showOptions();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
            }
        });


        Crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                idTerapia = "1";

                imageString = bitmapToBase64(((BitmapDrawable) mSetImage.getDrawable()).getBitmap());
                imageString2 = bitmapToBase64(((BitmapDrawable) mSetImage2.getDrawable()).getBitmap());
                imageString3 = bitmapToBase64(((BitmapDrawable) mSetImage3.getDrawable()).getBitmap());

                try {
                    imageString = URLEncoder.encode(imageString, "utf-8");
                    imageString2 = URLEncoder.encode(imageString2, "utf-8");
                    imageString3 = URLEncoder.encode(imageString3, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    Log.i("SET DATA", e.toString());
                }

                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton selectedRadioButton = findViewById(selectedId);
                    rightChoice = selectedRadioButton.getText().toString();
                }

                sendTarea();
            }
        });

        Volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent Volver = new Intent(TareaElegirImagen1Activity.this, ElegirTarea.class);
                startActivity(Volver);
            }
        });
    }

    private void sendTarea() {
        String url = WebServer.generateUrlCreateTarea();
        Log.i("URL", url);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(" RESPONSE", response);
                Toast.makeText(TareaElegirImagen1Activity.this, "Tarea cargada exitosamente",Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(" RESPONSE", error.toString());
                Toast.makeText(TareaElegirImagen1Activity.this, "Error al cargar la tarea",Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();

                params.put("consigna",etConsigna.getText().toString());
                params.put("f_programada",viewFProgramada.getText().toString());
                params.put("tipo","1");
                params.put("id_terapia",idTerapia);
                params.put("ubicacion1",imageString);
                params.put("ubicacion2",imageString2);
                params.put("ubicacion3",imageString3);
                params.put("imagencorrecta",rightChoice);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private String bitmapToBase64(Bitmap image) {
        image = Bitmap.createScaledBitmap(image,300,300,true); //Escalar imagen

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }


    private boolean mayRequestStoragePermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return true;
        }
        if ((checkSelfPermission(WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)&&
                (checkSelfPermission(CAMERA)== PackageManager.PERMISSION_GRANTED)){
            return true;
        }
        if ((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) || (shouldShowRequestPermissionRationale(CAMERA))){
            Snackbar.make(layout,"Los permisos son necesarios para poder usar la aplicacion.",Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok, new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                     requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA},MY_PERMISSIONS);

                }
            }).show();
        }else {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA},MY_PERMISSIONS);
        }
        return false;
    }

    private void showOptions() {
        final CharSequence[] option = {"Tomar foto","Elegir de galeria","Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(TareaElegirImagen1Activity.this);
        builder.setTitle("Elige una opcion");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (option[which] == "Tomar foto"){
                    Log.i("INFO","camara");
                    openCamara();
                }else if(option[which] == "Elegir de galeria") {
                    Log.i("INFO","galeria");
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent.createChooser(intent,"Selecciona app de imagen"),SELECT_PICTURE);
                }else {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }

    private void openCamara() {
        File file = new File(Environment.getExternalStorageDirectory(),MEDIA_DIRECTORY);
        boolean isDirectoryCreated = file.exists();

        if (!isDirectoryCreated){
            isDirectoryCreated = file.mkdirs();
        }
        if (isDirectoryCreated){
            Long timestamp = System.currentTimeMillis() / 1000;
            String imageName = timestamp.toString() + ".jpg";

            mPath = Environment.getExternalStorageDirectory() + File.separator + MEDIA_DIRECTORY
                    + File.separator + imageName;

            File newFile = new File(mPath);


            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(newFile));
            startActivityForResult(intent,PHOTO_CODE);

        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("file_path", mPath);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPath = savedInstanceState.getString("file_path");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case PHOTO_CODE:
                        MediaScannerConnection.scanFile(this,
                                new String[]{mPath}, null,
                                new MediaScannerConnection.OnScanCompletedListener() {
                                    @Override
                                    public void onScanCompleted(String path, Uri uri) {
                                        Log.i("ExternalStorage", "Scanned" + path + ":");
                                        Log.i("ExternalStorage", "-> Uri = " + uri);
                                    }
                                });

                        Bitmap bitmap = BitmapFactory.decodeFile(mPath);
                        imageViewAuxiliar.setImageBitmap(bitmap);


                        break;
                    case SELECT_PICTURE:
                        Uri path = data.getData();
                        imageViewAuxiliar.setImageURI(path);


                        break;

                }
            }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==MY_PERMISSIONS){
            if (grantResults.length==2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(TareaElegirImagen1Activity.this,"Permisos aceptados",Toast.LENGTH_SHORT).show();
                mOptionButton.setEnabled(true);
                mOptionButton2.setEnabled(true);
                mOptionButton3.setEnabled(true);

            }

        }else {
            showExplanation();
        }
    }

    private void showExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TareaElegirImagen1Activity.this);
        builder.setTitle("Permisos denegados");
        builder.setMessage("Para usar las funciones de la app necesitas aceptar los permisos");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               dialog.dismiss();
               finish();
            }
        });
        builder.show();
    }
}






