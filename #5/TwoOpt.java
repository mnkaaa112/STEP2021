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

public class TwoOpt{

    static void reverse(MapPoint from, MapPoint to){ //順番を逆にする関数
        MapPoint current = from.prev;
        while(current!=to){
            MapPoint temp = current.next;
            current.next = current.prev;
            current.prev = temp;
            current = current.prev;
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
            
            MapPoint begin = mapdata.get(0);
            MapPoint current = begin;
            writer.println("index");
            int j = 0; 
            while(j<n-1){
                
                double min_distance = 100000000;
                MapPoint nextPoint = null;
            
                int i=1;
                //一番近い点を調べる
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
            current.next = begin;



            //2-opt
            current = begin;
            while(current.next.next!=begin){
                //currentよりも後ろに繋がっている点の中からcurrentに二番目に近い点を探す
                double min_distance = 100000000;
                MapPoint second_close_Point = null; //二番目に近い点
                MapPoint temp = current.next.next;
                while(temp!=begin){
                    
                    double d = Math.pow((temp.x-current.x), 2)+Math.pow((temp.y-current.y), 2);
                    if(min_distance>d){
                        min_distance = d;
                        second_close_Point = temp;
    
                    }
                    temp = temp.next;
                }
                double distance = Math.pow((current.next.x-current.x), 2)+Math.pow((current.next.y-current.y), 2); //currentからnextの距離の二乗
                double second_close_point_distance = Math.pow((second_close_Point.next.x-second_close_Point.x), 2)+Math.pow((second_close_Point.next.y-second_close_Point.y), 2); //二番目に近い点とそのnextの距離
                double temp_distance = Math.pow((current.next.x-second_close_Point.next.x), 2)+Math.pow((current.next.y-second_close_Point.next.y), 2)+Math.pow((second_close_Point.x-current.x), 2)+Math.pow((second_close_Point.y-current.y), 2); //付け替えた時の距離
                
                if((distance+second_close_point_distance)>temp_distance){
                    //交差しているので付け替える
                    MapPoint tempA = current.next;
                    MapPoint tempB = second_close_Point;
                    MapPoint tempC = second_close_Point.next;

                    current.next = tempB;
                    tempB.next = tempB.prev;
                    tempB.prev = current;
                    tempA.prev = tempA.next;
                    tempA.next = tempC;
                    tempC.prev = tempA;
                    reverse(tempA, tempB); //付け替えた点の間は逆にたどることになるのでnextとprevを逆にする
                    second_close_Point = tempA;
                }
                current = current.next;
            }
    
            //出力
            writer.println("0");
            current = begin.next;
            while(current!=begin){
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
