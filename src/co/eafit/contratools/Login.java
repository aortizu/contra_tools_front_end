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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class Login extends Activity {

	private EditText in1, in2;
	private String s1;
	private String s2;
	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
		
		in1 = (EditText) findViewById(R.id.editText1);
		in2 = (EditText) findViewById(R.id.editText2);

		in1.setText(prefs.getString("nombre", ""));
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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

	public void login(View v) {

		Toast.makeText(getBaseContext(), " Login", Toast.LENGTH_LONG).show();

		s1 = in1.getText().toString();
		s2 = in2.getText().toString();

		isConnected();

		new HttpAsyncTask()
				.execute("http://contratools-143332.sae1.nitrousbox.com:8080/usuario/?usuario="
						+ s1);

	}

	public void registro(View v) {
		Toast.makeText(getBaseContext(), "Registro", Toast.LENGTH_LONG).show();
		Intent in = new Intent(this, Registro.class);
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
		Intent homeIntent = new Intent(getApplicationContext(), Home.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeIntent);
		finish();
	}

	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {

			return GET(urls[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				if (result.contains(s1) && result.contains(s2)) {
					SharedPreferences.Editor editor = prefs.edit();

					String id = result;
					id = id.substring(id.indexOf("id\":") + 5);
					id = id.substring(0, id.indexOf(","));
					if (!id.equals("null"))
						editor.putString("userID", id);
					
					String nombre =  result;
					nombre = nombre
							.substring(nombre.indexOf("usuario\":") + 11);
					nombre = nombre.substring(0, nombre.indexOf("\""));
					if (!nombre.equals("null"))
						editor.putString("nombre", nombre);
					
					editor.commit();
					
					navigatetoHomeActivity();
				} else{
					Toast.makeText(getBaseContext(),
							"Usuario o password incorrecto", Toast.LENGTH_LONG)
							.show();
				} 
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
}
