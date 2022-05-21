#include <string.h>
#include "malloc.h"
#include "vector.h"

#define MIN(a, b) (((a)<(b))?(a):(b))

void vector_reserve(basic_vector_t *v, size_t new_cap) {
    void **new_alloc = malloc(new_cap * sizeof(void *));
    size_t old_cap = v->capacity;

    memcpy(new_alloc, v->items, MIN(new_cap, old_cap) * sizeof(void *));
    free(v->items);

    v->items = new_alloc;
    v->capacity = new_cap;
}

void vector_push_back(basic_vector_t *v, void *item) {
    if (v->size >= v->capacity) {
        vector_reserve(v, v->capacity * VECTOR_GROWTH_FACTOR);
    }

    v->items[v->size++] = item;
}

basic_vector_t *vector_alloc(size_t cap) {
    basic_vector_t *v = malloc(sizeof(basic_vector_t));
    basic_vector_t v_init = MAKE_VECTOR();

    vector_reserve(&v_init, cap);
    memcpy(v, &v_init, sizeof(basic_vector_t));

    return v;
}

void vector_clear(basic_vector_t *v, dealloc *dealloc_fn) {
    if (dealloc_fn == NULL) goto skip;

    for (size_t i = 0; i < v->size; i++) {
        dealloc_fn(v->items[i]);
    }

    skip:
    v->size = 0;
}
