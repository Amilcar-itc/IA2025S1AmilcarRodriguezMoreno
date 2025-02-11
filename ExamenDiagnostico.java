package inteligenciaArtificial;

import java.util.Scanner;

class Arbol <T extends Comparable<T>>{
	Nodo<T> raiz ;
	
	Arbol() {
		raiz = null;
	}
	Arbol (T objeto) {
		this();
		insertar(objeto);
	}
	
	void insertar(T objeto) {
		if (vacio())
			raiz = new Nodo<T> (objeto);
		else {
			raiz.insertar(objeto);
		}
	}
	
	boolean vacio () {
		return raiz == null;
	}
	
	Nodo<T> buscarNodo (T objeto) {
		if (raiz == null) {
			return null;
		} else if (objeto.compareTo(raiz.contenido) == 0) {
			return raiz;
		}
		return buscarNodo (raiz, objeto);
	}
	private Nodo<T> buscarNodo (Nodo<T> nodo, T objeto) {
		int comparacion = objeto.compareTo(nodo.contenido);
		if (comparacion == 0)
			return nodo;
		if (nodo.esHoja())
			return null;
		if (comparacion < 0)
			return buscarNodo(nodo.izquierda, objeto);
		return buscarNodo(nodo.derecha, objeto);
	}
	void imprimirArbol() {
		imprimirArbol(raiz,"Raíz",0);
	}
	private void imprimirArbol(Nodo<T> nodo, String nombre, int nivel) {
		
		for (int i=0; i<nivel; i++)
			System.out.print('|');
		if (nodo == null) //
			System.out.println("└ null"); //
		else if (!nodo.esHoja()){
			/*System.out.println('└'+nombre+((nodo.esHoja())?" (Hoja)":"")+": "+nodo.contenido.toString());
			if (nodo.izquierda != null) {
				imprimirArbol(nodo.izquierda,("Nodo izq. Nivel"+(nivel+1)), nivel+1);
			}
			if (nodo.derecha != null) {
				imprimirArbol(nodo.derecha,("Nodo der. Nivel"+(nivel+1)), nivel+1);
			}/**/
			imprimirArbol(nodo.izquierda,("Nodo izq. Nivel"+(nivel+1)), nivel+1);
			imprimirArbol(nodo.derecha,("Nodo der. Nivel"+(nivel+1)), nivel+1);
		}
	}
}

class Nodo <T extends Comparable<T>> {
	Nodo<T> izquierda, derecha;
	T contenido;
	
	Nodo(T objeto) {
		contenido = objeto;
		izquierda = null; derecha = null;
	}
	
	void insertar(T objeto) {
		if (objeto.compareTo(contenido) < 0) {
			if (izquierda == null)
				izquierda = new Nodo<T> (objeto);
			else
				izquierda.insertar(objeto);
		} else {
			if (derecha == null)
				derecha = new Nodo<T> (objeto);
			else
				derecha.insertar(objeto);
		}
	}
	
	boolean esHoja() {
		return (izquierda == null && derecha == null);
	}
}

public class ExamenDiagnostico {
	// Menú ejemplo de uso del árbol con String
	public static void main(String args[]) {
		Scanner leer = new Scanner(System.in);
		Arbol<String> a = new Arbol<>();
		int opcion = -1;
		
		while (opcion != 0) {
			opcion = -1;
			System.out.print("\nÁrbol binario de búsqueda"
					+ "\n[0] Salir | [1] Insertar cadena | [2] Buscar nodo | [3] Imprimir árbol"
					+ "\nIngrese una opción: ");
			int min = 0, max = 3;
			while (opcion < min || opcion > max) {
				opcion = leer.nextInt();
				if (opcion < min || opcion > max) {
					System.out.println("Opción inválida");
				}
			}
			if (opcion == 1) {
				System.out.println("Ingrese cadena: ");
				String cadena = leer.next();
				a.insertar(cadena);
			} else if (opcion == 2) {
				System.out.println("Ingrese cadena a buscar: ");
				String cadena = leer.next();
				Nodo<String> n = a.buscarNodo(cadena);
				if (n == null)
					System.out.println("Nodo no encontrado");
				else 
					System.out.println("Nodo encontrado");
			} else if (opcion == 3) {
				System.out.println("Estructura actual del árbol: ");
				a.imprimirArbol();
			}
		}
		System.out.print("Fin del programa");
		leer.close();
	}
}

