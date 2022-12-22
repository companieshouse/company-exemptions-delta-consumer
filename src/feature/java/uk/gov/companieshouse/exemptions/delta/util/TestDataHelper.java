package uk.gov.companieshouse.exemptions.delta.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.springframework.util.FileCopyUtils;

public class TestDataHelper {

    public static String getInputData() {
        String path = "src/feature/resources/fragments/exemptions_delta_input.json";
        return readFile(path);
    }

    public static String getOutputData() {
        String path = "src/feature/resources/fragments/exemptions_delta_output.json";
        return readFile(path).replaceAll("\n", "");
    }

    private static String readFile(String path) {
        String data;
        try {
            data = FileCopyUtils.copyToString(new InputStreamReader(new FileInputStream(new File(path))));
        } catch (IOException e) {
            data = null;
        }
        return data;
    }
}
