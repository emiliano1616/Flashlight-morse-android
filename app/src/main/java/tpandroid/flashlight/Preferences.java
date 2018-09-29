package tpandroid.flashlight;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by emiliano on 07/06/2015.
 */
public class Preferences extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.preferences_layout);
        SharedPreferences sp = getSharedPreferences("CamReader", 0);
        int value = sp.getInt("lightAccurate", 60);

        EditText myText = (EditText) findViewById(R.id.preferenceText);
        myText.setText(String.valueOf( value), TextView.BufferType.EDITABLE);
    }

    public void onSaveClick(View view) {
        EditText myText = (EditText) findViewById(R.id.preferenceText);
        int value = 70;
        try{

             value = Integer.parseInt(myText.getText().toString());
        }catch (Exception e) {
            Log.e("Error",e.getMessage());
            return;
        }


        SharedPreferences sp = getSharedPreferences("CamReader", 0);
        SharedPreferences.Editor editor = sp.edit();

        editor.putInt("lightAccurate", value);
        editor.commit();

        new AlertDialog.Builder(this)
                .setTitle("Informacion")
                .setMessage("La configuracion ha sido guardada")
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();

    }
}
