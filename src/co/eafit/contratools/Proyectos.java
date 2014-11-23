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

public class Proyectos extends Activity {

	private ListView listView;
	private List<ItemProyecto> items;
	private CheckBox check;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_proyectos);

		this.listView = (ListView) findViewById(R.id.list);
		items = new ArrayList<ItemProyecto>();
		check = (CheckBox) findViewById(R.id.checkBox1);

		if (isConnected()) {
			new HttpAsyncTask()
					.execute("http://contratools-143332.sae1.nitrousbox.com:8080/proyectos/");
		} else {
			Toast.makeText(getBaseContext(),
					"Error realizando conexión al servicio!", Toast.LENGTH_LONG)
					.show();
		}
	}

	public void reload(View v) {
		if (isConnected()) {
			new HttpAsyncTask()
					.execute("http://contratools-143332.sae1.nitrousbox.com:8080/proyectos/");
		} else {
			Toast.makeText(getBaseContext(),
					"Error realizando conexión al servicio!", Toast.LENGTH_LONG)
					.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.proyectos, menu);
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

	public void nuevoProyecto(View v) {
		Toast.makeText(getBaseContext(), "Nuevo proyecto", Toast.LENGTH_LONG)
				.show();
		Intent in = new Intent(this, NuevoProyecto.class);
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
				JSONArray json = new JSONArray(result);
				String respuesta = json.toString(1);

				ArrayList<String> listdata = new ArrayList<String>();

				if (json != null) {
					for (int i = 0; i < json.length(); i++) {
						listdata.add(json.get(i).toString());
					}
				}

				if (respuesta.contains("inicio")
						&& respuesta.contains("provista")) {
					items.clear();
					for (int i = 0; i < listdata.size(); i++) {

						String id = listdata.get(i);
						id = id.substring(id.indexOf("id\":") + 4);
						id = id.substring(0, id.indexOf(","));

						String nombre = listdata.get(i);
						nombre = nombre
								.substring(nombre.indexOf("nombre\":") + 9);
						nombre = nombre.substring(0, nombre.indexOf("\""));

						String inicio = listdata.get(i);
						inicio = inicio
								.substring(inicio.indexOf("inicio\":") + 9);
						inicio = inicio.substring(0, inicio.indexOf("\""));

						String provista = listdata.get(i);
						provista = provista.substring(provista
								.indexOf("provista\":") + 11);
						provista = provista
								.substring(0, provista.indexOf("\""));

						String real = listdata.get(i);
						real = real.substring(real.indexOf("real\":") + 7);
						real = real.substring(0, real.indexOf("\""));

						String activo = listdata.get(i);
						activo = activo
								.substring(activo.indexOf("activo\":") + 9);
						activo = activo.substring(0, activo.indexOf("\""));

						String lugar = listdata.get(i);
						lugar = lugar.substring(lugar.indexOf("lugar\":") + 8);
						lugar = lugar.substring(0, lugar.indexOf("\""));

						String cliente = listdata.get(i);
						cliente = cliente.substring(cliente
								.indexOf("cliente\":") + 10);
						cliente = cliente.substring(0, cliente.indexOf("\""));

						String comentarios = listdata.get(i);
						comentarios = comentarios.substring(comentarios
								.indexOf("comentarios\":") + 14);
						comentarios = comentarios.substring(0,
								comentarios.indexOf("\""));
						if (check.isChecked()){
							items.add(new ItemProyecto(R.drawable.proyectoitem,
									nombre, inicio, provista, real, activo, lugar,
									cliente, comentarios, id));
						}else if (!check.isChecked()&& activo.equals("1")){
							items.add(new ItemProyecto(R.drawable.proyectoitem,
									nombre, inicio, provista, real, activo, lugar,
									cliente, comentarios, id));
						}

					}
					listView.setAdapter(new ItemAdapterProyecto(Proyectos.this,
							items));

					listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> adapter,
								View view, int position, long arg) {
							ItemProyecto item = (ItemProyecto) listView
									.getAdapter().getItem(position);

							Toast.makeText(getBaseContext(),
									"Información de empleado",
									Toast.LENGTH_LONG).show();

							Intent intent = new Intent(getApplicationContext(),
									InformacionProyecto.class);

							intent.putExtra("id", item.getId());
							intent.putExtra("nombre", item.getNombre());
							intent.putExtra("inicio", item.getInicio());
							intent.putExtra("provista", item.getProvista());
							intent.putExtra("real", item.getReal());
							intent.putExtra("activo", item.getActivo());
							intent.putExtra("lugar", item.getLugar());
							intent.putExtra("cliente", item.getCliente());
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

}
