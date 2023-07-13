package com.rramsauer.iotlocatiotrackerapp.services.ble;

import android.content.Context;

import androidx.annotation.XmlRes;

import com.rramsauer.iotlocatiotrackerapp.util.log.Logger;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class provides a service which allows read the supported ble characteristic from a xml file and provides it.
 * <p>
 * In this class the services are read from an XML and provided as an ArrayList<> and HashMap.
 * The XML file used is "../res/xml/btle_supported_gatt_attributes.xml".
 *
 * @author Ramsauer René
 * @version V2.0
 * @implNote Attention: the format of the XML should have a specify format.
 * <service uuid="..." name="..." >
 * <characteristic uuid="..." name="..."/>
 * </service>
 * @see org.xmlpull.v1
 */
public class BTLE_GattAttributes {
    public static final String TAG = "BTLE_GattAttributes";
    public static final int GATT_SERVICE = 0;
    public static final int GATT_CHARACTERISTIC = 1;
    private XmlPullParserFactory pullParserFactory;
    private Context context;
    private ArrayList<Service> services;
    private HashMap<String, String> uuids;
    private HashMap<String, String> serviceHashMap;
    private HashMap<String, HashMap<String, String>> characteristicHashMap;

    /**
     * CONSTRUCTOR
     * initialization of BTLE_GattAttributes.
     *
     * @param context The context to use. Usually your android.app.Application or android.app
     * @author Ramsauer René
     * @version V0.1
     */
    public BTLE_GattAttributes(Context context) {
        this.context = context;
    }

    /**
     * This function is needed to inizialize the objecte in the class BTLE_GattAttributes class and reads the XML file.
     *
     * @param id Passed the xml id of the file with should be initialized.
     * @author Ramsauer René
     * @version V0.2
     * @implNote Example: readSupportedGattAttributesFromXMl(R.xml...)
     * Attention: the format of the XML should have a specify format.
     * <service uuid="..." name="..." >
     * <characteristic uuid="..." name="..."/>
     * </service>
     * @see org.xmlpull.v1
     */
    public void readSupportedGattAttributesFromXMl(@XmlRes int id) {
        try {
            Logger.i(TAG, "Reade supported gatt attributes from xml...");
            XmlPullParser xpp = context.getResources().getXml(id);
            parseXML(xpp);
            for (Service service : services) {
                String serviceName = service.getName();
                String serviceUuid = service.getUuid();
                Logger.v(TAG, "Supported Service: " + serviceName + " {" + serviceUuid + "}");
                for (Characteristic characteristic : service.getCharacteristics()) {
                    Logger.i(TAG, "Supported Characteristic of Service(" + serviceName + ") Characteristic :" + characteristic.getName() + " {" + characteristic.getUuid() + "}");
                }
            }
            Logger.i(TAG, "Supported gatt attributes were read from the xml");
        } catch (XmlPullParserException xppe) {
            Logger.e(TAG, "Reade supported gatt attributes from xml failed. (ExceptionTyp: XmlPullParserException)");
            xppe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * This function manage the xml Parsing of the specify xml.
     * If the design of the XML is changed, this function must be revised.
     *
     * @param parser Passed an XmlPullParser.
     * @throws XmlPullParserException
     * @throws IOException
     * @author Ramsauer René
     * @version V0.1
     * @implNote Example: readSupportedGattAttributesFromXMl(R.xml...)
     * @see org.xmlpull.v1
     */
    private void parseXML(XmlPullParser parser) throws XmlPullParserException, IOException {
        serviceHashMap = null;
        characteristicHashMap = null;
        services = null;
        int eventType = parser.getEventType();
        Service service = null;
        HashMap<String, String> tempCharacteristicHashMap = new HashMap<>();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name;
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    services = new ArrayList<>();
                    serviceHashMap = new HashMap<>();
                    characteristicHashMap = new HashMap<>();
                    uuids = new HashMap<>();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equals("service")) {
                        service = new Service();
                        service.setUuid(parser.getAttributeValue(null, "uuid"));
                        service.setName(parser.getAttributeValue(null, "name"));
                        serviceHashMap.put(service.getUuid(), service.getName());
                        uuids.put(parser.getAttributeValue(null, "id"), service.uuid);
                    } else if (service != null) {
                        if (name.equals("characteristic")) {
                            Characteristic characteristic = new Characteristic();
                            characteristic.setUuid(parser.getAttributeValue(null, "uuid"));
                            characteristic.setName(parser.getAttributeValue(null, "name"));
                            service.addCharacteristics(characteristic);
                            tempCharacteristicHashMap.put(characteristic.getUuid(), characteristic.getName());
                            uuids.put(parser.getAttributeValue(null, "id"), characteristic.uuid);
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("service") && service != null) {
                        services.add(service);
                        characteristicHashMap.put(service.getUuid(), tempCharacteristicHashMap);
                    }
            }
            eventType = parser.next();
        }
    }

