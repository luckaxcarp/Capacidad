package opr.capacidad.Data;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

final public class WebServer implements Response.Listener<JSONObject>, Response.ErrorListener {

    private static String AWS_SERVER = "http://18.218.177.65/";
    private static String LOCAL_SERVER = "http://192.168.1.43/capacidad/";

    private static String INTERFAZ_INSERT_TAREA_ELEGIR_IMAGEN = "ins-tarea-elegirimagen.php";
    private static String INTERFAZ_SELECT_TAREA_ELEGIR_IMAGEN = "sel-tarea-elegirimagen.php";
    private static String NUEVA_INTERFAZ_SELECT_TAREA_ELEGIR_IMAGEN = "resolver-tarea.php";

    // Constants of insert Tarea
    private static String CAMPO_CONSIGNA = "consigna";
    private static String CAMPO_FECHA_PROGRAMADA = "f_programada";
    private static String CAMPO_TIPO = "tipo";
    private static String CAMPO_ID_TERAPIA = "id_terapia";
    private static String CAMPO_UBICACION1 = "ubicacion1";
    private static String CAMPO_UBICACION2 = "ubicacion2";
    private static String CAMPO_UBICACION3 = "ubicacion3";
    private static String CAMPO_IMAGENCORRECTA = "imagencorrecta";
    private static String VALUE_TIPO_ELEGIR_IMAGEN = "1";

    // Constants of select Tarea
    private static String CAMPO_ID_TAREA = "tarea";

    private static String SERVER_URL = LOCAL_SERVER;

    private String url = "";
    private RequestQueue request;
    private JsonObjectRequest jsonObjectRequest;

    private JSONObject connResponse;
    private VolleyError connError;
    private boolean errorState;

    public WebServer(Context context) {
        request = Volley.newRequestQueue(context);
        Log.i("WebServer", "constructor");
    }

    public static String generateUrlCreateTarea() {
        String url = SERVER_URL + INTERFAZ_INSERT_TAREA_ELEGIR_IMAGEN;

        return url;
    }

    public static String oldGenerateUrlCreateTarea(String consigna, String fechaProgramada, String idTerapia,
                                                String ubicacion1, String ubicacion2, String ubicacion3,
                                                String imagencorrecta) {
        Log.i("WebServer", "generateUrlCreateTarea");
        String tipoTarea = VALUE_TIPO_ELEGIR_IMAGEN;

        String url = SERVER_URL + INTERFAZ_INSERT_TAREA_ELEGIR_IMAGEN + "?" + CAMPO_CONSIGNA + "=" +
                consigna + "&" + CAMPO_FECHA_PROGRAMADA + "=" + fechaProgramada + "&" +
                CAMPO_TIPO + "=" + tipoTarea + "&" + CAMPO_ID_TERAPIA + "=" + idTerapia +
                "&" + CAMPO_UBICACION1 + "=" + ubicacion1 + "&" + CAMPO_UBICACION2 + "=" + ubicacion2 +
                "&" + CAMPO_UBICACION3 + "=" + ubicacion3 + "&" + CAMPO_IMAGENCORRECTA + "=" + imagencorrecta;

        url = url.replace(" ", "%20");

        Log.i("WebServer", "url: " + url);

        return url;
    }

    public String generateUrlResolverTarea(String idtarea) {
        return url = SERVER_URL + NUEVA_INTERFAZ_SELECT_TAREA_ELEGIR_IMAGEN + "?" + CAMPO_ID_TAREA + "=" +
                idtarea;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        errorState = true;
        connError = error;
        Log.i("TAREA", error.toString());
    }

    @Override
    public void onResponse(JSONObject response) {
        errorState = false;
        connResponse = response;
        Log.i("TAREA", connResponse.toString());
    }
}
