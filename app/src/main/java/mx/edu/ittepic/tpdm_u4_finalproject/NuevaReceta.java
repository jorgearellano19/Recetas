package mx.edu.ittepic.tpdm_u4_finalproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.sql.*;

public class NuevaReceta extends AppCompatActivity {
    RequestQueue queue;
    ArrayList<String> categorias = new ArrayList<String>();
    ArrayList<String> catId = new ArrayList<String>();
    Spinner spinner;
    Button añadir, nueva;
    EditText nombre, ingredientes, procedimiento,dificultad;
    FloatingActionButton fab;
    LinearLayout images;
    ArrayList<byte[]> imagesInByte = new ArrayList();
    int idMax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        idMax = intent.getExtras().getInt("idMax");
        Log.e("Num receta max",idMax+"");
        setContentView(R.layout.activity_nueva_receta);
        queue = Volley.newRequestQueue(this); // this = context
        spinner = (Spinner) findViewById(R.id.spinner);
        getCategoria();
        añadir = (Button) findViewById(R.id.button);
        nombre = (EditText) findViewById(R.id.editText2);
        ingredientes = (EditText) findViewById(R.id.editText3);
        procedimiento = (EditText) findViewById(R.id.editText4);
        dificultad=(EditText)findViewById(R.id.editText5);
        nueva = (Button) findViewById(R.id.button3);
        images = (LinearLayout)findViewById(R.id.images);
        fab = (FloatingActionButton)findViewById(R.id.fabCamera);

        nueva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarReceta();
            }
        });

        añadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageDialog("Nombre de la categoría");
            }
        });
        //post();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera,1);
            }
        });
    }

    private void agregarReceta(){
        String n = nombre.getText().toString();
        String ing = ingredientes.getText().toString();
        String proc = procedimiento.getText().toString();
        String dif=dificultad.getText().toString();
        int cat = (int)spinner.getSelectedItemId();
        String cate = catId.get(cat);
        postReceta(n, cate, ing, proc,dif);

        postFotos();
    }

    private void postFotos() {
        String url = "http://ealejandrocasillas.96.lt/recetas/foto.php";
        for (int i=0;i<imagesInByte.size();i++) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("id_receta",idMax+1+"");
            Blob blob = new Blob() {
                @Override
                public long length() throws SQLException {
                    return 0;
                }

                @Override
                public byte[] getBytes(long pos, int length) throws SQLException {
                    return new byte[0];
                }

                @Override
                public InputStream getBinaryStream() throws SQLException {
                    return null;
                }

                @Override
                public long position(byte[] pattern, long start) throws SQLException {
                    return 0;
                }

                @Override
                public long position(Blob pattern, long start) throws SQLException {
                    return 0;
                }

                @Override
                public int setBytes(long pos, byte[] bytes) throws SQLException {
                    return 0;
                }

                @Override
                public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
                    return 0;
                }

                @Override
                public OutputStream setBinaryStream(long pos) throws SQLException {
                    return null;
                }

                @Override
                public void truncate(long len) throws SQLException {

                }

                @Override
                public void free() throws SQLException {

                }

                @Override
                public InputStream getBinaryStream(long pos, long length) throws SQLException {
                    return null;
                }
            };
            try {
                blob.setBytes(1, imagesInByte.get(i));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            params.put("imagen",blob.toString());
            JSONObject jsonObject = new JSONObject(params);
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
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
            queue.add(postRequest);
        }

    }

    public void messageDialog(String mensaje) {
        android.app.AlertDialog.Builder m = new android.app.AlertDialog.Builder(this);
        final EditText nombre = new EditText(this);
        m.setTitle("Añadir categoria");
        m.setView(nombre);

        m.setMessage(mensaje).setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!nombre.getText().toString().isEmpty()) {
                    postCategoria(nombre.getText().toString());
                } else {
                    Toast.makeText(NuevaReceta.this, "El campo esta vacio", Toast.LENGTH_LONG);
                }

                dialog.dismiss();
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();
    }

    public void getCategoria() {
        final String url = "http://ealejandrocasillas.96.lt/recetas/categoria.php";
        // prepare the Request
        categorias.clear();
        catId.clear();
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
                                categorias.add(cat);
                                catId.add(id);
                                Log.e("cat", cat);
                            }
                            updateSpinner();
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

    private void updateSpinner() {
        ArrayAdapter<String> categorylist = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categorias);
        spinner.setAdapter(categorylist);
    }

    public void postReceta(String nombre, String categoria, String descripcion, String ingredientes,String dificultad){
        String url = "http://ealejandrocasillas.96.lt/recetas/receta.php";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_categoria", categoria);
        params.put("nombre", nombre);
        params.put("descripcion", descripcion);
        params.put("ingredientes", ingredientes);
        params.put("dificultad", dificultad);
        JSONObject jsonObject = new JSONObject(params);
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
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
        queue.add(postRequest);



    }

    public void postCategoria(final String category) {
        String url = "http://ealejandrocasillas.96.lt/recetas/categoria.php";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("nombre", category);
        JSONObject jsonObject = new JSONObject(params);
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        VolleyLog.v("jorch", response.toString());
                        categorias.add(category);
                        updateSpinner();
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
        queue.add(postRequest);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1 && resultCode==RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap photo = (Bitmap) extras.get("data");
            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(photo);
            byte[] imageByte = DbBitmapUtility.getBytes(photo);
            Log.e("Byte 1",imageByte+"");
            imagesInByte.add(imageByte);
            images.addView(imageView);
        }
    }

}