    /**
     * This function returns a HashMap <uuid String, description String> of gatt characteristics for a specific gatt service.
     * This function is needed to initialize the object in the BTLE_GattAttributes class and reads the XML file.
     *
     * @param uuid Passed the uuid string of the service.
     * @return Return a HashMap <uuid String, description String> of characteristics for a specific service.
     * @author Ramsauer René
     * @version V0.1
     * @implNote Example: getHashSetCharacteristicOfService("0000180f-0000-1000-8000-00805f9b34fb")
     */
    public HashMap<String, String> getHashSetCharacteristicOfService(String uuid) {
        if (characteristicHashMap == null) {
            Logger.e(TAG, "The class was not initialized. The readSupportedGattAttributesFromXMl() function must be called first.");
        }
        return characteristicHashMap.get(uuid);
    }

    /**
     * This function returns a HashMap <uuid String, description String> of supported gatt service.
     * This function is needed to initialize the object in the BTLE_GattAttributes class and reads the XML file.
     *
     * @return Returns a HashMap <uuid String, description String> of supported service.
     * @author Ramsauer René
     * @version V0.1
     * @implNote Example: getServiceHashMap()
     */
    public HashMap<String, String> getServiceHashMap() {
        if (serviceHashMap == null) {
            Logger.e(TAG, "The class was not initialized. The readSupportedGattAttributesFromXMl() function must be called first.");
        }
        return serviceHashMap;
    }

    /**
     * This function returns a ArrayList with all supported gatt services and gatt characteristics.
     * This function is needed to initialize the object in the BTLE_GattAttributes class and reads the XML file.
     *
     * @return Returns a ArrayList of Services
     * @author Ramsauer René
     * @version V0.1
     * @implNote Example: getArrayListOfServicesAndCharacteristic()
     */
    public ArrayList<Service> getArrayListOfServicesAndCharacteristic() {
        if (services == null) {
            Logger.e(TAG, "The class was not initialized. The readSupportedGattAttributesFromXMl() function must be called first.");
        }
        return services;
    }

    /**
     * This function translate returns the description.
     *
     * @param typ Passed GATT_SERVICE ore GATT_CHARACTERISTIC
     * @return Returns description string
     * @author Ramsauer René
     * @version V0.1
     * @implNote Example: lookup(GATT_SERVICE,"0000180f-0000-1000-8000-00805f9b34fb")
     */
    public String lookup(int typ, String uuid) {
        String name = "";
        switch (typ) {
            case GATT_SERVICE:
                name = serviceHashMap.get(uuid);
                break;
            case GATT_CHARACTERISTIC:
                for (Map.Entry<String, HashMap<String, String>> gattService : characteristicHashMap.entrySet()) {
                    if (gattService.getValue().get(uuid) != null) {
                        name = gattService.getValue().get(uuid);
                    }
                }
                break;
            default:
                name = "";
                Logger.e(TAG, "Programming ERROR: Passe false parameter [typ]", Thread.currentThread().getStackTrace()[2]);
        }
        return name;
    }

    /**
     * This function translate returns the description.
     *
     * @param id Passed gatt characteristic id
     * @return Returns uuid
     * @author Ramsauer René
     * @version V0.1
     * @implNote Example: getString("battery_service")
     */
    public String getString(String id) {
        return uuids.get(id);
    }

    /**
     * This class is used to merge the data of an BLE gatt characteristics.
     * The Characteristic class represents a Bluetooth GATT Characteristic,
     * which contains a value and properties that describe how the value can be accessed and used.
     * A characteristic is identified by a UUID and may also have a human-readable name.
     *
     * @author Ramsauer René
     * @version V0.1
     */
    public static class Characteristic {
        private String uuid;
        private String name;

        /**
         * Constructs an empty Characteristic object.
         */
        public Characteristic() {
        }

        /**
         * Constructs a Characteristic object with the specified UUID and name.
         *
         * @param uuid the UUID of the characteristic
         * @param name the name of the characteristic
         */
        public Characteristic(String uuid, String name) {
            this.uuid = uuid;
            this.name = name;
        }

        /* Getter */
        public String getUuid() {
            return uuid;
        }

        /* Setter */
        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * This class is used to merge the data of an BLE gatt service.
     *
     * @author Ramsauer René
     * @version V0.1
     */
    public class Service {
        private String uuid;
        private String name;
        private ArrayList<Characteristic> characteristics;

        /* Constructor */
        public Service() {
            characteristics = new ArrayList<>();
        }

        public Service(String uuid, String name, ArrayList<Characteristic> characteristics) {
            this.uuid = uuid;
            this.name = name;
            this.characteristics = characteristics;
        }

        /* Getter */
        public String getUuid() {
            return uuid;
        }

        /* Setter */
        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ArrayList<Characteristic> getCharacteristics() {
            return characteristics;
        }

        /**
         * This function add a characteristic to the service
         *
         * @param characteristic Passed the gatt characteristic with should passed to the gatt service.
         * @author Ramsauer René
         * @version V0.1
         * @implNote getArrayListOfServicesAndCharacteristic()
         */
        public void addCharacteristics(Characteristic characteristic) {
            this.characteristics.add(characteristic);
        }
    }

}