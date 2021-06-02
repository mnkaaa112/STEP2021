# 課題①
"Google"から"渋谷"をたどる最短経路を探す方法
最短経路を探したいのでBFS(幅優先探索)で実装(BFS.java)

既に調べたものを記録しておくためのリストを二分木で実装
```java
class binarytree{
    String id; //IDを記録
    binarytree source;  //最後に最短経路を出力できるようにどのリンク元からたどってきたかを記録
    binarytree left; 
    binarytree right;

    binarytree(String id, String source, binarytree original){ //コンストラクタの宣言
        this.id = id;
        if(original==null){
            this.source = null;
        }else{
            this.source = original.get(source);
        }
        this.left = null;
        this.right = null;
    }
    
    void insert(String id, String source){ //binarytreeに挿入
        ...
    }

    boolean ischecked(String s){ //binarytree内に存在するかどうか
        ...
    }

    binarytree get(String s){ //binarytree内の要素を取り出す
        ...
    }
}
```
###  実行方法
①下図のようにプログラムとデータを配置
step_wikipedia-graph
├── data
│   ├── graph_small.png
│   ├── links_small.txt
│   ├── links.txt
│   ├── pages_small.txt
│   └── pages.txt
├── .gitignore
├── README.md
├── wikipedia_sample.cc
├── BFS.java
└── Isolate.java

②BFS.javaにおいて小さいデータで試したい時はファイル入力をlinks_small.txt, pages_small.txtに変更する
③コンパイルする
```shell
javac BFS.java
```
④実行する **ヒープエラーが出る場合があるので以下のように実行する**
```shell
java -Xmx5000M BFS "どこから" "どこまで"
//例　java -Xmx5000M BFS Google 渋谷
```
⑤20分ほどで結果が得られる
targetからsourceまでのpathが出力される
例) 人工知能<-Google

# 課題②
どこからも辿れずどこにも繋がっていないpageを探す(Isolate.java)


###  実行方法
①下図のようにプログラムとデータを配置
step_wikipedia-graph
├── data
│   ├── graph_small.png
│   ├── links_small.txt
│   ├── links.txt
│   ├── pages_small.txt
│   └── pages.txt
├── .gitignore
├── README.md
├── wikipedia_sample.cc
├── BFS.java
└── Isolate.java

②BFS.javaにおいて小さいデータで試したい時はファイル入力をlinks_small.txt, pages_small.txtに変更する
③コンパイルする
```shell
javac Isolate.java
```
④実行する **ヒープエラーが出る場合があるので以下のように実行する**
```shell
java -Xmx5000M Isolate
```
⑤9分ほどで結果が得られる
題意のpageが一覧で出力される
