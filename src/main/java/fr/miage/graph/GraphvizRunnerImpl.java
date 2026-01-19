package fr.miage.graph;

import fr.miage.app.Errors;
import fr.miage.app.Result;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class GraphvizRunnerImpl implements IGraphvizRunner {

    @Override
    public Result renderPng(String dot, Path outPng) {
        try {
            Path parent = outPng.getParent();
            if (parent != null) Files.createDirectories(parent);
        } catch (IOException e) {
            return Result.err(Errors.CANNOT_WRITE_FILE);
        }

        try {
            ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng", "-Gdpi=300", "-o", outPng.toString());
            Process p = pb.start();

            p.getOutputStream().write(dot.getBytes(StandardCharsets.UTF_8));
            p.getOutputStream().close();

            int code = p.waitFor();
            if (code != 0) return Result.err(Errors.GRAPHVIZ_FAILED);

            if (!Files.exists(outPng)) return Result.err(Errors.CANNOT_WRITE_FILE);
            return Result.ok("Graph traced to " + outPng);

        } catch (IOException e) {
            return Result.err(Errors.GRAPHVIZ_NOT_INSTALLED);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Result.err(Errors.GRAPHVIZ_FAILED);
        }
    }
}
