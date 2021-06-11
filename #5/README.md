# TSP

## TSP_greedy.java
現在いる点(current)から一番近い点をnextとして繋げていくアルゴリズム  
各点をMapPointクラスに格納し前後のMapPointを保持
```java
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
```
  
  
## TwoOpt.java
TSP_greedy.javaを改良したプログラム  
一番近い点をnextとして繋げた終えた後、交差している線を繋げ直す


#### 繋げなおす処理
```java
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
```
#### void reverse()
```
static void reverse(MapPoint from, MapPoint to){ //順番を逆にする関数
        MapPoint current = from.prev;
        while(current!=to){
            MapPoint temp = current.next;
            current.next = current.prev;
            current.prev = temp;
            current = current.prev;
        }
    }
```
#### 結果
|N| TSP_greedy.java | TwoOpt.java |
|----|----|---- 
|5|3418.1| 3418.1 |
|8| 3832.29| 3832.29 |
|16| 5449.44 | 4994.89 |
|64| 10519.16 | 10150.33 |
|128| 12684.06 | 12455.46 |
|524| 25331.84| 23809.94|
|2048| 49892.05| 47752.11 |

Nが大きい時は多少の改善が見られたが、大幅な改善には至らなかった。  
二番目に近い点だけでなく全ての点に対して繋げ直した距離を比較した場合はTwoOpt.javaよりも性能が悪かった。

（6/11追記）
2Optを実現したつもりですがあまりいい結果になりませんでした。どこが原因なのでしょうか、、、。
