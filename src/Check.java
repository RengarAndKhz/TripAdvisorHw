import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Tianyang on 5/9/16.
 */
public class Check {
    /**
     * data structure for hotel and capacity
     */
    private class HotelInfo {
        private String name;
        private int capacity;
    }

    /**
     * data structure for hotel name and the dates was booked
     */
    private class HotelBook {
        private String name;
        private String checkin;
        private String checkout;
    }

    private String capacityFile;
    private String bookingFile;
    private Date checkin;
    private Date checkout;
    public final String dateFormat = "yyyy-MM-dd";

    Check(String capacityFile, String bookingFile, String checkin, String checkout){
        this.capacityFile = capacityFile;
        this.bookingFile = bookingFile;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        try{
            this.checkout = simpleDateFormat.parse(checkout);
        }catch (ParseException e){
            System.out.println("Checkout format is not available");
        }
        try{
            this.checkin = simpleDateFormat.parse(checkin);
        }catch (ParseException e){
            System.out.println("Checkin format is not available");
        }
    }

    /**
     * scan the first csv hotel capacity file
     *
     * @return capacity a HashMap of hotel's name and capacity
     * @throws FileNotFoundException
     */
    public Map<String, Integer> scanCapacityFile() throws IOException {
        String filename = capacityFile;
        Map<String, Integer> capacity = new HashMap<String, Integer>();
        List<HotelInfo> hotelInfos = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        Scanner scanner;
        int index = 0;
        while ((line = reader.readLine()) != null) {
            HotelInfo hotelInfo = new HotelInfo();
            scanner = new Scanner(line);
            scanner.useDelimiter(", ");
            while (scanner.hasNext()) {
                String data = scanner.next();
                if (index == 0) hotelInfo.name = data;
                else hotelInfo.capacity = Integer.parseInt(data);
                index++;

            }
            index = 0;
            hotelInfos.add(hotelInfo);
        }
        for (HotelInfo curr : hotelInfos) {
            capacity.put(curr.name, curr.capacity);
        }


        return capacity;
    }

    /**
     * Scan the second csv file get the dates were booked and hotel name
     * @return bookingInfo, a map Map<HotelName, Map<Bookeddate, times></>></>
     * @throws IOException
     * @throws ParseException
     */
    Map<String, Map<String, Integer>> scanBookings() throws IOException, ParseException {
        String filename = bookingFile;
        Map<String, Map<String, Integer>> bookingInfo = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        Scanner scanner;
        int index = 0;
        List<HotelBook> hotelBooks = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        while ((line = reader.readLine()) != null) {
            HotelBook hotelBook = new HotelBook();
            scanner = new Scanner(line);
            scanner.useDelimiter(", ");
            while (scanner.hasNext()) {
                String data = scanner.next();
                if (index == 0) hotelBook.name = data;
                else if (index == 1) hotelBook.checkin = data;
                else hotelBook.checkout = data;
                index++;
            }
            index = 0;
            hotelBooks.add(hotelBook);
        }
        for (HotelBook curr : hotelBooks){
            String name = curr.name;
            String checkin = curr.checkin;
            String checkout = curr.checkout;
            Date checkinDate = simpleDateFormat.parse(checkin);
            Date checkoutDate = simpleDateFormat.parse(checkout);
            if (!bookingInfo.containsKey(name)){
                bookingInfo.put(name, new HashMap<String, Integer>());
                for (String currDate : getDateList(checkinDate, checkoutDate)){
                    bookingInfo.get(name).put(currDate, 1);
                }

            }
            else{
                for (String currDate : getDateList(checkinDate, checkoutDate)){
                    if (!bookingInfo.get(name).containsKey(currDate)){
                        bookingInfo.get(name).put(currDate, 1);
                    }
                    else{
                        bookingInfo.get(name).put(currDate, bookingInfo.get(name).get(currDate)+1);
                    }
                }
            }


        }

        return bookingInfo;
    }

    /**
     * get the list of intervals between start date and end date
     * @param checkin
     * @param checkout
     * @return dates
     * @throws ParseException
     */
    public final List<String> getDateList(Date checkin, Date checkout) throws ParseException{
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        List<String> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(checkin);
        while (calendar.getTime().before(checkout)){
            dates.add(simpleDateFormat.format(calendar.getTime()));
            calendar.add(Calendar.DATE, 1);

        }
        return dates;
    }

    /**
     * main detector to detect if any hotel are available
     * @param capacity
     * @param bookingInfo
     * @return availableHotels a list of available hotels
     * @throws ParseException
     */
    public List<String> detector(Map<String, Integer> capacity, Map<String, Map<String, Integer>> bookingInfo) throws ParseException{
        Date checkinDate = checkin;
        Date checkoutDate = checkout;
        List<String> availableHotels = new ArrayList<>();
        if (checkoutDate.before(checkinDate)){
            throw new IllegalArgumentException();
        }
        List<String> bookingDates = getDateList(checkinDate, checkoutDate);
        flag : for (String hotelName : capacity.keySet()){
            if (bookingInfo.containsKey(hotelName)){
                Map<String, Integer> booked = bookingInfo.get(hotelName);
                for (String bookingDate : bookingDates){
                    if (booked.containsKey(bookingDate) && capacity.get(hotelName)-booked.get(bookingDate) <= 0){
                        continue flag;
                    }
                }
                availableHotels.add(hotelName);
            }
            else{
                availableHotels.add(hotelName);
            }
        }
        return availableHotels;
    }



    public static void main(String[] args) throws ParseException {

        try {
            Check check = new Check("test.csv", "book.csv", "2015-04-02", "2015--03");
            Map<String, Integer> capacity = check.scanCapacityFile();
            Map<String, Map<String, Integer>> bookingInfo = check.scanBookings();
            List<String> availableHotels = check.detector(capacity, bookingInfo);
            for (String hotelName : availableHotels){
                System.out.println(hotelName);
            }
        } catch (IOException e) {
            System.out.print("not found cvs file, check the address of them");
        }catch (IllegalArgumentException e){
            System.out.println("Check out date should be after check in date");
        }

    }

}
