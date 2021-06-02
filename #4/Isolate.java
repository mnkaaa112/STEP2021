import java.io.*;
import java.util.*;

public class Isolate {
    public static void main(String[] args) {
        Map<String, String> pages = new TreeMap<String, String>();
        Map<String, Set<String>> links = new TreeMap<String, Set<String>>();
        

        try {
            File pageFile = new File("data/pages.txt");
            Scanner pageReader = new Scanner(pageFile);
            while (pageReader.hasNextLine()) {
                String[] page = pageReader.nextLine().split("\t", 0);
                // page[0]: id, page[1]: title
                pages.put(page[0], page[1]);
            }
            pageReader.close();

            File linkFile = new File("data/links.txt");
            Scanner linkReader = new Scanner(linkFile);
            while (linkReader.hasNextLine()) {
                String[] link = linkReader.nextLine().split("\t", 0);
                // link[0]: id (from), links[1]: id (to)
                if (!links.containsKey(link[0])) {
                    links.put(link[0], new TreeSet<String>());
                }
                links.get(link[0]).add(link[1]);
            }
            linkReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            return;
        }

        //どこからも遷移せずどこにもリンクを持たない孤立したページを探す
        for(Map.Entry<String, Set<String>> linkset : links.entrySet()){
            pages.remove(linkset.getKey());//link元のpageを削除
            for(String link : linkset.getValue()){
                pages.remove(link); //link先のpageを削除
            }
        }


        for(String isolate_page : pages.values()){
            System.out.println(isolate_page);
        }
    }
}
