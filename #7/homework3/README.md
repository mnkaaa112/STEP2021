## TSP Challenge
### 検討手法1: TSP_TwoOpt.java(2-Optを繰り返す)
以下に示すMapPointクラスとchangePointメソッドを用いて2-Optを実現した。  
結果が最も良かった2-Optの繰り返し回数＝12回
```java
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
```
```java
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
```

### 検討手法2: TSP_4area.java(4分割×2-Opt)
以下に示すように(x,y)=(800,450)を中心として４分割してそれぞれで2-Optを行ってから、四つを繋げたのちにさらに2-Optを行った。  
結果が最も良かった2-Optの繰り返し回数 (エリア内)＝13回　かつ　(全体)＝14回
```java
while((l = reader.readLine())!=null){
                String[] temp = l.split(",");
                float x = Float.parseFloat(temp[0]);
                float y = Float.parseFloat(temp[1]);
                
                double r = (x-800)*(x-800)+(y-450)*(y-450); //中心に一番近い点を各エリアの始点とする
                if(x-800.0>0 && y-450.0>=0){
                    mapdata1.add(new MapPoint(N, x, y));
                    if(r<first1){
                        first1 = r;
                        first[0] = N;
                    }
                }else if(x-800.0<=0 && y-450.0>0){
                    mapdata2.add(new MapPoint(N, x, y));
                    if(r<first2){
                        first2 = r;
                        first[1] = N;
                    }
                }else if(x-800.0<0 && y-450.0<=0){
                    mapdata3.add(new MapPoint(N, x, y));
                    if(r<first3){
                        first3 = r;
                        first[2] = N;
                    }
                }else if(x-800.0>=0 && y-450.0<0){
                    mapdata4.add(new MapPoint(N, x, y));
                    if(r<first4){
                        first4 = r;
                        first[3] = N;
                    }
                }
                N++;
            }
```
### 検討手法3: TSP_8area.java(8分割×2-Opt)
以下に示すように8分割してTSP_4area.javaと同様に探索を行った。8分割は横×縦=4×2の分割であり、各エリアは中心に最も近い点から探索を始め繋ぎ合わせた。
結果が最も良かった2-Optの繰り返し回数 (エリア内)＝13回　かつ　(全体)＝14回
```java
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
```

点を四つのエリアに分け、それぞれを2-Optで繋ぎ、さらに全体を2-Optで最適化した。

### 結果(input_7 N=8192)
|Test Case|TSP_TwoOpt|TSP_4area|TSP_8area|
|----|----|----|----|
|Challenge 6|44083.25|**43094.73**|43242.19|
|Challenge 7|**84127.73**|84843.11|85037.05|

したがって、Challenge6ではTSP_TwoOptが最適であったが、Challenge7ではTSP_4areaが最適であるという結果が得られた。
