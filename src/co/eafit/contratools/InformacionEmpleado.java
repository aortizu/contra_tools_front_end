package co.eafit.contratools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class InformacionEmpleado extends Activity {
	
	
	private TextView txt1,txt2,txt3,txt4,txt5;
	private String id; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_informacion_empleado);
		
		txt1 = (TextView) findViewById(R.id.textNombre);
		txt2 = (TextView) findViewById(R.id.TextCargo);
		txt3 = (TextView) findViewById(R.id.TextDocumento);
		txt4 = (TextView) findViewById(R.id.TextVinculacion);
		txt5 = (TextView) findViewById(R.id.TextComentarios);
		
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			id= extras.getString("id", "");
			txt1.setText(extras.getString("nombre", ""));
			txt2.setText(extras.getString("cargo", ""));
			txt3.setText(extras.getString("documento", ""));
			txt4.setText(extras.getString("vinculacion", ""));
			txt5.setText(extras.getString("comentarios", ""));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.informacion_empleado, menu);
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
	
	
	public void atras (View v){
		finish();
	}
	
	public void asignarHerramienta (View v){
		Toast.makeText(getBaseContext(), "Asignar Herramienta", Toast.LENGTH_LONG).show();
		Intent in = new Intent(this, Escaner.class);
		in.putExtra("asignar", "true");
		in.putExtra("id_empleado", id);
		in.putExtra("nombre_empleado", txt1.getText().toString());
		startActivity(in);
	}
	
	public void listaHerramientas (View v){
		finish();
	}
	
}
