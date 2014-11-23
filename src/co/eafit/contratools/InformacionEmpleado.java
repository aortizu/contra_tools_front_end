package co.eafit.contratools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.zbar.Symbol;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class InformacionEmpleado extends Activity {

	private TextView txt1, txt2, txt3, txt4, txt5;
	private String id;
	private static final int ZBAR_SCANNER_REQUEST = 0;
	private static final int ZBAR_QR_SCANNER_REQUEST = 1;
	private String QR = "";
	private String scann;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_informacion_empleado);

		txt1 = (TextView) findViewById(R.id.textNombre);
		txt2 = (TextView) findViewById(R.id.TextCargo);
		txt3 = (TextView) findViewById(R.id.TextDocumento);
		txt4 = (TextView) findViewById(R.id.TextVinculacion);
		txt5 = (TextView) findViewById(R.id.TextComentarios);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			id = extras.getString("id", "");
			txt1.setText(extras.getString("nombre", ""));
			txt2.setText(extras.getString("cargo", ""));
			txt3.setText(extras.getString("documento", ""));
			txt4.setText(extras.getString("vinculacion", ""));
			txt5.setText(extras.getString("comentarios", ""));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.informacion_empleado, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void eliminar(View v) {
		if (isConnected()) {
			if (isConnected()) {
				new HttpAsyncTaskEliminar()
						.execute("http://contratools-143332.sae1.nitrousbox.com:8080/empleado/destroy/"
								+ id);
			} else {
				Toast.makeText(getBaseContext(),
						"Error realizando conexión al servicio!",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	public void editar(View v) {
		Toast.makeText(getBaseContext(), "Editar empleado", Toast.LENGTH_LONG)
				.show();
		Intent intent = new Intent(this, EditarEmpleado.class);
		intent.putExtra("id", id);
		intent.putExtra("nombre", txt1.getText().toString());
		intent.putExtra("cargo", txt2.getText().toString());
		intent.putExtra("documento", txt3.getText().toString());
		intent.putExtra("vinculacion", txt4.getText().toString());
		intent.putExtra("comentarios", txt5.getText().toString());
		startActivity(intent);
		finish();
	}

	public void listaHerramientas(View v) {
		Toast.makeText(getBaseContext(), "Herramientas asignadas",
				Toast.LENGTH_LONG).show();
		Intent intent = new Intent(this, EmpleadoHerramientas.class);
		intent.putExtra("ID", id);
		startActivity(intent);
	}

	public void launchQRScanner(View v) {
		Toast.makeText(getBaseContext(), "Asignar Herramienta",
				Toast.LENGTH_LONG).show();
		if (isCameraAvailable()) {
			scann = "asignar";
			Intent intent = new Intent(this, ZBarScannerActivity.class);
			intent.putExtra(ZBarConstants.SCAN_MODES,
					new int[] { Symbol.QRCODE });
			startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
		} else {
			Toast.makeText(this, "Rear Facing Camera Unavailable",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void retirar(View v) {
		Toast.makeText(getBaseContext(), "Asignar Herramienta",
				Toast.LENGTH_LONG).show();
		if (isCameraAvailable()) {
			scann = "retirar";
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
				if (!QR.isEmpty()) {
					if (scann.equals("asignar")) {
						new HttpAsyncTaskConsultar()
								.execute("http://contratools-143332.sae1.nitrousbox.com:8080/herramienta/"
										+ data.getStringExtra(ZBarConstants.SCAN_RESULT));
					} else {
						new HttpAsyncTaskRetirar()
								.execute("http://contratools-143332.sae1.nitrousbox.com:8080/empleado_herramienta/retirar");
					}
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

	public static String POST(List<NameValuePair> pairs, String url) {
		InputStream inputStream = null;
		String result = "";
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);
			UrlEncodedFormEntity ok = new UrlEncodedFormEntity(pairs);
			post.setEntity(ok);
			HttpResponse httpResponse = httpclient.execute(post);
			inputStream = httpResponse.getEntity().getContent();

			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "ERROR";
		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}
		return result;
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
				result = "ERROR";
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

	private class HttpAsyncTaskConsultar extends
			AsyncTask<String, Void, String> {
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

					new HttpAsyncTaskAsignar()
							.execute("http://contratools-143332.sae1.nitrousbox.com:8080/empleado_herramienta/create?nombre_empleado="
									+ txt1.getText().toString()
									+ "&id_empleado="
									+ id
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

	private class HttpAsyncTaskAsignar extends AsyncTask<String, Void, String> {
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

	private class HttpAsyncTaskRetirar extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("id_herramienta", QR));
			pairs.add(new BasicNameValuePair("id_empleado", id));

			return POST(pairs, urls[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			try {

				if (result.contains("succes")) {
					Toast.makeText(getBaseContext(),
							"Herramienta retirada correctamente",
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getBaseContext(),
							"Error realizando el retiro de la herramienta",
							Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class HttpAsyncTaskEliminar extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("id_herramienta", QR));
			pairs.add(new BasicNameValuePair("id_empleado", id));

			return POST(pairs, urls[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			try {

				if (result.contains("nombre")
						&& result.contains(txt1.getText().toString())) {
					Toast.makeText(getBaseContext(),
							"Empleado eliminado correctamente",
							Toast.LENGTH_LONG).show();
					finish();
				} else {
					Toast.makeText(getBaseContext(),
							"Error eliminando el empleado", Toast.LENGTH_LONG)
							.show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
