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

public class NuevoProyecto extends Activity {

	private EditText in1, in2, in3, in4, in5, in6;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nuevo_proyecto);

		this.in1 = (EditText) findViewById(R.id.editText1);
		this.in2 = (EditText) findViewById(R.id.editText2);
		this.in3 = (EditText) findViewById(R.id.documento);
		this.in4 = (EditText) findViewById(R.id.vinculacion);
		this.in5 = (EditText) findViewById(R.id.EditText01);
		this.in6 = (EditText) findViewById(R.id.Comentario);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nuevo_proyecto, menu);
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
		Toast.makeText(getBaseContext(), "Proyectos", Toast.LENGTH_LONG).show();
		this.finish();
	}

	public void crearProyecto(View v) {
		Toast.makeText(getBaseContext(), "Guardar", Toast.LENGTH_LONG).show();
		String nombre = in1.getText().toString();
		String inicio = in2.getText().toString();
		String provista = in3.getText().toString();
		String lugar = in4.getText().toString();
		String cliente = in5.getText().toString();
		String comentario = in6.getText().toString();

		nombre = nombre.replace(" ", "%20");
		inicio = inicio.replace(" ", "%20");
		provista = provista.replace(" ", "%20");
		lugar = lugar.replace(" ", "%20");
		cliente = cliente.replace(" ", "%20");
		comentario = comentario.replace(" ", "%20");

		new HttpAsyncTaskInsertar()
				.execute("http://contratools-143332.sae1.nitrousbox.com:8080/proyectos/create?nombre="
						+ nombre
						+ "&inicio="
						+ inicio
						+ "&provista="
						+ provista
						+ "&real="
						+ provista
						+ "&activo="
						+ "true"
						+ "&lugar="
						+ lugar
						+ "&cliente="
						+ cliente
						+ "&comentarios=" + comentario);

	}

	public static String POST(String url) {
		InputStream inputStream = null;
		String result = "";
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse httpResponse = httpclient.execute(new HttpPost(url));
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

	private class HttpAsyncTaskInsertar extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			return POST(urls[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				if (result.contains(in1.getText().toString())) {

					String ID = result;
					ID = ID.substring(ID.indexOf("id\":") + 5);
					ID = ID.substring(0, ID.indexOf("}"));

					String nombre = in1.getText().toString();
					nombre = nombre.replace(" ", "%20");

					new HttpAsyncTaskEmpleados()
							.execute("http://contratools-143332.sae1.nitrousbox.com:8080/proyecto_empleado/create?ids_empleados=%20&nombre_proyecto="
									+ nombre + "&id_proyecto=" + ID);
					Toast.makeText(getBaseContext(),
							"Proyecto guardado con exito", Toast.LENGTH_LONG)
							.show();
					finish();
				} else {
					Toast.makeText(getBaseContext(),
							"Error al guardar el proyecto", Toast.LENGTH_LONG)
							.show();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	private class HttpAsyncTaskEmpleados extends
			AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			return POST(urls[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				System.out.println(result);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

}
