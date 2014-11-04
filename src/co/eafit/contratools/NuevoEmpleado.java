package co.eafit.contratools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
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

public class NuevoEmpleado extends Activity {

	private EditText in1, in2, in3, in4, in5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nuevo_empleado);

		in1 = (EditText) findViewById(R.id.actualPassword);
		in2 = (EditText) findViewById(R.id.editText2);
		in3 = (EditText) findViewById(R.id.documento);
		in4 = (EditText) findViewById(R.id.vinculacion);
		in5 = (EditText) findViewById(R.id.Comentario);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.registro, menu);
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
		Toast.makeText(getBaseContext(), "Cancelar", Toast.LENGTH_LONG).show();
		Intent in = new Intent(this, Empleados.class);
		startActivity(in);
		finish();
	}

	public static String POST(String url) {
		InputStream inputStream = null;
		String result = "";
		try {

			// create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpPost(url));

			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
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

	public void crearEmpleado(View v) {

		Toast.makeText(getBaseContext(), "Guardar", Toast.LENGTH_LONG).show();
		String nombre = in1.getText().toString();
		String cargo = in2.getText().toString();
		String documento = in3.getText().toString();
		String vinculacion = in4.getText().toString();
		String comentario = in5.getText().toString();

		nombre = nombre.replace(" ", "%20");
		cargo = cargo.replace(" ", "%20");
		documento = documento.replace(" ", "%20");
		vinculacion = vinculacion.replace(" ", "%20");
		comentario = comentario.replace(" ", "%20");

		new HttpAsyncTask()
				.execute("http://contratools-143332.sae1.nitrousbox.com:8080/empleado/create?nombre="
						+ nombre
						+ "&cargo="
						+ cargo
						+ "&documento="
						+ documento
						+ "&vinculacion="
						+ vinculacion
						+ "&comentarios=" + comentario);

	}

	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {

			return POST(urls[0]);
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			try {
				if (result.contains(in1.getText().toString())) {
					Toast.makeText(getBaseContext(),
							"Empleado guardado con exito", Toast.LENGTH_LONG)
							.show();
					finish();
				} else {
					Toast.makeText(getBaseContext(),
							"Error al guardar el empleado", Toast.LENGTH_LONG)
							.show();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

}
