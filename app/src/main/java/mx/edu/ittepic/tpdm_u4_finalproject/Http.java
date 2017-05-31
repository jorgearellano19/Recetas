package mx.edu.ittepic.tpdm_u4_finalproject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import com.loopj.android.http.*;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.StatusLine;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class Http {
    public void postHTTP(){
        String url = "url_to_you_server_api.dev/postservice"

        HttpClient client = new DefaultHttpClient();

        HttpPost request = new HttpPost(url);

        JSONObject parametros = new JSONObject();

        parametros.put("usuario","usuario");
        parametros.put("clave","top_secret");

        StringEntity jsonEntity = new StringEntity( dataJson.toString() );

        HttpPost request = new HttpPost(url);

        //Agregar esto ya que algunos frameworks (nodejs frameworks) necesitan esto
        //para poder parsear los payloads automaticamente
        request.addHeader("Content-Type","application/json");

        //Se configura el payload a enviar al servicio
        request.setEntity(jsonEntity);

        //Ejecutamos el request ya compuesto
        response = client.execute(request);

        //De este punto en adelante es los mismo que una llamada GET al servicio.
        StatusLine statusLine = response.getStatusLine();

    }
    public void getHTTP(){
        //service api url
        String url = "http://localhost.dev/country";

        // declarar un client http en este caso DefaultHttpClient
        HttpClient client = new DefaultHttpClient();

        //Como nuestro servicio es un metodo get usamos HTTPGet
        HttpGet request = new HttpGet(url);

        HttpResponse response;
        List entityResult;

        try {
            //Ejecutamos el request ya compuesto
            response = client.execute(request);

            StatusLine statusLine = response.getStatusLine();

            //El statusLine retorna el resultado del request a nivel de statusCode
            //AKA ( 200,404,500,401,403... )
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {

                //Si queremos el resultado del servicio (json) debemos de obtenerlo del payload del request
                //Que es obtenido como un InputStream el cual tendremos que convertir a String
                InputStream in = response.getEntity().getContent();


                BufferedReader buffered = new BufferedReader(new InputStreamReader(in));
                StringBuilder fullLines = new StringBuilder();

                String line;

                //
                while ((line = buffered.readLine()) != null) {
                    fullLines.append(line);
                }

                String result = fullLines.toString();

                JSONArray objetos = new JSONArray(result);

                for(int i=0;i<objetos.length();i++){

                    JSONObject objeto =  objetos.getJSONObject(i);

                    /*

                        cada objecto sera compuesto por esto
                        {
                            country: "pais",
                            capital:"capital"
                        }

                    */

                }
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {} catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String inputStreamToString(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        StringBuilder sb = new StringBuilder();

        String line;

        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        return sb.toString();
    }

}
