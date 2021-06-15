# malloc 
## *First Fit* vs *Best Fit* vs *Worst Fit* 
First Fit, Best Fit, Worst Fitについて性能比較した結果
| | |First Fit|Best Fit|Worst Fit|
| ---- | ---- | ---- | ---- | ---- |
| callenge1 | time |11ms|1494ms|1531ms|
|           | utilization |70%|70%|70%|
| callenge2 | time |11ms|665ms|720ms|
|           | utilization |40%|40%|40%|
| callenge3 | time |160ms|819ms|98216ms|
|           | utilization |7%|50%|4%|
| callenge4 | time |43965ms|10922ms|1265690ms|
|           | utilization |15%|71%|7%|
| callenge5 | time |39024ms|6052ms|1085979ms|
|           | utilization |15%|74%|7%|

### First Fit 
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
```
