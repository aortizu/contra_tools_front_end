package co.eafit.contratools;

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

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class EmpleadoHerramientas extends Activity {

	private ListView listView;
	private List<ItemHerramienta> items;
	private Bundle extras;
	private String id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_empleado_herramientas);

		this.listView = (ListView) findViewById(R.id.list);
		items = new ArrayList<ItemHerramienta>();

		extras = getIntent().getExtras();
		if (extras != null) {
			id = extras.getString("ID", "");
		}

		if (isConnected()) {
			new HttpAsyncTask()
					.execute("http://contratools-143332.sae1.nitrousbox.com:8080/empleado_herramienta/");
		} else {
			Toast.makeText(getBaseContext(),
					"Error realizando conexión al servicio!", Toast.LENGTH_LONG)
					.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.empleado_herramientas, menu);
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

	public void reload(View v) {
		if (isConnected()) {
			new HttpAsyncTask()
					.execute("http://contratools-143332.sae1.nitrousbox.com:8080/empleado_herramienta/");
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

	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {

			return GET(urls[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				JSONArray json = new JSONArray(result);
				String respuesta = json.toString(1);

				ArrayList<String> listdata = new ArrayList<String>();

				if (json != null) {
					for (int i = 0; i < json.length(); i++) {
						listdata.add(json.get(i).toString());
					}
				}

				if (respuesta.contains("id_empleado")) {
					items.clear();
					for (int i = 0; i < listdata.size(); i++) {
						String id_herramienta = listdata.get(i);
						id_herramienta = id_herramienta
								.substring(id_herramienta
										.indexOf("id_herramienta\":") + 16);
						id_herramienta = id_herramienta.substring(0,
								id_herramienta.indexOf(","));

						String nombre_herramienta = listdata.get(i);
						nombre_herramienta = nombre_herramienta
								.substring(nombre_herramienta
										.indexOf("nombre_herramienta\":") + 21);
						nombre_herramienta = nombre_herramienta.substring(0,
								nombre_herramienta.indexOf("\""));

						String id_empleado = listdata.get(i);
						id_empleado = id_empleado.substring(id_empleado
								.indexOf("id_empleado\":") + 13);
						id_empleado = id_empleado.substring(0,
								id_empleado.indexOf(","));

						String nombre_empleado = listdata.get(i);
						nombre_empleado = nombre_empleado
								.substring(nombre_empleado
										.indexOf("nombre_empleado\":") + 18);
						nombre_empleado = nombre_empleado.substring(0,
								nombre_empleado.indexOf("\""));

						if (id.equals(id_empleado)) {
							items.add(new ItemHerramienta(
									R.drawable.herramienta, nombre_herramienta,
									"", nombre_empleado, "", id_herramienta));
						}
					}

					listView.setAdapter(new ItemAdapterHerramienta(
							EmpleadoHerramientas.this, items));

				} else if (respuesta.equals("[]")) {
					items.clear();
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
