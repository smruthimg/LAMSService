package Service;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.sun.org.apache.xpath.internal.SourceTree;
import components.data.*;
import Business.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


/**
 * Created by smrut on 4/18/2018.
 */
public class LAMSService {
    DBSingleton dbSingleton;
    public String  initialize(){
        dbSingleton = DBSingleton.getInstance();
        dbSingleton.db.initialLoad("LAMS");
        return "Database Initialized";
    }

    public String stringtoXML(List<Object> objs){
        //initialize();
        dbSingleton = DBSingleton.getInstance();
        String xml="<AppointmentList></AppointmentList> \n";
        //System.out.println(objs.size());
        try {
            TransformerFactory transformerFactory=TransformerFactory.newInstance();
            Transformer transformer =transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT,"yes");
            transformer.setOutputProperty(OutputKeys.STANDALONE,"yes");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc=dBuilder.parse(new InputSource(new StringReader(xml)));
            //doc.getDocumentElement().normalize();
            //add element

            Element root=doc.getDocumentElement();
            //Element appointmentList=doc.createElement("AppointmentList");
            for (Object obj:objs
                 ) {
                Appointment app=(Appointment)obj;
                Element appointment=doc.createElement("appointment");
                appointment.setAttribute("date",app.getApptdate().toString());
                appointment.setAttribute("time",app.getAppttime().toString());
                appointment.setAttribute("id",app.getId());

                Element patient=doc.createElement("patient");
                patient.setAttribute("id",app.getPatientid().getId());
                Element name=doc.createElement("name");
                Text nameText=doc.createTextNode(app.getPatientid().getName());
                name.appendChild(nameText);
                patient.appendChild(name);

                Element address=doc.createElement("address");
                Text addressText=doc.createTextNode(app.getPatientid().getAddress());
                address.appendChild(addressText);
                patient.appendChild(address);

                Element insurance=doc.createElement("insurance");
                Text insuranceText=doc.createTextNode(""+app.getPatientid().getInsurance());
                insurance.appendChild(insuranceText);
                patient.appendChild(insurance);

                Element dob=doc.createElement("dob");
                Text dobText=doc.createTextNode(app.getPatientid().getDateofbirth().toString());
                dob.appendChild(dobText);
                patient.appendChild(dob);

                appointment.appendChild(patient);

                Element phleb=doc.createElement("phlebotomist");
                phleb.setAttribute("id",app.getPhlebid().getId());

                Element phlebName=doc.createElement("name");
                Text phlebNameText=doc.createTextNode(app.getPhlebid().getName());
                phlebName.appendChild(phlebNameText);
                phleb.appendChild(phlebName);

                appointment.appendChild(phleb);

                Element psc=doc.createElement("psc");
                psc.setAttribute("id",app.getPscid().getId());

                Element pscName=doc.createElement("name");
                Text pscNameText=doc.createTextNode(app.getPscid().getName());
                pscName.appendChild(pscNameText);
                psc.appendChild(pscName);

                appointment.appendChild(psc);

                Element allLabTests=doc.createElement("allLabTests");
                for (AppointmentLabTest aplt:app.getAppointmentLabTestCollection()
                     ) {
                    Element appointmentLabTest=doc.createElement("appointmentLabTest");
                    appointmentLabTest.setAttribute("appointmentId",aplt.getAppointmentLabTestPK().getApptid());
                    appointmentLabTest.setAttribute("dxcode",aplt.getAppointmentLabTestPK().getDxcode());
                    appointmentLabTest.setAttribute("labTestId",aplt.getAppointmentLabTestPK().getLabtestid());
                    allLabTests.appendChild(appointmentLabTest);
                }
                appointment.appendChild(allLabTests);

                //appointmentList.appendChild(appointment);

                root.appendChild(appointment);





                //close appointment




            }

            if(objs.size()==0){
               // System.out.println("size");
                Element error=doc.createElement("error");
                Text errorText=doc.createTextNode("ERROR:Appointment is not available");
                error.appendChild(errorText);
                root.appendChild(error);
            }

            DOMSource source=new DOMSource(doc);
            StringWriter writer=new StringWriter();
            StreamResult res=new StreamResult(writer);
            transformer.transform(source,res);

            System.out.println("\nXML in String format:\n"+writer.toString());
            xml=writer.toString();

        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return xml;
    }


    public String getAllAppointments(){
        String result;
        dbSingleton = DBSingleton.getInstance();
       // dbSingleton.db.initialLoad("LAMS");
        //initialize();
        System.out.println("All appointments");
        List<Object> objs = dbSingleton.db.getData("Appointment", "");


//        for (Object obj : objs){
//            System.out.println(obj);
//            System.out.println("");
//        }
        return stringtoXML(objs);
    }

