package org.nicholas.spring.batchtask.config.tasklets;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class FileMovingTasklet implements Tasklet {
    private final String FROM;
    private final String TO;

    public FileMovingTasklet(String FROM, String TO) {
        this.FROM = FROM;
        this.TO = TO;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        String fileName = TO + "/" + getNewFileName(FROM);
        Files.move(Paths.get(FROM), Paths.get(fileName));
        return RepeatStatus.FINISHED;
    }

    private String getNewFileName(String oldName){
        String[] composedName = composeName(oldName);
        StringBuilder newName = new StringBuilder();

        for (int i=0; i<composedName.length-1; i++) { //without last part (.csv)
            newName.append(composedName[i] + ".");
        }

        newName.deleteCharAt(newName.lastIndexOf(".")); //removing last "."
        newName.append("_" + System.currentTimeMillis()); //add Current Time in millis
        newName.append("."+composedName[composedName.length-1]);

        return newName.toString();
    }

    private String[] composeName(String name){
        String[] delimitedPath = name.split("/");
        String[] delimitedFileName = delimitedPath[delimitedPath.length-1].split("\\.");
        return delimitedFileName;
    }
}
