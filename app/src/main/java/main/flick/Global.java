package main.flick;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Noam on 01/10/2015.
 */
public class Global extends Application{



    private List imageList = new ArrayList<>();

    private String category = null;

    public void addItem(Object obj)
    {
        imageList.add(obj);
    }

    public int getSize(){
        return imageList.size();
    }

    public void setCategory(String category){this.category=category;}

    public String getCategory(){return category;}

    public List getImageList(){return imageList;}
}
