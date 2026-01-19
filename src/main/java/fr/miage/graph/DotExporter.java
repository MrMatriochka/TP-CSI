package fr.miage.graph;

import fr.miage.domain.*;
import fr.miage.service.OfferService;

public final class DotExporter {
    private DotExporter() {}

    public static String toDot(Degree d, OfferService service) {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph G {\n");
        sb.append("  rankdir=TB;\n");
        sb.append("  bgcolor=\"white\";\n");
        sb.append("  fontname=\"Helvetica\";\n");
        sb.append("  splines=true;\n");

        // Style global
        sb.append("  node [shape=box, style=\"rounded,filled\", fontname=\"Helvetica\", fontsize=12, color=\"#333333\", penwidth=1.2];\n");
        sb.append("  edge [fontname=\"Helvetica\", color=\"#666666\", penwidth=1.2, arrowhead=vee, arrowsize=0.8];\n");

        // Degree node (fontsize=18)
        String degNode = "DEG_" + safe(d.getName());
        sb.append("  ").append(degNode)
                .append(" [fontsize=18, fillcolor=\"#DDEBFF\", label=\"DEGREE ")
                .append(escape(d.getName())).append("\\n")
                .append(escape(d.getType().toString())).append("\"];\n");

        for (Year y : d.getYears()) {
            // Year node (fontsize=14)
            String yearNode = "YEAR_" + safe(d.getName()) + "_" + y.getIndex();
            sb.append("  ").append(yearNode)
                    .append(" [fontsize=14, fillcolor=\"#F2F2F2\", label=\"YEAR ")
                    .append(y.getIndex()).append("\"];\n");

            sb.append("  ").append(degNode).append(" -> ").append(yearNode)
                    .append(" [penwidth=1.6];\n");

            for (UE ue : y.getUes()) {
                String ueNode = "UE_" + safe(ue.getName());

                int planned = ue.totalHours();
                int assigned = service.assignedHoursForUE(ue.getName());
                int cover = (planned == 0) ? 0 : (assigned * 100) / planned;

                String fill = coverColor(cover);

                // UE node : label HTML (titre 16, détails 10)
                sb.append("  ").append(ueNode).append(" [fillcolor=\"").append(fill).append("\", label=<")
                        .append("<TABLE BORDER=\"0\" CELLBORDER=\"0\" CELLPADDING=\"3\">")
                        .append("<TR><TD><B><FONT POINT-SIZE=\"16\">UE ")
                        .append(html(ue.getName()))
                        .append("</FONT></B></TD></TR>")
                        .append("<TR><TD><FONT POINT-SIZE=\"10\">")
                        .append("ects=").append(ue.getEcts())
                        .append("  hours=").append(planned)
                        .append("  sessions=").append(ue.sessions())
                        .append("  cover=").append(cover).append("%")
                        .append("</FONT></TD></TR>")
                        .append("</TABLE>")
                        .append(">];\n");

                sb.append("  ").append(yearNode).append(" -> ").append(ueNode).append(";\n");
            }
        }

        sb.append("}\n");
        return sb.toString();
    }

    private static String coverColor(int cover) {
        if (cover < 50) return "#FFB3B3";  // rouge clair
        if (cover < 80) return "#FFE7A3";  // jaune clair
        return "#B9F6C4";                  // vert clair
    }

    private static String safe(String s) {
        return s.replaceAll("[^a-zA-Z0-9_]", "_");
    }

    private static String escape(String s) { // pour labels "classiques"
        return s.replace("\"", "\\\"");
    }

    private static String html(String s) {   // pour labels HTML
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
