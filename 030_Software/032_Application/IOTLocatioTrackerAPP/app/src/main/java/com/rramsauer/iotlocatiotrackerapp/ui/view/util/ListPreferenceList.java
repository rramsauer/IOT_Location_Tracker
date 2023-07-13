package com.rramsauer.iotlocatiotrackerapp.ui.view.util;

import java.util.ArrayList;

public class ListPreferenceList {

    private ArrayList<ListPreferenceElement> listPreferenceElements;

    public ListPreferenceList(ArrayList<ListPreferenceElement> listPreferenceElements) {
        this.listPreferenceElements = listPreferenceElements;
    }

    public ListPreferenceList() {
        this.listPreferenceElements = new ArrayList<ListPreferenceElement>();
    }

    public ArrayList<ListPreferenceElement> getListPreferenceElements() {
        return listPreferenceElements;
    }

    public void setListPreferenceElements(ArrayList<ListPreferenceElement> listPreferenceElements) {
        this.listPreferenceElements = listPreferenceElements;
    }

    public ArrayList<ListPreferenceElement> getDevaultElement(String value, String entry) {
        ArrayList<ListPreferenceElement> listPreferenceElements = new ArrayList<>();
        listPreferenceElements.add(new ListPreferenceElement(value, entry));
        return listPreferenceElements;
    }

    public void setListPreferenceListToDevault(String value, String entry) {
        this.listPreferenceElements = listPreferenceElements = new ArrayList<>();
        this.listPreferenceElements.add(new ListPreferenceElement(value, entry));
    }

    public String[] getEntryArray() {
        String[] strArr1 = new String[listPreferenceElements.size()];
        int listIndex = 0;
        for (ListPreferenceElement element : listPreferenceElements) {
            strArr1[listIndex] = element.getEntry();
            listIndex++;
        }
        return strArr1;
    }

    public String[] getValueArray() {
        String[] strArr = new String[listPreferenceElements.size()];
        int listIndex = 0;
        for (ListPreferenceElement element : listPreferenceElements) {
            strArr[listIndex] = element.getValue();
            listIndex++;
        }
        return strArr;
    }

    public void addElementToList(ListPreferenceElement listPreferenceElements) {
        this.listPreferenceElements.add(listPreferenceElements);
    }

    public void clear() {
        this.listPreferenceElements.clear();
    }

    public boolean isEmpty() {
        return this.listPreferenceElements.isEmpty();
    }

    public boolean removeElement(ListPreferenceElement listPreferenceElement) {
        return this.listPreferenceElements.remove(listPreferenceElement);
    }

    public int size() {
        return this.listPreferenceElements.size();
    }

}
