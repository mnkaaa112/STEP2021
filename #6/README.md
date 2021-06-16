# malloc 
## *First Fit* vs *Best Fit* vs *Worst Fit* 
First Fit, Best Fit, Worst Fitについて性能比較した結果　  
(right-connectはfreeする際に後ろに繋げられるfree領域があったときに連結する処理を行なった場合)
| | |First Fit|First Fit(right-connect)|Best Fit|Best Fit(right-connect)|Worst Fit|Worst Fit(right-connect)|
| ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- |
| callenge1 | time |11ms|885ms|1494ms|1256ms|1531ms|2497ms|
|           | utilization |70%|70%|70%|70%|70%|70%|
| callenge2 | time |11ms|382ms|665ms|613ms|720ms|1117ms|
|           | utilization |40%|40%|40%|40%|40%|40%|
| callenge3 | time |160ms|5256ms|819ms|850ms|98216ms|19084ms|
|           | utilization |7%|17%|50%|48%|4%|10%|
| callenge4 | time |43965ms|17957ms|10922ms|2612ms|1265690ms|123587ms|
|           | utilization |15%|26%|71%|69%|7%|19%|
| callenge5 | time |39024ms|28076ms|6052ms|3573ms|1085979ms|148808ms|
|           | utilization |15%|21%|74%|71%|7%|15%|

＜考察＞  
First Fit, Best Fit, Worst Fitでは**Best Fit**が最も性能(Utilization)が良かった。  
right-connectした時に性能が向上したのは**First Fit, Worst Fit**の場合であった。  
my_free()内の処理が適切であるとするならば、Best Fitで性能が向上しなかったのは、連結してfree領域のサイズが大きくなったことでBest Fitとして見つける最適サイズのfree領域が少なくなった、fragmentが返って増えることになったからではないかと考える。(ただのバグである可能性もあり。)

＜各コードの詳細＞  
コード全体をgitにあげていますが、simple_malloc.cから修正した箇所はそれぞれ以下の箇所のみです。  

first_malloc.c
```java
void *my_malloc(size_t size) {
  simple_metadata_t *metadata = simple_heap.free_head;
  simple_metadata_t *prev = NULL;
  // First-fit: Find the first free slot the object fits.
  while (metadata && metadata->size < size) {
    prev = metadata;
    metadata = metadata->next;
  }
  ...
```
best_malloc.c
```java
void *my_malloc(size_t size) {
  simple_metadata_t *metadata = simple_heap.free_head;
  simple_metadata_t *prev = NULL;
  // Best-fit: Find the best free slot the object fits.
  simple_metadata_t *best = NULL;
  simple_metadata_t *best_prev = NULL;
  size_t best_size = 4960;
  
  while (metadata) {
    //sizeを満たし、かつbest_sizeよりも小さければbestを更新
    if(metadata->size >= size && metadata->size < best_size){
      best_prev = prev;
      best = metadata;
      best_size = metadata->size;
    }
    prev = metadata;
    metadata = metadata->next;
  }
  metadata = best;//metadataにbestを受け渡す
  prev = best_prev;
  ...
```

worst_malloc.c
```java
void *my_malloc(size_t size) {
  simple_metadata_t *metadata = simple_heap.free_head;
  simple_metadata_t *prev = NULL;
  // Worst-fit: Find the worst free slot the object fits.
  simple_metadata_t *worst = NULL;
  simple_metadata_t *worst_prev = NULL;
  size_t worst_size = size;

  while (metadata) {
    //worst_sizeより大きければworstを更新
    if(metadata->size >= worst_size){
      worst_prev = prev;
      worst = metadata;
      worst_size = metadata->size;
    }
    prev = metadata;
    metadata = metadata->next;
  }
  metadata = worst;
  prev = worst_prev;
  ...
```

my_free
```java
void my_free(void *ptr) {
  simple_metadata_t *metadata = (simple_metadata_t *)ptr - 1;
  simple_metadata_t *next_metadata = (simple_metadata_t *)((char *)ptr + metadata->size);
  simple_metadata_t *temp_metadata = simple_heap.free_head;
  simple_metadata_t *temp_prev_metadata = NULL;
  while(temp_metadata){
    
    if(temp_metadata == next_metadata){ //新しくfreeする箇所の右側が連結できるとき
      size_t temp = metadata->size;
      metadata->size = temp + temp_metadata->size + sizeof(simple_metadata_t); //temp_metadata自身のサイズも
      simple_remove_from_free_list(temp_metadata, temp_prev_metadata);
      break;
    }
    temp_prev_metadata = temp_metadata;
    temp_metadata = temp_metadata->next;
  }
  simple_add_to_free_list(metadata);
}
```
