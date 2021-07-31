package com.example.imhungry3.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.imhungry3.Activity.Recipe.CreateRecipeActivity;
import com.example.imhungry3.Activity.Search.ResultSearchActivity;
import com.example.imhungry3.Adapter.AdapterIngredientsDisplay;
import com.example.imhungry3.Adapter.AdapterSearchIngredientsDisplay;
import com.example.imhungry3.Adapter.AdapterWhoIn;
import com.example.imhungry3.Adapter.AutoCompleteIngredientsAdapter;
import com.example.imhungry3.Class.IngredientsItemClass;
import com.example.imhungry3.Class.MyToast;
import com.example.imhungry3.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FragmentSearchIngredients extends Fragment implements View.OnClickListener {
    private int radioChioce;
    private RecyclerView listIngredietns;
    private AutoCompleteTextView search;
    private int positionTemp;
    private ArrayList<String>  amountList,ingredientsList, imageIngredientsList,idRecipe;
    private AdapterIngredientsDisplay adapterDisplay;
    private Button  okDialog,scaleIgrediantDialogBtn, cancelDialog;
    private EditText amount;
    private List<String> tNameIngredient, tUriIngredient;
    private static List<String> nameIngredient, uriIngredient;
    private FloatingActionButton floatButton;
    private FirebaseDatabase firebaseDatabase;
    private int positionIngredientTemp;
    private DatabaseReference databaseReference;
    private RadioButton contian,onlyContain;
    private RadioGroup radioGroup;
    private Dialog dialog;
    private List<IngredientsItemClass> innList;
    private View child;



    public FragmentSearchIngredients() {
        amountList = new ArrayList<>();
        ingredientsList = new ArrayList<>();
        imageIngredientsList = new ArrayList<>();
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_search_ingridian, container, false);

        search = v.findViewById(R.id.searchIngreidients2);
        radioGroup = v.findViewById(R.id.radioGroup);
        contian = v.findViewById(R.id.radioButton);
        onlyContain = v.findViewById(R.id.radioButton2);
        floatButton = v.findViewById(R.id.floatbtn);
        floatButton.setOnClickListener(this);
        listIngredietns = v.findViewById(R.id.listIngridiants2);
        listIngredietns.setLayoutManager(new LinearLayoutManager(getContext()));
        nameIngredient = new ArrayList<>();
        uriIngredient= new ArrayList<>();

        tNameIngredient = Arrays.asList(getResources().getStringArray(R.array.ingredients));
        tUriIngredient = Arrays.asList(getResources().getStringArray(R.array.uri));
        for (int i = 0; i < tNameIngredient.size()-1; i++) {
            nameIngredient.add(tNameIngredient.get(i));
            uriIngredient.add(tUriIngredient.get(i));
        }

        fillCountryList();
        AutoCompleteIngredientsAdapter adapter = new AutoCompleteIngredientsAdapter(getContext(), innList);
        search.setAdapter(adapter);

        /**
         * dialog - ingrediants scale dialog
         * cancelDialog - button => close the dialog on click
         * okDialog - button => save the inpute details on click
         * scaleIngrediantsDialog - vutton => open scale menu on click
         */
        dialog = new Dialog(getContext());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setContentView(R.layout.dialog_amount_ingredients);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        okDialog = dialog.findViewById(R.id.okDialog);
        cancelDialog = dialog.findViewById(R.id.cancelDialog);
        amount = dialog.findViewById(R.id.etAmount);
        scaleIgrediantDialogBtn = dialog.findViewById(R.id.scaleIngrediantsDialog);
        scaleIgrediantDialogBtn.setOnClickListener(this);
        okDialog.setOnClickListener(this);
        cancelDialog.setOnClickListener(this);


//        listIngredietns.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
//                                           int position, long id) {
//                positionTemp = position;
//                dialog.show();
//
//                return true;
//            }
//        });




        search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                if (view instanceof ViewGroup) {
                    ViewGroup viewGroup = (ViewGroup) view;
                    for(int i = 0; i< viewGroup.getChildCount(); ++i) {
                        child = viewGroup.getChildAt(i);
                    }
                }
                TextView textView = (TextView) child;
                String str = textView.getText().toString();
                positionIngredientTemp = nameIngredient.indexOf(str);
                if(ingredientsList.contains(nameIngredient.get(positionIngredientTemp))== true)
                {
//                    Toast.makeText(getBaseContext(),getResources().getString(R.string.onList),Toast.LENGTH_SHORT).show();
                    search.setText("");
                }else
                {
                    dialog.show();
                    search.setText("");

                }

            }
        });

        ItemTouchHelper.SimpleCallback deleteIngredients = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                switch (direction) {
                    case ItemTouchHelper.LEFT:

                        String tempIngName = ingredientsList.get(position),tempAmount = amountList.get(position),imageIngTemp = imageIngredientsList.get(position);
                        Snackbar.make(listIngredietns, tempIngName, Snackbar.LENGTH_LONG).setText(tempIngName)
                                .setAction("Undo", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        amountList.add(position, tempAmount);
                                        imageIngredientsList.add(position, imageIngTemp);
                                        ingredientsList.add(position, tempIngName);
                                        adapterDisplay = new AdapterIngredientsDisplay(getContext(), ingredientsList, imageIngredientsList, amountList);
                                        listIngredietns.setAdapter(adapterDisplay);
                                    }
                                }).show();
                        amountList.remove(position);
                        imageIngredientsList.remove(position);
                        ingredientsList.remove(position);
                        adapterDisplay = new AdapterIngredientsDisplay(getContext(), ingredientsList, imageIngredientsList, amountList);
                        listIngredietns.setAdapter(adapterDisplay);
                        break;

                }
            }

        };
        ItemTouchHelper ingredientsTuchHelper = new ItemTouchHelper(deleteIngredients);
        ingredientsTuchHelper.attachToRecyclerView(listIngredietns);

        return v;
    }


    @Override
    public void onClick(View v) {



        if (v == scaleIgrediantDialogBtn){
            //Creating the instance of PopupMenu
            PopupMenu popup = new PopupMenu(getContext(), scaleIgrediantDialogBtn);
            //Inflating the Popup using xml file
            popup.getMenuInflater().inflate(R.menu.dialog_scale_ingrediants, popup.getMenu());
            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    scaleIgrediantDialogBtn.setText(item.getTitle());
                    return true;
                }
            });
            popup.show();//showing popup menu
        }


        if (v == okDialog) {
            if (scaleIgrediantDialogBtn.getText().toString().equals("Units"))
            {
                MyToast.showShort(getContext(),"Select a unit of measure");
                return;
            }else
            {
                if(!amount.getText().toString().equals(""))
                {
                    ingredientsList.add(nameIngredient.get(positionIngredientTemp));
                    imageIngredientsList.add(uriIngredient.get(positionIngredientTemp));
                    amountList.add(amount.getText().toString()+" "+scaleIgrediantDialogBtn.getText().toString());
                    adapterDisplay = new AdapterIngredientsDisplay(getContext(), ingredientsList, imageIngredientsList, amountList);
//            adapterDisplay.notifyDataSetChanged();
                    listIngredietns.setAdapter(adapterDisplay);
                    dialog.dismiss();
                }
                else
                {
                    MyToast.showShort(getContext(),"Please enter amount");
                }

            }

        }

        if (v == cancelDialog) {
            dialog.dismiss();
        }

