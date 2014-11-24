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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class BuscarEmpleado extends Activity {

	private ListView listView;
	private List<ItemEmpleado> items;
	private CheckBox check1, check2;
	private EditText txt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buscar_empleado);

		this.listView = (ListView) findViewById(R.id.list);
		this.check1 = (CheckBox) findViewById(R.id.checkBox1);
		this.check2 = (CheckBox) findViewById(R.id.checkBox2);
		this.txt = (EditText) findViewById(R.id.editText1);
		items = new ArrayList<ItemEmpleado>();
		
		if (isConnected()) {
			new HttpAsyncTaskText()
					.execute("http://contratools-143332.sae1.nitrousbox.com:8080/empleado/");
		} else {
			Toast.makeText(getBaseContext(),
					"Error realizando conexión al servicio!", Toast.LENGTH_LONG)
					.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.buscar_empleado, menu);
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

	public void buscar(View v) {
		if (isConnected()) {
			new HttpAsyncTaskText()
					.execute("http://contratools-143332.sae1.nitrousbox.com:8080/empleado/");
		} else {
			Toast.makeText(getBaseContext(),
					"Error realizando conexión al servicio!", Toast.LENGTH_LONG)
					.show();
		}
	}

	public void nombre(View v) {
		check1.setChecked(true);
		check2.setChecked(false);
	}

	public void documento(View v) {
		check1.setChecked(false);
		check2.setChecked(true);
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

	private class HttpAsyncTaskText extends AsyncTask<String, Void, String> {
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

				if (respuesta.contains("documento")
						&& respuesta.contains("cargo")) {
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

						if (check1.isChecked()
								&& nombre.contains(txt.getText().toString())) {
							items.add(new ItemEmpleado(R.drawable.empleado,
									nombre, cargo, documento, vinculacion,
									comentarios, id));
						} else if (check2.isChecked()
								&& documento.contains(txt.getText().toString())) {
							items.add(new ItemEmpleado(R.drawable.empleado,
									nombre, cargo, documento, vinculacion,
									comentarios, id));
						}
					}

					listView.setAdapter(new ItemAdapterEmpleado(
							BuscarEmpleado.this, items));

					listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> adapter,
								View view, int position, long arg) {

							ItemEmpleado item = (ItemEmpleado) listView
									.getAdapter().getItem(position);

							Toast.makeText(getBaseContext(),
									"Información de empleado",
									Toast.LENGTH_LONG).show();
							Intent in = new Intent(BuscarEmpleado.this,
									InformacionEmpleado.class);
							in.putExtra("nombre", item.getNombre());
							in.putExtra("cargo", item.getCargo());
							in.putExtra("documento", item.getDocumento());
							in.putExtra("vinculacion", item.getVinculacion());
							in.putExtra("comentarios", item.getComentarios());
							in.putExtra("ID", item.getId());
							startActivity(in);
						}
					});
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
