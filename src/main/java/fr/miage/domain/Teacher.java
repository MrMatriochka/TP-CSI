package fr.miage.domain;

public class Teacher {
    private final String lastName;
    private final String firstName;

    public Teacher(String lastName, String firstName) {
        this.lastName = lastName;
        this.firstName = firstName;
    }

    public String getLastName() { return lastName; }
    public String getFirstName() { return firstName; }
}