    public String getAppointment(String Id){

        //initialize();
        dbSingleton = DBSingleton.getInstance();
        //dbSingleton.db.initialLoad("LAMS");
        List<Object> objs = dbSingleton.db.getData("Appointment", "id='"+Id+"'");
//        Patient patient = null;
//        Phlebotomist phleb = null;
//        PSC psc = null;
//        for (Object obj : objs){
//            System.out.println(obj);
//            patient = ((Appointment)obj).getPatientid();
//            phleb = ((Appointment)obj).getPhlebid();
//            psc = ((Appointment)obj).getPscid();
//        }
        return stringtoXML(objs);

    }

    public String addAppointment(String xmlStyle){
        BusinessLayer bl=new BusinessLayer();
        LAMSService lm=new LAMSService();
        dbSingleton = DBSingleton.getInstance();
        //initialize();
        String out="";
        boolean validApp=false;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(new StringReader(xmlStyle)));

            NodeList nList=doc.getElementsByTagName("appointment");
            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode=nList.item(i);
                if(nNode.getNodeType()==Node.ELEMENT_NODE){
                    Element ele=(Element)nNode;
                    String AppId=bl.getLatestAppId();
                    String date=ele.getElementsByTagName("date").item(0).getTextContent();
                    //validate date
                    DateFormat formatter=new SimpleDateFormat("HH:mm");
                    String time=ele.getElementsByTagName("time").item(0).getTextContent();
                    java.sql.Time appttime= new java.sql.Time(formatter.parse(time).getTime());




                    String patId=ele.getElementsByTagName("patientId").item(0).getTextContent();
                    String phyId=ele.getElementsByTagName("physicianId").item(0).getTextContent();
                    String pscId=ele.getElementsByTagName("pscId").item(0).getTextContent();
                    String phlebId=ele.getElementsByTagName("phlebotomistId").item(0).getTextContent();




                    Patient pat=(Patient) bl.getPatient(patId);

                    Physician phy =(Physician)bl.getPhysician(phyId) ;

                    PSC psc=(PSC)bl.getPSC(pscId);
                   // Phlebotomist phleb=()bl.getPhleb(phlebId);

                    Phlebotomist pleb=(Phlebotomist)bl.getPhleb(phlebId);
                    Appointment newApp=null;

                    if( bl.validateDate(date) && bl.validateTime(time) && (pat!=null && bl.isvalidPat(pat.getId())) && (pleb!=null && bl.isvalidPhleb(pleb.getId())) && (psc!=null && bl.isvalidPsc(psc.getId()))
                            && (phy!= null && bl.isvalidPhy(phy.getId()))){
                         newApp = new Appointment(AppId, java.sql.Date.valueOf(date), appttime);
                        newApp.setPatientid(pat);
                        newApp.setPhlebid(pleb);
                        newApp.setPscid(psc);
                        validApp=true;

                    //Appointment newApp = new Appointment(AppId, date, time);
                    List<AppointmentLabTest> tests= new ArrayList<AppointmentLabTest>();
                    NodeList nl=ele.getElementsByTagName("test");
                    for (int j = 0; j < nl.getLength(); j++) {
                        Node nN = nl.item(i);

                        if(nN.getNodeType()==Node.ELEMENT_NODE) {
                            Element element = (Element) nNode;
                            String labtestId = element.getElementsByTagName("test").item(j).getAttributes().item(1).getTextContent();
                           // System.out.println(labtestId +" " +j);
                            String dxcode = element.getElementsByTagName("test").item(j).getAttributes().item(0).getTextContent();
                            //System.out.println(dxcode +" " +j);
                            if(bl.isValidTest(labtestId) && bl.isValidDXCode(dxcode)) {


                                AppointmentLabTest aptest = new AppointmentLabTest(AppId, labtestId, dxcode);
                                aptest.setDiagnosis((Diagnosis) bl.getDiagnosis(dxcode));
                                aptest.setLabTest((LabTest) bl.getLabTest(labtestId));
                                tests.add(aptest);
                                validApp=true;


                            }
                            else{
                                //out=lm.stringtoXML(new ArrayList<Object>());
                                validApp=false;
                                break;

                            }

                        }
                    }
                        newApp.setAppointmentLabTestCollection(tests);
                        DBSingleton dbS = DBSingleton.getInstance();
                       // if(bl.validateDate(date) && bl.validateTime(time)) {//validate
                            if(validApp && newApp.getId()!=null && !bl.isDuplicate(newApp) && bl.validateDuration(newApp) && bl.validPhlepTime(newApp ) ) {
                                boolean good = dbS.db.addData(newApp);
                                //System.out.println(good);
                                out = getAppointment(AppId);
                                validApp=true;
                            }
                            else{
                                out=lm.stringtoXML(new ArrayList<Object>());
                                validApp=false;
                                break;
                                //System.out.println(out);
                            }

                        }
                    else{
                        out=lm.stringtoXML(new ArrayList<Object>());
                        validApp=false;
                        //System.out.println(out);
                    }

                    }



                    //ystem.out.println("pscId: "+ ele.getElementsByTagName("pscId"));

                }

        }
        catch (Exception ex) {
            ex.printStackTrace();

        }
        return validApp?out:"";

    }
}
