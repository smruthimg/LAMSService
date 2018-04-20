package Service;

import Business.BusinessLayer;

/**
 * Created by smrut on 4/18/2018.
 */
public class TestDB {
    DBSingleton dbSingleton;
    public void  initialize(){
        dbSingleton = DBSingleton.getInstance();
        dbSingleton.db.initialLoad("LAMS");
        System.out.println( "Database Initialized");
    }
    public static void main(String[] args) {
        LAMSService lm= new LAMSService();
        TestDB t=new TestDB();
        //t.initialize();

        //DBSingleton dbSingleton;
        BusinessLayer bl=new BusinessLayer();
        //dbSingleton = DBSingleton.getInstance();
        //dbSingleton.db.initialLoad("LAMS");
        //lm.getAllAppointments();
        //lm.getAppointment("791");
        String xml="<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?><appointment>  <date>2018-2-2</date>  <time>14:05</time>  <patientId>220</patientId>  <physicianId>20</physicianId>  <pscId>520</pscId>  <phlebotomistId>110</phlebotomistId>  <labTests>    <test id=\"86900\" dxcode=\"292.9\" />    <test id=\"80200\" dxcode=\"307.3\" />  </labTests></appointment>";
        lm.addAppointment(xml);
        //lm.addAppointment(xml);
        //lm.getAllAppointments();
        //dbSingleton.db.getDBInfo();
    }
}
