package co.eafit.contratools;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemAdapterEmpleado extends BaseAdapter {

	private Context context;
	private List<ItemEmpleado> items;

	public ItemAdapterEmpleado(Context context, List<ItemEmpleado> items) {
		this.context = context;
		this.items = items;
	}

	@Override
	public int getCount() {
		return this.items.size();
	}

	@Override
	public Object getItem(int position) {
		return this.items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View rowView = convertView;

		if (convertView == null) {
			// Create a new view into the list.
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.list_item, parent, false);
		}

		// Set data into the view.
		ImageView imagen = (ImageView) rowView.findViewById(R.id.ivItem);
		TextView nombre = (TextView) rowView.findViewById(R.id.nombre);
		TextView intereses = (TextView) rowView.findViewById(R.id.intereses);

		ItemEmpleado item = this.items.get(position);
		nombre.setText(item.getNombre());
		intereses.setText(item.getCargo());
		imagen.setImageResource(item.getImage());

		return rowView;
	}

}
