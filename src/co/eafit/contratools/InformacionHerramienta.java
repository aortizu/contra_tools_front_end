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
import android.widget.TextView;
import android.widget.Toast;

public class InformacionHerramienta extends Activity {

	private Bundle extras;
	private TextView txt1, txt2, txt3, txt4, txt5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_informacion_herramienta);
		this.txt1 = (TextView) findViewById(R.id.textNombre);
		this.txt2 = (TextView) findViewById(R.id.TextCargo);
		this.txt3 = (TextView) findViewById(R.id.TextDocumento);
		this.txt4 = (TextView) findViewById(R.id.TextComentarios);
		this.txt5 = (TextView) findViewById(R.id.TextView02);

		extras = getIntent().getExtras();
		if (extras != null) {
			txt1.setText(extras.getString("nombre", ""));
			txt2.setText(extras.getString("serial", ""));
			txt3.setText(extras.getString("descripcion", ""));
			txt4.setText(extras.getString("comentarios", ""));
			txt5.setText(extras.getString("ID", ""));
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.informacion_herramienta, menu);
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

	public void eliminar(View v) {
		if (isConnected()) {
			if (isConnected()) {
				new HttpAsyncTaskEliminar()
						.execute("http://contratools-143332.sae1.nitrousbox.com:8080/herramienta/eliminar/"
								+ txt5.getText().toString());
			} else {
				Toast.makeText(getBaseContext(),
						"Error realizando conexión al servicio!",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	public void editar(View v) {
		Toast.makeText(getBaseContext(), "Editar herramienta",
				Toast.LENGTH_LONG).show();
		Intent in = new Intent(InformacionHerramienta.this,
				EditarHerramienta.class);
		in.putExtra("nombre", txt1.getText().toString());
		in.putExtra("serial", txt2.getText().toString());
		in.putExtra("descripcion", txt3.getText().toString());
		in.putExtra("comentarios", txt4.getText().toString());
		in.putExtra("ID", txt5.getText().toString());
		startActivity(in);
		finish();
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

	private class HttpAsyncTaskEliminar extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {

			return GET(urls[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			try {

				if (result.contains("succes")) {
					Toast.makeText(getBaseContext(),
							"Herramienta eliminada exitosamente!",
							Toast.LENGTH_LONG).show();
					finish();
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
