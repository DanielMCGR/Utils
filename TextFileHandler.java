import java.io.*;
import java.nio.file.Files;
import java.util.stream.Stream;

/**
 * This class is used for basic handling of txt files
 *
 * @author Daniel Rocha
 * @version 1.0
 */

public class TextFileHandler {
    
    /**
     * Searches a file for a specified string
     *
     * @param string the string to search for
     * @param file the txt file to search on
     * @return the line where the string is (returns -1 if it isn't there)
     * @throws Exception
     */
    public static int LineOfString(String string, File file) throws Exception{
        int line = -1;
        try {
            for(int i=0;i<GetLinesAmount(file);i++) {
                if(string.equals(ReadLine(i,file))) {
                    return line;
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return line; //this returns -1 meaning the string couldn't be found on the file
    }

    /**
     * Checks if the file has a specified string
     *
     * @param string the string to search for
     * @param file the txt file to search on
     * @return whether or not the file contains the string
     * @throws Exception
     */
    public static boolean FileContains(String string, File file) throws Exception{
        return LineOfString(string,file)!=-1;
    }

    /**
     * Gets the amount of lines on a txt file
     *
     * @param file the file to count the lines
     * @return the amount of lines
     * @throws Exception
     */
    public static int GetLinesAmount(File file) throws Exception{
        int lines = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while (reader.readLine() != null) lines++;
            reader.close();
            return lines;
        } catch (Exception e) {
            System.out.println(e);
            throw e;
        }
    }

    /**
     * Converts the txt file into an array
     *
     * @param file the file to be converted
     * @return the converted Array
     * @throws Exception
     */
    public static String[] GetFileAsArray(File file) throws Exception{
        try {
            String[] array = new String[GetLinesAmount(file)];
            for(int i=0;i<GetLinesAmount(file);i++) {
                array[i]=ReadLine(i,file);
            }
            return array;
        }
        catch(Exception e){
            System.out.println(e);
            throw e;
        }
    }

    /**
     * Fills the txt file based on an array
     *
     * @param array the array to get text from
     * @param file the file where the array will be written
     * @throws Exception
     */
    public static void SetFileFromArray(String[] array,File file) throws Exception{
        try {
            FileWriter fw = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fw);
            for(int i=0;i<array.length;i++) {
                writer.write(array[i]);
                if(i!=array.length-1) {
                    writer.newLine();
                }
            }
            writer.flush();
            writer.close();
        }
        catch(Exception e){
            System.out.println(e);
            throw e;
        }
    }

    /**
     * Gets the content on a specified line
     *
     * @param num the line number
     * @param file the file to search
     * @return the content on the line
     */
    public static String ReadLine(int num, File file){
        String line;
        try (Stream<String> lines = Files.lines(file.toPath())) {
            line = lines.skip(num).findFirst().get();
            return line;
        }
        catch(IOException e){
            System.out.println(e);
            return null;
        }
    }
}
