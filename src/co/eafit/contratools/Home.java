package co.eafit.contratools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class Home extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
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

	public void escaner(View v) {
		Toast.makeText(getBaseContext(), "Escanear", Toast.LENGTH_LONG).show();
		Intent in = new Intent(this, Escaner.class);
		startActivity(in);
	}

	public void herramientas(View v) {
		Toast.makeText(getBaseContext(), "Herramientas", Toast.LENGTH_LONG)
				.show();
		Intent in = new Intent(this, Herramientas.class);
		startActivity(in);
	}
	
	public void empleados(View v) {
		Toast.makeText(getBaseContext(), "Empleados", Toast.LENGTH_LONG)
				.show();
		Intent in = new Intent(this, Empleados.class);
		startActivity(in);
	}

}
