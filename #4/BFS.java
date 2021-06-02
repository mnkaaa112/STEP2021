import java.io.*;
import java.util.*;

//checkしたかどうかを記録するリストを二分木で実装するためのclass
class binarytree{
    String id;
    binarytree source;  //最後に最短経路を出力できるようにリンク元を保持
    binarytree left;
    binarytree right;

    binarytree(String id, String source, binarytree original){
        this.id = id;
        if(original==null){
            this.source = null;
        }else{
            this.source = original.get(source);
        }
        this.left = null;
        this.right = null;
    }

    void insert(String id, String source){
        binarytree next = this;
        while(true){
           if(Integer.parseInt(id)<Integer.parseInt(next.id)){
                if(next.left==null){
                    next.left = new binarytree(id, source, this);
                    break;
                }else{
                    next = next.left;
                }
            }else{
                if(next.right==null){
                    next.right = new binarytree(id, source, this);
                    break;
                }else{
                    next = next.right;
                }
            }
        }
    }

    boolean ischecked(String s){ //binarytree内に存在するかどうか
        binarytree next = this;
        while(true){
            if(next==null){
                return false;
            }else if(next.id.equals(s)){
                return true;
            }else if(Integer.parseInt(s)<Integer.parseInt(next.id)){
                next = next.left;
            }else{
                next = next.right;
            }
        }
    }

    binarytree get(String s){ //binarytree内の要素を取り出す
        if(s==null) return null;
        binarytree next = this;
        while(true){
            if(next.id.equals(s)){
                return next;
            }else if(Integer.parseInt(s)<Integer.parseInt(next.id)){
                next = next.left;
            }else if(Integer.parseInt(s)>Integer.parseInt(next.id)){
                next = next.right;
            }else{
                System.out.println("can't find "+s);
                return null;
            }
        }
    }
}

public class BFS {
    public static void main(String[] args) {
        Map<String, String> pages = new TreeMap<String, String>();
        Map<String, Set<String>> links = new TreeMap<String, Set<String>>();
        Queue<String> queue = new ArrayDeque<String>();


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

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String source = args[0]; //どこから
            String sourceID = "";
            String target = args[1]; //どこまで
            String targetID = "";

            //pagesを検索してsourceとtargetのidを得る
            int getsource = 0; 
            int gettarget = 0;
            for (Map.Entry<String, String> page : pages.entrySet()) {
                if (page.getValue().equals(source)) {
                    sourceID = page.getKey();
                    queue.add(sourceID);
                    getsource = 1;
                }else if(page.getValue().equals(target)){
                    targetID = page.getKey();
                    gettarget = 1;
                }
                if(getsource==1 && gettarget==1) break;
            }
            if(getsource == 0 || gettarget == 0){
                //入力した単語がpagesに無い時
                throw new Error();
            }

            binarytree checktree = new binarytree(sourceID, null, null); //checkしたpageのidを保持するbinarytreeを作成
            
            //queueを使って調べていく
            while(true){
                sourceID = queue.poll();
                if(sourceID.equals(targetID)){
                    //targetに辿り着いた
                    break;
                }else{
                    //targetでないのでそのリンク先をqueueに追加
                    if(links.get(sourceID)==null) continue; //link先を持たないpageは飛ばす
                    for(String link : links.get(sourceID)){  
                        if(checktree.ischecked(link)){
                            continue;  //linkが既にcheckされていたらqueueに入れない
                        }else{
                            queue.add(link); //queueにリンクを追加
                            checktree.insert(link, sourceID); //queueに追加する時点でchecktreeにも追加
                        }
                    }
                }
            }

            //targetからsourceまでの最短経路を表示するための操作
            binarytree sourcetree;
            binarytree targettree = checktree.get(targetID);
            System.out.print(target);
            while((sourcetree = targettree.source)!=null){
                System.out.print("<-"+pages.get(sourcetree.id));
                targettree = sourcetree;
            }
            
        } catch (Exception e) {
            System.out.println(e);
        } catch (Error e){
            System.out.println("this word don't exist in Wikipeia");
        } 
    }
}
