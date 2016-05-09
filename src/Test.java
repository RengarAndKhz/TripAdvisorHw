import sun.java2d.pipe.SpanShapeRenderer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Tianyang on 5/9/16.
 */
public class Test {




    public static void main(String[] args) throws FileNotFoundException, IOException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String checkin= "2016-05-08";
        String checkout = "2016-05-10";
        try{
            Date checkinDate = simpleDateFormat.parse(checkin);
            Date checkoutDate = simpleDateFormat.parse(checkout);

            //System.out.println(simpleDateFormat.format(date));
            //date.after()
            //System.out.println(date.after(date));
        }catch (ParseException e){
            e.printStackTrace();
        }
    }
}
