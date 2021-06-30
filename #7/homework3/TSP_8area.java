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

public class Test{

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

        ArrayList<MapPoint> mapdata1 = new ArrayList<MapPoint>();
        ArrayList<MapPoint> mapdata2 = new ArrayList<MapPoint>();
        ArrayList<MapPoint> mapdata3 = new ArrayList<MapPoint>();
        ArrayList<MapPoint> mapdata4 = new ArrayList<MapPoint>();
        ArrayList<MapPoint> mapdata5 = new ArrayList<MapPoint>();
        ArrayList<MapPoint> mapdata6 = new ArrayList<MapPoint>();
        ArrayList<MapPoint> mapdata7 = new ArrayList<MapPoint>();
        ArrayList<MapPoint> mapdata8 = new ArrayList<MapPoint>();

        double first1 = 1000000;
        double first2 = 1000000;
        double first3 = 1000000;
        double first4 = 1000000;
        double first5 = 1000000;
        double first6 = 1000000;
        double first7 = 1000000;
        double first8 = 1000000;
        
        int[] first = new int[8];

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
                double r = (x-800)*(x-800)+(y-450)*(y-450);
                if(x-1200.0>0 && y-450.0>=0){ //上半分
                    mapdata1.add(new MapPoint(N, x, y));
                    if(r<first1){
                        first1 = r;
                        first[0] = N;
                    }
                }else if(x-800.0>0 && y-450.0>=0){
                    mapdata2.add(new MapPoint(N, x, y));
                    if(r<first2){
                        first2 = r;
                        first[1] = N;
                    }
                }else if(x-400.0>0 && y-450.0>=0){
                    mapdata3.add(new MapPoint(N, x, y));
                    if(r<first3){
                        first3 = r;
                        first[2] = N;
                    }
                }else if(y-450.0>=0){
                    mapdata4.add(new MapPoint(N, x, y));
                    if(r<first4){
                        first4 = r;
                        first[3] = N;
                    }
                }else if(x-400<0 && y-450.0<0){ //下半分
                    mapdata5.add(new MapPoint(N, x, y));
                    if(r<first5){
                        first5 = r;
                        first[4] = N;
                    }
                }else if(x-800<0 && y-450.0<0){
                    mapdata6.add(new MapPoint(N, x, y));
                    if(r<first6){
                        first6 = r;
                        first[5] = N;
                    }
                }else if(x-1200.0<0 && y-450.0<0){
                    mapdata7.add(new MapPoint(N, x, y));
                    if(r<first7){
                        first7 = r;
                        first[6] = N;
                    }
                }else if(y-450.0<0){
                    mapdata8.add(new MapPoint(N, x, y));
                    if(r<first8){
                        first8 = r;
                        first[7] = N;
                    }
                }
                N++;
            }
            reader.close();
        }catch(IOException e){
            System.out.println(e);
        }

        ArrayList<ArrayList<MapPoint>> mapdata_all = new ArrayList<ArrayList<MapPoint>>();
        mapdata_all.add(mapdata1);
        mapdata_all.add(mapdata2);
        mapdata_all.add(mapdata3);
        mapdata_all.add(mapdata4);
        mapdata_all.add(mapdata5);
        mapdata_all.add(mapdata6);
        mapdata_all.add(mapdata7);
        mapdata_all.add(mapdata8);
        
        ArrayList<MapPoint> whole_map = new ArrayList<MapPoint>();

        int map_index = 0; 
        for(ArrayList<MapPoint> mapdata : mapdata_all){
            
            int firstKey = first[map_index];
            MapPoint firstPoint = null; //象限内で一番初めにたどる点
            for(MapPoint temp : mapdata){
                if(temp.key == firstKey){
                    firstPoint = temp;
                }
            }
            
            //firstPointを一番初めに持ってくる
            mapdata.remove(firstPoint);
            mapdata.add(0, firstPoint);

            //greedyで並べ替え
            for(int i=0;i<mapdata.size()-1;i++){
                MapPoint current = mapdata.get(i);
                double min_distance = 10000;
                int min_index = 0;
                float x = current.x;
                float y = current.y;
                for(int j = i+1;j<mapdata.size();j++){
                    MapPoint temp = mapdata.get(j);
                    float dx = x-temp.x;
                    float dy = y-temp.y;
                    double distance = Math.pow(dx*dx+dy*dy,0.5);
                    if(distance<min_distance){
                        min_distance = distance;
                        min_index = j;
                    }
                }
                MapPoint closest = mapdata.get(min_index);
                mapdata.remove(min_index);
                mapdata.add(i+1, closest);
            }

            //2-opt
            int n = 0;
            while(n<13){ //エリア内で13回試行が最も性能が良かった
                for(int i=0;i<mapdata.size()-1;i++){
                    MapPoint p1 = mapdata.get(i);
                    MapPoint p2 = mapdata.get(i+1);
                    float p1_x = p1.x;
                    float p1_y = p1.y;
                    float p2_x = p2.x;
                    float p2_y = p2.y;
                    double d1 = Math.pow((p1_x-p2_x)*(p1_x-p2_x)+(p1_y-p2_y)*(p1_y-p2_y),0.5);
                    for(int j = i+2;j<mapdata.size()-1;j++){
                        MapPoint p3 = mapdata.get(j);
                        MapPoint p4 = mapdata.get(j+1);
                        
                        float p3_x = p3.x;
                        float p3_y = p3.y;
                        float p4_x = p4.x;
                        float p4_y = p4.y;
                        double d2 = Math.pow((p4_x-p3_x)*(p4_x-p3_x)+(p4_y-p3_y)*(p4_y-p3_y),0.5);
                        double d3 = Math.pow((p1_x-p3_x)*(p1_x-p3_x)+(p1_y-p3_y)*(p1_y-p3_y),0.5);
                        double d4 = Math.pow((p4_x-p2_x)*(p4_x-p2_x)+(p4_y-p2_y)*(p4_y-p2_y),0.5);
                        //System.out.println((d1+d2)+"vs"+(d3+d4));
                        if(d1+d2>d3+d4){
                            //繋ぎ直す
                            //System.out.println("change"+(i+1)+"-"+j);
                            changePoint(mapdata, i+1, j);
                        }
                    }
                }
                n++;
            }

            for(MapPoint temp : mapdata){
                whole_map.add(temp);
            }

            map_index++;
        }
        whole_map.add(whole_map.get(0));

        //2-opt
        int m =0;
        while(m<15){ //全体で15回試行が最も性能が良かった
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
                    //繋ぎ直す
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
