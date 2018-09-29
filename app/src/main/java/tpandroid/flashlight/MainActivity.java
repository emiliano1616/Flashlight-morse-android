package tpandroid.flashlight;

import java.util.List;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ToggleButton;

@SuppressWarnings("deprecation")
public class MainActivity extends ActionBarActivity {

	private Camera mCamera;
	private Thread blinkThread;

	/**
	 * Setea el estado de la linterna (on/off) de acuerdo al estado del toggleButton
	 * 
	 * @parametro v
	 */
	public void switchFlashlight(View v)
    {
		toggleButtonAction();
	}

	private void toggleButtonAction() {
		ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggleButton1);
		flashlight(toggleButton.isChecked());
	}

	/**
	 * Prende y apaga la linterna
	 * 
	 * @parametro on
	 */
	public void flashlight(boolean on) {
        checkCameraInstance();
		Parameters p = mCamera.getParameters();
		if (on) {
			if (!p.getFlashMode().equals(Parameters.FLASH_MODE_TORCH)) {
				p.setFlashMode(Parameters.FLASH_MODE_TORCH);
				mCamera.setParameters(p);
			}
		} else {
			if (p.getFlashMode().equals(Parameters.FLASH_MODE_TORCH)) {
				p.setFlashMode(Parameters.FLASH_MODE_OFF);
				mCamera.setParameters(p);
			}
		}
	}

	/**
	 * Esta función será la que realizará el parpadeo de acuerdo al patrón descrito por la lista de booleanos
	 * una unidad de tiempo tiene una duracion de 100ms
	 * 
	 * @parametro signals
	 */
	public void flashPattern(final List<Boolean> signals) {
		if (blinkThread != null) {
			blinkThread.interrupt();
		}
		blinkThread = new Thread() {
			@Override
			public void run() {
				for (Boolean signal : signals) {
					if (isInterrupted()) {// die gracefully
						break;
					}
					flashlight(signal);
					try {
						sleep(100);
					} catch (InterruptedException e) {// die gracefully
						break;
					}
				}
				toggleButtonAction();
			}

		};
		blinkThread.start();

	}

	/**
	 * Convierte el contenido de la caja de texto en un patrón de señales y juega con éste
	 * a través del flash de la cámara
	 * 
	 * @parametros view
	 */
	public void flash(View v) {
		EditText editText = (EditText) findViewById(R.id.editText1);
		Morse morse = new Morse();
		List<Boolean> signals = morse.messageToSignal(editText.getText()
				.toString());
		flashPattern(signals);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        checkCameraInstance();
	}

    private void checkCameraInstance() {
        SystemClock.sleep(1000);
        if (mCamera == null) {
            mCamera = Camera.open();
        }
    }

	@Override
	public void onPause() {
		super.onPause();
		if (blinkThread != null) {
			blinkThread.interrupt();// stop blinking
			try {
				blinkThread.join();
			} catch (InterruptedException e) {
				//do nothing
			}
		}
		if (mCamera != null) {
			mCamera.release();// release the cam to other apps
			mCamera = null;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		//toggleButtonAction();
	}

    public void OpenCamActivity(View view) {
        Intent intent = new Intent(this, MorseCamReaderActivity.class);
        startActivity(intent);
    }

    public void onPreferencesClick(View view) {
        Intent intent = new Intent(this,Preferences.class);
        //start the second Activity
        this.startActivity(intent);
    }
}
