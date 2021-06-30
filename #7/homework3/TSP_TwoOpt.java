import java.io.*;
import java.util.*;

class MapPoint{
    final int key;
    final float x;
    final float y;

    MapPoint(int n, float x_point, float y_point){
        this.key = n;
        this.x = x_point;
        this.y = y_point;
    }
}

public class TSP_TwoOpt{

    static void changePoint(ArrayList<MapPoint> mapdata, int index1, int index2){
        for(int i = 0;i<(index2-index1+1)/2;i++){
            MapPoint tempPoint1 = mapdata.get(index1+i);
            MapPoint tempPoint2 = mapdata.get(index2-i);
            
            mapdata.remove(index2-i);
            mapdata.add(index1+i, tempPoint2);
            mapdata.remove(index1+1+i);
            mapdata.add(index2-i, tempPoint1);
        }
    }

    public static void main(String[] args){
        if(args.length != 2){
            System.out.println("使用法：java TSP iputFile outputFile");
            System.out.println("ex) java TSP input.csv output.csv");
            System.exit(0);
        }
        String inputFile = args[0];
        String outputFile = args[1];

        ArrayList<MapPoint> whole_map = new ArrayList<MapPoint>();
        int N=0; //Pointのカウント

        try{
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));

            reader.readLine(); //最初の行は読み飛ばす
            //データの読み込み
            String l;
            while((l = reader.readLine())!=null){
                String[] temp = l.split(",");
                float x = Float.parseFloat(temp[0]);
                float y = Float.parseFloat(temp[1]);
                whole_map.add(new MapPoint(N, x, y));
                N++;
            }
            reader.close();
        }catch(IOException e){
            System.out.println(e);
        }

        //greedyで並べ替え
        for(int i=0;i<whole_map.size()-1;i++){
            MapPoint current = whole_map.get(i);
            double min_distance = 10000;
            int min_index = 0;
            float x = current.x;
            float y = current.y;
            for(int j = i+1;j<whole_map.size();j++){
                MapPoint temp = whole_map.get(j);
                float dx = x-temp.x;
                float dy = y-temp.y;
                double distance = Math.pow(dx*dx+dy*dy,0.5);
                if(distance<min_distance){
                    min_distance = distance;
                    min_index = j;
                }
            }
            MapPoint closest = whole_map.get(min_index);
            whole_map.remove(min_index);
            whole_map.add(i+1, closest);
        }
        
        //2-optで最適化
        int m =0;
        while(m<12){ //12回試行が最も性能が良かった
        for(int i=0;i<whole_map.size()-1;i++){
            MapPoint p1 = whole_map.get(i);
            MapPoint p2 = whole_map.get(i+1);
            float p1_x = p1.x;
            float p1_y = p1.y;
            float p2_x = p2.x;
            float p2_y = p2.y;
            double d1 = Math.pow((p1_x-p2_x)*(p1_x-p2_x)+(p1_y-p2_y)*(p1_y-p2_y),0.5);
            for(int j = i+2;j<whole_map.size()-1;j++){
                MapPoint p3 = whole_map.get(j);
                MapPoint p4 = whole_map.get(j+1);
                
                float p3_x = p3.x;
                float p3_y = p3.y;
                float p4_x = p4.x;
                float p4_y = p4.y;
                double d2 = Math.pow((p4_x-p3_x)*(p4_x-p3_x)+(p4_y-p3_y)*(p4_y-p3_y),0.5);
                double d3 = Math.pow((p1_x-p3_x)*(p1_x-p3_x)+(p1_y-p3_y)*(p1_y-p3_y),0.5);
                double d4 = Math.pow((p4_x-p2_x)*(p4_x-p2_x)+(p4_y-p2_y)*(p4_y-p2_y),0.5);
                
                if(d1+d2>d3+d4){
                    changePoint(whole_map, i+1, j);
                }
            }
        }
        m++;
        }

        try(PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)))){
            writer.println("index");
            for(MapPoint temp : whole_map){
                writer.println(temp.key);
            }
            writer.close();
        }catch(IOException e){
            System.out.println(e);
        }
    }


}
