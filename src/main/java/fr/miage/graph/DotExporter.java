package fr.miage.graph;

import fr.miage.domain.*;

public final class DotExporter {
    private DotExporter() {}

    public static String toDot(Degree d) {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph G {\n");
        sb.append("  rankdir=TB;\n");
        sb.append("  node [shape=box];\n");

        String degNode = "DEG_" + safe(d.getName());
        sb.append("  ").append(degNode).append(" [label=\"DEGREE ")
                .append(escape(d.getName())).append("\\n")
                .append(escape(d.getType().toString())).append("\"];\n");

        for (Year y : d.getYears()) {
            String yearNode = "YEAR_" + safe(d.getName()) + "_" + y.getIndex();
            sb.append("  ").append(yearNode).append(" [label=\"YEAR ")
                    .append(y.getIndex()).append("\"];\n");
            sb.append("  ").append(degNode).append(" -> ").append(yearNode).append(";\n");

            for (UE ue : y.getUes()) {
                String ueNode = "UE_" + safe(ue.getName());
                sb.append("  ").append(ueNode).append(" [label=\"UE ")
                        .append(escape(ue.getName()))
                        .append("\\nects=").append(ue.getEcts())
                        .append("\\nhours=").append(ue.totalHours())
                        .append("\\nsessions=").append(ue.sessions())
                        .append("\"];\n");
                sb.append("  ").append(yearNode).append(" -> ").append(ueNode).append(";\n");
            }
        }

        sb.append("}\n");
        return sb.toString();
    }

    private static String safe(String s) {
        return s.replaceAll("[^a-zA-Z0-9_]", "_");
    }

    private static String escape(String s) {
        return s.replace("\"", "\\\"");
    }
}
