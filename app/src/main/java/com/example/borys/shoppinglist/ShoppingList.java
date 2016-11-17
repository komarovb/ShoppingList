package com.example.borys.shoppinglist;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.example.borys.shoppinglist.adapters.ItemAdapter;
import com.example.borys.shoppinglist.data.ShoppingListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShoppingList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ShoppingList extends Fragment implements ItemAdapter.UsefulThing {

    private ListView list;

    private OnFragmentInteractionListener mListener;

    ArrayList<ShoppingListItem> items;
    ItemAdapter adapter;

    private final String fileName = "file_to_write_items.json";

    public ShoppingList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            loadData();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadData() throws FileNotFoundException, JSONException {
        items = new ArrayList<>();
        File file = new File(getContext().getFilesDir(),fileName);
        if(file.exists()) {
            JSONObject jsonObj = readJSONFile(file);
            JSONArray jsonArr = (JSONArray) jsonObj.get("items");
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject item = jsonArr.getJSONObject(i);
                items.add(ShoppingListItem.createNewInstance(item.getString("title"),"",item.getString("image"),item.getBoolean("checked")));
            }
        }
        else{
            System.out.println("Item file is empty");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_shopping_list, container, false);
        list = (ListView) v.findViewById(R.id.list);
        setList();
//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                builder.setTitle("Delete Item");
//                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        deleteItem(position);
//                    }
//                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//                builder.show();
//            }
//        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public void setList(){
        adapter = new ItemAdapter(getActivity().getApplicationContext(),items, ShoppingList.this);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ShoppingListItem item = (ShoppingListItem) adapterView.getItemAtPosition(i);
//                mListener.displayItem(item);
                showDeleteDialog(i,"Check/uncheck item?",2);
            }
        });
    }

    public void addItem(String s) throws IOException, JSONException {
        ShoppingListItem item = ShoppingListItem.createNewInstance(s,"");
        items.add(item);
        adapter.notifyDataSetChanged();

        saveItemToFile(item);
    }

    private void saveItemToFile(ShoppingListItem item) throws JSONException, IOException {
        JSONObject jsonItem = new JSONObject();
        jsonItem.put("title", item.title);
        jsonItem.put("image",item.imageName);
        jsonItem.put("checked", false);

        File file = new File(getContext().getFilesDir(),fileName);
        Scanner sc;
        if(file.exists()) {
            JSONObject jsonObj = readJSONFile(file);
            JSONArray jsonArr = (JSONArray) jsonObj.get("items");
            jsonArr.put(jsonItem);

            writeToFile(jsonObj);

        }
        else{
            System.out.println("UNABLE TO OPEN FILE TO WRITE - CREATING NEW FILE");
            JSONObject jsonObj = new JSONObject();
            JSONArray jsonArr = new JSONArray();
            jsonArr.put(jsonItem);
            jsonObj.put("items",jsonArr);

            writeToFile(jsonObj);
        }

    }

    private void writeToFile(JSONObject jsonObj) throws IOException {
        OutputStream outputStream;
        outputStream = getContext().openFileOutput(fileName, Context.MODE_PRIVATE);
        outputStream.write(jsonObj.toString().getBytes());
        outputStream.close();
    }
    private JSONObject readJSONFile(File file) throws JSONException, FileNotFoundException {
        Scanner sc = new Scanner(file);
        String text = "";
        while(sc.hasNextLine()){
            text += sc.nextLine();
        }
        sc.close();
        JSONObject jsonObj = new JSONObject(text);
        return jsonObj;
    }

    public void updateItem(String newTitle, int position, int operation, String imagePath) throws IOException, JSONException {

        File file = new File(getContext().getFilesDir(),fileName);
        JSONObject jsonObj = readJSONFile(file);
        JSONArray jsonArr = (JSONArray) jsonObj.get("items");
        if(operation==1){
            jsonArr.getJSONObject(position).put("title",newTitle);
            items.get(position).title=newTitle;
        }
        else if(operation==2){
            Boolean val = !jsonArr.getJSONObject(position).getBoolean("checked");
            items.get(position).checked=val;
            jsonArr.getJSONObject(position).put("checked",val);
        }
        else if(operation==3){
            jsonArr.getJSONObject(position).put("image", imagePath);
            items.get(position).imageName = imagePath;
        }

        try {
            writeToFile(jsonObj);
        } catch (IOException e) {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();
    }
    public void deleteItem(int position){
        items.remove(position);
        File file = new File(getContext().getFilesDir(),fileName);
        if(file.exists()) {
            JSONObject jsonObj = null;
            try {
                jsonObj = readJSONFile(file);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            JSONArray jsonArr = null;
            try {
                jsonArr = (JSONArray) jsonObj.get("items");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    jsonArr.remove(position);
                }
                else System.out.println("UPDATE your Android version to remove items");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            adapter.notifyDataSetChanged();
            try {
                writeToFile(jsonObj);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showDeleteDialog(final int id, String text, final int operation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(text);
        builder.setPositiveButton("Sure", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(operation==1)
                    deleteItem(id);
                else if(operation==2) {
                    try {
                        updateItem("", id, 2, "");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
    private void showEditDialog(final int id) {
        ShoppingListItem item = items.get(id);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Update Item");
        final EditText input = new EditText(getContext());
        input.setText(item.title);
        builder.setView(input);
        builder.setPositiveButton("Update title", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    updateItem(input.getText().toString(),id, 1, "");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    public void delete(int id) {
        showDeleteDialog(id, "Delete Item", 1);
    }

    @Override
    public void edit(int id) {
        showEditDialog(id);
    }

    @Override
    public void changePic(int id) {
        String path = mListener.makePicAndSave(id);
//        if(!path.equals("")){
//            try {
//                updateItem("", id, 3, path);
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
    }

    public void sayThatImageWasSaved(String f, int id) throws IOException, JSONException {
        updateItem("", id, 3, f);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void displayItem(ShoppingListItem item);
        String makePicAndSave(int id);
    }
}