//        if (v == cancelDialog) {
//            dialog.dismiss();
//
//        }
//        if (v == okDialog) {
////            uriIngredient.add(imageIngredientsList.get(positionTemp));
////            nameIngredient.add(ingredientsList.get(positionTemp));
//            imageIngredientsList.remove(positionTemp);
//            ingredientsList.remove(positionTemp);
//            adapterDisplay = new AdapterIngredientsDisplay(getContext(), ingredientsList, imageIngredientsList, amountList);
//            listIngredietns.setAdapter(adapterDisplay);
//            dialog.dismiss();
//        }
        if (v == floatButton) {
            if(ingredientsList.size() != 0)
            {

                resultRecipe(new FirebaseCallback() {
                    public void onCallback(ArrayList<String> idRecipe) {

                        Bundle bundle = new Bundle();
                        ConstraintLayout temp = v.findViewById(R.id.layout2) ;
//                        int size = idRecipe.size()-1;
//
//                        idRecipe.remove(size);
                        Intent intent = new Intent(getContext(), ResultSearchActivity.class);
                        intent.putExtra("list",idRecipe);
                        ingredientsList.clear();
                        imageIngredientsList.clear();
                        listIngredietns.setAdapter(null);
                        startActivity(intent);
                    }
                });



            }
        }
    }


    public void resultRecipe(FirebaseCallback firebaseCallback)
    {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("recipe");
//        send request to get the data
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                get Details
                DataSnapshot numRecipe = snapshot;
                Iterable<DataSnapshot> numberCildren = numRecipe.getChildren();
                ArrayList<String> idRecipe = new ArrayList<>();
                radioChioce = radioGroup.getCheckedRadioButtonId();
                contian = getView().findViewById(radioChioce);


                for (DataSnapshot data : numberCildren)
                {
                    boolean flag = true;
                    Iterable<DataSnapshot> ingridentsCildren = data.child("ingredients").getChildren();
                    long count =data.child("ingredients").getChildrenCount();
                    if( contian.getText().toString().equals("contain only the ingredients"))
                    {
                        if (count == ingredientsList.size())
                        {

                            for (DataSnapshot data2 : ingridentsCildren) {
                                if (ingredientsList.contains(data2.child("ingredientName").getValue().toString()) == true ) {
                                    int tmpIndex = ingredientsList.indexOf(data2.child("ingredientName").getValue().toString());

                                    if (Integer.parseInt(amountList.get(tmpIndex).split(" ")[0]) >= Integer.parseInt( data2.child("ingredientAmount").getValue().toString().split(" ")[0]) &&
                                            amountList.get(tmpIndex).split(" ")[1].equals( data2.child("ingredientAmount").getValue().toString().split(" ")[1])
                                    ){
                                        continue;
                                    }

                                }else{
                                    flag = false;
                                    break;
                                }

                            }
                            if (flag == true)
                            {
                                idRecipe.add(data.getKey());
                            }

                        }
                    }
                    else
                    {

                        for (int i = 0; i <ingredientsList.size() ; i++) {
                            if(flag == true){

                                for (DataSnapshot data2 : ingridentsCildren) {
                                    int tmpIndex = ingredientsList.indexOf(data2.child("ingredientName").getValue().toString());
                                    if (ingredientsList.get(i).equals(data2.child("ingredientName").getValue().toString()) && (Integer.parseInt(amountList.get(tmpIndex).split(" ")[0]) >= Integer.parseInt( data2.child("ingredientAmount").getValue().toString().split(" ")[0]) &&
                                            amountList.get(tmpIndex).split(" ")[1].equals( data2.child("ingredientAmount").getValue().toString().split(" ")[1])
                                    ))
                                    {
                                        flag = true;
                                        break;
                                    }
                                    else
                                    {
                                        flag=false;
                                    }

                                }
                            }
                            else
                            {
                                flag = false;
                                break;
                            }

                        }

                        if (flag == true)
                        {
                            idRecipe.add(data.getKey());
                        }

                    }
                }
                if (idRecipe.size()==0)
                {
                    MyToast.showShort(getContext(),"not found any recipe");
                    return;
                }

                firebaseCallback.onCallback(idRecipe);
            }


            @Override
            public void onCancelled (@NonNull DatabaseError error){

            }
        });

    }

    public interface FirebaseCallback {
        void onCallback(ArrayList<String> idRecipe);
    }

    private void fillCountryList() {
        innList = new ArrayList<>();
        nameIngredient = Arrays.asList(getResources().getStringArray(R.array.ingredients));
        uriIngredient = Arrays.asList(getResources().getStringArray(R.array.uri));
        for (int i = 0; i <nameIngredient.size() ; i++) {
            innList.add(new IngredientsItemClass(nameIngredient.get(i),uriIngredient.get(i)));
        }

    }
}