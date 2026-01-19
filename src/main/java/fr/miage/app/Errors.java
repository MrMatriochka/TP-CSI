package fr.miage.app;

public final class Errors {
    private Errors() {}

    // Erreurs de parsing
    public static final String INVALID_COMMAND = "ERR: Invalid command";
    public static final String INVALID_ARGUMENTS = "ERR: Invalid arguments";
    public static final String INVALID_NUMBER = "ERR: Invalid number";

    // Erreurs métier
    public static final String NO_DEGREE_SELECTED = "ERR: No degree selected";
    public static final String NO_YEAR_SELECTED = "ERR: No year selected";
    public static final String DEGREE_NOT_FOUND = "ERR: Degree not found";
    public static final String DEGREE_ALREADY_EXISTS = "ERR: Degree already exists";
    public static final String UE_ALREADY_EXISTS = "ERR: UE already exists";
    public static final String INVALID_NAME = "ERR: Invalid name";
    public static final String INVALID_DEGREE_TYPE = "ERR: Invalid degree type";
    public static final String INVALID_YEAR = "ERR: Invalid year";
    public static final String UE_HOURS_GT_30 = "ERR: UE hours > 30";
    public static final String MAX_6_UE = "ERR: Max 6 UE/year";
    public static final String ECTS_GT_60 = "ERR: ECTS > 60/year";
    public static final String INVALID_TYPE_DURATION = "ERR: Invalid duration for type";
    public static final String STUDENTS_OVER_0= "ERR: maxStudents must be > 0";
    public static final String ECT_OVER_0 = "ERR: ectsTotal must be > 0";
    public static final String ECT_60_PER_YEAR = "ERR: ectsTotal must equal years*60";
    public static final String HOURS_OVER_0 = "ERR: hours must be >= 0";
    public static final String TEACHER_ALREADY_EXISTS = "ERR: Teacher already exists";
    public static final String TEACHER_NOT_FOUND = "ERR: Teacher not found";
    public static final String UE_NOT_FOUND = "ERR: UE not found";
    public static final String MAX_TEACHER_HOURS_90 = "ERR: Teacher hours > 90";
    public static final String UE_OVER_ASSIGNED = "ERR: Assigned hours > UE total";
    public static final String ASSIGNMENT_ALREADY_EXISTS = "ERR: Assignment already exists";
    public static final String AMBIGUOUS_NAME = "ERR: Ambiguous name";
    public static final String UE_NOT_IN_ANY_YEAR = "ERR: UE not linked to any year";
    public static final String EDIT_BREAKS_ASSIGNMENTS = "ERR: Assigned hours > new UE total";
    public static final String ECTS_OVER_0 = "ERR: ects must be > 0";


}
