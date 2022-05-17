#pragma once

#include <stddef.h>

#define VECTOR_STARTING_CAPACITY 4
#define VECTOR_GROWTH_FACTOR 2

#define MAKE_VECTOR() { \
    .items = NULL, \
    .size = 0, \
    .capacity = 0, \
}

typedef struct basic_vector {
    void **items;
    size_t size;
    size_t capacity;
} basic_vector_t;

void vector_reserve(basic_vector_t *v, size_t new_cap);
void vector_push_back(basic_vector_t *v, void *item);

basic_vector_t *vector_alloc();