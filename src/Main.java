import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws  FileNotFoundException {
        if (args.length == 2) {
            String inputFileName = args[0];
            String outputFileName = args[1];
            Management.scanFile(inputFileName, outputFileName);
        }

    }
}