package tests;

import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import model.People;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utils.FilesUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CheckFilesTests {

    final FilesUtils filesUtils = new FilesUtils();

    @DisplayName("Проверка на содержание необходимых файлов")
    @Test
    void verifyFilesInZip() throws Exception {
        try (ZipInputStream zis = new ZipInputStream(
                getClass().getResourceAsStream(filesUtils.getZipName())
        )) {
            ZipEntry entry;
            List<String> expectedFiles = new ArrayList<>(List.of("pdf.pdf", "csv.csv", "xlsx.xlsx"));
            List<String> actualFiles = new ArrayList<>();

            while ((entry = zis.getNextEntry()) != null) {
                actualFiles.add(entry.getName());
            }

            assertEquals(expectedFiles, actualFiles);
        }
    }

    @DisplayName("Проверка pdf файла")
    @Test
    void pdfTest() throws Exception {
        try (InputStream pdfFile = filesUtils.getFileFromZip("pdf.pdf")) {
            PDDocument document = PDDocument.load(pdfFile);
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            assertThat(text).contains("Это pdf файл");
        }
    }

    @DisplayName("Проверка xlsx файла")
    @Test
    void xlsxTest() throws Exception {
        try (InputStream xlsxFile = filesUtils.getFileFromZip("xlsx.xlsx")) {
            XLS xlsFile = new XLS(xlsxFile);
            String actualTitle = xlsFile.excel.getSheetAt(0).getRow(0).getCell(0).getStringCellValue();
            assertThat(actualTitle).isEqualTo("ID");
        }
    }

    @DisplayName("Проверка csv файла")
    @Test
    void csvTest() throws Exception {
        try (InputStream csvFile = filesUtils.getFileFromZip("csv.csv")) {
            CSVReader csvReader = new CSVReader(new InputStreamReader(csvFile));
            List<String[]> data = csvReader.readAll();
            assertThat(data.size()).isEqualTo(2);
        }
    }

    @DisplayName("Проверка json файла")
    @Test
    void testJsonParsing() throws IOException {
        String json = "[\n" +
                "    {\n" +
                "        \"name\": \"Alice Green\",\n" +
                "        \"age\": 42,\n" +
                "        \"address\": {\n" +
                "            \"street\": \"789 Pine St\",\n" +
                "            \"city\": \"Mapleton\",\n" +
                "            \"zipcode\": \"54321\"\n" +
                "        }\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Bob Johnson\",\n" +
                "        \"age\": 34,\n" +
                "        \"address\": {\n" +
                "            \"street\": \"101 Birch Rd\",\n" +
                "            \"city\": \"Lakeside\",\n" +
                "            \"zipcode\": \"98765\"\n" +
                "        }\n" +
                "    }\n" +
                "]";

        ObjectMapper objectMapper = new ObjectMapper();
        List<People> people = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, People.class));
        assertEquals(2, people.size(), "The list should contain 2 persons");
        People firstPerson = people.get(0);
        assertEquals("Alice Green", firstPerson.getName(), "First person name should be 'Alice Green'");
        assertEquals(42, firstPerson.getAge(), "First person age should be 42");
    }
}