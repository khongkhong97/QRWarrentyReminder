package com.khong.qrwarrentyreminder;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ItemList extends ArrayAdapter<NewItem> {

    private Activity context;
    private List<NewItem> itemList;

    public ItemList(Activity context, List<NewItem> itemList){
        super(context, R.layout.list_layout,itemList);
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.list_layout,null,true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewItemName);

        NewItem item = itemList.get(position);

        String temp = item.getItemDescription();
        textViewName.setText(temp);

        return listViewItem;
    }
}
