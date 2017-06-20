package mocosose17.wgapp;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

public class Item {
    private long id;
    private String name;
    private boolean bought;
    private String unitkind;
    private String amount;
    private boolean newlyCreated=false;

    public EditText etName;
    public EditText etAmount;
    public Spinner spUnitKind;
    public CheckBox cbBought;
    public LinearLayout layout;

    public Item(){}
    public Item(JSONObject itemObject){
        try {
            setUnitkind(itemObject.getString("type"));
            setName(itemObject.getString("articleName"));
            setAmount(Integer.toString(itemObject.getInt("quantity")));
            newlyCreated = false;
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getBought() {
        return bought;
    }

    public void setBought(boolean value) {
        this.bought = value;
    }

    public boolean getNewlyCreated() {
        return newlyCreated;
    }

    public void setNewlyCreated(boolean newlyCreated) {
        this.newlyCreated = newlyCreated;
    }

    public String getUnitkind() {
        return unitkind;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
    public void setUnitkind(String unitkind) {
        this.unitkind = unitkind;
    }


    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return name;
    }
}

