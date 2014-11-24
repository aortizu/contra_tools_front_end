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
import android.content.Intent;
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

public class EditarEmpleado extends Activity {

	private EditText in1, in2, in3, in4, in5;
	private String id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editar_empleado);

		in1 = (EditText) findViewById(R.id.editText1);
		in2 = (EditText) findViewById(R.id.editText2);
		in3 = (EditText) findViewById(R.id.documento);
		in4 = (EditText) findViewById(R.id.vinculacion);
		in5 = (EditText) findViewById(R.id.Comentario);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			id = extras.getString("id", "");
			in1.setText(extras.getString("nombre", ""));
			in2.setText(extras.getString("cargo", ""));
			in3.setText(extras.getString("documento", ""));
			in4.setText(extras.getString("vinculacion", ""));
			in5.setText(extras.getString("comentarios", ""));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.editar_empleado, menu);
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

	public void cerrar(View v) {
		Toast.makeText(getBaseContext(), "Información de empleado",
				Toast.LENGTH_LONG).show();
		this.finish();
	}

	public void editar(View v) {
		if (isConnected()) {
			String nombre = in1.getText().toString();
			String cargo = in2.getText().toString();
			String documento = in3.getText().toString();
			String fecha = in4.getText().toString();
			String comentario = in5.getText().toString();

			nombre = nombre.replace(" ", "%20");
			cargo = cargo.replace(" ", "%20");
			documento = documento.replace(" ", "%20");
			fecha = fecha.replace(" ", "%20");
			comentario = comentario.replace(" ", "%20");

			new HttpAsyncTaskEditar()
					.execute("http://contratools-143332.sae1.nitrousbox.com:8080/empleado/update/"
							+ id
							+ "?nombre="
							+ nombre
							+ "&cargo="
							+ cargo
							+ "&documento="
							+ documento
							+ "&vinculacion="
							+ fecha + "&comentarios=" + comentario);
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

	private class HttpAsyncTaskEditar extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {

			return GET(urls[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				if (result.contains("nombre")
						&& result.contains(in1.getText().toString())) {
					Toast.makeText(getBaseContext(),
							"Empleado editado exitosamente!", Toast.LENGTH_LONG)
							.show();
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
