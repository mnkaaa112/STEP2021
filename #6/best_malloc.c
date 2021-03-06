////////////////////////////////////////////////////////////////////////////////
/*                 (๑＞◡＜๑)  Malloc Challenge!!  (◍＞◡＜◍)                   */
////////////////////////////////////////////////////////////////////////////////

//
// Welcome to Malloc Challenge!! Your job is to invent a smart malloc algorithm.
//
// Rules:
//
// 1. Your job is to implement my_malloc(), my_free() and my_initialize().
//   *  my_initialize() is called only once at the beginning of each challenge.
//      You can initialize the memory allocator.
//   *  my_malloc(size) is called every time an object is allocated. In this
//      challenge, |size| is guaranteed to be a multiple of 8 bytes and meets
//      8 <= size <= 4000.
//   * my_free(ptr) is called every time an object is freed.
//   * Additionally, my_finalize() is called only once at the end of each
//   challenge,
//     so you can use this function for doing some clean ups if you want.
// 2. The only library functions you can use in my_malloc() and my_free() are
//    mmap_from_system() and munmap_to_system().
//   *  mmap_from_system(size) allocates |size| bytes from the system. |size|
//      needs to be a multiple of 4096 bytes. mmap_from_system(size) is a
//      system call and heavy. You are expected to minimize the call of
//      mmap_from_system(size) by reusing the returned
//      memory region as much as possible.
//   *  munmap_to_system(ptr, size) frees the memory region [ptr, ptr + size)
//      to the system. |ptr| and |size| need to be a multiple of 4096 bytes.
//      You are expected to free memory regions that are unused.
//   *  You are NOT allowed to use any other library functions at all, including
//      the default malloc() / free(), std:: libraries etc. This is because you
//      are implementing malloc itself -- if you use something that may use
//      malloc internally, it will result in an infinite recurion.
// 3. simple_malloc(), simple_free() and simple_initialize() in simple_malloc.c
//    are an example of straightforward implementation.
//    Your job is to invent a smarter malloc algorithm than the simple malloc.
// 4. There are five challenges (Challenge 1, 2, 3, 4 and 5). Each challenge
//    allocates and frees many objects with different patterns. Your malloc
//    is evaluated by two criteria.
//   *  [Speed] How faster your malloc finishes the challange compared to
//      the simple malloc.
//   *  [Memory utilization] How much your malloc is memory efficient.
//      This is defined as (S1 / S2), where S1 is the total size of objects
//      allocated at the end of the challange and S2 is the total size of
//      mmap_from_system()ed regions at the end of the challenge. You can
//      improve the memory utilization by decreasing memory fragmentation and
//      reclaiming unused memory regions to the system with munmap_to_system().
// 5. This program works on Linux and Mac but not on Windows. If you don't have
//    Linux or Mac, you can use Google Cloud Shell (See
//    https://docs.google.com/document/d/1TNu8OfoQmiQKy9i2jPeGk1DOOzSVfbt4RoP_wcXgQSs/edit#).
// 6. You need to specify an '-lm' option to compile this program.
//   *  gcc malloc_challenge.c -lm
//   *  clang malloc_challenge.c -lm
//
// Enjoy! :D
//

#include <assert.h>
#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

void *mmap_from_system(size_t size);
void munmap_to_system(void *ptr, size_t size);

typedef struct simple_metadata_t {
  size_t size;
  struct simple_metadata_t *next;
} simple_metadata_t;

typedef struct simple_heap_t {
  simple_metadata_t *free_head;
  simple_metadata_t dummy;
} simple_heap_t;

simple_heap_t simple_heap;

// Add a free slot to the beginning of the free list.
static void simple_add_to_free_list(simple_metadata_t *metadata) {
  assert(!metadata->next);
  metadata->next = simple_heap.free_head;
  simple_heap.free_head = metadata;
}

// Remove a free slot from the free list.
static void simple_remove_from_free_list(simple_metadata_t *metadata,
                                  simple_metadata_t *prev) {
  if (prev) {
    prev->next = metadata->next;
  } else {
    simple_heap.free_head = metadata->next;
  }
  metadata->next = NULL;
}

// my_initialize() is called only once at the beginning of each challenge.
void my_initialize() {
  simple_heap.free_head = &simple_heap.dummy;
  simple_heap.dummy.size = 0;
  simple_heap.dummy.next = NULL;
  // Implement here!
}

// my_malloc() is called every time an object is allocated. |size| is guaranteed
// to be a multiple of 8 bytes and meets 8 <= |size| <= 4000. You are not
// allowed to use any library functions other than mmap_from_system /
// munmap_to_system.
void *my_malloc(size_t size) {
  // Implement here!
  simple_metadata_t *metadata = simple_heap.free_head;
  simple_metadata_t *prev = NULL;
  // Best-fit: Find the best free slot the object fits.
  simple_metadata_t *best = NULL;
  simple_metadata_t *best_prev = NULL;
  size_t best_size = 5000;
  
  while (metadata) { //free領域のmetadataを順に参照
    
    if(metadata->size >= size && metadata->size < best_size){ //確保したいサイズよりも大きく、かつその中で一番小さいものを見つける

      best_prev = prev;
      best = metadata;
      best_size = metadata->size;

    }
    prev = metadata;
    metadata = metadata->next;
  }
 
  metadata = best; //metadataに代入してFirst-Fitと同様に処理
  prev = best_prev;

  if (!metadata) {
    // There was no free slot available. We need to request a new memory region
    // from the system by calling mmap_from_system().
    //
    //     | metadata | free slot |
    //     ^
    //     metadata
    //     <---------------------->
    //            buffer_size
    size_t buffer_size = 4096;
    simple_metadata_t *metadata =
        (simple_metadata_t *)mmap_from_system(buffer_size);
    metadata->size = buffer_size - sizeof(simple_metadata_t);
    metadata->next = NULL;
    // Add the memory region to the free list.
    simple_add_to_free_list(metadata);
    // Now, try simple_malloc() again. This should succeed.
    return my_malloc(size);
  }
  //printf("here");
  //metadata = best;
  //prev = best_prev;
  // |ptr| is the beginning of the allocated object.
  //
  // ... | metadata | object | ...
  //     ^          ^
  //     metadata   ptr
  void *ptr = metadata + 1;
  size_t remaining_size = metadata->size - size;
  metadata->size = size;
  // Remove the free slot from the free list.
  simple_remove_from_free_list(metadata, prev);

  if (remaining_size > sizeof(simple_metadata_t)) {
    // Create a new metadata for the remaining free slot.
    //
    // ... | metadata | object | metadata | free slot | ...
    //     ^          ^        ^
    //     metadata   ptr      new_metadata
    //                 <------><---------------------->
    //                   size       remaining size
    simple_metadata_t *new_metadata = (simple_metadata_t *)((char *)ptr + size);
    new_metadata->size = remaining_size - sizeof(simple_metadata_t);
    new_metadata->next = NULL;
    // Add the remaining free slot to the free list.
    simple_add_to_free_list(new_metadata);
  }
  return ptr;
  //return mmap_from_system(4096);
}

// my_free() is called every time an object is freed.  You are not allowed to
// use any library functions other than mmap_from_system / munmap_to_system.
void my_free(void *ptr) {
  simple_metadata_t *metadata = (simple_metadata_t *)ptr - 1;
  // Add the free slot to the free list.
  simple_add_to_free_list(metadata);
  // Implement here!
  //munmap_to_system(ptr, 4096);
}

void my_finalize() {
  // Implement here!
}

void test() {
  // Implement here!
  assert(1 == 1); /* 1 is 1. That's always true! (You can remove this.) */
}