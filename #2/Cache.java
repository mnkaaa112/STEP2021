import java.util.LinkedList;

class Cache {
    int n;
    String[][] cache = new String[n][4]; //static size cache -> cache[url][contents][prev][next]
    LinkedList<String>[][] database; //database in case collision
    int newest = 0;
    int oldest = 0;

    void init(){
        for(int i=0;i<n;i++){
            cache[i]=null;
        }
    }

    int hash(String s){
        int hash_value=0;
        for(int i=0;i<s.length();i++){
            hash_value+=s.charAt(i);
        }
        hash_value = hash_value%n;
        return hash_value;
    }

    void access_page(String url, String contents){
        int key = hash(url);
        if(cache[key][0]!=url){
        //url doesn't exist or collision
            //search database
            int index=-1;
            if((index=database[key][0].indexOf(url))!=-1){  
            //this process isn't O(1)
            //cache has the url
                String where = database[key][1].get(index);
                int newkey = Integer.parseInt(where);
                newest = newkey;
            }else{
                //should put the url at another hashkey
                int newkey = oldest;
                database[key][0].add(url);
                database[key][1].add(String.valueOf(newkey));
                oldest = Integer.parseInt(cache[oldest][3]); //next
                cache[newkey][0]=url;
                cache[newkey][1]=contents;
                cache[newkey][2]=String.valueOf(newest); //newprev
                cache[newkey][3]=null; //newnext
                newest = newkey;
            }
        }else{
        //cache has the url at the right space
            newest = key;
        }
    }
}
