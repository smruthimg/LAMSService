package Business;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
//import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import Service.*;
import com.sun.org.apache.xpath.internal.SourceTree;
import components.data.Appointment;
import components.data.AppointmentLabTest;
import components.data.LabTest;

public class BusinessLayer{
    DBSingleton dbSingleton;
    public void  initialize(){
        dbSingleton = DBSingleton.getInstance();
        dbSingleton.db.initialLoad("LAMS");
        System.out.println( "Database Initialized");
    }

    public Object getPatient(String id){
     //   initialize();
        List<Object> obj = dbSingleton.db.getData("Patient", "Id='" +id+"'");

        return obj.isEmpty()?null:obj.get(0);
    }

    public Object getPhleb(String id){
       // initialize();
        List<Object> obj = dbSingleton.db.getData("Phlebotomist", "Id='" +id+"'");
        return obj.isEmpty()?null:obj.get(0);
    }

    public Object getPSC(String id){
        //initialize();
        List<Object> obj = dbSingleton.db.getData("PSC","Id='" +id+"'");
        return obj.isEmpty()?null:obj.get(0);
    }

    public Object getDiagnosis(String id){
      //  initialize();
        //System.out.println(id);
        Object obj = dbSingleton.db.getData("Diagnosis", "code='"+id +"'").get(0);
        return obj;
    }

    public Object getPhysician(String id){
     //   initialize();
        List<Object> obj = dbSingleton.db.getData("Physician", "Id='" +id+"'");
        return obj.isEmpty()? null :obj.get(0);
    }

    public Object getLabTest(String id){
        //initialize();
        //System.out.println(id);
        if(!id.isEmpty()) {
            Object obj = dbSingleton.db.getData("LabTest", "Id='" + id + "'").get(0);
            return obj;
        }
        return new Object();
    }
    public boolean validateDate(String date){
        if(date.isEmpty()) return false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        return sdf.parse(date, new ParsePosition(0)) != null;
    }

