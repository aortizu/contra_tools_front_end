package co.eafit.contratools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.zbar.Symbol;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import co.eafit.escaner.ZBarConstants;
import co.eafit.escaner.ZBarScannerActivity;

public class BuscarHerramienta extends Activity {

	private static final int ZBAR_SCANNER_REQUEST = 0;
	private static final int ZBAR_QR_SCANNER_REQUEST = 1;
	private ListView listView;
	private List<ItemHerramienta> items;
	private CheckBox check1, check2;
	private EditText txt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buscar_herramienta);

		this.listView = (ListView) findViewById(R.id.list);
		this.check1 = (CheckBox) findViewById(R.id.checkBox1);
		this.check2 = (CheckBox) findViewById(R.id.checkBox2);
		this.txt = (EditText) findViewById(R.id.editText1);
		items = new ArrayList<ItemHerramienta>();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.buscar_herramienta, menu);
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
					.execute("http://contratools-143332.sae1.nitrousbox.com:8080/herramienta/");
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

	public void serial(View v) {
		check1.setChecked(false);
		check2.setChecked(true);
	}

	public void launchQRScanner(View v) {
		Toast.makeText(getBaseContext(), "Escaneando", Toast.LENGTH_LONG)
				.show();
		if (isCameraAvailable()) {
			Intent intent = new Intent(this, ZBarScannerActivity.class);
			intent.putExtra(ZBarConstants.SCAN_MODES,
					new int[] { Symbol.QRCODE });
			startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
		} else {
			Toast.makeText(this, "Rear Facing Camera Unavailable",
					Toast.LENGTH_SHORT).show();
		}
	}

	public boolean isCameraAvailable() {
		PackageManager pm = getPackageManager();
		return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ZBAR_SCANNER_REQUEST:
		case ZBAR_QR_SCANNER_REQUEST:
			if (resultCode == RESULT_OK) {
				String QR = data.getStringExtra(ZBarConstants.SCAN_RESULT);
				Toast.makeText(this, "Scan Result = " + QR, Toast.LENGTH_SHORT)
						.show();
				if (!QR.isEmpty()) {
					new HttpAsyncTaskScanner()
							.execute("http://contratools-143332.sae1.nitrousbox.com:8080/herramienta/"
									+ QR);
				}

			} else if (resultCode == RESULT_CANCELED && data != null) {
				String error = data.getStringExtra(ZBarConstants.ERROR_INFO);
				if (!TextUtils.isEmpty(error)) {
					Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
				}
			}
			break;
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

				if (respuesta.contains("serial")) {
					items.clear();
					for (int i = 0; i < listdata.size(); i++) {
						String ID = listdata.get(i);
						ID = ID.substring(ID.indexOf("id\":") + 4);
						ID = ID.substring(0, ID.indexOf(","));

						String nombre = listdata.get(i);
						nombre = nombre
								.substring(nombre.indexOf("nombre\":") + 9);
						nombre = nombre.substring(0, nombre.indexOf("\""));

						String serial = listdata.get(i);
						serial = serial
								.substring(serial.indexOf("serial\":") + 9);
						serial = serial.substring(0, serial.indexOf("\""));

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
						if (check1.isChecked()
								&& nombre.contains(txt.getText().toString())) {
							items.add(new ItemHerramienta(
									R.drawable.herramienta, nombre, serial,
									descripcion, comentario, ID));
						} else if (check2.isChecked()
								&& serial.contains(txt.getText().toString())) {
							items.add(new ItemHerramienta(
									R.drawable.herramienta, nombre, serial,
									descripcion, comentario, ID));
						}
					}

					listView.setAdapter(new ItemAdapterHerramienta(
							BuscarHerramienta.this, items));

					listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> adapter,
								View view, int position, long arg) {

							ItemHerramienta item = (ItemHerramienta) listView
									.getAdapter().getItem(position);

							Toast.makeText(getBaseContext(),
									"Información de herramienta",
									Toast.LENGTH_LONG).show();
							Intent in = new Intent(BuscarHerramienta.this,
									InformacionHerramienta.class);
							in.putExtra("nombre", item.getNombre());
							in.putExtra("serial", item.getSerial());
							in.putExtra("descripcion", item.getDescripcion());
							in.putExtra("comentarios", item.getComentario());
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

	private class HttpAsyncTaskScanner extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {

			return GET(urls[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				if (result.contains("nombre") && result.contains("serial")) {
					String ID = result;
					ID = ID.substring(ID.indexOf("id\":") + 5);
					ID = ID.substring(0, ID.indexOf(","));

					String nombre = result;
					nombre = nombre.substring(nombre.indexOf("nombre\":") + 10);
					nombre = nombre.substring(0, nombre.indexOf("\""));

					String serial = result;
					serial = serial.substring(serial.indexOf("serial\":") + 10);
					serial = serial.substring(0, serial.indexOf("\""));

					String descripcion = result;
					descripcion = descripcion.substring(descripcion
							.indexOf("descripcion\":") + 15);
					descripcion = descripcion.substring(0,
							descripcion.indexOf("\""));

					String comentario = result;
					comentario = comentario.substring(comentario
							.indexOf("comentario\":") + 14);
					comentario = comentario.substring(0,
							comentario.indexOf("\""));

					Toast.makeText(getBaseContext(),
							"Información de herramienta", Toast.LENGTH_LONG)
							.show();
					Intent in = new Intent(BuscarHerramienta.this,
							InformacionHerramienta.class);
					in.putExtra("nombre", nombre);
					in.putExtra("serial", serial);
					in.putExtra("descripcion", descripcion);
					in.putExtra("comentarios", comentario);
					in.putExtra("ID", ID);
					startActivity(in);
				} else {
					Toast.makeText(getBaseContext(),
							"La herramienta NO existe", Toast.LENGTH_LONG)
							.show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
