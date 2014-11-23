package co.eafit.contratools;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.TextView;

public class InformacionProyecto extends Activity {

	private TextView txt1, txt2, txt3, txt4, txt5, txt6, txt7;
	private CheckBox check;
	private String id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_informacion_proyecto);
		
		txt1= (TextView) findViewById(R.id.textNombre);
		txt2= (TextView) findViewById(R.id.TextCargo);
		txt3= (TextView) findViewById(R.id.TextDocumento);
		txt4= (TextView) findViewById(R.id.TextView02);
		txt5= (TextView) findViewById(R.id.TextView07);
		txt6= (TextView) findViewById(R.id.TextView09);
		txt7= (TextView) findViewById(R.id.TextComentarios);
		
		check= (CheckBox)findViewById(R.id.checkBox1);
		
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
			
			if (extras.getString("activo", "").equals("1")){
				check.setChecked(true);
			}else{
				check.setChecked(false);
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
}
