package fr.miage.graph;

import fr.miage.app.CommandExecutor;
import fr.miage.app.Result;
import fr.miage.graph.IGraphvizRunner;
import fr.miage.service.OfferService;
import fr.miage.service.OfferServicePersistence;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TraceGraphAcceptanceI3 {

    static class FakeRunnerOk implements IGraphvizRunner {
        @Override
        public Result renderPng(String dot, Path outPng) {
            try {
                if (outPng.getParent() != null) Files.createDirectories(outPng.getParent());
                // écrit un fichier non vide (on ne cherche pas un vrai PNG ici)
                Files.write(outPng, new byte[]{1, 2, 3});
                return Result.ok("Graph traced to " + outPng);
            } catch (Exception e) {
                return Result.err("ERR: Cannot write file");
            }
        }
    }

    private void runSessionWithAutosave(List<String> lines, OfferService service, Path saveFile) throws Exception {
        var exec = new CommandExecutor(service);

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;

            if (trimmed.equalsIgnoreCase("EXIT")) {
                // save final
                OfferServicePersistence.save(service, saveFile);
                break;
            }

            var result = exec.executeLine(trimmed);

            // autosave after OK
            if (result.ok()) {
                OfferServicePersistence.save(service, saveFile);
            }
        }
    }

    @Test
    void ES_02_auto_save_and_load_two_sessions() throws Exception {
        Path tmpDir = Files.createTempDirectory("autosave-es02");
        Path saveFile = tmpDir.resolve("save.txt");

        // -------- Session 1 --------
        var s1 = new OfferService();
        runSessionWithAutosave(List.of(
                "CREATE DEGREE D1 Master 2 100 120",
                "SELECT DEGREE D1",
                "CREATE UE Python 15 10 10 10",
                "EXIT"
        ), s1, saveFile);

        assertTrue(Files.exists(saveFile));
        assertTrue(Files.size(saveFile) > 0);

        // -------- Session 2 --------
        var s2 = new OfferService();
        OfferServicePersistence.loadInto(s2, saveFile);

        var exec2 = new CommandExecutor(s2);
        var result = exec2.executeLine("DISPLAY GRAPH D1");

        assertTrue(result.ok());
        assertTrue(result.message().contains("DEGREE D1"));
        assertTrue(result.message().contains("UE Python"));
    }

    @Test
    void trace_graph_ok() throws Exception {
        Path tmp = Files.createTempDirectory("trace-ok");
        Path out = tmp.resolve("miage.png");

        var service = new OfferService(new FakeRunnerOk());
        var exec = new CommandExecutor(service);

        assertTrue(exec.executeLine("CREATE DEGREE MIAGE Master 2 100 120").ok());

        var r = exec.executeLine("TRACE GRAPH MIAGE " + out);
        assertTrue(r.ok());
        assertTrue(Files.exists(out));
        assertTrue(Files.size(out) > 0);
    }

    @Test
    void trace_graph_degree_not_found() throws Exception {
        Path tmp = Files.createTempDirectory("trace-ko");
        Path out = tmp.resolve("x.png");

        var service = new OfferService(new FakeRunnerOk());
        var exec = new CommandExecutor(service);

        var r = exec.executeLine("TRACE GRAPH UNKNOWN " + out);
        assertFalse(r.ok());
        assertEquals("ERR: Degree not found", r.message());
    }
}
