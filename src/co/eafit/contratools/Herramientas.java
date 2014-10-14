package co.eafit.contratools;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

public class Herramientas extends Activity {

	private ListView listView;
	private List<ItemHerramienta> items;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Use a custom layout file
		setContentView(R.layout.activity_herramientas);

		this.listView = (ListView) findViewById(R.id.list);
		items = new ArrayList<ItemHerramienta>();

		if (isConnected()) {
			new HttpAsyncTask()
					.execute("http://contratools-143332.sae1.nitrousbox.com:8080/herramienta/");
		} else {
			Toast.makeText(getBaseContext(),
					"Error realizando conexión al servicio!", Toast.LENGTH_LONG)
					.show();
		}

	}

	public void reload(View v) {
		if (isConnected()) {
			new HttpAsyncTask()
					.execute("http://contratools-143332.sae1.nitrousbox.com:8080/herramienta/");
		} else {
			Toast.makeText(getBaseContext(),
					"Error realizando conexión al servicio!", Toast.LENGTH_LONG)
					.show();
		}
	}

	public void nuevaHerramienta(View v) {
		Intent in = new Intent(this, NuevaHerramienta.class);
		startActivity(in);
	}

	public static String GET(String url) {
		InputStream inputStream = null;
		String result = "";
		try {

			// create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

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

	public void navigatetoHomeActivity() {
		Intent homeIntent = new Intent(getApplicationContext(),
				Herramientas.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeIntent);
		finish();
	}

	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {

			return GET(urls[0]);
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			try {
				// Toast.makeText(getBaseContext(), "Received!",
				// Toast.LENGTH_LONG).show();
				JSONArray json = new JSONArray(result);
				String respuesta = json.toString(1);

				ArrayList<String> listdata = new ArrayList<String>();

				if (json != null) {
					for (int i = 0; i < json.length(); i++) {
						listdata.add(json.get(i).toString());
					}
				}

				if (respuesta.contains("serial")) {
					items.clear();
					for (int i = 0; i < listdata.size(); i++) {
						String nombre = listdata.get(i);
						nombre = nombre
								.substring(nombre.indexOf("nombre\":") + 9);
						nombre = nombre.substring(0, nombre.indexOf("\""));

						String serial = listdata.get(i);
						serial = serial.substring(serial
								.indexOf("serial\":") + 9);
						serial = serial.substring(0,
								serial.indexOf("\""));
						
						String descripcion = listdata.get(i);
						descripcion = descripcion.substring(descripcion
								.indexOf("descripcion\":") + 14);
						descripcion = descripcion.substring(0,
								descripcion.indexOf("\""));
						
						String comentario = listdata.get(i);
						comentario = comentario.substring(comentario
								.indexOf("comentario\":") + 13);
						comentario = comentario.substring(0,
								comentario.indexOf("\""));

						items.add(new ItemHerramienta(R.drawable.herramienta, nombre,
								serial,descripcion,comentario));
					}

					// Sets the data behind this ListView
					listView.setAdapter(new ItemAdapterHerramienta(Herramientas.this,
							items));


					listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> adapter,
								View view, int position, long arg) {
							// Sets the visibility of the indeterminate progress
							// bar in the
							// title
							setProgressBarIndeterminateVisibility(true);
							// Item item = (Item)
							// listView.getAdapter().getItem(position);
							// ProgressDialog.show(Home.this,
							// item.getTitle(), "Cargando");

						}
					});
				} else if (respuesta.equals("[]")) {
					items.clear();
				} else {
					Toast.makeText(getBaseContext(),
							"Error realizando conexión al servicio!",
							Toast.LENGTH_LONG).show();
				}
				// etResponse.setText(json.toString(1));
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	public void atras(View v) {
		Toast.makeText(getBaseContext(), "Atras!", Toast.LENGTH_LONG).show();
		finish();
	}

}
