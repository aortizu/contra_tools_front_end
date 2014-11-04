package co.eafit.contratools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import net.sourceforge.zbar.Symbol;
import co.eafit.escaner.ZBarConstants;
import co.eafit.escaner.ZBarScannerActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class Escaner extends Activity {

	private static final int ZBAR_SCANNER_REQUEST = 0;
	private static final int ZBAR_QR_SCANNER_REQUEST = 1;
	private String asignar;
	private Bundle extras;
	private String QR = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_escaner);

		extras = getIntent().getExtras();
		if (extras != null) {
			asignar = extras.getString("asignar", "");
		}

	}

	public void launchScanner(View v) {
		if (isCameraAvailable()) {
			Intent intent = new Intent(this, ZBarScannerActivity.class);
			startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
		} else {
			Toast.makeText(this, "Rear Facing Camera Unavailable",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void launchQRScanner(View v) {
		Toast.makeText(getBaseContext(), "Escaneando", Toast.LENGTH_LONG)
				.show();
		if (isCameraAvailable()) {
			Intent intent = new Intent(this, ZBarScannerActivity.class);
			intent.putExtra(ZBarConstants.SCAN_MODES,
					new int[] { Symbol.QRCODE });
			startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
		} else {
			Toast.makeText(this, "Rear Facing Camera Unavailable",
					Toast.LENGTH_SHORT).show();
		}
	}

	public boolean isCameraAvailable() {
		PackageManager pm = getPackageManager();
		return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ZBAR_SCANNER_REQUEST:
		case ZBAR_QR_SCANNER_REQUEST:
			if (resultCode == RESULT_OK) {
				QR = data.getStringExtra(ZBarConstants.SCAN_RESULT);
				Toast.makeText(this, "Scan Result = " + QR, Toast.LENGTH_SHORT)
						.show();
				if (!asignar.isEmpty()) {
					new HttpAsyncTask()
							.execute("http://contratools-143332.sae1.nitrousbox.com:8080/herramienta/"
									+ data.getStringExtra(ZBarConstants.SCAN_RESULT));
					asignar = "";
				}

			} else if (resultCode == RESULT_CANCELED && data != null) {
				String error = data.getStringExtra(ZBarConstants.ERROR_INFO);
				if (!TextUtils.isEmpty(error)) {
					Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
				}
			}
			break;
		}
	}

	public static String GET(String url) {
		InputStream inputStream = null;
		String result = "";
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
			inputStream = httpResponse.getEntity().getContent();
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}

	private static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;

	}

	public boolean isConnected() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected())
			return true;
		else
			return false;
	}

	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {

			return GET(urls[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			try {

				if (result.contains("nombre")) {

					String nombre = result;
					nombre = nombre.substring(nombre.indexOf("nombre\":") + 10);
					nombre = nombre.substring(0, nombre.indexOf("\""));

					new HttpAsyncTask1()
							.execute("http://contratools-143332.sae1.nitrousbox.com:8080/empleado_herramienta/create?nombre_empleado="
									+ extras.getString("nombre_empleado")
									+ "&id_empleado="
									+ extras.getString("id_empleado")
									+ "&nombre_herramienta="
									+ nombre
									+ "&id_herramienta=" + QR);

				} else if (result.contains("[]")) {
					Toast.makeText(
							getBaseContext(),
							"Error al asignar herramienta, posible codigo qr errado",
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getBaseContext(),
							"Error realizando conexión al servicio!",
							Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class HttpAsyncTask1 extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {

			return GET(urls[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			try {

				if (result.contains("nombre_herramienta")) {
					Toast.makeText(getBaseContext(),
							"Herramienta asignada correctamente",
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getBaseContext(),
							"Error realizando la asignacion de la herramienta",
							Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
