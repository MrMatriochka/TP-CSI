package fr.miage.domain;

public class Assignment {
    private final Teacher teacher;
    private final UE ue;
    private final int hours;

    public Assignment(Teacher teacher, UE ue, int hours) {
        this.teacher = teacher;
        this.ue = ue;
        this.hours = hours;
    }

    public Teacher getTeacher() { return teacher; }
    public UE getUe() { return ue; }
    public int getHours() { return hours; }
}
