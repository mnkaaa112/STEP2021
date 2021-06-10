import java.io.*;
import java.util.*;

class MapPoint{
    int key;
    float x;
    float y;
    MapPoint prev;
    MapPoint next;
    MapPoint(int n, float x_point, float y_point, MapPoint prev_point, MapPoint next_point){
        this.key = n;
        this.x = x_point;
        this.y = y_point;
        this.prev = prev_point;
        this.next = next_point;
    }
}


public class TSP{
    public static void main(String[] args){
        if(args.length != 2){
            System.out.println("使用法：java TSP iputFile outputFile");
            System.out.println("ex) java TSP input.csv output.csv");
            System.exit(0);
        }
        String inputFile = args[0];
        String outputFile = args[1];

        try{
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));

            reader.readLine();
            String l;
            ArrayList<MapPoint> mapdata = new ArrayList<MapPoint>(); //
        
            //データの読み込み
            int n = 0;
            while((l = reader.readLine())!=null){
                String[] temp = l.split(",");
                mapdata.add(new MapPoint(n, Float.parseFloat(temp[0]), Float.parseFloat(temp[1]), null, null));
                n++;
        
            }
         
            MapPoint current = mapdata.get(0);
            writer.println("index");
            
            int j = 0; //
            while(j<n-1){
                
                double min_distance = 100000000;
                MapPoint nextPoint = null;
                
                int i=1;
                //近い点を調べる
                while(i<n){
                    MapPoint temp = mapdata.get(i);
                    if((temp.prev)!=null){
                        i++;
                        continue; //もし既に経路として数えていたらパス
                    }
                    double distance = Math.pow((temp.x-current.x), 2)+Math.pow((temp.y-current.y), 2);
                    
                    if(min_distance>distance){
                        min_distance = distance;
                        nextPoint = temp;
                        
                    }
                    i++;
                
                }
                current.next = nextPoint;
                nextPoint.prev = current;
                current = nextPoint;
                j++;
            }
            current.next = null; //最後のMapPoint

            current = mapdata.get(0);
            while(current!=null){
                writer.println(current.key);
                current = current.next;
            }

            reader.close();

            writer.close();
        }catch(IOException e){
            System.out.println(e);
        }
    }


}
