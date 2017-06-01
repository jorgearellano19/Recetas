package mx.edu.ittepic.tpdm_u4_finalproject;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by cideti-1 on 17/11/2016.
 */

public interface VolleyCallback {
    void onSuccessArray(JSONArray result);
    void onSuccessObject(JSONObject result);
    void onSuccessString(String result);
}