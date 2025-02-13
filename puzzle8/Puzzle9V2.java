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
		for (int i=0;i<prueba2.length;i++) {
			System.out.println("sucesion "+i);
			prueba2[i].imprimir();
		}
	}
}

class PuzzleXSolver {
	private NodoPuzzle nodoInicial, solucion;
	private byte[][] estadoObjetivo;
	private ListaNivelProfundidad niveles;
	PuzzleXSolver() {
		limpiar();
	}
	private void limpiar() {
		nodoInicial = null;
		solucion = null;
		estadoObjetivo = null;
		niveles = null;
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
		//resolver2(nodoInicial);
		niveles = new ListaNivelProfundidad(nodoInicial); 
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
		ListaSimplePuzzle nivelActual = niveles.getListaNivel(0);
		for (int profundidad = 0; profundidad <= niveles.getProfundidad(); profundidad++) {
			System.out.println("Profundidad: "+profundidad+" | Casos posibles: "+niveles.getLongitud(profundidad));
			nivelActual = niveles.getListaNivel(profundidad);
			ListaSimplePuzzle nivelSiguiente = null;
			while(nivelActual != null) {
				NodoPuzzle nodoActual = nivelActual.getNodo();
				if (nodoActual.esIgual(estadoObjetivo)) {
					solucion = nodoActual;
					return;
				} else {
					NodoPuzzle[] sucesores = sucesores(nodoActual);
					for (NodoPuzzle sucesor : sucesores) {
						if (!existeAntes(sucesor)) {
							sucesor.setPadre(nodoActual);
							if (nivelSiguiente == null)
								nivelSiguiente = new ListaSimplePuzzle(sucesor);
							else
								nivelSiguiente.add(sucesor);
						}
					}
				}
				nivelActual = nivelActual.getSiguiente();
			}
			niveles.addNivel(nivelSiguiente);
		}
	}
	private boolean existeAntes(NodoPuzzle estado) {
		for (int profundidad=0, fin=estado.getProfundidad(); profundidad<fin; profundidad++) {
			ListaSimplePuzzle nivelActual = niveles.getListaNivel(profundidad);
			while(nivelActual!=null) {
				if (nivelActual.getNodo().esIgual(estado))
					return true;
				nivelActual = nivelActual.getSiguiente();
			}
		}
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
	private NodoPuzzle padre;
	
	NodoPuzzle(byte[][] tablero, int profundidad){
		this.tablero = new byte[tablero.length][];
		for (int i = 0; i < tablero.length; i++)
			this.tablero[i] = tablero[i].clone();
		this.profundidad = profundidad;
		padre = null;
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
}

class ListaSimplePuzzle {
	private ListaSimplePuzzle siguiente;
	private NodoPuzzle nodo;
	ListaSimplePuzzle(NodoPuzzle nodo) {
		this.nodo = nodo;
		siguiente = null;
	}
	void setNodo(NodoPuzzle nodo) {
		this.nodo = nodo;
	}
	NodoPuzzle getNodo() {
		return nodo;
	}
	void add(NodoPuzzle nodo) {
		if (siguiente==null)
			siguiente = new ListaSimplePuzzle(nodo);
		else
			siguiente.add(nodo);
	}
	ListaSimplePuzzle getSiguiente() {
		return siguiente;
	}
	int getProfundidad() {
		return nodo.getProfundidad();
	}
}

class ListaNivelProfundidad {
	private ListaSimplePuzzle raiz;
	private ListaNivelProfundidad siguienteNivel;
	ListaNivelProfundidad(ListaSimplePuzzle listaRaiz) {
		raiz = listaRaiz;
	}
	ListaNivelProfundidad(NodoPuzzle tablero) {
		this(new ListaSimplePuzzle(tablero));
	}
	ListaSimplePuzzle getListaNivel(int profundidad) {
		if (profundidad<0)
			return null;
		if (raiz.getProfundidad()==profundidad)
			return raiz;
		if (siguienteNivel == null)
			return null;
		return siguienteNivel.getListaNivel(profundidad);
	}
	void addNivel(ListaSimplePuzzle listaRaiz) {
		if (siguienteNivel == null)
			siguienteNivel = new ListaNivelProfundidad(listaRaiz);
		else
			siguienteNivel.addNivel(listaRaiz);
	}
	int getProfundidad() {
		if (siguienteNivel == null)
			return raiz.getProfundidad();
		else
			return siguienteNivel.getProfundidad();
	}
	long getLongitud(int profundidad) {
		if(profundidad<1||raiz.getProfundidad()>profundidad)
			return 0;
		if (raiz.getProfundidad()<profundidad)
			return siguienteNivel.getLongitud(profundidad);
		ListaSimplePuzzle pivote = raiz;
		long i;
		for(i=0; pivote.getSiguiente()!=null; i++)
			pivote = pivote.getSiguiente();
		return i;
	}
}
