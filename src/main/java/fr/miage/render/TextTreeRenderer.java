package fr.miage.render;

import fr.miage.domain.*;

public class TextTreeRenderer {

    public String render(Degree degree) {
        StringBuilder sb = new StringBuilder();

        sb.append("DEGREE ").append(degree.getName())
                .append(" (").append(degree.getType()).append(")")
                .append(" - maxStudents=").append(degree.getMaxStudents())
                .append(" ectsTotal=").append(degree.getEctsTotal())
                .append("\n");

        for (Year y : degree.getYears()) {
            sb.append("  YEAR ").append(y.getIndex()).append("\n");
            if (y.getUes().isEmpty()) {
                sb.append("    (no UE)\n");
            } else {
                for (UE ue : y.getUes()) {
                    sb.append("    UE ").append(ue.getName())
                            .append(" ects=").append(ue.getEcts())
                            .append(" hours=").append(ue.totalHours())
                            .append(" [CM=").append(ue.getCmHours())
                            .append(", TD=").append(ue.getTdHours())
                            .append(", TP=").append(ue.getTpHours())
                            .append("]\n");
                }
            }
        }
        return sb.toString();
    }
}
