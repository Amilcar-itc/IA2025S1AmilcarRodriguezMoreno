package inteligenciaArtificial;

public class Puzzle9 { // Amilcar Rodriguez Moreno
	// Generador aleatorio:
	// https://sliding.toys/mystic-square/8-puzzle/
	// Resolvedor en linea:
	// https://8puzzlesolver.com/#step3
	public static void main(String[]args) {
		// System.out.println("ola");
		byte[][] tablero = {
			    {1, 2, 3},
			    {4, 0, 6},
			    {7, 5, 8}
			};/**/
		byte[][] tablero2 = {
			    {0, 2, 3, 4},
			    {1, 5, 7, 8},
			    {9, 6, 10, 12},
			    {13, 14, 11, 15}
			};/**/
		byte[][] tablero3 = {
			    {13, 11, 3, 15},
			    {0, 5, 1, 9},
			    {12, 8, 4, 14},
			    {10, 6, 2, 7}
			};
		byte[][] tablero4 = {
			    {4, 3, 1},
			    {5, 6, 8},
			    {7, 2, 0}
			};/**/
		byte[][] tablero5 = { // sin solucion
			    {3, 2},
			    {1, 0}
			};/**/
		NodoPuzzle prueba = new NodoPuzzle(tablero4, 0);
		prueba.imprimir();
		
		/*NodoPuzzle[] prueba2 = sucesores(prueba);
		for (int i=0;i<prueba2.length;i++) {
			System.out.println("sucesion "+i);
			prueba2[i].imprimir();
		}*/
		
		PuzzleXSolver resolvedor = new PuzzleXSolver();
		resolvedor.setJuego(prueba);
		NodoPuzzle[] prueba2 = resolvedor.getSolucion();
		if (prueba2 != null)
			for (int i=0;i<prueba2.length;i++) {
				System.out.println("sucesion "+i);
				prueba2[i].imprimir();
			}
	}
}

class PuzzleXSolver {
	private NodoPuzzle nodoInicial, solucion;
	private byte[][] estadoObjetivo;
	PuzzleXSolver() {
		limpiar();
	}
	private void limpiar() {
		nodoInicial = null;
		solucion = null;
		estadoObjetivo = null;
	}
	void setJuego(byte[][] juegoInicial) {
		// validar matriz: rectangular
		// validar numeros: secuenciales, no repetidos, hay un 0, no negativos
		
		// encontrar el numero menor
		int filas = juegoInicial.length, columnas = juegoInicial[0].length;
		byte menor = Byte.MAX_VALUE;
		for (int i = 0; i < filas; i++)
			for (int j = 0; j < columnas; j++)
				if (juegoInicial[i][j] != 0 && juegoInicial[i][j] < menor)
					menor = juegoInicial[i][j];
		// crear version resuelta
		byte[][] solucion = new byte[filas][columnas];
		for (int i = 0; i < filas; i++)
			for (int j = 0; j < columnas; j++)
				solucion[i][j] = menor++;
		solucion[filas - 1][columnas - 1] = 0;
		setJuego(juegoInicial, solucion);
	}
	void setJuego(NodoPuzzle juegoInicial) {
		setJuego(juegoInicial.getTablero());
	}
	void setJuego(byte[][] juegoInicial,byte[][] estadoObjetivo) {
		// validar matrices: rectangular (ambas), del mismo tamaño
		// validar numeros: concuerdan ambas matrices, no negativos
		limpiar();
		
		nodoInicial = new NodoPuzzle(juegoInicial,0);
		byte[][] copia = new byte[estadoObjetivo.length][];
	    for (int i = 0; i < estadoObjetivo.length; i++)
	    	copia[i] = estadoObjetivo[i].clone();
		this.estadoObjetivo = copia;
		resolver();
	}
	void setJuego(NodoPuzzle juegoInicial,NodoPuzzle estadoObjetivo) {
		setJuego(juegoInicial.getTablero(),estadoObjetivo.getTablero());
	}
	void setJuego(byte[][] juegoInicial,NodoPuzzle estadoObjetivo) {
		setJuego(juegoInicial,estadoObjetivo.getTablero());
	}
	void setJuego(NodoPuzzle juegoInicial,byte[][] estadoObjetivo) {
		setJuego(juegoInicial.getTablero(),estadoObjetivo);
	}
	
