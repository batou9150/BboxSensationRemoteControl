package fr.batoucada.bboxsensationremotecontrol;


import android.os.AsyncTask;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SNMPSet extends AsyncTask<String, Void, Void> {

    private String ipAddress;

    public SNMPSet(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    protected Void doInBackground(String... sysContactValues) {
        if(sysContactValues.length < 1) return null;
        String sysContactValue = sysContactValues[0];
        OID oid = new OID("1.3.6.1.4.1.8711.101.13.1.3.28.0");

        try {
            // Create TransportMapping and Listen
            TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
            Snmp snmp = new Snmp(transport);
            transport.listen();

            // Create Target Address object
            CommunityTarget target = new CommunityTarget();
            target.setCommunity(new OctetString("public"));
            target.setAddress(GenericAddress.parse("udp:" + ipAddress + "/161"));
            target.setRetries(2);
            target.setTimeout(1000);
            target.setVersion(SnmpConstants.version1);

            // Create the PDU object
            PDU pdu = new PDU();
            pdu.add(new VariableBinding(oid, new OctetString(sysContactValue)));

            System.out.println("Request : " + pdu.toString());
            System.out.println("Target : " + target.toString());

            ResponseEvent response = snmp.set(pdu, target);

            // Process Agent Response
            if (response != null) {
                System.out.println("\nResponse:\nGot Snmp Set Response from Agent");
                PDU responsePDU = response.getResponse();

                if (responsePDU != null) {
                    int errorStatus = responsePDU.getErrorStatus();
                    int errorIndex = responsePDU.getErrorIndex();
                    String errorStatusText = responsePDU.getErrorStatusText();

                    if (errorStatus == PDU.noError) {
                        System.out.println("Snmp Set Response = " + responsePDU.getVariableBindings());
                    } else {
                        System.out.println("Error: Request Failed");
                        System.out.println("Error Status = " + errorStatus);
                        System.out.println("Error Index = " + errorIndex);
                        System.out.println("Error Status Text = " + errorStatusText);
                    }
                } else {
                    System.out.println("Error: Response PDU is null");
                }
            } else {
                System.out.println("Error: Agent Timeout... ");
            }
            snmp.close();
        } catch (Exception e) {
            if (e.getCause() == null)
                System.out.println("Exception: " + e.toString());
            else
                System.out.println("Exception: " + e.getCause().toString() + " caused by " + e.getMessage());
        }
        return null;
    }
}