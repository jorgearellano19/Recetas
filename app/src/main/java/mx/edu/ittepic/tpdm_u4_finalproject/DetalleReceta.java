package mx.edu.ittepic.tpdm_u4_finalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class DetalleReceta extends AppCompatActivity {
    RequestQueue queue;
    TextView nombre, descripcion, categoria, ingredientes, dificultad;
    ImageView imagen;
    String n,c_id,d,i,des,categoryContent,r_id;
    ArrayList<String> fotosList;
    Button eliminar, actualizar;
    ArrayList<ImageView> imageViewList;

    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_receta);

        r_id = this.getIntent().getExtras().getSerializable("id").toString();
        n=this.getIntent().getExtras().getSerializable("nombre").toString();
        c_id=this.getIntent().getExtras().getSerializable("categoria").toString();
        Log.e("ID category",c_id);
        d=this.getIntent().getExtras().getSerializable("dificultad").toString();
        i=this.getIntent().getExtras().getSerializable("ingredientes").toString();
        des=this.getIntent().getExtras().getSerializable("descripcion").toString();
        eliminar = (Button) findViewById(R.id.button2);
        actualizar = (Button) findViewById(R.id.button4);
        queue = Volley.newRequestQueue(this); // this = context

        fotosList = new ArrayList<String>();
        imageViewList = new ArrayList<ImageView>();

        getCategoria();
        getFotos();

        linearLayout = (LinearLayout) findViewById(R.id.images);

        nombre = (TextView)findViewById(R.id.textView5);
        categoria = (TextView)findViewById(R.id.textView10);
        dificultad = (TextView)findViewById(R.id.textView11);
        ingredientes = (TextView)findViewById(R.id.textView12);
        descripcion = (TextView)findViewById(R.id.textView13);
        nombre.setText(n);
        categoria.setText(categoryContent);
        dificultad.setText(d);
        ingredientes.setText(i);
        descripcion.setText(des);

        imagen = (ImageView) findViewById(R.id.imageView);

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteReceta(r_id);
                openMain();
            }
        });

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActualizar();
            }
        });
    }

    public void openActualizar(){
        Intent v = new Intent(DetalleReceta.this, NuevaReceta.class);
        v.putExtra("id",r_id);
        v.putExtra("nombre",n);
        v.putExtra("categoria",c_id);
        v.putExtra("dificultad",d);
        v.putExtra("ingredientes",i);
        v.putExtra("descripcion",des);
        startActivity(v);
        finish();
    }

    public void openMain(){
        Intent v = new Intent(DetalleReceta.this, VistaRecetas.class);
        startActivity(v);
        finish();
    }

    public void deleteReceta(String id){
        String url = "http://ealejandrocasillas.96.lt/recetas/receta.php?id=" + id;
        HashMap<String, String> params = new HashMap<String, String>();
        JSONObject jsonObject = new JSONObject(params);
        JsonObjectRequest deleteRequest = new JsonObjectRequest(Request.Method.DELETE, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        VolleyLog.v("jorch", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        );
        queue.add(deleteRequest);
    }

    public void getCategoria() {
        final String url = "http://ealejandrocasillas.96.lt/recetas/categoria.php";
        // prepare the Request
        JsonObjectRequest getRequest;
        getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        try {
                            JSONArray categories = response.getJSONArray("categorias");
                            for (int i = 0; i < categories.length(); i++) {
                                JSONObject category = categories.getJSONObject(i);
                                String cat = category.getString("nombre");
                                String id = category.getString("id");
                                if(c_id.equals(id)){
                                    categoryContent=cat;
                                    categoria.setText(categoryContent);
                                }
                                Log.e("cat", cat);
                            }

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

    public void getFotos() {
        final String url = "http://ealejandrocasillas.96.lt/recetas/foto.php";
        // prepare the Request
        JsonObjectRequest getRequest;
        getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        try {
                            JSONArray fotos = response.getJSONArray("fotos");
                            for (int i = 0; i < fotos.length(); i++) {
                                JSONObject category = fotos.getJSONObject(i);
                                String id = category.getString("id_receta");
                                String imagen = category.getString("imagen");
                                //Log.d("Imagen", imagen);

                                if(r_id.equals(id)){
                                    Log.d("Hi", "ENTRAAAAAA");
                                    fotosList.add(imagen);
                                }
                            }


                            for(int i = 0; i<fotosList.size(); i++){
                                ImageView imageView = new ImageView(DetalleReceta.this);
                                byte[] decodedString = Base64.decode(fotosList.get(i), Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                ImageView img = newImageView();
                                img.setImageBitmap(decodedByte);
                                linearLayout.addView(img);
                                Log.d("FOTOS", fotosList.get(i));
                            }

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

    private ImageView newImageView(){
        ImageView iv = new ImageView(this);
        int width = 800;//ancho
        int height =1000;//altura
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width,height);
        parms.setMargins(0,0,0,0);
        iv.setLayoutParams(parms);
        return iv;
    }
}
