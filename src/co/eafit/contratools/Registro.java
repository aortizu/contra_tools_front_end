package co.eafit.contratools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class Registro extends Activity {

	private EditText in1, in2, in3, in4, in5;
	private static Pattern pattern;
	private static Matcher matcher;
	// Email Pattern
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registro);

		in1 = (EditText) findViewById(R.id.actualPassword);
		in2 = (EditText) findViewById(R.id.editText2);
		in3 = (EditText) findViewById(R.id.editText3);
		in4 = (EditText) findViewById(R.id.editText4);
		in5 = (EditText) findViewById(R.id.editText5);
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

	public void crearUsuario(View v) {

		Toast.makeText(getBaseContext(), "Registrar", Toast.LENGTH_LONG).show();
		String usuario = in1.getText().toString();
		String password = in2.getText().toString();
		String repassword = in3.getText().toString();
		String email = in4.getText().toString();
		String empresa = in5.getText().toString();

		pattern = Pattern.compile(EMAIL_PATTERN);
		matcher = pattern.matcher(email);

		if (password.equals(repassword)) {
			if (matcher.matches()) {
				isConnected();

				new HttpAsyncTask()
						.execute("http://contratools-143332.sae1.nitrousbox.com:8080/usuario/create?usuario="
								+ usuario
								+ "&password="
								+ password
								+ "&email="
								+ email + "&empresa=" + empresa);
			} else {
				Toast.makeText(getBaseContext(),
						"Por favor ingrese un correo user@domain.com valido",
						Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(getBaseContext(), "Los password no coinciden",
					Toast.LENGTH_LONG).show();
		}

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
							"Usuario creado con exito", Toast.LENGTH_LONG)
							.show();
					finish();
				} else {
					Toast.makeText(getBaseContext(),
							"Error al crear el usuario", Toast.LENGTH_LONG)
							.show();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

}
