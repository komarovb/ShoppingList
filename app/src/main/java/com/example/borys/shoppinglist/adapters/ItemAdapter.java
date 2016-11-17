package com.example.borys.shoppinglist.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.borys.shoppinglist.R;
import com.example.borys.shoppinglist.ShoppingList;
import com.example.borys.shoppinglist.data.ShoppingListItem;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Borys on 11/6/16.
 */

public class ItemAdapter extends ArrayAdapter<ShoppingListItem> {
    // View lookup cache
    private static class ViewHolder {
        TextView title;
        ImageView image;
        ImageView edit;
        ImageView delete;
    }

    UsefulThing list;

    public ItemAdapter(Context context, ArrayList<ShoppingListItem> items, ShoppingList l) {
        super(context, R.layout.list_item, items);
        this.list = l;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ShoppingListItem item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.item_title);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.image_place);
            viewHolder.edit = (ImageView) convertView.findViewById(R.id.edit_icon);
            viewHolder.delete = (ImageView) convertView.findViewById(R.id.delete_icon);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        if(!item.imageName.equals("")){
            File imgFile = new  File(getContext().getFilesDir(),item.imageName);
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                viewHolder.image.setImageBitmap(myBitmap);
            }
//            else{
//                imgFile.delete();
//                getItem(position).imageName="";
//            }
        }
        else{
            viewHolder.image.setImageDrawable(ResourcesCompat.getDrawable(getContext().getResources(),R.drawable.camera,null));
        }
        viewHolder.title.setText(item.title);
        if(item.checked)
            viewHolder.title.setTextColor(ResourcesCompat.getColor(getContext().getResources(), R.color.colorChecked, null));
        else viewHolder.title.setTextColor(ResourcesCompat.getColor(getContext().getResources(), R.color.colorBlack, null));
        viewHolder.delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                list.delete(position);
            }
        });
        viewHolder.edit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                list.edit(position);
            }
        });
        viewHolder.image.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                list.changePic(position);
            }
        });
//        viewHolder.title.setText(item.title);
        // Return the completed view to render on screen
        return convertView;
    }

    public interface UsefulThing{
        void delete(int id);
        void edit(int id);
        void changePic(int id);
    }
}