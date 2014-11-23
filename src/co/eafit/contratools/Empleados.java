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

public class Empleados extends Activity {

	private ListView listView;
	private List<ItemEmpleado> items;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_empleados);

		this.listView = (ListView) findViewById(R.id.list);
		items = new ArrayList<ItemEmpleado>();

		if (isConnected()) {
			new HttpAsyncTask()
					.execute("http://contratools-143332.sae1.nitrousbox.com:8080/empleado/");
		} else {
			Toast.makeText(getBaseContext(),
					"Error realizando conexión al servicio!", Toast.LENGTH_LONG)
					.show();
		}

	}

	public void reload(View v) {
		if (isConnected()) {
			new HttpAsyncTask()
					.execute("http://contratools-143332.sae1.nitrousbox.com:8080/empleado/");
		} else {
			Toast.makeText(getBaseContext(),
					"Error realizando conexión al servicio!", Toast.LENGTH_LONG)
					.show();
		}
	}

	public void nuevoEmpleado(View v) {
		Intent in = new Intent(this, NuevoEmpleado.class);
		startActivity(in);
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

						items.add(new ItemEmpleado(R.drawable.empleado, nombre,
								cargo, documento, vinculacion, comentarios, id));
					}
					listView.setAdapter(new ItemAdapterEmpleado(Empleados.this,
							items));

					listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> adapter,
								View view, int position, long arg) {
							ItemEmpleado item = (ItemEmpleado) listView
									.getAdapter().getItem(position);

							Toast.makeText(getBaseContext(),
									"Información de empleado",
									Toast.LENGTH_LONG).show();

							Intent intent = new Intent(getApplicationContext(),
									InformacionEmpleado.class);

							intent.putExtra("id", item.getId());
							intent.putExtra("nombre", item.getNombre());
							intent.putExtra("cargo", item.getCargo());
							intent.putExtra("documento", item.getDocumento());
							intent.putExtra("vinculacion",
									item.getVinculacion());
							intent.putExtra("comentarios",
									item.getComentarios());
							startActivity(intent);
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

	public void buscarEmpleado(View v) {
		Toast.makeText(getBaseContext(), "Buscar empleado", Toast.LENGTH_LONG)
				.show();
		Intent in = new Intent(this, BuscarEmpleado.class);
		startActivity(in);
	}

}
