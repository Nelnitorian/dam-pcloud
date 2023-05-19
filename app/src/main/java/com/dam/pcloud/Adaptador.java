package com.dam.pcloud;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;

import java.util.ArrayList;

public class Adaptador extends BaseAdapter implements Filterable {
    private ArrayList<ListItem> listItems;
    private ArrayList<ListItem> filteredListItems;
    private Context context;

    public Adaptador(Context context, ArrayList<ListItem> listItems) {
        this.context = context;
        this.listItems = listItems;
    }

    @Override
    public int getCount() {
        if (filteredListItems != null) {
            return filteredListItems.size();
        } else {
            return listItems.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (filteredListItems != null) {
            return filteredListItems.get(position);
        } else {
            return listItems.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // OBTENER EL OBJETO POR CADA ITEM A MOSTRAR
        ListItem item = (ListItem) getItem(position);

        if (filteredListItems != null) {
            item = filteredListItems.get(position);
        } else {
            item = (ListItem) getItem(position);
        }

        // CREAMOS E INICIALIZAMOS LOS ELEMENTOS DEL ITEM DE LA LISTA
        convertView = LayoutInflater.from(context).inflate(R.layout.formato_item, null);
        ImageView imageLeft = (ImageView) convertView.findViewById(R.id.imageLeft);
        TextView textItem = (TextView) convertView.findViewById(R.id.textItem);
        ImageView points = (ImageView) convertView.findViewById(R.id.points);

        // LLENAMOS LOS ELEMENTOS CON LOS VALORES DE CADA ITEM
        imageLeft.setImageResource(item.getImgLeft());
        textItem.setText(item.getTextItem());
        points.setImageResource(item.getImgPoints());

        //Si hacemos click en los 3 puntitos de algún elemento llamamos a showDesplegable
        points.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDesplegable(v, position);
            }
        });

        return convertView;
    }

    //Método para mostrar desplegable al pulsar los puntitos de la pantalla Inicio y seleccionar una opción
    private void showDesplegable(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(context, view);

        // Obtener los elementos del array
        String[] pointsArray = context.getResources().getStringArray(R.array.points);

        // Agregar los elementos al desplegable
        for (int i = 0; i < pointsArray.length; i++) {
            popupMenu.getMenu().add(Menu.NONE, i, i, pointsArray[i]);
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuItemId = item.getItemId();

                // Obtener el elemento seleccionado del array
                String selectedItem = pointsArray[menuItemId];
                // Obtener el objeto ListItem correspondiente a la posición seleccionada
                ListItem selectedListItem = (ListItem) getItem(position);
                // Obtener el nombre del archivo del ListItem seleccionado
                String fileName = selectedListItem.getTextItem();

                // Mostrar el nombre del archivo en el log
                Log.d("Adaptador/Inicio", "Se ha seleccionado la opción '" + selectedItem + "' del archivo: " + fileName + ".");

                // Realizar la acción correspondiente según el elemento seleccionado
                if (selectedItem.equals("Renombrar")) {
                    //metodoOpcion1();
                } else if (selectedItem.equals("Copiar")) {
                    //metodoOpcion2();
                } else if (selectedItem.equals("Eliminar")) {
                    //metodoOpcion3();
                }

                return true;
            }
        });

        popupMenu.show();
    }

    //Método getFilter para poder filtrar las búsquedas en la pantalla Inicio
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                if (constraint == null || constraint.length() == 0) {
                    // Si no se proporciona ningún criterio de búsqueda, mostramos todos los elementos
                    results.count = listItems.size();
                    results.values = listItems;
                } else {
                    // Realizamos el filtrado en base al criterio de búsqueda
                    ArrayList<ListItem> filteredItems = new ArrayList<>();

                    for (ListItem item : listItems) {
                        // Aquí defines el criterio de filtrado según tus necesidades.
                        // Por ejemplo, si buscas por texto, puedes utilizar item.getTextItem().
                        if (item.getTextItem().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            filteredItems.add(item);
                        }
                    }

                    results.count = filteredItems.size();
                    results.values = filteredItems;
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredListItems = (ArrayList<ListItem>) results.values;
                notifyDataSetChanged();
            }
        };
    }

}