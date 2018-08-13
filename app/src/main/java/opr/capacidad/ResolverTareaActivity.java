package opr.capacidad;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import opr.capacidad.Data.WebServer;
import opr.capacidad.model.Chronometer;
import opr.capacidad.model.Tarea;

public class ResolverTareaActivity extends AppCompatActivity {

    private String idTarea;

    private Tarea tarea = null;
    private TextView tvTittle;
    private TextView tvConsigna;
    private ImageView ivImg1;
    private ImageView ivImg2;
    private ImageView ivImg3;
    private RadioGroup rbGroup;
    private RadioButton rbImg1;
    private RadioButton rbImg2;
    private RadioButton rbImg3;
    private RadioButton rightChoice;
    private Button btnSubmit;
    private double resolutionTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resolver_tarea);

        tvTittle = findViewById(R.id.tarea_tittle);
        tvConsigna = findViewById(R.id.tarea_consigna);
        ivImg1 = findViewById(R.id.iv_img1);
        ivImg2 = findViewById(R.id.iv_img2);
        ivImg3 = findViewById(R.id.iv_img3);
        rbGroup = findViewById(R.id.radioGroupResolver);
        rbImg1 = findViewById(R.id.radio_img1);
        rbImg2 = findViewById(R.id.radio_img2);
        rbImg3 = findViewById(R.id.radio_img3);
        btnSubmit = findViewById(R.id.btnSubmit);

        idTarea = getIntent().getStringExtra("ID_TAREA");
        Log.i("IDTAREA", idTarea);
        loadData();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("CRONOMETRO", String.format("%.2f", resolutionTime) + "s");

                stopChronometer();

                if (rbGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getApplicationContext(), "Por favor elija una imagen.", Toast.LENGTH_SHORT).show();
                } else {
                    int selectedId = rbGroup.getCheckedRadioButtonId();
                    RadioButton selectedRD = findViewById(selectedId);

                    // --- PENDIENTE --- Enviar consulta a base de datos aumentando en uno la cantidad de intentos de esta tarea.

                    if (selectedRD == rightChoice) {
                        Toast.makeText(getApplicationContext(), "¡Felicidades! Respondiste correctamente.", Toast.LENGTH_SHORT).show();

                        int i = (int) resolutionTime; //temp
                        tarea.setTareaCompletada(i);

                    } else {
                        Toast.makeText(getApplicationContext(), "¡Casi! Sigue intentándolo.", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });

        rbGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
            }
        });
    }

    protected void onDestroy() {
        stopChronometer();
        super.onDestroy();
    }

    private void initializeChronometer() {
        Intent service = new Intent(this, Chronometer.class);
        startService(service);
    }

    private void stopChronometer() {
        Intent service = new Intent(this, Chronometer.class);
        stopService(service);
    }

    public void updateChronometer(double time) {
        resolutionTime = time;
    }

    private void loadData() {
        tarea = new Tarea();
        WebServer webServer = new WebServer(this);
        String url = webServer.generateUrlResolverTarea(idTarea);
        Log.i("URL", url);

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("RESPONSE", response.toString());

                        JSONArray json = null;
                        JSONObject jsonObject = null;
                        try {
                            json = response.getJSONArray("tarea");
                            jsonObject = json.getJSONObject(0);
                        } catch (JSONException e) {
                            Log.i("JSON", e.toString());
                        }

                        //tvTittle.setText("Vacío"); //tarea.getTittle()
                        tvConsigna.setText(jsonObject.optString("consigna"));

                        JSONObject jsonObj1 = null;
                        JSONObject jsonObj2 = null;
                        JSONObject jsonObj3 = null;

                        try {
                            json = response.getJSONArray("1");
                            jsonObj1 = json.getJSONObject(0);

                            json = response.getJSONArray("2");
                            jsonObj2 = json.getJSONObject(0);

                            json = response.getJSONArray("3");
                            jsonObj3 = json.getJSONObject(0);

                        } catch (JSONException e) {
                            Log.i("JSON", e.toString());
                        }

                        Log.i("IMAGENES","Imagen 1: " + jsonObj1.optString("contenido") +
                                        " Imagen 2: " + jsonObj2.optString("contenido") +
                                        " Imagen 3: " + jsonObj3.optString("contenido"));

                        ivImg1.setImageBitmap(base64toBitmap(jsonObj1.optString("contenido")));
                        ivImg2.setImageBitmap(base64toBitmap(jsonObj2.optString("contenido")));
                        ivImg3.setImageBitmap(base64toBitmap(jsonObj3.optString("contenido")));

                        int num = 0;
                        JSONObject jsonObj;

                        try {
                            json = response.getJSONArray("imagencorrecta");
                            jsonObj = json.getJSONObject(0);
                            num = Integer.parseInt(jsonObj.optString("numero"));
                        } catch (NumberFormatException nfe) {
                            Log.i("ERROR", "Opcion correcta invalida");
                        } catch (JSONException jsone) {
                            Log.i("JSON", jsone.toString());
                        }

                        switch (num) {
                            case 1:
                                rightChoice = rbImg1;
                                break;
                            case 2:
                                rightChoice = rbImg2;
                                break;
                            case 3:
                                rightChoice = rbImg3;
                                break;
                        }

                        Toast.makeText(ResolverTareaActivity.this,jsonObject.optString("numero"),Toast.LENGTH_LONG);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.i("RESPONSE", error.toString());
                    }
                });
        queue.add(jsonObjectRequest);


        if (true) {
            Log.i("DEBUG", "-- aqui se llama al servicio --");
            Chronometer.setUpdateListener(this);
            initializeChronometer();
        } else {
            Toast.makeText(this,"No se pudieron cargar los datos",Toast.LENGTH_LONG).show();
        }
    }

    private Bitmap base64toBitmap(String image) {
        Bitmap decodedImage = null;
        try {
            image = URLDecoder.decode(image, "utf-8");
            byte[] imageBytes = Base64.decode(image, Base64.DEFAULT);
            decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0 , imageBytes.length);
        } catch (UnsupportedEncodingException uee) {
            Log.i("DECODE", "Error al decodificar url. " + uee.toString());
        } catch (IllegalArgumentException iae) {
            Log.i("DECODE", "Error al decodificar url. " + iae.toString());
        }
        return decodedImage;
    }
}