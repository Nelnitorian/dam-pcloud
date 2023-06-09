package com.dam.pcloud;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;

import com.dam.pcloud.rest.PCloudItem;

import java.util.ArrayList;

public class Adaptador extends BaseAdapter implements Filterable {
    private ArrayList<ListItem> listItems;
    private ArrayList<ListItem> filteredListItems;
    private FolderContents context;
    private ImageView imageLeft;
    private TextView textItem;
    private ImageView points;
    private static final String LOG_TAG = "Adaptador/Inicio";

    public Adaptador(Context context, ArrayList<ListItem> listItems) {
        this.context = (FolderContents)context;
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
        imageLeft = (ImageView) convertView.findViewById(R.id.imageLeft);
        textItem = (TextView) convertView.findViewById(R.id.textItem);
        points = (ImageView) convertView.findViewById(R.id.points);

        // LLENAMOS LOS ELEMENTOS CON LOS VALORES DE CADA ITEM
        imageLeft.setImageResource(item.getImgLeft());
        textItem.setText(item.getTextItem());
        points.setImageResource(item.getImgPoints());

        ListItem finalItem = item;
        //Si hacemos click en los 3 puntitos de algún elemento llamamos a showDesplegable
        points.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDesplegable(v, position, finalItem);
            }
        });

        if (item.getPcloudItem().getType() == PCloudItem.ItemType.FOLDER) {
            textItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, FolderContents.class);
                    intent.putExtra("folder_id", finalItem.getPcloudItem().getId());

                    context.startActivity(intent);
                }
            });
        }

        return convertView;
    }

    //Método para mostrar desplegable al pulsar los puntitos de la pantalla Inicio y seleccionar una opción
    private void showDesplegable(View view, int position, ListItem item) {
        PopupMenu popupMenu = new PopupMenu(context, view);

        // Obtener los elementos del array
        String[] pointsArray = context.getResources().getStringArray(R.array.points);

        // Agregar los elementos al desplegable
        for (int i = 0; i < pointsArray.length; i++) {
            popupMenu.getMenu().add(Menu.NONE, i, i, pointsArray[i]);
        }

        if(item.getPcloudItem().getType() != PCloudItem.ItemType.FOLDER)
            popupMenu.getMenu().add(Menu.NONE, pointsArray.length, pointsArray.length, "Copiar");

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String selectedItem = (String)item.getTitle();

                // Obtener el objeto ListItem correspondiente a la posición seleccionada
                ListItem selectedListItem = (ListItem) getItem(position);
                // Obtener el nombre del archivo del ListItem seleccionado
                String fileName = selectedListItem.getTextItem();

                // Mostrar el nombre del archivo en el log
                Log.d(LOG_TAG, "Se ha seleccionado la opción '" + selectedItem + "' del archivo: " + fileName + ".");

                // Realizar la acción correspondiente según el elemento seleccionado
                if (selectedItem.equals("Renombrar")) {
                    mostrarDialogoRenombrar(selectedListItem);
                } else if (selectedItem.equals("Copiar")) {
                    copyItem(selectedListItem);
                } else if (selectedItem.equals("Eliminar")) {
                    mostrarDialogoEliminar(selectedListItem);
                }

                return true;
            }
        });

        popupMenu.show();
    }
    //Método para mostrar un cuadro de diálogo
    private void mostrarDialogoRenombrar(ListItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Renombrar");

                // Inflar el diseño personalizado para el contenido del diálogo
                View dialogView = LayoutInflater.from(context).inflate(R.layout.alert_dialog, null);
                builder.setView(dialogView);

                // Obtener la referencia a la caja de texto
                EditText editText = dialogView.findViewById(R.id.editText);
                builder.setPositiveButton("Renombrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nuevoNombre = editText.getText().toString();
                        Log.d(LOG_TAG, "Nuevo nombre: "+nuevoNombre);
                        context.renameEntryPoint(item, nuevoNombre);
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //nada
                    }
                });
                builder.show();
    }

    private void mostrarDialogoEliminar(ListItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("¿Está seguro de que desea Eliminar?");

        builder.setMessage("Esta acción es irreversible.");
        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(LOG_TAG, "Borrado: "+item.getPcloudItem().getName());
                context.deleteEntryPoint(item);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //nada
            }
        });
        builder.show();
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

    private void copyItem(ListItem listItem){
        context.copyItemEntryPoint(listItem);
    }
}
