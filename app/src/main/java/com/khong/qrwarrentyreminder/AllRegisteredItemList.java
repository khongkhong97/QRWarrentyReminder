package com.khong.qrwarrentyreminder;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class AllRegisteredItemList extends ArrayAdapter<RegisterItem> {

    private Activity context;
    private List<RegisterItem> registeredItemList;

    public AllRegisteredItemList(Activity context, List<RegisterItem> registeredItemList){
        super(context,R.layout.all_registered_item_list_layout,registeredItemList);
        this.context = context;
        this.registeredItemList = registeredItemList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewRegisteredItem = inflater.inflate(R.layout.all_registered_item_list_layout, null, true);

        TextView textViewRegisteredName = (TextView) listViewRegisteredItem.findViewById(R.id.textViewRegisteredName);
        TextView textViewRegisteredUid = (TextView) listViewRegisteredItem.findViewById(R.id.textViewRegisteredUID);
        TextView textViewRegisteredSerialNo = (TextView) listViewRegisteredItem.findViewById(R.id.textViewREgisteredSerialNumber);
        TextView textViewRegisteredItemColour = (TextView) listViewRegisteredItem.findViewById(R.id.textViewRegisteredItemColour);
        TextView textViewRegisteredItemWarrentyPeriod = (TextView) listViewRegisteredItem.findViewById(R.id.textViewRegisteredItemWarrentyPeriod);
        TextView textViewRegisteredItemPurchaseDate = (TextView) listViewRegisteredItem.findViewById(R.id.textViewRegisteredItemPurchaseDate);
        TextView textViewRegisteredWarrentyExpired = (TextView) listViewRegisteredItem.findViewById(R.id.textViewRegisteredWarrentyExpired);


        RegisterItem registerItem = registeredItemList.get(position);

        String date = registerItem.getItemPurchaseDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (registerItem.getItemWarrentyPeriod().equals("1 Week")) {
            c.add(Calendar.DATE, 7);
        } else if (registerItem.getItemWarrentyPeriod().equals("1 Month")){
            c.add(Calendar.MONTH, 1);
        }
        else if (registerItem.getItemWarrentyPeriod().equals("3 Month")){
            c.add(Calendar.MONTH, 3);
        }
        else if (registerItem.getItemWarrentyPeriod().equals("6 Month")){
            c.add(Calendar.MONTH, 6);
        }
        else if (registerItem.getItemWarrentyPeriod().equals("1 Year")) {
            c.add(Calendar.YEAR, 1);
        }
        else if (registerItem.getItemWarrentyPeriod().equals("2 Year")){
            c.add(Calendar.YEAR, 2);
        }
        else if (registerItem.getItemWarrentyPeriod().equals("5 Year")){
            c.add(Calendar.YEAR, 5);
        }
        SimpleDateFormat sf1 = new SimpleDateFormat("dd/MM/yyyy");
        String output = sf1.format(c.getTime());


        textViewRegisteredName.setText("Item Name:" + registerItem.getItemName());
        textViewRegisteredUid.setText("Registered by:"+ registerItem.getUid());
        textViewRegisteredSerialNo.setText("Item Serial No"+registerItem.getSerialNumber());
        textViewRegisteredItemColour.setText("Item Colour  :"+ registerItem.getItemColour());
        textViewRegisteredItemWarrentyPeriod.setText("Item Warrenty Period :"+registerItem.getItemWarrentyPeriod());
        textViewRegisteredItemPurchaseDate.setText("Item Puchase Date :" +registerItem.getItemPurchaseDate());
        textViewRegisteredWarrentyExpired.setText("Item Warrenty Expired :"+output);

        return listViewRegisteredItem;
    }
}