	private void resolver() {
		NodoPuzzle apuntadorFinal = nodoInicial, nodoActual = nodoInicial;
		int profundidad = 0, casosPosibles = 1, total = 1;
		while (nodoActual != null) {
			if (nodoActual.esIgual(estadoObjetivo)) {
				solucion = nodoActual;
				System.out.println("Solucion encontrada\nProfundidad: "+profundidad+
						" | Casos posibles: "+casosPosibles+" | total: "+total);
				return;
			} else {
				NodoPuzzle[] sucesores = sucesores(nodoActual);
				for (NodoPuzzle sucesor : sucesores) { // recorrer hijos de nodos
					if (!existeAntes(sucesor)) {
						sucesor.setPadre(nodoActual);
						apuntadorFinal.setSiguiente(sucesor);
						apuntadorFinal = sucesor;
						/// Controlar crecimiento
						if (profundidad < sucesor.getProfundidad()) {
							System.out.println("Profundidad: "+profundidad+
									" | Casos analizados: "+casosPosibles+" | total: "+total);/**/
							casosPosibles = 0;
						}
						profundidad = sucesor.getProfundidad();
						casosPosibles++;
						total++;
					}
				}
			}
			nodoActual = nodoActual.getSiguiente();
		}
		System.out.println("Sin solucion posible");
	}
	private boolean existeAntes(NodoPuzzle estado) {
		for (NodoPuzzle nodo=nodoInicial; nodo!=null; nodo=nodo.getSiguiente())
			if (nodo.esIgual(estado))
				return true;
		return false;
	}
	
	NodoPuzzle[] sucesores(NodoPuzzle estado) {
		NodoPuzzle[] sucesores;
		int tamEjeX = estado.getTablero().length;
		int tamEjeY = estado.getTablero()[0].length;
		int nSucesores = 0;
		byte[][] norte = null, sur = null, este = null, oeste = null;
		
		// Buscar la ubicacion del espacio (numero 0)
		boolean bandera = false;
		int x = -1, y = -1;
		for (int i = 0; i < tamEjeX && !bandera; i++)
			for (int j = 0; j < tamEjeY && !bandera; j++)
				if (estado.getTablero()[i][j] == 0) {
					x = i; y = j; bandera = true;
				}
		//System.out.println(x+","+y);
		if (x-1>=0) {
			nSucesores++;
			norte = intercambio(estado.getTablero(),x,y,x-1,y);
		}
		if (x+1<tamEjeX) {
			nSucesores++;
			sur = intercambio(estado.getTablero(),x,y,x+1,y);
		}
		if (y-1>=0) {
			nSucesores++;
			oeste = intercambio(estado.getTablero(),x,y,x,y-1);
		}
		if (y+1<tamEjeY) {
			nSucesores++;
			este = intercambio(estado.getTablero(),x,y,x,y+1);
		}
		sucesores = new NodoPuzzle[nSucesores];
		int i=0;
		if (norte != null) {
			sucesores[i] = new NodoPuzzle(norte, estado.getProfundidad()+1);
			i++;
		}
		if (oeste != null) {
			sucesores[i] = new NodoPuzzle(oeste, estado.getProfundidad()+1);
			i++;
		}
		if (este != null) {
			sucesores[i] = new NodoPuzzle(este, estado.getProfundidad()+1);
			i++;
		}
		if (sur != null) {
			sucesores[i] = new NodoPuzzle(sur, estado.getProfundidad()+1);
			i++;
		}
		return sucesores;
	}
	private byte[][] intercambio(byte[][] tablero, int x1, int y1, int x2, int y2) {
		byte[][] copia = new byte[tablero.length][]; 
		for (int i = 0; i < tablero.length; i++)
			copia[i] = tablero[i].clone();
		byte aux = copia[x1][y1];
		copia[x1][y1] = copia[x2][y2];
		copia[x2][y2] = aux;
		return copia;
	}

