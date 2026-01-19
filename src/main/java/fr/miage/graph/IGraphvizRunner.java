package fr.miage.graph;

import fr.miage.app.Result;
import java.nio.file.Path;

public interface IGraphvizRunner {
    Result renderPng(String dot, Path outPng);
}
