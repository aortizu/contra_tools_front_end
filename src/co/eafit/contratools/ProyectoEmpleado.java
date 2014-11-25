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
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

public class ProyectoEmpleado extends Activity {

	private Bundle extras;
	private ListView listView;
	private List<ItemSelect> items, aux;
	private String empleados, id;
	private CheckBox check;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_proyecto_empleado);

		empleados = "";

		check = (CheckBox) findViewById(R.id.checkBox2);

		extras = getIntent().getExtras();
		if (extras != null) {
			id = extras.getString("id", "");
		}

		this.listView = (ListView) findViewById(R.id.list);
		items = new ArrayList<ItemSelect>();

		if (isConnected()) {
			new HttpAsyncTask()
					.execute("http://contratools-143332.sae1.nitrousbox.com:8080/empleado/");
		} else {
			Toast.makeText(getBaseContext(),
					"Error realizando conexión al servicio!", Toast.LENGTH_LONG)
					.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.proyecto_empleado, menu);
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

	public void guardar(View V) {

		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).isChecked()) {
				empleados = empleados + items.get(i).getId() + ",";
			}
		}

		if (isConnected()) {
			new HttpAsyncTaskEmpleados()
					.execute("http://contratools-143332.sae1.nitrousbox.com:8080/proyecto_empleado/add/"
							+ id);
		} else {
			Toast.makeText(getBaseContext(),
					"Error realizando conexión al servicio!", Toast.LENGTH_LONG)
					.show();
		}
	}

	public void filtro(View V) {
		if (isConnected()) {
			new HttpAsyncTask()
					.execute("http://contratools-143332.sae1.nitrousbox.com:8080/empleado/");
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

	public void navigatetoHomeActivity() {
		Intent homeIntent = new Intent(getApplicationContext(), Empleados.class);
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

				if (respuesta.contains("cargo")) {
					items.clear();
					for (int i = 0; i < listdata.size(); i++) {

						String id = listdata.get(i);
						id = id.substring(id.indexOf("id\":") + 4);
						id = id.substring(0, id.indexOf(","));

						String nombre = listdata.get(i);
						nombre = nombre
								.substring(nombre.indexOf("nombre\":") + 9);
						nombre = nombre.substring(0, nombre.indexOf("\""));

						String cargo = listdata.get(i);
						cargo = cargo.substring(cargo.indexOf("cargo\":") + 8);
						cargo = cargo.substring(0, cargo.indexOf("\""));

						String documento = listdata.get(i);
						documento = documento.substring(documento
								.indexOf("documento\":") + 12);
						documento = documento.substring(0,
								documento.indexOf("\""));

						String vinculacion = listdata.get(i);
						vinculacion = vinculacion.substring(vinculacion
								.indexOf("vinculacion\":") + 14);
						vinculacion = vinculacion.substring(0,
								vinculacion.indexOf("\""));

						String comentarios = listdata.get(i);
						comentarios = comentarios.substring(comentarios
								.indexOf("comentarios\":") + 14);
						comentarios = comentarios.substring(0,
								comentarios.indexOf("\""));

						items.add(new ItemSelect(R.drawable.empleado, nombre,
								cargo, documento, vinculacion, comentarios, id));
					}
					new HttpAsyncTaskConsultar()
							.execute("http://contratools-143332.sae1.nitrousbox.com:8080/proyecto_empleado/add/"
									+ ProyectoEmpleado.this.id);

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

	private class HttpAsyncTaskEmpleados extends
			AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			return GET(urls[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				if (result.contains("id") && result.contains(id)) {
					String ID = result;
					ID = ID.substring(ID.indexOf("id\":") + 5);
					ID = ID.substring(0, ID.indexOf(","));

					if (!empleados.equals(""))
						new HttpAsyncTaskAsignar()
								.execute("http://contratools-143332.sae1.nitrousbox.com:8080/proyecto_empleado/update/"
										+ ID + "?ids_empleados=" + empleados);
				} else {
					Toast.makeText(getBaseContext(),
							"Error realizando conexión al servicio!",
							Toast.LENGTH_LONG).show();
				}

			} catch (Exception e) {
				// TODO: handle exception
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
				if (result.contains("id")) {
					Toast.makeText(getBaseContext(),
							"Trabajadores asignados correctamente",
							Toast.LENGTH_LONG).show();
					finish();
				} else {
					Toast.makeText(getBaseContext(),
							"Error realizando conexión al servicio!",
							Toast.LENGTH_LONG).show();
				}

			} catch (Exception e) {
				// TODO: handle exception
			}
		}
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
				if (result.contains("id")) {

					String ids = result;
					ids = ids.substring(ids.indexOf("ids_empleados\":") + 17);
					ids = ids.substring(0, ids.indexOf("\""));

					String[] array;

					if (!ids.isEmpty()) {
						if (ids.contains(",")) {
							array = ids.split(",", -1);
							for (int i = 0; i < array.length - 1; i++) {
								for (int j = 0; j < items.size(); j++) {
									if (items.get(j).getId().equals(array[i]))
										items.get(j).setChecked(true);
								}
							}
						}
					}

					aux = new ArrayList<ItemSelect>();

					if (check.isChecked()) {
						for (int i = 0; i < items.size(); i++) {
							if (items.get(i).isChecked())
								aux.add(items.get(i));
						}
					} else {
						aux = items;
					}
					
					listView.setAdapter(new ItemAdapterSelect(
							ProyectoEmpleado.this, aux));

					listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> adapter,
								View view, int position, long arg) {
							ItemSelect item = (ItemSelect) listView
									.getAdapter().getItem(position);
							item.setChecked(!item.isChecked());
							listView.setAdapter(new ItemAdapterSelect(
									ProyectoEmpleado.this, aux));
						}
					});

				} else {
					Toast.makeText(getBaseContext(),
							"Error realizando conexión al servicio!",
							Toast.LENGTH_LONG).show();
				}

			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	public void buscarEmpleado(View v) {
		Toast.makeText(getBaseContext(), "Buscar empleado", Toast.LENGTH_LONG)
				.show();
		Intent in = new Intent(this, BuscarEmpleado.class);
		startActivity(in);
	}
}
