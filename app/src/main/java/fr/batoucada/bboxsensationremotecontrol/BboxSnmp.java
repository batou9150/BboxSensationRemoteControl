package fr.batoucada.bboxsensationremotecontrol;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

class BboxSnmp {

    private CommunityTarget target = new CommunityTarget();
    private OID oid = new OID("1.3.6.1.4.1.8711.101.13.1.3.28.0");

    BboxSnmp(String ipAddress) {
        // Create Target Address object
        target.setCommunity(new OctetString("public"));
        target.setAddress(GenericAddress.parse("udp:" + ipAddress + "/161"));
        target.setRetries(2);
        target.setTimeout(1000);
        target.setVersion(SnmpConstants.version1);
    }

    ResponseEvent set(String sysContactValue) {
        ResponseEvent response = null;
        try {
            // Create TransportMapping and Listen
            TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
            Snmp snmp = new Snmp(transport);
            transport.listen();

            // Create the PDU object
            PDU pdu = new PDU();
            pdu.add(new VariableBinding(oid, new OctetString(sysContactValue)));

            response = snmp.set(pdu, target);

            snmp.close();
        } catch (Exception e) {
            System.out.println("BboxSnmp.set - Target : " + target.toString());
            if (e.getCause() == null)
                System.out.println("BboxSnmp.set - Exception: " + e.toString());
            else
                System.out.println("BboxSnmp.set - Exception: " + e.getCause().toString() + " caused by " + e.getMessage());
        }
        return response;
    }

    boolean test() {
        boolean isOK = false;
        try {
            // Create TransportMapping and Listen
            TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
            Snmp snmp = new Snmp(transport);
            transport.listen();

            // Create the PDU object
            PDU pdu = new PDU();
            pdu.add(new VariableBinding(oid, new OctetString("0")));

            ResponseEvent response = snmp.get(pdu, target);

            // Process Agent Response
            if (response != null) {
                PDU responsePDU = response.getResponse();
                if (responsePDU != null) {
                    isOK = responsePDU.getErrorStatus() == PDU.noError;
                }
            }
            snmp.close();
        } catch (Exception e) {
            if (e.getCause() == null)
                System.out.println("Exception: " + e.toString());
            else
                System.out.println("Exception: " + e.getCause().toString() + " caused by " + e.getMessage());
        }
        return isOK;
    }

    static void handleResponse(ResponseEvent response) {
        // Process Agent Response
        if (response != null) {
            PDU responsePDU = response.getResponse();

            if (responsePDU != null) {
                int errorStatus = responsePDU.getErrorStatus();
                int errorIndex = responsePDU.getErrorIndex();
                String errorStatusText = responsePDU.getErrorStatusText();

                if (errorStatus == PDU.noError) {
                    System.out.println("BboxSnmp Response = " + responsePDU.getVariableBindings());
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
    }

    static String getBboxIp() {
        String myIp = getIp();
        if (myIp == null) return null;
        String subnet = getSubnet(myIp);
        if (subnet == null) return null;
        return getReachableHost(subnet);
    }

    static final String dot = ".";
    private static final String ipSplitter = "\\.";

    static String getIp() {
        try {
            final DatagramSocket socket = new DatagramSocket();
            final String s = "8.8.8.8";
            socket.connect(InetAddress.getByName(s), 10002);
            return socket.getLocalAddress().getHostAddress();
        } catch (SocketException socketException) {
            socketException.printStackTrace();
            return null;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }

    static String getSubnet(String ip) {
        String[] ipSplitted = ip.split(ipSplitter);
        if (ipSplitted.length == 4)
            return ipSplitted[0] + dot + ipSplitted[1] + dot + ipSplitted[2];
        else
            return null;
    }

    static String getReachableHost(String subnet) {
        for (int i = 1; i < 255; i++) {
            String host = subnet + dot + i;
            try {
                if (InetAddress.getByName(host).isReachable(100)) {
                    BboxSnmp bboxSnmp = new BboxSnmp(host);
                    if (bboxSnmp.test()) return host;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

