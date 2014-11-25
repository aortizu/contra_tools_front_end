package co.eafit.contratools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Admon extends Activity {

	private EditText in1, in2, in3;
	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admon);

		prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);

		in1 = (EditText) findViewById(R.id.editText1);
		in2 = (EditText) findViewById(R.id.editText2);
		in3 = (EditText) findViewById(R.id.editText3);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.admon, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void cerrar(View v) {
		Toast.makeText(getBaseContext(), "Home", Toast.LENGTH_LONG).show();
		this.finish();
	}

	public void cambiar(View v) {
		Toast.makeText(getBaseContext(), "Cambiar password",
				Toast.LENGTH_LONG).show();
		if (!in2.getText().toString().equals(in3.getText().toString())) {
			Toast.makeText(
					getBaseContext(),
					"El nuevo password no coincide con el campo de verificación",
					Toast.LENGTH_LONG).show();
		} else {
			new HttpAsyncTaskEditar()
			.execute("http://contratools-143332.sae1.nitrousbox.com:8080/usuario/"
					+ prefs.getString("userID", ""));
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

	private class HttpAsyncTaskEditar extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {

			return GET(urls[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				if (result.contains(in1.getText().toString())) {
					String contrasenia = in2.getText().toString();
					contrasenia = contrasenia.replace(" ", "%20");
					new HttpAsyncTaskCambiar()
							.execute("http://contratools-143332.sae1.nitrousbox.com:8080/usuario/update/"
									+ prefs.getString("userID", "")
									+ "?password="
									+ contrasenia);
				} else {
					Toast.makeText(getBaseContext(),
							"Password actual invalido",
							Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class HttpAsyncTaskCambiar extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {

			return GET(urls[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				if (result.contains("usuario")) {
					Toast.makeText(getBaseContext(),
							"Password cambiado exitosamente!",
							Toast.LENGTH_LONG).show();
					navigatetoHomeActivity();
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

	public void navigatetoHomeActivity() {
		Intent homeIntent = new Intent(getApplicationContext(), Home.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeIntent);
		finish();
	}

}
