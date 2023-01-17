package ru.krey.sandmine.controller;

import org.neo4j.driver.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.krey.sandmine.DTO.ExportDTO;
import ru.krey.sandmine.DTO.ImportDto;

import java.io.*;
import java.util.Properties;
import java.util.stream.Collectors;

@RestController
public class ImportExportController {
    private final Driver driver;
    final String usernameKey = "spring.neo4j.authentication.username";
    final String passwordKey = "spring.neo4j.authentication.password";

    ImportExportController() {
        Properties properties = new Properties();
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        AuthToken authToken = AuthTokens.basic(properties.getProperty(usernameKey), properties.getProperty(passwordKey));
        driver = GraphDatabase.driver(
                "neo4j://localhost:7687",
                authToken
        );
    }

    private String importPath;
    private String homeDirectory;

    private synchronized String homeDirectory(){
        if(homeDirectory == null){
            homeDirectory = driver.session().beginTransaction().run(
                    "CALL dbms.listConfig() YIELD name, value " +
                            "WHERE name='dbms.directories.neo4j_home'" +
                            "RETURN value"
            ).next().get("value").asString();
        }
        return homeDirectory;
    }
    private synchronized String getImportPath() {
        if (importPath == null) {
            importPath = driver.session().beginTransaction().run(
                    "CALL dbms.listConfig() YIELD name, value " +
                            "WHERE name='dbms.directories.import'" +
                            "RETURN value"
            ).next().get("value").asString();
        }
        return importPath;
    }

    @GetMapping("/export")
    ExportDTO exportDatabase() throws IOException {
        String fileName = "graph";

        Transaction transaction = driver.session().beginTransaction();
        transaction.run(
                "CALL apoc.export.graphml.all('" + fileName + "', " +
                        "{batchSize: 10000, readLabels: true, storeNodeIds: false, useTypes:true})");
        transaction.commit();

        BufferedReader bufferedReader = new BufferedReader(new FileReader(homeDirectory()+"/"+getImportPath() + "/" + fileName));
        ExportDTO result = new ExportDTO(bufferedReader.lines().collect(Collectors.joining(System.lineSeparator())));
        bufferedReader.close();
        return result;
    }

    @PostMapping("/import")
    void importDatabase(@RequestBody ImportDto importDto) throws IOException {
        PrintWriter printWriter = new PrintWriter(new FileWriter(homeDirectory()+"/"+getImportPath() + "/" + importDto.getFileName()));
        printWriter.println(importDto.getData());

        Session session = driver.session();
        Transaction transaction = session.beginTransaction();
        transaction.run(
                "CALL apoc.periodic.iterate('MATCH (n) RETURN n', 'DETACH DELETE n', {batchSize:1000})"
        );
        transaction.run(
                "CALL apoc.import.graphml('"+importDto.getFileName()+"', " +
                        "{batchSize: 10000, readLabels: true, storeNodeIds: false, useTypes:true})"
        );
        transaction.commit();
    }

    @PostMapping("/clear")
    void clearDatabase() {
        Transaction transaction = driver.session().beginTransaction();
        transaction.run(
                "CALL apoc.periodic.iterate('MATCH (n) RETURN n', 'DETACH DELETE n', {batchSize:1000})"
        );
        transaction.commit();
    }
}