	NodoPuzzle[] getSolucion() {
		if (solucion == null)
			return null;
		NodoPuzzle pivote = solucion;
		NodoPuzzle[] familia = new NodoPuzzle[] {pivote};
		while (pivote.getPadre() != null) {
			pivote = pivote.getPadre();
			NodoPuzzle[] aux = familia.clone();
			familia = new NodoPuzzle[aux.length+1];
			familia[0] = pivote;
			for(int i=0; i<aux.length; i++)
				familia[i+1] = aux[i];
		}
		return familia;
	}
}

class NodoPuzzle {
	private byte[][] tablero;
	private int profundidad;
	private NodoPuzzle padre, siguiente;
	
	NodoPuzzle(byte[][] tablero, int profundidad){
		this.tablero = new byte[tablero.length][];
		for (int i = 0; i < tablero.length; i++)
			this.tablero[i] = tablero[i].clone();
		this.profundidad = profundidad;
		padre = null;
		siguiente = null;
	}
	boolean esIgual(NodoPuzzle otro) {
		if (otro == null) return false;
		return esIgual(otro.getTablero());
	}
	boolean esIgual(byte[][] otro) {
		if (otro == null) return false;
		// Verificar dimensiones
		if (tablero.length != otro.length|| tablero[0].length != otro[0].length)
			return false;
		for (int i = 0; i < tablero.length; i++)
			for (int j = 0; j < tablero[i].length; j++)
				if (tablero[i][j] != otro[i][j])
					return false;
		return true;
	}
	void imprimir() {
		int tamEjeX = tablero.length;
		int tamEjeY = tablero[0].length;
		String encabezado = "+";
		for (int i=0; i<tamEjeY; i++)
			encabezado += "-----+";
		System.out.println(encabezado);
		for (int x=0; x<tamEjeX; x++) {
			String fila = "|";
			for (int y=0; y<tamEjeY; y++)
				fila += String.format(" %-3d |", tablero[x][y]);
			System.out.println(fila+"\n"+encabezado);
		}
	}
	byte[][] getTablero() {
		byte[][] copia = new byte[tablero.length][];
	    for (int i = 0; i < tablero.length; i++)
	    	copia[i] = tablero[i].clone();
	    return copia;
	}
	int getProfundidad() {
		return profundidad;
	}
	void setPadre(NodoPuzzle nuevoPadre) {
		this.padre = nuevoPadre;
	}
	NodoPuzzle getPadre() {
		return padre;
	}
	void setSiguiente(NodoPuzzle siguiente) {
		this.siguiente = siguiente;
	}
	NodoPuzzle getSiguiente() {
		return siguiente;
	}
}


