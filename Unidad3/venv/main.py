import math
import multiprocessing
import random
import sys
import time


def merge(*args):
    # Support explicit left/right args, as well as a two-item
    # tuple which works more cleanly with multiprocessing.
    left, right = args[0] if len(args) == 1 else args
    left_length, right_length = len(left), len(right)
    left_index, right_index = 0, 0
    merged = []
    while left_index < left_length and right_index < right_length:
        if left[left_index] <= right[right_index]:
            merged.append(left[left_index])
            left_index += 1
        else:
            merged.append(right[right_index])
            right_index += 1
    if left_index == left_length:
        merged.extend(right[right_index:])
    else:
        merged.extend(left[left_index:])
    print(merged)
    return merged


def merge_sort_secuencial(data):
    longitud = len(data)
    if longitud <= 1:
        return data
    medio = int(longitud / 2)
    mitadIzquierda = merge_sort_secuencial(data[:medio])
    mitadDerecha = merge_sort_secuencial(data[medio:])
    return merge(mitadIzquierda, mitadDerecha)


def merge_sort_paralelo(data):
    # Creates a pool of worker processes, one per CPU core.
    # We then split the initial data into partitions, sized
    # equally per worker, and perform a regular merge sort
    # across each partition.
    totalProcesos = multiprocessing.cpu_count()
    print('totalProcesos', totalProcesos)
    pool = multiprocessing.Pool(processes=totalProcesos)
    longitudSubarray = int(math.ceil(float(len(data)) / totalProcesos))
    data = [data[i * longitudSubarray:(i + 1) * longitudSubarray] for i in range(totalProcesos)]
    data = pool.map(merge_sort_secuencial, data)
    # Each partition is now sorted - we now just merge pairs of these
    # together using the worker pool, until the partitions are reduced
    # down to a single sorted result.
    while len(data) > 1:
        # If the number of partitions remaining is odd, we pop off the
        # last one and append it back after one iteration of this loop,
        # since we're only interested in pairs of partitions to merge.
        extra = data.pop() if len(data) % 2 == 1 else None
        data = [(data[i], data[i + 1]) for i in range(0, len(data), 2)]
        data = pool.map(merge, data) + ([extra] if extra else [])
    return data[0]


if __name__ == "__main__":
    numeroExpediente = 1000 #21869286
    arrayNoOrdenado = [random.randint(-numeroExpediente, numeroExpediente) for _ in range(numeroExpediente)]
    print(arrayNoOrdenado)
    print('=======================================')
    for sort in merge_sort_secuencial, merge_sort_paralelo:
        tiempoInicio = time.time()
        arrayOrdenado = sort(arrayNoOrdenado)
        tiempoFinal = time.time() - tiempoInicio
        # print(sort.__name__, arrayOrdenado)
        print(sort.__name__, tiempoFinal)
        print('---------------------------------------')
