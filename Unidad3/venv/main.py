# coding=utf-8
import math
import multiprocessing
import random
import sys
import time

# Este es el alogritmo que hace la mezcla de los subarrays ordenados
def merge(*args):
    # Se utiliza argumentos variables porque en modo secuencial
    # se pasan de manera explícita dos argumentos: mitad_izquierda y mitad_derecha
    # En modo paralelo se pasa un único argumento que es una tupla de dos
    # elementos con la forma: ([],[])
    izquierda, derecha = args[0] if len(args) == 1 else args

    longitud_izq, longitud_derecha = len(izquierda), len(derecha)
    indice_izq, indice_derecho = 0, 0
    resultado = []
    while indice_izq < longitud_izq and indice_derecho < longitud_derecha:
        if izquierda[indice_izq] <= derecha[indice_derecho]:
            resultado.append(izquierda[indice_izq])
            indice_izq += 1
        else:
            resultado.append(derecha[indice_derecho])
            indice_derecho += 1
    if indice_izq == longitud_izq:
        resultado.extend(derecha[indice_derecho:])
    else:
        resultado.extend(izquierda[indice_izq:])
    return resultado

# Este es el algoritmo merge sort divide and conquer.
# Nada en especial que señalar en esta implementación
def merge_sort(data):
    longitud = len(data)
    if longitud <= 1:
        return data
    medio = int(longitud / 2)
    mitad_izquierda = merge_sort(data[:medio])
    mitad_derecha = merge_sort(data[medio:])
    return merge(mitad_izquierda, mitad_derecha)

# Este es el algoritmo merge sort modificado para ejecutarse de manera paralela.
def merge_sort_paralelo(data):
    # Obtenemos la cantidad de cores que tiene el CPU
    total_procesos = multiprocessing.cpu_count()

    # Creamos un pool de workers processes, uno por core
    pool = multiprocessing.Pool(processes=total_procesos)

    # Obtenemos la longitud de los subarrays que se van a repartir a los workers.
    # Esto es lo que se conoce como load balance
    longitud_subarray = int(math.ceil(float(len(data)) / total_procesos))

    # Sobrescribimos data con la lista de subarrays
    data = [data[i * longitud_subarray:(i + 1) * longitud_subarray] for i in range(total_procesos)]

    # Cada worker levanta un proceso donde ejecuta el merge sort al subarray correspondiente
    # Cuando todos los workers terminan, los subarrays ya estarían ordenados
    data = pool.map(merge_sort, data)

    # Una vez los subarrays se encuentran ordenados ahora tenemos que mezclarlos de dos en dos
    # con el objetivo de reducir todos los subarrays a un solo array ordenado

    while len(data) > 1:
        # If the number of partitions remaining is odd, we pop off the
        # last one and append it back after one iteration of this loop,
        # since we're only interested in pairs of partitions to merge.

        # Si el número de subarrays es impar, nos guardamos el último para mezclarlo en
        # próximas iteraciones del while
        extra = data.pop() if len(data) % 2 == 1 else None

        # Se combinan los subarrays de dos en dos para obtener una estructura de tuplas
        # como la siguiente: [(subarray1, subarray2), (subarray3,subarray4)...]
        data = [(data[i], data[i + 1]) for i in range(0, len(data), 2)]

        # Ahora reutilizamos el pool creado para paralelizar el merge sort de las tuplas de subarrays
        # Si data es impar añadimos al final el último subarray el cual no pudo formar parte de
        # ninguna tupla
        data = pool.map(merge, data) + ([extra] if extra else [])
    return data[0]


if __name__ == "__main__":
    # Imprimo en pantalla los cores del ordenador solo a modo informativo
    total_procesos = multiprocessing.cpu_count()
    print '======================================='
    print 'Total de cores: ', total_procesos
    print '======================================='
    print

    # Defino mi número de expediente con el cual vamos a generar los datos de manera aleatroria.
    numero_expediente = 21869286
    array_no_ordenado = [random.randint(-numero_expediente, numero_expediente) for _ in range(numero_expediente)]

    # Mostramos los datos desordenados
    print 'Array no ordenado'
    print array_no_ordenado
    print '======================================='
    print

    # Vamos a probar los dos algoritmos de merge sort que hemos hecho (secuencial y paralelo)
    # Vamos a medir el tiempo que toma cada uno de estos para poder comparar su rendimiento.
    for sort in merge_sort, merge_sort_paralelo:
        tiempo_inicio = time.time()
        array_ordenado = sort(array_no_ordenado)
        tiempo_final = time.time() - tiempo_inicio
        print sort.__name__, tiempo_final
        print '======================================='
        print
