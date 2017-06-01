package mx.edu.ittepic.tpdm_u4_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VistaRecetas extends AppCompatActivity {
    RequestQueue queue;
    ListView list;
    ArrayList<String> recetas = new ArrayList<>();
    String[] res;
    Integer[] ico;
    String[] difi;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_recetas);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        list = (ListView)findViewById(R.id.list);
        queue=Volley.newRequestQueue(this); // this = context
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        get();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirVentanta();
            }
        });
        //get();
    }
    public void abrirVentanta(){
        Intent nueva= new Intent(VistaRecetas.this,NuevaReceta.class);
        Bundle b = new Bundle();
        b.putInt("idMax",recetas.size()-1);
        nueva.putExtras(b);startActivity(nueva);
    }
    public void get(){
        recetas.clear();
        final String url = "http://ealejandrocasillas.96.lt/recetas/receta.php";
        // prepare the Request
        JsonObjectRequest getRequest;
        getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        try {
                            JSONArray categories = response.getJSONArray("recetas");
                            res= new String[categories.length()];
                            ico= new Integer[categories.length()];
                            difi= new String[categories.length()];
                            for (int i = 0; i < categories.length(); i++) {
                                JSONObject category = categories.getJSONObject(i);
                                String nombre = category.getString("nombre");
                                String id = category.getString("id_categoria");
                                String descripcion = category.getString("descripcion");
                                String ingredientes = category.getString("ingredientes");
                                String dificultad = category.getString("dificultad");
                                recetas.add(nombre);
                                res[i]=nombre;
                                ico[i]=R.drawable.icono;
                                difi[i]=dificultad;
                                Log.e("cat", nombre);
                            }
                            updateList();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Log.e("Response", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        );
        // add it to the RequestQueue
        queue.add(getRequest);
    }

    private void updateList() {
        LenguajeListAdapter adapt= new LenguajeListAdapter(this,res,difi,ico);
        list.setAdapter(adapt);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.listview_recetas, res);
        //list.setAdapter(adapter);
    }
}