/*
14 Febrero 2025

Version optimizada:
 - El sistema es el mismo, se simplifica la logica de apuntadores.
 - Se optimiza el uso de memoria gracias a una comparacion mas detallada, aumentan
los analisis por descendiente pero se descartan mayores escenarios.
 - Se eliminan las clases ListaSimplePuzzle y ListaNivelProfundidad, ademas de los
usos de metodos, atributos y variables hacia estas.
 - Se complementa impresion para depuracion y analisis de rendimiento.
 - Se corrige error por caso de juego imposible.

	Antes:
Los nodos NodoPuzzle solo eran contenedores de escenarios con apuntadores hacia sus
padres y metodos utiles. Se buscaba tenerlos ordenados por niveles de profundidad, la
logica era recorrer todo un nivel de nodos obteniendo sus hijos, filtrandolos y
guardandolos en un nivel posterior. Los niveles se guardaban en nodos superiores de
la clase ListaSimplePuzzle, que eran contenedores simples de nodos de un mismo nivel.
Pero para ordenar los niveles era necesario otra lista a las raices de esas listas de
nodos, por lo que se creo la clase ListaNivelProfundidad, que ordenaba las listas de
ListaSimplePuzzle y contenia metodos que median los niveles de profundidad y la
longitud de cada nivel.

	Ahora:
Los nodos NodoPuzzle tienen un segundo apuntador hacia un nodo siguiente, pasando de
ser una lista de hijos hacia padres, a tambien ser la lista contenedora de todos los
escenarios. Ya no se requiere un control sobre los niveles de profundidad, ahora se
identifican mediante el atributo "profundidad" de NodoPuzzle y la logica al momento
de su creacion (con el metodo "sucesores" de la clase PuzzleXSolver). Tambien se
filtran mayores escenarios ya que el metodo que buscaba si un escenario ya se habia
identificado antes, no tomaba en cuenta los escenarios del mismo nivel en el que se
creaba el nuevo escenario. La comparacion con estos escenarios, aun requiriendo un
mayor uso de operaciones por sucesor filtrado de cada escenario, obtuvo un nivel de
ahorro de memoria aproximado de un 19%. Por ultimo, habia un error de apuntadores en
el caso de que el escenario de entrada no tuviera una solucion posible, lo cual fue
corregido con la nueva logica de nodos.

	Posibles mejoras:
 - Filtrado de casos de entrada:
Se tiene contemplado filtrar casos invalidos ya identificados en la entrada de juegos
en los diferentes metodos setJuego de la clase PuzzleXSolver.
 - Metodo de comprobacion de progreso en cada sucesion:
Se tiene la idea de generar una forma de cuantificar cuan diferente es un estado del
juego de llegar al estado objetivo, esto con el proposito de descartar escenarios que
obtengan una menor puntuacion que su escenario padre. Se espera que esta nueva
comprobacion tenga un mayor coste de operaciones para el filtrado de descendencia
pero que disminuya de una manera considerable el crecimiento exponencial de los casos
por nivel de descendencia.
 - Traducir el codigo a Ingles
Refactorizacion de clases, atributos, metodos y variables dentro del codigo al idioma
Ingles no solo con el proposito de formalizarlo y hacerlo legible a un mayor publico,
sino tambien para reducir la extension de las palabras que estructuran el codigo.
 - Estandarizacion en Java
Aplicar interfaces como Comparable, etc...
 - Documentacion detallada de la logica
Agregar comentarios dentro de secciones clave del codigo asi como las clases y sus
metodos para explicar sus roles y funcionamiento


Probado con la siguiente matriz:
+-----+-----+-----+
| 4   | 3   | 1   |
+-----+-----+-----+
| 5   | 6   | 8   |
+-----+-----+-----+
| 7   | 2   | 0   |
+-----+-----+-----+

Resultados anteriores:
Profundidad: 0 | Casos posibles: 0
Profundidad: 1 | Casos posibles: 2
Profundidad: 2 | Casos posibles: 4
Profundidad: 3 | Casos posibles: 8
Profundidad: 4 | Casos posibles: 16
Profundidad: 5 | Casos posibles: 20
Profundidad: 6 | Casos posibles: 39
Profundidad: 7 | Casos posibles: 63
Profundidad: 8 | Casos posibles: 127
Profundidad: 9 | Casos posibles: 163
Profundidad: 10 | Casos posibles: 315
Profundidad: 11 | Casos posibles: 455
Profundidad: 12 | Casos posibles: 861
Profundidad: 13 | Casos posibles: 1205
Profundidad: 14 | Casos posibles: 2259
Profundidad: 15 | Casos posibles: 3161
Profundidad: 16 | Casos posibles: 5835

Resultados actuales:
Profundidad: 0 | Casos analizados: 1 | total: 1
Profundidad: 1 | Casos analizados: 2 | total: 3
Profundidad: 2 | Casos analizados: 4 | total: 7
Profundidad: 3 | Casos analizados: 8 | total: 15
Profundidad: 4 | Casos analizados: 16 | total: 31
Profundidad: 5 | Casos analizados: 20 | total: 51
Profundidad: 6 | Casos analizados: 39 | total: 90
Profundidad: 7 | Casos analizados: 62 | total: 152
Profundidad: 8 | Casos analizados: 116 | total: 268
Profundidad: 9 | Casos analizados: 152 | total: 420
Profundidad: 10 | Casos analizados: 286 | total: 706
Profundidad: 11 | Casos analizados: 396 | total: 1102
Profundidad: 12 | Casos analizados: 748 | total: 1850
Profundidad: 13 | Casos analizados: 1024 | total: 2874
Profundidad: 14 | Casos analizados: 1893 | total: 4767
Profundidad: 15 | Casos analizados: 2512 | total: 7279
Profundidad: 16 | Casos analizados: 4485 | total: 11764
Solucion encontrada
Profundidad: 17 | Casos analizados: 2894 | total: 14658

*/
