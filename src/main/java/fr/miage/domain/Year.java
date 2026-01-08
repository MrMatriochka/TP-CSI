package fr.miage.domain;

import java.util.ArrayList;
import java.util.List;

public class Year {
    private final int index;
    private final List<UE> ues = new ArrayList<>();

    public Year(int index) { this.index = index; }

    public int getIndex() { return index; }
    public List<UE> getUes() { return ues; }
}