    public boolean validateTime(String time){
        if(time.isEmpty()) return false;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.setLenient(false);
        return (sdf.parse(time, new ParsePosition(0)) != null );

    }
// redundant
    public boolean isvalidDuration(String time){

        List<Object> oldApp= dbSingleton.db.getData("Appointment","patientid='220'");
        DateFormat formatter=new SimpleDateFormat("HH:mm");
        Time appttime=null;
        Long fifteen= Long.valueOf(15*60*1000);
        long newTime=0;
        //Date newTime=new Date(appttime.getTime());
        //new Date(System.currentTimeMillis()+(15*60*1000));

        try {
            appttime= new Time(formatter.parse(time).getTime());
            newTime=appttime.getTime()+ TimeUnit.MINUTES.toMillis(15);
            System.out.println(new Time(formatter.parse("8:00:00").getTime()).getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(oldApp.size()>0) {
            for (Object o : oldApp
                    ) {

                Appointment a = (Appointment) o;
                System.out.println(new Time(newTime));
                //System.out.println(appttime.getTime()+fifteen);

                //System.out.println(app.getPatientid().getId()+""+a.getAppttime() + " " + a.getApptdate());
//                if ( ) {
//                    System.out.println("duplicate");
//                    return true;
//                }

            }
        }
        return false;
    }

    public boolean validPhlepTime(Appointment app) {
        List<Object> oldApp= dbSingleton.db.getData("Appointment","phlebid='"+app.getPhlebid().getId()+"'");
        DateFormat formatter=new SimpleDateFormat("HH:mm");
        Time appttime=null;
        long newTimeadd=0;
        long newTimesub=0;
        if(oldApp.size()>0) {
            for (Object o : oldApp
                    ) {

                Appointment a = (Appointment) o;
                //appttime= new Time(formatter.parse(app.getAppttime().toString()).getTime());
                newTimeadd=app.getAppttime().getTime()+ TimeUnit.MINUTES.toMillis(45);
                newTimesub=app.getAppttime().getTime()- TimeUnit.MINUTES.toMillis(45);
                //System.out.println(newTimeadd);
                //System.out.println(newTimesub);
                //System.out.println(app.getPatientid().getId()+""+a.getAppttime() + " " + a.getApptdate());
                try {
                    //System.out.println(new Time(formatter.parse("8:00:00").getTime()).getTime());
                    //System.out.println((app.getAppttime().getTime()<=(new Time(formatter.parse("8:00:00").getTime()).getTime()) || app.getAppttime().getTime()>=(new Time(formatter.parse("17:00:00").getTime()).getTime()))
                    //);
//                    if(app.getAppttime().getTime()<=(new Time(formatter.parse("8:00:00").getTime()).getTime()) && app.getAppttime().getTime()>=(new Time(formatter.parse("17:00:00").getTime()).getTime()))
//                    {
                    if ((a.getApptdate().equals(app.getApptdate()) && a.getAppttime().getTime() >= newTimesub && a.getAppttime().getTime() <= newTimeadd)) {
                        //
                        //System.out.println(a.getAppttime().getTime()>=newTimesub && a.getAppttime().getTime()<=newTimeadd);

                        return false;
                    }

                    //}
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

        return true;
    }
    public boolean validateDuration(Appointment app){


        List<Object> oldApp= dbSingleton.db.getData("Appointment","patientid='"+app.getPatientid().getId()+"'");
        DateFormat formatter=new SimpleDateFormat("HH:mm");
        Time appttime=null;
        long newTimeadd=0;
        long newTimesub=0;
        if(oldApp.size()>0) {
            for (Object o : oldApp
                    ) {

                Appointment a = (Appointment) o;
                //appttime= new Time(formatter.parse(app.getAppttime().toString()).getTime());
                newTimeadd=app.getAppttime().getTime()+ TimeUnit.MINUTES.toMillis(15);
                newTimesub=app.getAppttime().getTime()- TimeUnit.MINUTES.toMillis(15);

                try {

                        if ((app.getAppttime().getTime()<=(new Time(formatter.parse("8:00:00").getTime()).getTime()) || app.getAppttime().getTime()>=(new Time(formatter.parse("17:00:00").getTime()).getTime()))
                        || (a.getApptdate().equals(app.getApptdate()) && a.getAppttime().getTime() >= newTimesub && a.getAppttime().getTime() <= newTimeadd)) {


                            return false;
                        }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }


        return true;

    }

    public boolean isDuplicate(Appointment app) {
      //  initialize();
        String ti= new SimpleDateFormat("HH:mm:ss").format(app.getAppttime());
        //System.out.println(new SimpleDateFormat("HH:mm").format(app.getAppttime()));
        String da=new SimpleDateFormat("MM/dd/yyyy").format(app.getApptdate());
        List<Object> oldApp= dbSingleton.db.getData("Appointment","patientid='"+app.getPatientid().getId()+"'");
       if(oldApp.size()>0) {
           for (Object o : oldApp
                   ) {

               Appointment a = (Appointment) o;
               //System.out.println(app.getPatientid().getId()+""+a.getAppttime() + " " + a.getApptdate());
               if (a.getAppttime().equals(app.getAppttime()) && a.getApptdate().equals(app.getApptdate()) && a.getPhlebid().getId().equals(app.getPhlebid().getId())
                       && a.getPscid().getId().equals(app.getPscid().getId()) ) {
                   //System.out.println("duplicate");
                   return true;
               }

           }
       }

        //System.out.println(oldApp.size());
        return false;
    }

    public boolean isNumeric(String id){
        return false;
    }

    public boolean isvalidPhleb(String id){
      //  initialize();
        if(!id.isEmpty()) {
            List<Object> objs = dbSingleton.db.getData("Phlebotomist", "id='" + id + "'");
            return objs.size() > 0;
        }
        return false;


    }

    public boolean isvalidPat(String id){
         // initialize();
        if(!id.isEmpty()) {
        List<Object> objs = dbSingleton.db.getData("Patient", "id='"+id+"'");
        return objs.size()>0;
        }
        return false;


    }
    public boolean isvalidPhy(String id){
       //   initialize();
        if(!id.isEmpty()) {
            List<Object> objs = dbSingleton.db.getData("Physician", "id='"+id+"'");
            return objs.size()>0;
        }

        return false;

    }
    public boolean isvalidPsc(String id){
         // initialize();
        if(!id.isEmpty()) {
        List<Object> objs = dbSingleton.db.getData("PSC", "id='"+id+"'");
        return objs.size()>0;
        }

        return false;


    }
    public boolean isValidTest(String id){
          //initialize();
        if(id.isEmpty() || id.matches(".*[a-zA-Z]+.*") || id.length()>5){
            return false;
        }
        return true;


    }
    public boolean isValidDXCode(String id){
        //  initialize();
        //System.out.println(id);
        if(id.isEmpty() || id.matches(".*[a-zA-Z]+.*") || id.length()>10|| Double.parseDouble(id)==0){
            return false;
        }
        return true;


    }


    public String getLatestAppId() {
    LAMSService lm=new LAMSService();
//    initialize();
        dbSingleton = DBSingleton.getInstance();
    List<Object> objs=dbSingleton.db.getData("Appointment","");
    int count=objs.size();
      Appointment a=(Appointment)objs.get(count-1);

    return ""+(Integer.parseInt(a.getId())+1);
    }


    public boolean validateApp(Appointment app){
        return false;

    }

    public static void main(String[] args) {
        BusinessLayer bl=new BusinessLayer();
        LAMSService lm=new LAMSService();
        bl.initialize();
        System.out.println(bl.isvalidDuration("13:00:00"));
        //String id=bl.getLatestAppId();
        //System.out.println(bl.isValidTest("ABC"));

    }
}