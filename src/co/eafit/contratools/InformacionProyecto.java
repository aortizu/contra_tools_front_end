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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class InformacionProyecto extends Activity {

	private TextView txt1, txt2, txt3, txt4, txt5, txt6, txt7;
	private Button btn1,btn2,btn4;
	private CheckBox check;
	private String id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_informacion_proyecto);

		txt1 = (TextView) findViewById(R.id.textNombre);
		txt2 = (TextView) findViewById(R.id.TextCargo);
		txt3 = (TextView) findViewById(R.id.TextDocumento);
		txt4 = (TextView) findViewById(R.id.TextView02);
		txt5 = (TextView) findViewById(R.id.TextView07);
		txt6 = (TextView) findViewById(R.id.TextView09);
		txt7 = (TextView) findViewById(R.id.TextComentarios);
		
		btn1 = (Button) findViewById(R.id.Button01);
		btn2 = (Button) findViewById(R.id.button1);
		btn4 = (Button) findViewById(R.id.Button02);

		check = (CheckBox) findViewById(R.id.checkBox1);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			id = extras.getString("id", "");
			txt1.setText(extras.getString("nombre", ""));
			txt2.setText(extras.getString("inicio", ""));
			txt3.setText(extras.getString("provista", ""));
			txt4.setText(extras.getString("real", ""));
			txt5.setText(extras.getString("lugar", ""));
			txt6.setText(extras.getString("cliente", ""));
			txt7.setText(extras.getString("comentarios", ""));

			if (extras.getString("activo", "").equals("1")) {
				check.setChecked(true);
				btn1.setVisibility(View.VISIBLE);
				btn2.setVisibility(View.VISIBLE);
				btn4.setVisibility(View.VISIBLE);
			} else {
				check.setChecked(false);
				btn1.setVisibility(View.INVISIBLE);
				btn2.setVisibility(View.INVISIBLE);
				btn4.setVisibility(View.INVISIBLE);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.informacion_proyecto, menu);
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

	public void editar(View v) {
		Toast.makeText(getBaseContext(), "Editar proyecto", Toast.LENGTH_LONG)
				.show();
		Intent in = new Intent(this, EditarProyecto.class);
		in.putExtra("nombre", txt1.getText().toString());
		in.putExtra("fecha", txt4.getText().toString());
		in.putExtra("lugar", txt5.getText().toString());
		in.putExtra("cliente", txt6.getText().toString());
		in.putExtra("comentarios", txt7.getText().toString());
		in.putExtra("id", id);
		startActivity(in);
		finish();
	}
	
	public void asignar(View v) {
		Toast.makeText(getBaseContext(), "Asignar empleados ", Toast.LENGTH_LONG)
				.show();
		Intent in = new Intent(this, ProyectoEmpleado.class);
		in.putExtra("id", id);
		startActivity(in);
		finish();
	}

	public void eliminar(View v) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Eliminar proyecto");
		alertDialogBuilder
				.setMessage("¿Realmente deseas eliminar el proyecto?")
				.setCancelable(false)
				.setPositiveButton("Si",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								new HttpAsyncTaskEliminar()
								.execute("http://contratools-143332.sae1.nitrousbox.com:8080/proyectos/destroy/"
										+ InformacionProyecto.this.id);
								
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();

	}

	
	public void concluir(View v) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Concluir proyecto");
		alertDialogBuilder
				.setMessage("¿Realmente deseas concluir el proyecto?")
				.setCancelable(false)
				.setPositiveButton("Si",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								new HttpAsyncTaskEditar()
								.execute("http://contratools-143332.sae1.nitrousbox.com:8080/proyectos/update/"
										+ InformacionProyecto.this.id
										+ "?activo=false");
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();

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
				if (result.contains(id)) {
					Toast.makeText(getBaseContext(),
							"Proyecto eliminado exitosamente!",
							Toast.LENGTH_LONG).show();
					navigatetoHomeActivity();
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
	
	
	private class HttpAsyncTaskEditar extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {

			return GET(urls[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				if (result.contains(id)) {
					Toast.makeText(getBaseContext(),
							"Proyecto concluido exitosamente!",
							Toast.LENGTH_LONG).show();
					navigatetoHomeActivity();
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
	
	public void navigatetoHomeActivity() {
		Intent homeIntent = new Intent(getApplicationContext(), Home.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeIntent);
		finish();
	}
	
}
