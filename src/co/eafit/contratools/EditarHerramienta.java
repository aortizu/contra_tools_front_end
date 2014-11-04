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

public class EditarHerramienta extends Activity {

	private Bundle extras;
	private EditText in1, in2, in3, in4;
	private String id;

	@Override
	protected void onCreate(Bundle savinnstanceState) {
		super.onCreate(savinnstanceState);
		setContentView(R.layout.activity_editar_herramienta);

		this.in1 = (EditText) findViewById(R.id.editText1);
		this.in2 = (EditText) findViewById(R.id.editText2);
		this.in3 = (EditText) findViewById(R.id.editText3);
		this.in4 = (EditText) findViewById(R.id.editText4);

		extras = getIntent().getExtras();
		if (extras != null) {
			in1.setText(extras.getString("nombre", ""));
			in2.setText(extras.getString("serial", ""));
			in3.setText(extras.getString("descripcion", ""));
			in4.setText(extras.getString("comentarios", ""));
			id = extras.getString("ID");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.editar_herramienta, menu);
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
		Toast.makeText(getBaseContext(), "Información de herramienta",
				Toast.LENGTH_LONG).show();
		this.finish();
	}

	public void editarHerramienta(View v) {
		if (isConnected()) {
			
			String nombre = in1.getText().toString();
			String serial = in2.getText().toString();
			String descripcion = in3.getText().toString();
			String comentario = in4.getText().toString();

			nombre = nombre.replace(" ", "%20");
			serial = serial.replace(" ", "%20");
			descripcion = descripcion.replace(" ", "%20");
			comentario = comentario.replace(" ", "%20");
			
			new HttpAsyncTaskintar()
					.execute("http://contratools-143332.sae1.nitrousbox.com:8080/herramienta/update/"
							+ id
							+ "?nombre="
							+ nombre
							+ "&serial="
							+ serial
							+ "&descripcion="
							+ descripcion
							+ "&comentario=" + comentario);
		} else {
			Toast.makeText(getBaseContext(),
					"Error realizando conexión al servicio!", Toast.LENGTH_LONG)
					.show();
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

	private class HttpAsyncTaskintar extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {

			return GET(urls[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				if (result.contains("nombre") && result.contains(in1.getText().toString())) {
					Toast.makeText(getBaseContext(),
							"Herramienta editada exitosamente!",
							Toast.LENGTH_LONG).show();
					finish();
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
}
